# Logros y descubrimiento progresivo

Propuesta inicial para **Arcanum Storage**. Este documento es de diseño: no añade ni cambia código, recetas ni recursos del juego.

## Propósito

Los logros deben animar al jugador a explorar las herramientas del Gremio de los Cartógrafos y ayudarle a entenderlas. No deberían convertirse en una lista de tareas repetitivas ni bloquear las funciones principales detrás de requisitos arbitrarios.

La progresión tiene dos partes relacionadas:

- **Logros de Minecraft:** celebran hitos concretos y apuntan al siguiente descubrimiento.
- **Registro de Campo del Archivista:** conserva páginas del Manual a medida que el jugador conoce materiales y usa dispositivos. Algunas páginas empiezan como notas incompletas y se aclaran cuando descubre los ingredientes que faltaban.

Los Anales pueden desbloquearse con hitos narrativos. El Manual debe aportar información útil aunque el jugador nunca encuentre documentos de loot.

## Ruta inicial de logros

Los nombres son provisionales. Las descripciones entre comillas son propuestas de texto para el jugador.

| Etapa | Logro | Criterio propuesto | Qué invita a descubrir |
|---|---|---|---|
| Inicio | **Una página en blanco** | Recoger cualquier libro vanilla por primera vez. | Desbloquea la receta del Registro de Campo del Archivista, según la propuesta de `LORE.md`. |
| Inicio | **Archivista de campo** | Fabricar el Registro de Campo. | Presenta el Manual y los Anales, y explica que el conocimiento se conserva. |
| Materiales | **Metal que conduce** | Obtener un lingote de cobre. | Revela la primera nota parcial sobre el Cable de Archivo. |
| Materiales | **Energía para la señal** | Obtener polvo de redstone. | Completa la explicación del cable junto con el descubrimiento del cobre. |
| Red | **El primer relé** | Fabricar un Cable de Archivo. | Anima a probar un medio de conexión entre bloques. |
| Red | **Un nodo para el archivo** | Fabricar y colocar un Storage Connector. | Presenta el conector como raíz física de la red. |
| Red | **Consulta de campo** | Colocar un Storage Display conectado y abrirlo por primera vez. | Enseña búsqueda, páginas, procedencia y extracción. |
| Red | **Suministros localizados** | Conectar el primer cofre y encontrar un objeto en el catálogo. | Explica que la red consulta cofres reales y que para guardar se usa el cofre elegido directamente. |
| Estuches | **Estuche de campaña** | Obtener una caja de shulker. | Introduce los contenedores portátiles y su valor como almacenamiento de expedición. |
| Estuches | **Lectura del estuche** | Encontrar en el catálogo un objeto guardado dentro de una shulker conectada. | Revela cómo consultar contenido anidado y los límites de profundidad. |
| Prensa | **Una caja con nombre tiene dueño** | Fabricar y colocar un Archive Connector junto a una red válida. | Presenta la Shulker Press y su regla de respetar etiquetas personalizadas. |
| Prensa | **Antes de mover, revisar** | Generar una vista previa de organización en la Shulker Press. | Enseña que el análisis no altera objetos y que la operación requiere confirmación. |
| Prensa | **Suministros reorganizados** | Confirmar una organización válida con la Shulker Press. | Celebra la consolidación sin premiar la cantidad de veces que se repite. |
| End | **Una puerta portátil** | Fabricar un End Pouch. | Revela la tecnología de reservas personales del gremio. |
| Vínculo | **Protocolo de emergencia** | Obtener un libro con Arcanum Binding. | Presenta el protocolo de recuperación y explica su coste y limitaciones antes de usarlo. |
| Vínculo | **El equipo queda a salvo** | Recuperar un Archive Cache generado por Arcanum Binding. | Completa el relato de la tecnología de emergencia. Este logro es opcional y no debe ser requisito para fabricar o usar los demás dispositivos. |

### Reglas para los logros

- Evitar objetivos de cantidad como “conecta 100 cables” o “extrae 10 000 objetos”. El gremio premia descubrimientos y uso informado.
- No exigir morir, perder objetos, encontrar una estructura rara ni depender de una oferta aleatoria para acceder a la información esencial.
- Los pasos principales deben poder completarse en supervivencia normal y en mundos ya avanzados.
- Los hitos que describen funciones futuras solo se añaden cuando esas funciones estén implementadas.
- Los logros opcionales de exploración pueden aportar Anales, pero no deben ocultar instrucciones necesarias del Manual.

## Páginas progresivas del Registro de Campo

Cada tema del Manual puede tener estados sencillos:

1. **Sin identificar:** aparece el título del tema y una frase que indique que faltan referencias.
2. **Nota parcial:** el jugador ha descubierto uno o más materiales relevantes; se muestra lo que ya puede deducirse y se nombra la pista que falta.
3. **Registro completo:** el jugador ha descubierto los componentes necesarios; se revela la explicación práctica completa.

El conocimiento se guarda para siempre en los datos del jugador. Consumir, guardar o perder un ingrediente no vuelve a ocultar una página.

### Ejemplo: Cable de Archivo

La página puede tener estos requisitos de conocimiento:

- Cobre descubierto: estado parcial.
- Polvo de redstone descubierto: se completa la nota del material conductor y el componente energético.
- Cable de Archivo fabricado: se registra el procedimiento práctico y su función en la red.

**Con cobre, sin redstone:**

> El cobre parece adecuado para conducir las señales del archivo. Falta identificar una fuente de energía capaz de activarlas.

**Después de descubrir redstone:**

