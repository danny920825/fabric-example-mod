# Revisión de código para 0.1

Revisión centrada en el alcance del primer release: un extractor local, seguro y sin rutas de depósito.

## Hallazgos corregidos

1. **Depósitos desde el catálogo.** `StorageScreenHandler` aceptaba clic con cursor y shift-clic desde el inventario, y `StorageNetwork.insert` escribía en la red. Se eliminaron el método de inserción y todas las ramas de depósito. Las ranuras del catálogo tienen `canInsert = false` y el shift-clic solo mueve del catálogo al inventario del jugador.
2. **Integridad de shulkers.** La extracción ya revalidaba la ruta, pero el contenedor vacío conservaba un componente de contenedor con stacks vacíos. Ahora se normaliza a `DEFAULT` y se elimina el componente. Extraer la shulker completa conserva su contenido; extraer el último objeto deja la caja vacía en su sitio.
3. **Referencias viejas.** Cada búsqueda, cambio de página y extracción comprueba conector y pantalla con `StorageNetwork.isOperational`, vuelve a resolver el cofre por posición y valida slot, stack raíz y ruta antes de escribir. Si algo cambió, no se mueve nada, se avisa y se refresca la lista. Vanilla además cierra la pantalla cuando `canUse` falla.
4. **Redstone.** Se retiraron `RepeaterConnectionMixin` y `StorageInteractionHandlers`, que forzaban encendido y delay 1. Los repeaters vuelven a comportarse como vanilla y solo se usan como marcadores de dirección. El conector mantiene su textura activa y su luz cuando la pantalla está montada, y la interfaz muestra el contador de conexiones.
5. **Profundidad y estados vacíos.** El catálogo explora hasta dos niveles de contenido anidado, avisa cuando oculta contenido más profundo y muestra estados para “sin conexiones”, “sin resultados”, “cofres vacíos” e “inventario lleno”.
6. **Interfaz.** Se corrigieron solapamientos entre el contador, la paginación y el título del inventario; se añadió el título y una línea de “solo extracción”. En 0.1.1 el panel se redujo a 176×238 con 45 resultados por página para que quepa completo en escalas de GUI habituales.
7. **Seguridad de red.** El paquete de búsqueda limita la cadena a 64 caracteres y el servidor vuelve a comprobar `canUse` antes de aplicar la consulta.

## Revisión 0.1.1

- **Tooltips de objetos.** En 1.21.9 `HandledScreen#render` ya no dibuja el tooltip del slot enfocado. `StorageDisplayScreen#render` llama a `drawMouseoverTooltip` después de `super.render`, igual que las pantallas vanilla de contenedor.
- **Letras de orientación.** Cada cara horizontal del conector usa una textura propia con la letra N/S/E/W y su variante activa con marco dorado. Los modelos mapean norte/sur/este/oeste a la letra correspondiente, de modo que “Origen: Norte” coincide con la cara física.
- **Interfaz compacta.** Catálogo de 9×5 en `y = 33 + 18r`, inventario del jugador en `159/177/195` y hotbar en `217`; el fondo pasó de 266 a 238 px y se retiró la etiqueta “Inventory” redundante.
- **Enlace de redstone.** El conector emite nivel 15 solo hacia lados con repetidor correctamente orientado y solo con pantalla montada; el repetidor se enciende para confirmar el enlace sin cambiar su delay. `FACING` se interpreta como el lado desde el que lee el repetidor, por lo que el origen coincide con el lado real.
- **Límite de listado.** `StorageNetwork.MAX_ENTRIES = 3000` y el indicador `truncated` evitan listados sin fin en redes grandes.
- **Ordenación.** `SortMode` (orden natural, nombre, cantidad) se aplica solo a la vista; no mueve objetos.

## Comprobaciones realizadas

- `gradlew clean build` finaliza correctamente.
- El JAR contiene `fabric.mod.json`, las dos recetas, loot tables, modelos, blockstates, textos en inglés/español, texturas (incluidas las ocho caras con letra del conector) y las clases de entrada.
- El JAR ya no contiene mixins ni las clases de redstone retiradas.
- No quedan llamadas a `StorageNetwork.insert` ni escrituras de objetos hacia cofres o shulkers; la única escritura en almacenamiento es la extracción en `StorageNetwork.extract`.

## Pendiente de prueba manual

- Fabricar y colocar ambos bloques, conectar los cuatro lados y probar cofres simples y dobles en varias orientaciones.
- Buscar, paginar, extraer stack/una unidad y shift-clic; confirmar que ningún intento de depósito modifica la red.
- Probar shulkers llenas, parciales y vacías; extraer la shulker completa y su último contenido.
- Desconectar un cofre y retirar la pantalla con la interfaz abierta.
- Cerrar y reabrir el mundo para comprobar persistencia.
- Probar el JAR en una instancia limpia de Minecraft 1.21.9 con Fabric.
