# Arcanum Storage — ruta de trabajo

> **Plan vigente (5 de noviembre de 2026):** las fases 2 y 3 forman la versión **0.2.1** (Conector de Archivo + Prensa + Cable + red de 20 cofres); el diezmo/economía queda para más adelante. Ver [PLAN_FASE_2_3.md](PLAN_FASE_2_3.md) y [LORE.md](LORE.md). Las versiones anteriores (0.1.x) son históricas.

La prioridad es publicar una primera versión pequeña que extraiga objetos con seguridad. Las fases posteriores no deben retrasar ese primer release.

> Estado de la revisión (5 de noviembre de 2026): el código está alineado con el alcance extractor de 0.1.2. Están implementados la ausencia total de depósitos, la revalidación en servidor, el límite de profundidad, la interfaz compacta de 45 resultados por página, los tooltips de objetos, la ordenación, el origen por lado, las letras de orientación del conector y la red de cables con 20 cofres. Quedan las pruebas manuales en mundo y la validación del JAR en una instancia limpia.

## Fase 0 — Preparar el release inicial

- [x] Retirar del ScreenHandler toda inserción desde el catálogo: clic con cursor, shift-clic desde el inventario del jugador y cualquier otra ruta de depósito.
- [x] Actualizar README y textos de interfaz para decir claramente que Storage Display solo extrae; los depósitos se hacen abriendo el cofre elegido.
- [x] Corregir y revisar extracción de objetos dentro de shulkers, especialmente extracción de la propia shulker y de su último contenido. La revisión de código está hecha; falta confirmarlo en la prueba de mundo.
- [x] Confirmar que cada operación vuelve a validar conector, cofre, slot y ruta de shulker en el servidor.
- [x] Decidir cómo indicar visualmente los conectores sin alterar circuitos vanilla: se retiró el mixin que forzaba encendido y delay y, más tarde, los repetidores se sustituyeron por cables. La interfaz muestra el contador de cofres válidos.
- [ ] Probar en mundo: receta, colocación, conexión de los cuatro lados, cofre simple/doble, búsqueda, páginas, extracción de stack/una unidad, shulkers, desconexión y rotura/recuperación.
- [ ] Verificar persistencia al cerrar el mundo y volver a abrirlo.
- [x] Compilar el JAR y revisar su contenido, metadatos y recursos antes de etiquetar la versión.
- [x] Crear el Registro de Campo del Archivista (field_ledger) con receta, textura y apertura de interfaz.
- [x] Definir hechos como advancements técnicos (`item:`, `craft:`, `place:`, `event:`).
- [x] Implementar siembra desde inventario, cofre del End, estadísticas y advancements vanilla.
- [x] Añadir `manual.json` con entrada de prueba (`archive_cable`) y los tres estados.
- [x] Implementar pantalla mínima, comandos de depuración y validación de contenido.
- [x] Probar en servidor dedicado: persistencia, dos jugadores, reinicio y mundo existente.
- [x] Bump a 1.0.0.
- [x] Crear el Registro de Campo del Archivista (field_ledger) con receta, textura y apertura de interfaz.
- [x] Definir hechos como advancements técnicos (`item:`, `craft:`, `place:`, `event:`).
- [x] Implementar siembra desde inventario, cofre del End, estadísticas y advancements vanilla.
- [x] Añadir `manual.json` con entrada de prueba (`archive_cable`) y los tres estados.
- [x] Implementar pantalla mínima, comandos de depuración y validación de contenido.
- [x] Probar en servidor dedicado: persistencia, dos jugadores, reinicio y mundo existente.
- [x] Bump a 1.0.0.

## Fase 1 — Publicar 0.1

- [x] Elegir versión y nombre visibles del primer release: **Storage Display 0.1.1**.
- [x] Escribir instrucciones de instalación y uso, limitaciones conocidas y versión exacta de Minecraft/Fabric.
- [x] Preparar changelog breve.
- [ ] Probar el JAR exportado en una instancia limpia compatible, no solo en el entorno de desarrollo.
- [ ] Guardar el JAR y fuentes del release de forma reproducible.

## Fase 2 — Conector de Archivo y Prensa de Shulkers

- [x] Definir el alcance aprobado de la fase 2: conector de archivo directo contra un cofre, sin repetidores, con prensa sobre un solo cofre.
- [x] Implementar el **Conector de Archivo** (`ARCHIVE_CONNECTOR`) con `FACING`, `LINKED`, detección de cofre adyacente y apertura de la pantalla de prensa.
- [x] Implementar `ShulkerPress`: análisis, agrupación por objeto + componentes, plan de reparto, validación de totales y aplicación con snapshot completo del cofre.
- [x] Implementar `ArchivePressScreenHandler` y `ArchivePressScreen` con resumen, estados y confirmación en dos pasos.
- [x] Añadir receta, loot table, models, blockstates, texturas, GUI `storage_press.png` y traducciones en inglés/español.
- [x] Compilar el JAR (`storage-display-0.1.2.jar`) y revisar su contenido, metadatos y recursos antes de cerrar la versión.
- [ ] Probar en mundo: fabricar y colocar el conector, enlazar cofre simple/doble, prensa con shulkers etiquetadas/llenas/parciales/vacías, vista previa, confirmación y aborto por cambio de cofre.
- [ ] Probar en una instancia limpia de Minecraft 1.21.9 con Fabric antes de publicar el JAR 0.1.2.

