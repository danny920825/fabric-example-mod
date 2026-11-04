# Storage Display — ruta de trabajo

La prioridad es publicar una primera versión pequeña que extraiga objetos con seguridad. Las fases posteriores no deben retrasar ese primer release.

> Estado de la revisión (4 de noviembre de 2026): el código está alineado con el alcance extractor de 0.1.1. Están implementados la ausencia total de depósitos, la revalidación en servidor, el límite de profundidad, la interfaz compacta de 45 resultados por página, los tooltips de objetos, la ordenación, el origen por lado y las letras de orientación del conector. Quedan las pruebas manuales en mundo y la validación del JAR en una instancia limpia.

## Fase 0 — Preparar el release inicial

- [x] Retirar del ScreenHandler toda inserción desde el catálogo: clic con cursor, shift-clic desde el inventario del jugador y cualquier otra ruta de depósito.
- [x] Actualizar README y textos de interfaz para decir claramente que Storage Display solo extrae; los depósitos se hacen abriendo el cofre elegido.
- [x] Corregir y revisar extracción de objetos dentro de shulkers, especialmente extracción de la propia shulker y de su último contenido. La revisión de código está hecha; falta confirmarlo en la prueba de mundo.
- [x] Confirmar que cada operación vuelve a validar conector, cofre, slot y ruta de shulker en el servidor.
- [x] Decidir cómo indicar visualmente los conectores sin alterar circuitos vanilla: se retiró el mixin que forzaba encendido y delay. El repeater es solo un marcador direccional y la interfaz muestra el contador de cofres válidos.
- [ ] Probar en mundo: receta, colocación, conexión de los cuatro lados, cofre simple/doble, búsqueda, páginas, extracción de stack/una unidad, shulkers, desconexión y rotura/recuperación.
- [ ] Verificar persistencia al cerrar el mundo y volver a abrirlo.
- [x] Compilar el JAR y revisar su contenido, metadatos y recursos antes de etiquetar la versión.

## Fase 1 — Publicar 0.1

- [x] Elegir versión y nombre visibles del primer release: **Storage Display 0.1.1**.
- [x] Escribir instrucciones de instalación y uso, limitaciones conocidas y versión exacta de Minecraft/Fabric.
- [x] Preparar changelog breve.
- [ ] Probar el JAR exportado en una instancia limpia compatible, no solo en el entorno de desarrollo.
- [ ] Guardar el JAR y fuentes del release de forma reproducible.

## Fase 2 — Mejoras posteriores de consulta

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
