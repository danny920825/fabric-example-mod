# Plan del mod Storage Display

## Objetivo

Crear un mod para Minecraft Java 1.21.9 con Fabric que permita consultar y administrar objetos guardados en cofres vanilla mediante una pantalla conectada. El sistema debe ser práctico sin sustituir la progresión normal del juego.

## Mecánica

- El sistema tiene dos bloques: un **Conector de almacenamiento** y una **Pantalla de almacenamiento** colocada encima.
- El conector ofrece hasta cuatro conexiones, una por cada lado horizontal. Cada conexión requiere un repeater colocado junto al conector, orientado hacia un cofre normal situado inmediatamente después. El repeater es un marcador direccional: no necesita estar encendido.
- El conector no modifica la señal, el delay ni el estado de los repeaters. Los circuitos de redstone vanilla siguen funcionando igual. El número de cofres válidos se muestra en la interfaz.
- Al colocar la pantalla sobre el conector, este cambia a una textura activa y emite luz; al retirar la pantalla vuelve a su estado normal.
- Cada cofre doble cuenta como una conexión. Ender Chests, cofres trampa y almacenamiento de otros mods quedan fuera de esta primera versión.
- La pantalla presenta los objetos de todos los cofres conectados, permite buscar por nombre o identificador y paginar los resultados. Explora también el contenido de las shulker boxes guardadas dentro de esos cofres, hasta dos niveles de anidamiento.
- El catálogo es **solo de extracción**. El jugador puede retirar objetos con clic izquierdo (stack completo), derecho (una unidad) o shift-clic hacia su inventario. Ninguna acción de la pantalla introduce objetos en cofres o shulkers: para guardar, se abre el cofre elegido.
- Los cambios dentro de una shulker se guardan en su caja original. Extraer el último contenido deja la shulker vacía; extraer la shulker mueve la caja completa con su contenido.

## Fabricación y balance

- **Conector:** cuatro bloques de redstone, cuatro repeaters y un comparator.
- **Pantalla:** cuatro lingotes de oro, cuatro lingotes de hierro, un diamante y dos ojos de Ender. El componente del End hace que la pantalla requiera progreso avanzado.
- Los ingredientes de los JSON usan la sintaxis de Minecraft 1.21.9 (identificadores de item como strings).

## Dirección visual

Las texturas se crean como pixel art de 16×16 con herramientas locales, sin usar generación de imágenes ni necesitar una API key.

- **Conector:** cuerpo rojo inspirado en un bloque de redstone; en cada una de sus cuatro caras laterales, un puerto oscuro en el centro horizontal y cerca de la parte inferior, con borde metálico y contactos rojos para señalar dónde conectar el repeater. La cara superior puede llevar cuatro trazos de circuito que salen del centro hacia los laterales.
- **Pantalla:** carcasa de metal gris oscuro, marco grueso y panel verde azulado. La cara frontal debe sugerir una cuadrícula de objetos y una lupa; los laterales pueden llevar ranuras de ventilación y pequeños detalles rojos o dorados. Evitar el aspecto de un observer.
- Orientar la pantalla hacia el jugador al colocarla para que la cara del panel sea visible. Los modelos, blockstates y modelos de ítem usan las nuevas texturas en las caras correspondientes.

## Verificación

- Ejecutar `gradlew build` y confirmar que el jar contiene las dos recetas, loot tables, modelos, blockstates, localizaciones y PNG.
- Probar `gradlew runClient` y verificar que se abre el cliente de desarrollo.
- En un mundo de prueba, comprobar cada lado, la orientación del repeater, cofres dobles, el límite de cuatro conexiones, búsqueda/paginación, retiros y ausencia total de depósitos desde el catálogo.
- Confirmar que no se duplican ni pierden objetos al actualizar o cerrar la pantalla, incluidas shulkers llenas, parciales y vacías.
- Comprobar que ya no se fuerza el encendido ni el delay de los repeaters y que los circuitos contiguos se comportan como en vanilla.

## Estado

La mecánica, los bloques, la interfaz, las recetas, las texturas PNG personalizadas y la orientación de la pantalla están implementadas. El catálogo es solo de extracción: no acepta cursor, shift-clic desde el inventario ni ninguna otra ruta de depósito. La extracción vuelve a validar conector, pantalla, cofre, slot y ruta de shulker en el servidor; la profundidad de contenedores está limitada a dos niveles y la pantalla avisa cuando oculta contenido. Extraer el último objeto de una shulker deja la caja vacía y extraer la shulker mueve la caja completa.

El forzado de encendido y delay de los repeaters se retiró junto con su mixin: los repeaters se usan solo como marcadores de dirección y la redstone vanilla no se altera. El conector mantiene su textura activa y su luz cuando la pantalla está montada, y la interfaz muestra el contador de conexiones y estados vacíos.

`clean build` compila correctamente y el JAR contiene recursos, recetas, loot tables, localizaciones y las dos clases de entrada. Queda pendiente la prueba manual completa en un mundo nuevo, la persistencia al cerrar y reabrir, y la prueba del JAR en una instancia limpia.