## Fase 3 — Cable de Archivo y red de 20 cofres (0.2.1)

- [x] Sustituir los repetidores por un cable propio (`ARCHIVE_CABLE`): conexiones en las 6 direcciones, forma fina de contorno/colisión y enlace a cables, al Storage Connector y a cofres.
- [x] Enraizar la red en el Storage Connector (la cara superior queda para el Storage Display) y usar sus 5 caras restantes.
- [x] Subir el tope a 20 cofres: 4 por rama lateral y 4 por la rama inferior, con cables ilimitados.
- [x] Permitir bucles y anillos: recorrido independiente por cada cara raíz con reclamación de cofres no vistos.
- [x] Deduplicar cofres dobles y mantener el tope de listado de 3000 entradas.
- [x] Eliminar la detección por repetidor y la emisión de redstone del conector.
- [x] Añadir receta, loot table, blockstate multipart, modelos, texturas y traducciones del cable.
- [x] Bump a 0.2.1 y actualizar README/CHANGELOG.
- [ ] Probar en mundo: ramas en las 5 caras, cables en anillo, cofres dobles, desconexión al romper un cable, tope de 4 por rama y 20 globales.
- [ ] Validar el JAR 0.2.1 en una instancia limpia.
- [ ] Diseñar el diezmo/economía de enlace (fuera del alcance de 0.2.1).

## Versión 0.2.1 — End Pouch y Conector de Archivo via cable

- [x] Nueva pestaña creativa propia: **Storage Display**, con Storage Display, Storage Connector, Archive Cable, Archive Connector y End Pouch; la cara del mod elegida es el Storage Display.
- [x] Implementar **End Pouch** (`end_pouch`): puerta portátil al cofre del End, abriéndolo con clic derecho al sostenerlo o en el inventario; no se consume.
- [x] Añadir receta del End Pouch (más cara que cualquier otra) con cofre del End, fruta chorus, cuero, hilo, ojos de End y caparazón de shulker; la receta queda restringida a tras derrotar a la dragona.
- [x] Cambiar el **Archive Connector** para que no se incruste contra un bloque, sino que se una mediante **Archive Cable**.
- [x] Hacer que la **Prensa de Shulkers** resuelva la red del conector y se apague (`MULTIPLE_CHESTS`) cuando la red tenga más de un cofre.

### Arcanum Binding (fase 3)

- [x] Añadir el encantamiento **Arcanum Binding** solo para el End Pouch, definido como datapack (`data/arcanum_storage/enchantment/arcanum_binding.json`) y con soporte limitado al objeto mediante la etiqueta `arcanum_storage:arcanum_binding_supported`.
- [x] Evitar que la mesa de encantamientos lo ofrezca: no pertenece a `#minecraft:in_enchanting_table`.
- [x] Aplicarlo desde un libro encantado en el yunque por exactamente **31 niveles** (`anvil_cost` 62; el yunque divide entre 2 los libros).
- [x] Vender el libro solo en **bibliotecarios maestros** (nivel 5): **64 esmeraldas + 1 libro**, ~10% de probabilidad, 1 uso por reposición y precio fijo.
- [x] Rediseñar la muerte: el End Pouch encantado se consume y sella armadura, hotbar y mano secundaria en un **Caché del Archivo** (shulker con nombre y lore) dentro del cofre del End; los 27 slots de mochila caen normalmente.
- [x] Entregar el caché al inventario del respawn si el cofre del End está lleno, y soltarlo en el punto de reaparición como último recurso.
- [x] Respetar `keepInventory`, las bolsas sin encantar, los objetos con Maldición de Desaparición y las bolsas dentro de shulkers (no detectadas); solo una bolsa encantada activa el caché por muerte.
- [x] Hacer que un Caché colocado desaparezca al vaciarse y cerrarse, para que las muertes repetidas no acumulen shulkers vacías con nombre.
- [x] Validar en servidor real: encantamiento cargado, fuera de la mesa, 15/200 bibliotecarios con la oferta, yunque 31 niveles y arranque del servidor con el nuevo hook de muerte.
- [x] Probar en servidor dedicado el Caché del Archivo: muerte normal, cofre del End lleno, keepInventory, bolsa sin encantar, equipo vacío, varias bolsas, objetos con Maldición de Desaparición y autodestrucción al vaciarse.

## Versión 1.0.0 — Registro de Campo del Archivista

> Plan detallado: [PLAN_LIBRO.md](PLAN_LIBRO.md). El libro es el marcador de 1.0.0: la versión no se cierra hasta que el Manual cubra todas las funciones implementadas y funcione en mundos nuevos y ya explorados. La progresión no depende de loot, estructuras ni RNG.

