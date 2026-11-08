# Plan de fases 2 y 3 — Conector de Archivo, prensa y red

Documento de planificación. Estado: **fases 2 y 3 implementadas en 0.2.1**, pendientes de prueba en mundo.

## Nomenclatura propuesta

- **Conector de Archivo** — bloque nuevo de la fase 2. Se coloca junto a un cofre y lo enlaza sin repetidor. Tiene la **Prensa de Shulkers** en su interfaz.
- **Nexo de Archivo** — papel que cumple el conector que lleva la pantalla encima. No hace falta un bloque distinto: el conector con pantalla es el nexo de la red.
- **Prensa de Shulkers** — función de organización, no un bloque separado. Así el bloque creado en fase 2 se reutiliza tal cual en fase 3.

## Fase 2 — Conector de Archivo y Prensa de Shulkers

### Objetivo

Sustituir el repetidor por un conector propio y añadir una prensa que organice las shulkers de un cofre, liberando las cajas vacías.

### Bloque y enlace

- El Conector de Archivo se coloca **pegado a un cofre**. Su cara activa apunta al cofre; no hay repetidor ni señal de redstone.
- Puede enlazarse a cofres simples o dobles. Un doble cuenta como un solo cofre.
- La pantalla puede colocarse encima del conector para consultar el catálogo de ese cofre.
- Sin cofre detectado, el conector se ve inactivo y muestra un aviso al abrirlo.
- En fase 2 solo enlaza **un cofre**. La red de varios cofres llega en fase 3.

### Interfaz y flujo

La prensa se abre con clic derecho en el conector.

1. **Estado:** cofre conectado, shulkers elegibles, shulkers con etiqueta ignoradas y estimación de cajas que quedarán vacías.
2. **Analizar:** genera una vista previa sin mover nada.
3. **Organizar:** solo se habilita tras analizar y muestra una confirmación.
4. **Resultado:** "3 shulkers vaciadas, 12 stacks movidos". Las cajas vacías siguen en el cofre.

La vista previa debe mostrar, como mínimo:

- Grupos de objetos ordenados de mayor a menor cantidad total.
- Cuántos stacks hay de cada grupo.
- Qué shulkers se llenarán y cuáles quedarán vacías.
- Shulkers ignoradas por tener etiqueta.

### Regla de "sin etiqueta"

Se considera etiqueta cualquier **nombre personalizado** o **lore personalizado** en la shulker. El color no importa.

- Shulker de cualquier color sin nombre/lore: elegible.
- Shulker con nombre o lore: se ignora por completo, incluido su contenido.
- Shulker con otra shulker dentro: la caja interior se trata como un objeto opaco; la prensa no la abre.

### Algoritmo propuesto

1. Leer el cofre completo y localizar las shulkers elegibles.
2. Extraer una copia de todos los stacks de su interior.
3. Agrupar por **objeto + componentes** (mismo criterio que el merging vanilla). Un objeto con otro nombre o encantamiento es otro grupo.
4. Ordenar los grupos por cantidad total descendente; desempate estable por ID de objeto.
5. Calcular el nuevo reparto:
   - Combinar primero los stacks parciales de cada grupo.
   - Colocar los grupos en orden, llenando las shulkers en el orden del cofre.
   - Un grupo puede ocupar varias shulkers si no cabe en una.
   - Se permite mezclar grupos en los huecos sobrantes para maximizar la densidad y liberar más cajas.
6. Validar que el total por grupo coincide exactamente con el original.
7. Aplicar el plan de forma atómica: limpiar solo las shulkers elegibles y escribir el nuevo contenido.
8. Si algo cambió entre el análisis y la aplicación, abortar sin mover nada.

### Seguridad

- Nunca se tocan cofres, objetos sueltos ni shulkers con etiqueta.
- No se crean, duplican ni destruyen objetos: el plan conserva cada stack y sus componentes.
- Antes de aplicar se revalida el cofre y las shulkers de origen.
- Si falla la escritura, restaurar el contenido original desde la copia de seguridad.
- La operación se ejecuta en el servidor; el cliente solo muestra la vista previa.

### Casos límite

- Cofre vacío o sin shulkers: mensaje claro.
- Todas las shulkers ya están compactadas: "No hay nada que organizar".
- Shulkers llenas: no se puede liberar ninguna; se informa.
- Objetos únicos o con componentes distintos: no se fusionan.
- Cofre doble: se trata como un inventario único.
- Shulker rota o movida durante la operación: abortar.

### Receta propuesta

```
C R C
R S R
C P C
```

- `C` = lingote de cobre
- `R` = polvo de redstone
- `S` = caparazón de shulker
- `P` = pistón

El caparazón sitúa el bloque en el juego medio-tardío y el pistón explica la prensa. Los números exactos se ajustan al probar.

### Compatibilidad

- El conector viejo con repetidores existió durante el desarrollo de la fase 2 como "relé provisional".
- La prensa solo funciona con el Conector de Archivo nuevo.
- El libro explica la diferencia y recomienda migrar.
- En fase 3 se retira la receta vieja y se ofrece una receta de actualización.

### Pruebas de la fase 2

- Un cofre simple y uno doble, con shulkers de varios colores.
- Shulkers con nombre/lore: no se tocan.
- Objetos con encantamientos/nombres distintos: no se fusionan.
- Shulkers llenas, parciales y vacías.
- Vista previa coincide con el resultado real.
- Cancelar/abortar no mueve nada.
- Cerrar y reabrir el mundo conserva el resultado.
- Rendimiento con 54 shulkers llenas en un cofre doble.

