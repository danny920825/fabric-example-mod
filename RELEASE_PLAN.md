# Storage Display: mejoras para el primer JAR

Revisión de producto y código: 2 de noviembre de 2026. Enfoque: primer lanzamiento pequeño, usable y seguro para los objetos. No hace falta que incluya todas las ideas futuras.

## Veredicto

El concepto funciona y tiene identidad: una terminal física conecta cofres vanilla y permite consultar también las shulkers guardadas en ellos. El primer JAR será un **extractor solamente**: permite buscar y sacar objetos, pero no depositarlos. Para guardar objetos, el jugador abre el cofre correspondiente y los coloca allí. Así cada depósito tiene un destino claro y no se necesita decidir desde la interfaz qué cofre o shulker debe recibirlo.

El primer JAR puede ser sencillo visualmente; lo que no debe hacer es borrar, duplicar o consumir objetos de forma inesperada. Conviene reducir el alcance inicial a una red local pequeña y documentar claramente qué almacenamiento admite.

## Estado de la implementación (4 de noviembre de 2026)

- **Integridad de shulkers:** revisada en código. La extracción de la shulker completa conserva su contenido, extraer el último objeto deja la caja vacía en su sitio y el componente de contenedor se normaliza al quedar vacío. Falta la prueba manual.
- **Sin inserción:** eliminadas las rutas de depósito con cursor y shift-clic. El shift-clic desde el catálogo solo mueve objetos al inventario del jugador. Las ranuras del catálogo no aceptan inserciones en ninguna página.
- **Cambios externos:** cada búsqueda, cambio de página y extracción vuelve a validar conector, pantalla, cofre, slot y ruta de shulker en el servidor. Si la referencia ya no es válida, no se mueve nada y la lista se actualiza.
- **Redstone:** se eliminó el mixin que forzaba encendido y delay 1. El conector emite una señal de nivel 15 solo hacia los lados con repetidor correctamente orientado y solo mientras tiene pantalla; el repetidor se enciende para confirmar el enlace sin cambiar su delay ni su comportamiento vanilla. La interfaz muestra el contador de conexiones.
- **Profundidad:** el catálogo limita los contenedores anidados a dos niveles y muestra un aviso cuando oculta contenido más profundo.
- **Interfaz 0.1.1:** panel compacto de 176×238 con 45 resultados por página, tooltips de objetos, ordenación por orden/nombre/cantidad, navegación con botones, rueda y teclado, origen por lado al pasar el ratón y letras N/S/E/W en las caras del conector.
- **Pruebas pendientes:** recorrido completo en mundo nuevo, cofres simples/dobles en varias orientaciones, shulkers llenas/parciales/vacías, desconexión, rotura/recuperación, persistencia al reabrir y JAR en instancia limpia.

## Bloqueadores antes de publicar el primer JAR

1. **Garantizar la integridad de objetos en shulkers.** Revisar extracción de la propia shulker y de su último objeto. La caja con contenido nunca debe desaparecer ni convertirse en una caja vacía por accidente. Para la primera versión se puede prohibir extraer una shulker que contenga objetos, siempre que la interfaz lo explique.
2. **Eliminar toda inserción desde el catálogo.** La GUI no acepta depósitos con cursor ni shift-clic desde el inventario del jugador. Para guardar, se usa el cofre vanilla concreto. Esto debe ser consistente en todas las ranuras y páginas.
3. **Definir comportamiento ante cambios externos.** Antes de cada extracción, volver a localizar la conexión y validar el objetivo real; mostrar el resultado actualizado. Si se desconecta un cofre o se retira la pantalla, la interfaz no debe operar sobre una referencia vieja.
4. **Probar el recorrido básico en un mundo nuevo.** Fabricación, colocación, conexión, apertura, búsqueda, paginación, retiro, ausencia de inserción, rotura y recuperación de ambos bloques. Usar cofres simples y dobles en varias orientaciones. Probar también guardar, salir y volver.
5. **Confirmar el comportamiento de redstone en circuitos contiguos.** El conector solo alimenta el repetidor enlazado y solo con pantalla montada. El repetidor conserva su delay, pero al encenderse emite poder fuerte hacia el cofre, que no conduce; conviene probar que no altera circuitos vecinos y documentarlo en el README.

## Alcance recomendado para 0.1.1

