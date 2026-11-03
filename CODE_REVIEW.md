# Revisión de código para 0.1

Revisión centrada en el alcance del primer release: un extractor local, seguro y sin rutas de depósito.

## Hallazgos corregidos

1. **Depósitos desde el catálogo.** `StorageScreenHandler` aceptaba clic con cursor y shift-clic desde el inventario, y `StorageNetwork.insert` escribía en la red. Se eliminaron el método de inserción y todas las ramas de depósito. Las ranuras del catálogo tienen `canInsert = false` y el shift-clic solo mueve del catálogo al inventario del jugador.
2. **Integridad de shulkers.** La extracción ya revalidaba la ruta, pero el contenedor vacío conservaba un componente de contenedor con stacks vacíos. Ahora se normaliza a `DEFAULT` y se elimina el componente. Extraer la shulker completa conserva su contenido; extraer el último objeto deja la caja vacía en su sitio.
3. **Referencias viejas.** Cada búsqueda, cambio de página y extracción comprueba conector y pantalla con `StorageNetwork.isOperational`, vuelve a resolver el cofre por posición y valida slot, stack raíz y ruta antes de escribir. Si algo cambió, no se mueve nada, se avisa y se refresca la lista. Vanilla además cierra la pantalla cuando `canUse` falla.
4. **Redstone.** Se retiraron `RepeaterConnectionMixin` y `StorageInteractionHandlers`, que forzaban encendido y delay 1. Los repeaters vuelven a comportarse como vanilla y solo se usan como marcadores de dirección. El conector mantiene su textura activa y su luz cuando la pantalla está montada, y la interfaz muestra el contador de conexiones.
5. **Profundidad y estados vacíos.** El catálogo explora hasta dos niveles de contenido anidado, avisa cuando oculta contenido más profundo y muestra estados para “sin conexiones”, “sin resultados”, “cofres vacíos” e “inventario lleno”.
6. **Interfaz.** Se corrigieron solapamientos entre el contador, la paginación y el título del inventario; se añadió el título, una línea de “solo extracción” y se ajustó la altura para que quepa en una escala de GUI 4 a 1080p.
7. **Seguridad de red.** El paquete de búsqueda limita la cadena a 64 caracteres y el servidor vuelve a comprobar `canUse` antes de aplicar la consulta.

## Comprobaciones realizadas

- `gradlew clean build` finaliza correctamente.
- El JAR contiene `fabric.mod.json`, las dos recetas, loot tables, modelos, blockstates, textos en inglés/español, texturas y las clases de entrada.
- El JAR ya no contiene mixins ni las clases de redstone retiradas.
- No quedan llamadas a `StorageNetwork.insert` ni escrituras de objetos hacia cofres o shulkers; la única escritura en almacenamiento es la extracción en `StorageNetwork.extract`.

## Pendiente de prueba manual

- Fabricar y colocar ambos bloques, conectar los cuatro lados y probar cofres simples y dobles en varias orientaciones.
- Buscar, paginar, extraer stack/una unidad y shift-clic; confirmar que ningún intento de depósito modifica la red.
- Probar shulkers llenas, parciales y vacías; extraer la shulker completa y su último contenido.
- Desconectar un cofre y retirar la pantalla con la interfaz abierta.
- Cerrar y reabrir el mundo para comprobar persistencia.
- Probar el JAR en una instancia limpia de Minecraft 1.21.9 con Fabric.