> El cobre transmite la señal y el polvo de redstone aporta la energía necesaria. El Gremio combinaba ambos para fabricar cables de archivo.

**Después de fabricar el cable:**

> Cable de Archivo: dos lingotes de cobre y un polvo de redstone producen cuatro cables. El cable transmite la conexión lógica; no transporta objetos ni actúa como cable de redstone convencional.

La página no tiene que mostrar una receta exacta en el primer estado. Puede revelar primero la hipótesis, luego la función y finalmente el procedimiento.

### Otros ejemplos de progresión

| Tema | Primera pista | Descubrimientos que completan el registro |
|---|---|---|
| Storage Connector y Display | Redstone: referencias a relés, lectura y control de señales. | Componentes de redstone pertinentes; después, fabricar/usar los bloques para desbloquear instrucciones de conexión. |
| Archive Connector y Shulker Press | Cobre o redstone: nota sobre un mecanismo de inspección y presión. | Shulker shell y pistón: explicación de la prensa; fabricarla y analizar una red válida revela el procedimiento. |
| End Pouch | Ender Chest: nota sobre reservas personales enlazadas. | Chorus fruit, shulker shell y demás componentes de la receta: explicación completa; fabricarlo revela las instrucciones de uso. |
| Arcanum Binding | End Pouch: referencia a un protocolo de emergencia sin detallar. | Encontrar/obtener el libro encantado revela el método; usarlo o recuperar el caché completa los Anales relacionados. |

Las páginas deben basarse en ingredientes reales de cada receta y en las reglas actuales que define el README. Si una receta cambia, sus requisitos de conocimiento deben revisarse con ella.

## Cómo recordar objetos descubiertos

El Registro no debería decidir el conocimiento mirando solo el inventario actual. El jugador puede haber consumido, guardado o perdido un material y aun así haberlo descubierto.

### Propuesta sencilla

- Mantener en datos persistentes del jugador un conjunto de identificadores de objetos ya descubiertos, por ejemplo `minecraft:copper_ingot` y `minecraft:redstone`.
- Registrar un objeto la primera vez que el servidor confirma que el jugador lo obtuvo. Entre las fuentes previstas están recogerlo, fabricarlo, recibirlo de una entidad o comercio, y recibirlo como botín o resultado de procesamiento.
- Guardar cada identificador una sola vez. La cantidad no importa para el conocimiento básico.
- Cuando cambia el conjunto, recalcular únicamente las páginas relacionadas y notificar al jugador de los nuevos descubrimientos.
- Al abrir el Registro, leer el conjunto persistente y mostrar el estado actual de cada página. No volver a comprobar si el objeto sigue en el inventario.
- Mantener el registro en el servidor para que sobreviva a cierres de sesión y sea coherente en multijugador.

No hace falta registrar cada unidad ni cada movimiento entre cofres. Basta con guardar “este jugador ya obtuvo al menos uno de este tipo”. Los objetos que solo estén guardados en un cofre ajeno y nunca hayan pasado por posesión del jugador no cuentan como descubiertos.

### Mundos existentes y primer registro

En mundos donde el mod ya estaba instalado antes de añadir el Registro, se puede sembrar el conocimiento al obtener el libro: reconocer objetos del inventario actual y, cuando sea sencillo y fiable, logros vanilla ya completados que demuestren descubrimientos previos. A partir de ese momento el registro persistente conserva nuevas adquisiciones.

No es posible reconstruir con certeza todos los objetos que un jugador tuvo en el pasado si ya los consumió y el mundo nunca guardó ese historial. En ese caso, el sistema debe presentarlo como conocimiento registrado desde que la función existe, sin fingir que conoce toda la partida anterior. Esta limitación no debe bloquear páginas: los ingredientes se pueden volver a obtener.

## Presentación y tono

- Los logros usan el tono sobrio del Gremio, con humor breve en algunas notas, siguiendo `LORE.md`.
- Un aviso discreto puede decir “Nueva nota en el Registro de Campo” y abrir el libro al hacer clic; no abrir la interfaz automáticamente durante combate o construcción.
- La página debe explicar qué se descubrió y qué pista falta, sin mensajes genéricos como “progreso actualizado”.
- El Manual contiene conocimiento práctico; los Anales aportan contexto histórico. Mantener esa separación.
- Si el jugador ya cumplió los requisitos antes de fabricar el libro, sus páginas deben aparecer recuperadas al abrirlo por primera vez.

## Orden recomendado de trabajo

1. Cerrar y aprobar el conjunto pequeño de logros de la ruta principal.
2. Definir los requisitos de descubrimiento por página a partir de las recetas vigentes.
3. Diseñar el registro persistente de objetos descubiertos y su comportamiento en mundos existentes.
4. Escribir y revisar los textos parciales/completos en español e inglés.
5. Implementar primero el registro y una página de prueba (Cable de Archivo); después añadir el resto gradualmente.
6. Incorporar logros de Minecraft solo cuando los criterios y páginas correspondientes estén estables.

## Decisiones pendientes

- Nombre definitivo en español del libro: “Registro de Campo del Archivista” o “Libro de Campo del Archivista”.
- Si la página completa de una receta se revela al conocer todos sus ingredientes o solo tras fabricar el objeto. Propuesta: revelar la receta al conocer ingredientes y reservar instrucciones/nota de uso para después de fabricarlo.
- Qué señales vanilla existentes se pueden usar para recuperar conocimiento en mundos antiguos sin dar falsos positivos.
- Si los Anales se desbloquean por logros, documentos opcionales o ambos.
