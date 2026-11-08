# Plan del Registro de Campo — objetivo 1.0.0

> Documento de diseño. No implementa código, recetas ni recursos. Define cómo convertir el **Registro de Campo del Archivista** en el marcador de la versión **1.0.0** y cómo resolver la progresión en mundos nuevos y en mundos ya explorados.

---

## 1. El problema que hay que resolver

La propuesta de `logros.md` acierta en la dirección, pero un mundo de 500 días cambia las reglas:

- Todo lo cercano ya está explorado.
- No quedan estructuras nuevas ni cofres de loot cercanos.
- El jugador ya tiene la mayoría de materiales y ha usado herramientas parecidas.
- Descubrir el mod mediante documentos encontrados en el mundo no es viable: no hay mundo nuevo que explorar.

La progresión no puede depender de **encontrar**. Tiene que depender de **reconstruir**:

> El jugador no descubre el Gremio encontrando sus papeles. Recupera el conocimiento del Gremio al identificar materiales, fabricar dispositivos y usar las herramientas que ya tiene delante.

Todo lo necesario para usar el mod debe poder obtenerse en una partida normal, con materiales renovables o ya disponibles, sin estructuras, sin loot obligatorio y sin depender de una tirada aleatoria.

---

## 2. La idea central

El **Registro de Campo del Archivista** (*Archivist's Field Ledger*) es un libro crafteable que contiene dos secciones:

- **Manual:** conocimiento práctico. Se recupera con hechos comprobables del jugador: obtener materiales, fabricar objetos, colocar bloques, abrir interfaces y completar acciones.
- **Anales:** historia y documentos del Gremio. Se desbloquean con hitos; los documentos de loot son opcionales y solo añaden variantes o notas de color.

El libro no es mágico: es un registro técnico que se completa solo cuando el servidor puede demostrar que el jugador hizo o conoció algo. El conocimiento es permanente: consumir, guardar o perder un objeto no borra una página ya recuperada.

### Qué cambia respecto a la propuesta inicial

| Propuesta inicial | Ajuste para 1.0 |
|---|---|
| El loot de cartógrafo aporta páginas | El loot solo aporta Anales opcionales. El Manual nunca depende de loot. |
| Los logros guían el descubrimiento | Los logros celebran hitos; el Manual se guía por hechos, no por logros. |
| Algunas páginas dependen de una oferta rara | La información nunca depende de RNG. La oferta rara puede ser un logro opcional, no una puerta de conocimiento. |
| Mundos nuevos | Funciona igual en mundos viejos: se siembra lo que se puede demostrar y el resto se recupera al repetir la acción. |

---

## 3. Arquitectura técnica

### 3.1 El libro

- **ID:** `arcanum_storage:field_ledger`.
- **Nombre:** “Registro de Campo del Archivista” / “Archivist's Field Ledger”.
- **Pila:** 1. Es un libro personal de consulta; el conocimiento es del jugador, no del objeto.
- **Uso:** clic derecho mientras se sostiene. Abre la interfaz del Registro.
- **Receta:** crafteable desde el principio con materiales renovables:

  ```
  I C
  B R
  ```

  - `I` = saco de tinta
  - `C` = lingote de cobre
  - `B` = libro
  - `R` = polvo de redstone

  Todos los materiales se pueden obtener o renovar en una partida normal. No requiere End, estructuras ni loot.

- **Desbloqueo en el libro de recetas:** un advancement de receta (`inventory_changed` con cualquier libro + `recipe_unlocked`) añade la receta al libro de recetas. La receta sigue siendo crafteable aunque el advancement no se haya concedido; el avance solo guía y evita una bloqueo circular.

- **Primer contacto:** el avance “Una página en blanco” se concede al obtener cualquier libro vanilla. En mundos viejos, también se concede al fabricar el Registro, para que nadie quede bloqueado por haber empezado antes de instalar el mod.

### 3.2 Hechos: la base de conocimiento

Un **hecho** es una afirmación verificable sobre lo que el jugador ha conocido o hecho. Los hechos se guardan como **advancements técnicos** (sin `display`), uno por hecho. Esto aporta:

- Persistencia en los datos de jugador de vanilla, sin formato de guardado propio.
- Supervivencia a la muerte, reconexión y reinicio del servidor.
- Aislamiento por jugador en multijugador.
- Compatibilidad con `/advancement`, datapacks y pruebas automatizadas.
- Cero migraciones al añadir hechos nuevos.

Tipos de hecho:

| Tipo | Ejemplo | Fuente |
|---|---|---|
| `item:` | `item:minecraft:copper_ingot` | `inventory_changed` + siembra de inventario |
| `craft:` | `craft:arcanum_storage:archive_cable` | `recipe_crafted` / `crafter_recipe_crafted` |
| `place:` | `place:arcanum_storage:storage_display` | `placed_block` |
| `event:` | `event:first_chest` | Código del mod (`impossible`) |
| `adv:` | `adv:minecraft:end/kill_dragon` | Solo para sembrar mundos existentes; nunca para completar una página por sí solo |

Convención de IDs:

- Hecho: `item:minecraft:copper_ingot`.
- Advancement técnico: `arcanum_storage:ledger/item/minecraft/copper_ingot` (se sustituyen los `:` por `/`).
- Todos los criterios se llaman `done` para que el código pueda concederlos de forma uniforme.

Ejemplo de hecho automático:

```json
{
  "parent": "arcanum_storage:ledger/root",
  "criteria": {
    "done": {
      "conditions": { "items": [{ "items": "minecraft:copper_ingot" }] },
      "trigger": "minecraft:inventory_changed"
    }
  },
  "requirements": [["done"]]
}
```

Ejemplo de hecho de fabricación:

```json
{
  "parent": "arcanum_storage:ledger/root",
  "criteria": {
    "done": {
      "conditions": { "recipe_id": "arcanum_storage:archive_cable" },
      "trigger": "minecraft:recipe_crafted"
    },
    "crafter": {
      "conditions": { "recipe_id": "arcanum_storage:archive_cable" },
      "trigger": "minecraft:crafter_recipe_crafted"
    }
  },
  "requirements": [["done", "crafter"]]
}
```

Ejemplo de hecho de evento, concedido por código:

```json
{
  "parent": "arcanum_storage:ledger/root",
  "criteria": { "done": { "trigger": "minecraft:impossible" } },
  "requirements": [["done"]]
}
```

El código concede un hecho con:

```java
AdvancementEntry entry = server.getAdvancementLoader().get(factAdvancementId);
player.getAdvancementTracker().grantCriterion(entry, "done");
```

Un hecho técnico **no** se muestra en la pantalla de logros. La raíz técnica `arcanum_storage:ledger/root` tampoco tiene `display`; sirve solo para organizar el árbol.

### 3.3 Registro y siembra

El servidor mantiene la base de conocimiento sin duplicar estado:

1. **Hechos automáticos:** los advancements con `inventory_changed`, `recipe_crafted` y `placed_block` se conceden solos cuando el jugador hace la acción.
2. **Hechos de evento:** el código del mod los concede en el punto exacto (abrir el display, extraer, analizar la prensa, crear/recuperar un caché, etc.).
3. **Siembra inicial:** en la primera conexión después de instalar 1.0 (o al abrir el Registro por primera vez), si el hecho `arcanum_storage:ledger/seeded` no está concedido:
   - se recorre el inventario, la armadura, la mano secundaria y el cofre del End;
   - se consultan las estadísticas vanilla (`CRAFTED`, `USED`, `MINED`, `PICKED_UP`) para los objetos y recetas relevantes;
   - se consulta un mapa corto de advancements vanilla ya completados (por ejemplo, `story/smelt_iron`, `story/enchant_item`, `end/kill_dragon`) para deducir acceso a tecnología, nunca para fingir una acción del mod;
   - se conceden los hechos demostrables;
   - se concede `arcanum_storage:ledger/seeded` y se muestra una nota única explicando que el Registro recuperó lo que pudo comprobar.
4. **Cambios posteriores:** un escaneo periódico ligero (cada 20 ticks, o al cambiar el contador de cambios del inventario) comprueba hechos que puedan haberse escapado a los criterios automáticos: objetos recibidos por comercio, cofres, shulkers o mods de terceros; libros encantados con Arcanum Binding; bolsas encantadas.
5. **Sin falsos positivos:** tener un objeto no implica haberlo fabricado, colocado o usado. Cada tipo de hecho se concede solo por su fuente real.

Limitación honesta: no se puede reconstruir el historial anterior a la instalación si el mundo nunca lo guardó. La nota de calibración debe decirlo sin prometer milagros. Los ingredientes siempre se pueden volver a obtener.

### 3.4 Páginas, estados y contenido

Cada entrada del Manual tiene hasta tres estados:

- **Desconocido:** solo el título y una frase que indica que faltan referencias.
- **Parcial:** se ha identificado al menos un componente o procedimiento; se muestra la hipótesis y la pista que falta.
- **Completo:** se conocen los componentes y la función práctica; se muestra la explicación completa.

Los datos viven en `data/arcanum_storage/ledger/manual.json`:

```json
{
  "chapters": [
    {
      "id": "building",
      "order": 3,
      "entries": [
        {
          "id": "archive_cable",
          "order": 1,
          "icon": "arcanum_storage:archive_cable",
          "stages": [
            { "state": "unknown", "requires": [] },
            { "state": "partial", "requires": ["item:minecraft:copper_ingot"] },
            {
              "state": "complete",
              "requires": [
                "item:minecraft:copper_ingot",
                "item:minecraft:redstone",
                "craft:arcanum_storage:archive_cable"
              ]
            }
          ]
        }
      ]
    }
  ]
}
```

Reglas de resolución:

- La etapa más alta cuyos requisitos estén todos presentes define el estado.
- La etapa `unknown` no tiene requisitos; siempre es el punto de partida.
- La etapa `complete` es obligatoria.
- Los hechos que faltan se muestran al jugador con su nombre legible: objetos y bloques usan los nombres vanilla; los eventos usan claves de traducción.
- Las entradas de solución de problemas pueden usar solo dos estados (inicial y resuelto) si el estado “desconocido” no aporta nada.
- El contenido se carga en el servidor con un `ResourceReloadListener`; un datapack puede añadir o sustituir capítulos sin recompilar el mod.
- El servidor valida al cargar: IDs únicos, etapas ordenadas, hechos existentes y `complete` presente. Los errores se registran y no rompen el mundo. La paridad de claves de idioma se comprueba en el build, no en tiempo de ejecución.

### 3.5 Interfaz del libro

- Pantalla propia (`LedgerScreen extends Screen`), sin `ScreenHandler`: es un documento de solo lectura.
- Dos páginas:
  - **Izquierda:** índice por capítulos, entradas con icono y estado (desconocido / parcial / completo), navegación.
  - **Derecha:** título, texto del estado, referencias que faltan, iconos de objetos y, cuando esté disponible, una rejilla de receta.
- Navegación: clic en el índice, flechas anterior/siguiente, rueda del ratón, `Page Up`/`Page Down`, `Esc` para cerrar.
- Búsqueda opcional por título y por nombre de objeto; filtra el índice, no el contenido.
- Los textos largos se parten con `textRenderer.wrapLines` y se paginan si no caben; nunca se recorta sin aviso.
- El libro se abre por una carga S2C (`OpenLedgerS2CPayload`) enviada por el servidor. La navegación posterior es local y no requiere ida y vuelta.
- El servidor envía capítulos, entradas, estado, hechos faltantes e iconos. Los textos no viajan: se resuelven en el cliente con claves de idioma, así el payload es pequeño y las traducciones son recursos normales.
- No se abre automáticamente durante combate o construcción. El jugador abre el libro cuando quiere.

### 3.6 Logros visibles

Los logros visibles son una capa fina sobre los hechos:

- Viven en `data/arcanum_storage/advancement/main/...` con `display` y una raíz propia (fondo de pergamino o piedra).
- Sus criterios son `minecraft:impossible`; el `LedgerService` los concede cuando se cumplen los hechos asociados.
- Un archivo `data/arcanum_storage/ledger/achievements.json` relaciona cada logro visible con sus hechos. Así se puede cambiar la ruta sin tocar código.
- Los logros visibles **no** bloquean recetas, páginas ni funciones. Son celebración y orientación.
- La ruta principal se puede completar sin morir, sin encontrar loot y sin depender de una oferta aleatoria.
- Los logros opcionales (caché recuperado, 20 cofres, documento encontrado) no son requisito para el logro final del Manual.

### 3.7 Avisos y notificaciones

- Cuando un hecho hace que una entrada suba de estado, el servidor envía `LedgerNoteS2CPayload`.
- El cliente muestra un aviso discreto: “Nueva nota en el Registro de Campo” con el título de la entrada y un enlace `[Abrir]`.
- El enlace ejecuta `/arcanum ledger open`, que envía la pantalla al jugador.
- Un toast es opcional; el chat es la fuente principal para no tapar la pantalla.
- Los avisos se agrupan en una ventana corta para no spamear al fabricar varias cosas seguidas.
- Al abrir el libro por primera vez tras una siembra, se muestra un resumen único: “Se recuperaron N referencias comprobables”.

### 3.8 Comandos y depuración

| Comando | Uso |
|---|---|
| `/arcanum ledger status` | Resumen de hechos y estados del jugador. |
| `/arcanum ledger facts` | Lista de hechos concedidos y pendientes. |
| `/arcanum ledger grant <hecho>` | Concede un hecho (nivel 2). |
| `/arcanum ledger reset [jugador]` | Borra hechos y siembra (nivel 2). |
| `/arcanum ledger seed` | Vuelve a ejecutar la siembra (nivel 2). |
| `/arcanum ledger open` | Abre la pantalla sin necesidad del libro (nivel 0, para pruebas). |

Los comandos son también la base del arnés de pruebas en servidor dedicado.

### 3.9 Rendimiento y persistencia

- No hay guardado propio: los hechos son advancements de vanilla.
- El escaneo periódico recorre todos los slots del inventario y del cofre del End de cada jugador cada 20 ticks; es despreciable para un servidor normal.
- Si `PlayerInventory.getChangeCount()` no cambió y no hay acciones del mod, se puede saltar el escaneo de objetos.
- La comprobación de progreso de hechos es un `Map` por jugador; se limpia al desconectar.
- El payload del libro contiene ~30 entradas y ~40 hechos: unos pocos KB.
- La pantalla no consulta el servidor al pasar páginas.

---

## 4. Contenido del Manual para 1.0

Solo se documentan funciones implementadas. El diezmo no está implementado en 0.2.1 y **no** debe aparecer como instrucción vigente; puede mencionarse en los Anales como historia.

### 4.1 Capítulos y entradas

| # | Capítulo | Entradas | Hechos principales |
|---:|---|---|---|
| 1 | El primer registro | `field_ledger`, `reading_states` | `craft:field_ledger`, primera nota recuperada |
| 2 | El primer sello | `storage_connector`, `storage_display`, `first_connection` | materiales de redstone, `craft:` y `place:` de ambos bloques, `event:first_chest` |
| 3 | Construir el archivo | `archive_cable`, `network_faces`, `capacity` | cobre/redstone, `craft:archive_cable`, `place:archive_cable`, `event:first_chest` |
| 4 | Leer el índice | `search`, `extraction`, `origin`, `depth_warning` | `event:searched`, `event:extracted_item`, `event:depth_warning` |
| 5 | Los estuches de campaña | `shulker_index`, `shulker_take`, `press`, `press_preview`, `press_rules`, `press_multiple` | shulker, `craft:archive_connector`, `event:press_analyzed`, `event:press_organized` |
| 6 | El diezmo | *(no incluido en 1.0)* | Solo histórico en Anales. |
| 7 | Reservas personales | `end_pouch`, `ender_access` | ingredientes del End, `craft:end_pouch`, `event:end_pouch_used` |
| 8 | Protocolos de recuperación | `arcanum_binding`, `anvil_cost`, `death_cache`, `cache_delivery`, `cache_cleanup` | `event:binding_obtained`, `event:binding_applied`, `event:cache_created`, `event:cache_recovered`, `event:cache_emptied` |
| 9 | Límites del archivo | `twenty_chests`, `listing_cap`, `extraction_only`, `local_only` | `event:twenty_chests`, `event:listing_warning`, `event:first_chest` |
| 10 | Solución de problemas | `no_chests`, `disconnected`, `refused_operation`, `press_multiple`, `cache_lost` | eventos de error y estados de la interfaz |

Cada entrada tiene título, textos `unknown`/`partial`/`complete` y, si aplica, una rejilla de receta.

### 4.2 Anales

Los Anales no son una historia lineal. Se desbloquean por hitos y, opcionalmente, por documentos de loot. Nunca contienen instrucciones necesarias para usar el mod.

| Documento | Desbloqueo principal | Loot opcional |
|---|---|---|
| El Gremio de los Cartógrafos | Fabricar el Registro | Variante de documento |
| Los puestos de avituallamiento | `event:first_chest` | Variante |
| La guerra de los relés | `craft:archive_cable` | Variante |
| Las generaciones de sellos | `craft:storage_connector` + `craft:storage_display` | Variante |
| El diezmo y la administración | `event:twenty_chests` | Variante |
| Los archivos abandonados | `event:depth_warning` | Variante |
| Las reservas de campaña | `craft:end_pouch` | Variante |
| El protocolo de recuperación | `event:cache_created` o `event:cache_recovered` | Variante |
| Notas de campo | `event:press_organized` | Variante |
| El último registro | Completar todas las entradas del Manual de la versión | — |

“El último registro” es el cierre narrativo de 1.0: no explica una catástrofe, solo deja las instalaciones preparadas para quien venga después. Es también el logro final del Manual.

### 4.3 Hechos de eventos

Además de los hechos automáticos, el código concede:

| Hecho | Momento |
|---|---|
| `event:first_chest` | Abrir el display con al menos un cofre conectado. |
| `event:twenty_chests` | Alcanzar 20 cofres en una red. |
| `event:searched` | Enviar una búsqueda no vacía. |
| `event:extracted_item` | Extraer un objeto del catálogo. |
| `event:extracted_shulker` | Extraer una shulker completa o un objeto de dentro de una shulker. |
| `event:depth_warning` | Mostrar el aviso de contenido oculto por profundidad. |
| `event:listing_warning` | Mostrar el aviso de límite de 3000 entradas. |
| `event:press_analyzed` | Generar una vista previa válida en la prensa. |
| `event:press_organized` | Confirmar una organización válida. |
| `event:press_multiple` | Abrir la prensa con más de un cofre. |
| `event:end_pouch_used` | Abrir el cofre del End con el End Pouch. |
| `event:binding_obtained` | Obtener un libro con Arcanum Binding. |
| `event:binding_applied` | Aplicar el encantamiento a un End Pouch. |
| `event:cache_created` | Crear un Archive Cache al morir. |
| `event:cache_recovered` | Abrir o vaciar un Archive Cache. |
| `event:cache_emptied` | Vaciar y cerrar un caché colocado. |
| `event:no_chests` | Abrir el display sin cofres. |
| `event:connection_error` | Ver un estado de desconexión o una operación rechazada. |
| `event:inventory_full` | Intentar extraer con el inventario lleno. |

---

## 5. Revisión de `logros.md`

### 5.1 Lo que funciona

- La separación entre logros y Registro.
- La regla de no premiar cantidades ni repetir acciones.
- La regla de no exigir muerte, pérdida de objetos, estructuras raras o RNG.
- La idea de que el conocimiento sea permanente y de que los ingredientes se puedan volver a obtener.
- La propuesta de revelar primero una hipótesis y después el procedimiento.
- La limitación honesta sobre los mundos existentes.

### 5.2 Ajustes necesarios

1. **“Recoger cualquier libro” no basta en mundos viejos.** El criterio `inventory_changed` no se dispara por objetos que ya estaban en el inventario. Hay que sembrar y, además, conceder el avance al fabricar el Registro.
2. **La información no puede depender de una oferta rara.** “Protocolo de emergencia” no debe ser la única puerta para saber que Arcanum Binding existe. La página debe abrirse al tener un End Pouch y explicar cómo conseguir el libro; el logro de obtenerlo sigue siendo opcional.
3. **La página de shulkers no debe exigir haber llegado al End.** La información básica puede abrirse al ver una shulker en el catálogo o al tener una; el logro de “estuche de campaña” puede seguir siendo un objetivo tardío.
4. **Demasiados pasos intermedios.** La ruta de 16 logros se puede consolidar en ~10 principales y 2–3 opcionales sin perder orientación.
5. **Separar recetas de logros visibles.** Los desbloqueos de recetas deben ser advancements técnicos o de receta; los logros visibles no deben ser requisitos de crafteo.
6. **Añadir un logro de cierre.** “El Registro completo” al completar el Manual de la versión da un final claro a 1.0 y no depende de loot.
7. **Aclarar el estado “desconocido”.** En entradas de solución de problemas, un estado “desconocido” genérico no aporta; usar dos estados.
8. **Hitos futuros.** No añadir logros de funciones no implementadas; el jugador vería una lista de tareas imposibles.

### 5.3 Ruta visible revisada

| Tipo | Logro | Criterio | Qué enseña |
|---|---|---|---|
| Principal | **Una página en blanco** | Obtener un libro vanilla (o fabricar el Registro) | Presenta el libro y desbloquea su receta en el libro de recetas. |
| Principal | **Archivista de campo** | Fabricar el Registro | Abre el Manual, los Anales y los tres estados. |
| Principal | **El primer sello** | Colocar Storage Connector y Storage Display | La pantalla necesita su conector. |
| Principal | **El archivo responde** | Abrir el display con un cofre conectado | Conectar cofres y leer el catálogo. |
| Principal | **Suministros localizados** | Extraer un objeto | Búsqueda, páginas y extracción. |
| Objetivo | **Estuche de campaña** | Extraer una shulker o un objeto de dentro de una shulker | Contenido anidado y profundidad. |
| Principal | **Una caja con nombre tiene dueño** | Colocar un Archive Connector y abrir la prensa | La prensa y su regla de etiquetas. |
| Principal | **Suministros reorganizados** | Confirmar una organización válida | Vista previa, confirmación y resultado. |
| Objetivo | **Una puerta portátil** | Fabricar un End Pouch | Reservas personales del End. |
| Opcional | **Protocolo de emergencia** | Obtener el libro con Arcanum Binding | La página ya está disponible sin el logro. |
| Opcional | **El equipo queda a salvo** | Recuperar un Archive Cache | No se exige morir para completar el Manual. |
| Principal | **El Registro completo** | Completar todas las entradas del Manual de 1.0 | Cierre de la progresión y de los Anales. |

---

## 6. Traducciones

- Tres archivos con paridad exacta de claves: `en_us.json`, `es_es.json`, `es_mx.json`.
- Claves de contenido:
  - `ledger.arcanum_storage.chapter.<capítulo>.title`
  - `ledger.arcanum_storage.<entrada>.title`
  - `ledger.arcanum_storage.<entrada>.unknown`
  - `ledger.arcanum_storage.<entrada>.partial`
  - `ledger.arcanum_storage.<entrada>.complete`
  - `ledger.arcanum_storage.fact.<hecho>` para eventos; los objetos usan nombres vanilla.
- Claves de interfaz: `gui.arcanum_storage.ledger.*`.
- Los archivos de datos no contienen texto visible.
- Añadir una comprobación automática de paridad de claves (test o script) antes de cada release.
- Revisar el tono: práctico, seco, técnico cuando haga falta, ligeramente irónico. Nada de épica grandilocuente.

---

## 7. Pruebas y validación

### 7.1 Pruebas unitarias

- Resolución de estados con etapas incompletas, hechos duplicados y entradas sin `complete`.
- Conversión `hecho` ↔ `advancement` y rechazo de IDs inválidos.
- Carga de `manual.json` y `achievements.json` con validación de referencias.
- Paridad de claves de idioma.

### 7.2 Servidor dedicado

- Dos jugadores: el conocimiento de uno no aparece en el otro.
- Desconexión y reconexión: los hechos persisten.
- Reinicio del servidor: los hechos persisten.
- Muerte y respawn: los hechos persisten; el caché no se pierde por el Registro.
- `/reload`: el contenido se recarga sin corromper el progreso.
- `/arcanum ledger grant/reset/seed`: funcionan y son idempotentes.

### 7.3 Mundo existente

- Mundo con inventario lleno, cofre del End con objetos y estadísticas antiguas.
- Abrir el Registro: se siembran los hechos demostrables; no se conceden acciones no demostrables.
- Repetir una acción (abrir el display, fabricar un cable): la página correspondiente se completa.
- Verificar que no se conceden `craft:` por tener el objeto, ni `place:` por tener el bloque.

### 7.4 Interfaz

- Escalas 1, 2, 3 y 4; resolución mínima 854×480 y 4K.
- Textos largos, entradas sin texto y claves faltantes (deben mostrar una clave visible, no crashear).
- Navegación con ratón, rueda y teclado.
- Avisos agrupados y enlace `[Abrir]`.
- El libro no se abre solo durante combate o construcción.

### 7.5 Rendimiento

- 10 jugadores, 40 hechos y 100 entradas: coste de tick despreciable.
- Apertura del libro por debajo de 50 ms en un servidor normal.
- Sin fugas de mapas por jugador al desconectar.

---

## 8. Hitos y versiones

| Versión | Nombre | Alcance |
|---|---|---|
| **0.3.0** | Conocimiento | Hechos técnicos, siembra, comandos, `field_ledger`, una entrada de prueba (`archive_cable`) y pantalla mínima. |
| **0.4.0** | Manual | Interfaz de dos páginas, capítulos 1–5 y 7–10, iconos, avisos y traducciones. |
| **0.5.0** | Logros y Anales | Árbol visible, logro de cierre, Anales por hitos y documentos opcionales de loot. |
| **1.0.0** | Registro completo | Contenido congelado, paridad de idiomas, validación en servidor dedicado, mundo viejo y JAR limpio. El libro es el marcador. |

El diezmo no entra en 1.0 salvo que se implemente antes. Si se implementa, sería una versión 0.6/1.1 y añadiría el capítulo 6 del Manual.

### Criterios de salida de 1.0.0

- Todas las entradas del Manual de las funciones implementadas están escritas en inglés y español.
- La ruta principal se completa en supervivencia normal sin loot, estructuras ni RNG.
- Un mundo de 500 días abre el Registro y recupera lo que puede demostrar; el resto se completa al repetir acciones.
- Los logros visibles no bloquean nada y los opcionales no son obligatorios.
- Los Anales se desbloquean por hitos; el loot solo añade variantes.
- Interfaz usable a todas las escalas y sin desbordes.
- Paridad de claves en `en_us`, `es_es` y `es_mx`.
- Servidor dedicado: dos jugadores, reinicio, reconexión, `/reload` y persistencia.
- `clean build` y JAR validado en una instancia limpia.
- README, CHANGELOG, TODO y este plan actualizados.

---

## 9. Riesgos y alternativas

| Riesgo | Mitigación |
|---|---|
| La API de advancements cambia en una versión futura | Está aislada en `LedgerService` y en los JSON; migrar a un guardado propio sería localizado. |
| Los advancements técnicos son visibles para operadores | No tienen `display`; no aparecen en la pantalla de logros. Son depurables con `/advancement`. |
| Un datapack elimina un hecho | Validación al cargar, aviso en consola y página que queda parcial; la siembra puede volver a concederlo. |
| Falsos positivos al sembrar | Hechos tipados: posesión ≠ fabricación ≠ colocación ≠ uso. |
| Contenido demasiado extenso para 1.0 | Empezar por una entrada (0.3) y crecer por capítulos; el contenido es dato, no código. |
| La rejilla de receta duplica la receta real | Resolver la receta en el servidor si la API lo permite; si no, usar una rejilla declarativa validada contra la receta. |
| El jugador espera que el libro se abra solo | No hacerlo; avisar y dejar que lo abra. Evita interrupciones en combate. |
| Mundos viejos con expectativas irreales | Nota de calibración honesta y páginas que se completan al repetir la acción. |

Alternativa de persistencia: si los advancements técnicos resultan frágiles, usar un `PersistentState` por mundo o un attachment de Fabric con codec. La interfaz de `LedgerService` permite cambiar el almacén sin tocar el contenido ni la pantalla.

---

## 10. Decisiones abiertas

1. **Almacén:** advancements técnicos (recomendado) frente a guardado propio.
2. **Receta del libro:** siempre crafteable (recomendado) frente a desbloqueo estricto por logro.
3. **Diezmo:** dejarlo fuera de 1.0 y mencionarlo solo en Anales (recomendado) frente a implementarlo antes.
4. **Anales:** hitos + loot opcional (recomendado) frente a solo hitos o solo loot.
5. **Estados:** tres para el Manual principal y dos para solución de problemas (recomendado).
6. **Rejillas de receta:** incluirlas en 1.0 si la API de recetas lo permite; si no, texto + iconos.
7. **Nombre:** “Registro de Campo del Archivista” (recomendado) frente a “Libro de Campo del Archivista”.
8. **Avisos:** chat con enlace (recomendado) + toast opcional.
9. **Libro compartido:** el objeto no guarda conocimiento; cada lector ve el suyo (recomendado).
10. **Cierre:** “El Registro completo” como logro final y desbloqueo de “El último registro” (recomendado).

---

## 11. Mapa de archivos previsto

```
src/main/java/com/bitqba/
  item/FieldLedgerItem.java
  ledger/LedgerFact.java
  ledger/LedgerEntry.java
  ledger/LedgerChapter.java
  ledger/LedgerState.java
  ledger/LedgerContent.java
  ledger/LedgerService.java
  ledger/LedgerAchievements.java
  command/LedgerCommand.java
  network/OpenLedgerS2CPayload.java
  network/LedgerNoteS2CPayload.java

src/client/java/com/bitqba/client/
  LedgerScreen.java
  LedgerToast.java

src/main/resources/
  assets/arcanum_storage/lang/{en_us,es_es,es_mx}.json
  assets/arcanum_storage/textures/gui/ledger.png
  assets/arcanum_storage/textures/item/field_ledger.png
  data/arcanum_storage/ledger/manual.json
  data/arcanum_storage/ledger/achievements.json
  data/arcanum_storage/advancement/ledger/root.json
  data/arcanum_storage/advancement/ledger/... (hechos)
  data/arcanum_storage/advancement/main/... (logros visibles)
  data/arcanum_storage/recipe/field_ledger.json
```

---

## 12. Primer paso recomendado

Implementar **0.3.0** con una sola entrada (`archive_cable`) de principio a fin:

1. `field_ledger` con receta, textura y uso.
2. Hechos `item:minecraft:copper_ingot`, `item:minecraft:redstone`, `craft:arcanum_storage:archive_cable`, `place:arcanum_storage:archive_cable`.
3. Siembra de inventario y estadísticas.
4. `manual.json` con el capítulo “Construir el archivo”.
5. Pantalla mínima que muestre los tres estados y los hechos faltantes.
6. Comandos y arnés de servidor dedicado.

Cuando esa tubería funcione, añadir capítulos es contenido, no arquitectura.