## Fase 3 — red de cables (versión 0.2.1, implementada)

### Objetivo

Sustituir los repetidores por un cable propio y permitir más de cuatro cofres por red, con un tope de **20 cofres** y sin economía todavía.

### Modelo de red

- **Raíz:** el **Storage Connector** actual, con el **Storage Display** siempre en su cara superior.
- **Ramas:** las cinco caras restantes (norte, sur, este, oeste y abajo).
- **Cable de Archivo (`ARCHIVE_CABLE`):** único medio de conexión. Se conecta a otros cables, al Storage Connector y a cofres; no transporta objetos ni redstone.
- Un cofre entra en la red si **toca cualquier cable** alcanzable desde la raíz. Un cofre doble cuenta como uno solo.
- El grafo se recorre una vez por cada cara raíz. Cada cara reclama hasta **4 cofres no vistos**, y la red entera se corta en **20**.
- Se permiten bucles y anillos: cada cara puede reclamar cofres distintos aunque el anillo sea compartido.
- La cantidad de cables es ilimitada; el límite es solo de cofres.
- Tope de listado: 3000 fichas, ya existente.
- El diezmo/economía de enlace **no se implementa en 0.2.1**; queda para una fase posterior.

### Bloque de cable

- Estado de conexión en las 6 direcciones (`north`, `south`, `east`, `west`, `up`, `down`).
- Blockstate multipart con núcleo y brazos; contorno y colisión finos.
- Se rompe a mano y siempre se recupera; no exige pico.
- Receta: dos lingotes de cobre y un polvo de redstone → 4 cables.
- Traducciones: “Archive Cable” / “Cable de Archivo”.

### Repetidores

- A partir de 0.2.1 los repetidores **dejan de detectarse**; el cable es el único medio.
- El Storage Connector ya no emite redstone ni altera circuitos vanilla.
- Las redes antiguas de repetidores deben reconstruirse con cables.

### Diezmo de red (futuro, no implementado)

Se pagará una vez por cofre válido al inscribirlo, desde el inventario del jugador. Los cuatro primeros serían gratis y a partir del quinto se aplicaría una curva. La tabla Fibonacci siguiente se conserva solo como referencia de diseño para cuando se retome:

| Cofre | Coste (bloques de redstone) |
|---|---|
| 5 | 1 |
| 6 | 1 |
| 7 | 2 |
| 8 | 3 |
| 9 | 5 |
| 10 | 8 |
| 11 | 13 |
| 12 | 21 |
| 13 | 34 |
| 14 | 55 |
| 15 | 89 |
| 16 | 144 |
| 17 | 233 |
| 18 | 377 |
| 19 | 610 |
| 20 | 987 |

No cobrar automáticamente al colocar el bloque: evita cargos accidentales. El cobro, si llega, se hará desde la interfaz mostrando el coste del siguiente cofre.

### Rendimiento

- El límite de 20 cofres y el recorrido por cada cara acotan el trabajo por búsqueda.
- Mantener el tope de 3000 fichas y avisar del truncado.
- Si 20 cofres llenos de shulkers resultan lentos, añadir un índice/caché por red antes de subir el límite.
- Medir con redes de 20 cofres antes de plantear más capacidad.

### Interfaz de red

- La pantalla muestra el número de cofres enlazados.
- Las etiquetas de origen incluyen las cinco caras, con “Below/Abajo” para la rama inferior.
- Pendiente: mostrar `conexiones / 20` y el estado de cada rama.

### Migración

- Los mundos de 0.1.1 conservan sus cofres y repetidores, pero los repetidores ya no enlazan.
- Para recuperar una red antigua: retirar los repetidores y tender cables desde las caras del Storage Connector hasta los cofres.
- Los repetidores se pueden romper y reutilizar; no se pierden.

### Pruebas de la fase 3

- Red de 4 cofres con cables, sin repetidores.
- Ramas en las cinco caras: 4 cofres por lateral y 4 por abajo.
- Cables en anillo: cada cara reclama cofres distintos sin duplicarlos.
- Cofre doble contado una sola vez.
- Cofre que toca un cable desconectado: no entra en la red.
- Romper un cable intermedio: los cofres de esa rama salen de la red.
- Tope de 4 por rama y 20 globales.
- Cofre roto con la pantalla abierta.
- Rendimiento con 20 cofres llenos.

## Preguntas para decidir

1. ¿Nombre del bloque: **Conector de Archivo** (recomendado) o Nexo de Archivo?
2. ¿La prensa solo toca shulkers, o también objetos sueltos del cofre? (Recomendado: solo shulkers.)
3. ¿Shulkers con lore pero sin nombre se ignoran? (Recomendado: sí, cualquier etiqueta cuenta.)
4. ~~¿Límite de 16 o 20 cofres?~~ Decidido: **20 cofres** (4 por lateral + 4 abajo) en 0.1.2.
5. ~~¿Diezmo en bloques de redstone puros o mezcla con oro/cobre?~~ Aplazado: la economía no entra en 0.1.2; la tabla Fibonacci queda como referencia.
6. ¿Vincular nodo desde la interfaz del nexo o con clic derecho + materiales? (Recomendado: interfaz, para mostrar el coste.)
7. ¿La prensa de fase 3 organiza toda la red o sigue siendo por cofre? (Recomendado: por cofre en 1.0; red completa más adelante con vista previa.)