- Solo cofres vanilla normales y sus shulkers anidadas; sin ender chests ni inventarios de otros mods.
- Conector físico de cuatro lados, con cada cofre justo detrás del repetidor correspondiente.
- Búsqueda por nombre visible y por identificador, paginación de 45 resultados y extracción de objetos (clic izquierdo para stack, derecho para una unidad, shift-clic desde el catálogo al inventario del jugador).
- Solo extracción: ninguna acción de la pantalla introduce objetos en cofres o shulkers. Los depósitos se hacen directamente en el contenedor elegido por el jugador.
- Capar la profundidad de shulkers y mostrar un aviso comprensible cuando un elemento no se puede operar.
- Mantener la consulta cerca del bloque y autoritativa en servidor.
- Interfaz compacta y legible: título, búsqueda, ordenación, contador de página/conexiones, tooltips, origen por lado y estados vacíos. Las caras del conector muestran N/S/E/W.
- Recetas y descripción que expliquen el coste y el alcance. No añadir todavía alcance remoto, canales, filtros avanzados o compatibilidad de terceros.

## Prioridad después del primer JAR

### Prioridad alta

- Índice/caché incremental si el escaneo por tecla se nota lento con redes grandes.
- Avisos de conexión: “sin cofres”, “cofre desconectado”, “sin resultados” y límites de anidamiento.
- Soporte para más contenedores vanilla y reglas explícitas de anidamiento/recursión.
- Pruebas automatizadas de la lógica de rutas y extracción, separadas de la interfaz Minecraft.

### Prioridad media

- Organizador de red, después del primer release: botón explícito para consolidar stacks equivalentes entre cofres y shulkers conectados.
- Ordenar resultados por nombre o cantidad, mostrar cantidad total agregada y ubicación del objeto.
- Filtros por categoría y etiquetas, botón para limpiar búsqueda, navegación de páginas con teclado.
- Ajustes para número máximo de conexiones y profundidad de búsqueda.
- Configuración de servidor para desactivar inserción/retiro en multijugador si el administrador desea modo consulta.
- Mejoras visuales: iconos, estados activos y animación sutil; conservar contraste y legibilidad.

### Posibles expansiones, sin comprometer el primer lanzamiento

- Integración opcional con inventarios de otros mods mediante compatibilidad por API, nunca como requisito.
- Lore en libros, avances, sonidos y nombres; canales o terminales temáticos solo si aportan una mecánica clara.
- Alcance mayor o red remota como mejora costosa, porque altera balance y rendimiento.

## Diseño del organizador futuro

El organizador sería una operación distinta del catálogo de extracción. Un botón en la interfaz inicia una reorganización explícita de los contenedores conectados: agrupa stacks del mismo objeto y componentes, llena stacks existentes y consolida el sobrante en shulkers, con el objetivo de vaciar shulkers poco ocupadas.

No debe añadirse como una simple ordenación visual. Antes de implementarlo hay que decidir el orden de destino de cofres y shulkers, si se permiten mover shulkers completas, cómo se preservan nombres/colores/componentes, qué ocurre con shulkers anidadas y qué hacer si una red cambia durante el proceso. Debe existir una vista previa o confirmación, limitarse a contenedores admitidos y ser transaccional: ante una condición inesperada, no perder ni duplicar objetos. Los detalles quedan en `TODO.md`.

## Lore propuesto

**Nombre de mundo:** Archivo de Cartógrafos (o Catálogo del Gremio). Los exploradores construyeron una terminal para registrar suministros en almacenes de campaña. El conector es un “nodo de inventario”; la pantalla, un “archivo de expedición”. Los repeaters son relés de señal que enlazan físicamente el terminal con cofres cercanos. La pantalla no crea ni teletransporta objetos: consulta y mueve existencias reales dentro del almacén conectado.

Esto encaja con lo que el mod ya hace y con su límite local. El lore puede aparecer en nombres traducidos, descripción de los bloques, texto de receta y un avance al conectar el primer cofre. No hace falta añadir NPC, dimensiones, energía nueva ni una historia extensa para que la mecánica tenga personalidad.

### Texto corto sugerido

- **Conector de almacenamiento:** “Un relé de campo que enlaza hasta cuatro cofres cercanos.”
- **Pantalla de almacenamiento:** “Consulta el inventario registrado por el relé, incluso dentro de cajas de shulker.”
- **Avance:** “Inventario a la vista” — conecta una pantalla con un cofre.

## Criterio de salida

El primer JAR está listo cuando el jugador puede fabricar, conectar, buscar y extraer objetos sin pérdidas ni duplicados en el alcance declarado; no existe ninguna ruta de inserción desde la GUI; los errores comunes se explican en pantalla; el comportamiento de redstone es intencional; y una prueba en mundo dedicado o integrado confirma apertura, reconexión y persistencia. El acabado visual puede seguir siendo temprano.