### 0.3.0 — Conocimiento

- [ ] Crear `field_ledger` con receta renovable, textura y apertura de interfaz.
- [ ] Definir hechos como advancements técnicos (`item:`, `craft:`, `place:`, `event:`).
- [ ] Implementar siembra desde inventario, cofre del End, estadísticas y advancements vanilla.
- [ ] Añadir `manual.json` con una entrada de prueba (`archive_cable`) y los tres estados.
- [ ] Implementar pantalla mínima, comandos de depuración y validación de contenido.
- [ ] Probar en servidor dedicado: persistencia, dos jugadores, reinicio y mundo existente.

### 0.4.0 — Manual

- [ ] Interfaz de dos páginas con índice, navegación, búsqueda, iconos y avisos.
- [ ] Escribir capítulos 1–5 y 7–10 para las funciones implementadas (sin diezmo).
- [ ] Añadir rejillas de receta o iconos de ingredientes.
- [ ] Paridad de claves en `en_us`, `es_es` y `es_mx`; revisión de tono.

### 0.5.0 — Logros y Anales

- [ ] Árbol visible de logros basado en hechos, con ruta principal y opcionales.
- [ ] Logro de cierre “El Registro completo”.
- [ ] Escribir los Anales y desbloquearlos por hitos.
- [ ] Documentos de loot opcionales, nunca necesarios para el Manual.
- [ ] Pulir la calibración de mundos existentes y el aviso de primera apertura.

### 1.0.0 — Cierre

- [ ] Congelar contenido y revisar que no haya capítulos de funciones no implementadas.
- [ ] Validar servidor dedicado, mundo de 500 días, `/reload` y JAR en instancia limpia.
- [ ] Medir rendimiento del escaneo y de la apertura del libro.
- [ ] Actualizar README, CHANGELOG y documentación; etiquetar 1.0.0.

### Decisiones pendientes

- [ ] Confirmar si el diezmo queda fuera de 1.0 (recomendado) o se implementa antes.
- [ ] Confirmar si la receta del libro está siempre disponible (recomendado) o se desbloquea estrictamente por logro.
- [ ] Confirmar el alcance de las rejillas de receta en la interfaz.

## Fase 2 (histórica) — Mejoras posteriores de consulta

- [x] Mostrar estados vacíos claros: sin conexiones, búsqueda sin resultados y aviso de contenido anidado oculto por profundidad.
- [x] Añadir ubicación del resultado (cofre/lado y ruta dentro de shulker) si resulta útil. Se muestra al pasar el ratón y las caras del conector llevan las letras N/S/E/W.
- [x] Añadir ordenación al catálogo por nombre/cantidad sin mover objetos.
- [ ] Medir el rendimiento de búsqueda con cofres llenos y contenedores anidados; optimizar solo si hace falta.
- [ ] Añadir más contenedores vanilla de forma deliberada y probar sus reglas de anidamiento.

## Fase 3 — Diseñar el organizador

- [ ] Especificar exactamente qué contenedores puede reorganizar; excluir inicialmente inventarios de otros mods.
- [ ] Definir equivalencia de stacks usando item y componentes, preservando nombres, encantamientos, datos y demás componentes.
- [ ] Definir recorrido determinista: orden de cofres, slots y shulkers de destino.
- [ ] Decidir si el organizador puede mover una shulker como objeto y si puede modificar el contenido de shulkers guardadas dentro de otras shulkers.
- [ ] Diseñar una vista previa con conteo de stacks que se consolidarán y shulkers que quedarían vacías.
- [ ] Añadir botón separado de “Organizar”, confirmación y mensaje de resultado; no activar por error al paginar o buscar.
- [ ] Diseñar ejecución segura en servidor: validar permisos/contenedores, calcular el plan, aplicarlo sin duplicaciones y manejar fallos o cambios durante la ejecución.
- [ ] Probar conservación exacta de cantidades y componentes, shulkers llenas/parciales/vacías, cofres dobles y red desconectada.
- [ ] Permitir recuperar las shulkers vacías resultantes; no destruirlas automáticamente.

## Fase 4 — Lore y presentación

- [ ] Elegir tema de lore (propuesta: Archivo de Cartógrafos / catálogo de expedición).
- [ ] Ajustar nombres, descripciones y mensajes en español e inglés.
- [ ] Añadir avance al conectar por primera vez la pantalla a un cofre.
- [ ] Pulir iconos, estados de conexión y diseño de pantalla después de estabilizar interacciones.

## Fuera de alcance hasta que exista una necesidad clara

- [ ] Depósitos desde el catálogo: requieren elegir un destino concreto y no forman parte del rol de extractor.
- [ ] Acceso remoto o redes ilimitadas.
- [ ] Integración automática con inventarios de otros mods.
- [ ] Automatización que mueva o destruya shulkers sin vista previa y confirmación.
