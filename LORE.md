# Lore de Arcanum Storage — El Archivo de los Cartógrafos

Documento de referencia narrativa y de diseño. Define la identidad del mod, su historia, las reglas que explican sus mecánicas y los principios que deben respetarse al incorporar nuevas funcionalidades.

Este documento describe tanto las características implementadas como las propuestas de expansión. Las mecánicas todavía no implementadas se identifican expresamente para evitar confundir el lore con el comportamiento actual del mod.

---

# 1. Premisa

Mucho antes de que el jugador descubriera sus instalaciones, existió una organización dedicada a explorar territorios, establecer rutas de suministro y documentar los recursos del mundo: el **Gremio de los Cartógrafos**.

Sus expediciones necesitaban algo más que mapas. Necesitaban almacenar provisiones, localizar herramientas, clasificar materiales y recuperar equipamiento cuando las expediciones terminaban mal.

Para resolver estos problemas, el gremio desarrolló una familia de tecnologías basadas en cobre, latón, redstone, mecanismos de presión y sistemas de catalogación.

Sus instalaciones se extendieron por distintas regiones. Cada puesto disponía de almacenes, terminales de consulta y herramientas especializadas. Algunos utilizaban generaciones antiguas de conectores; otros experimentaban con sistemas de señal más avanzados.

El gremio no desapareció en una única catástrofe. Se dispersó progresivamente. Sus expedicionarios dejaron de mantener las rutas, los puestos quedaron abandonados y sus documentos se separaron de las tecnologías que describían.

Sin embargo, algunos dispositivos todavía funcionan.

El jugador es un **archivista de campo**: alguien que encuentra documentación incompleta, identifica componentes, reconstruye herramientas y recupera instalaciones olvidadas.

No existe una profecía, un elegido ni un enemigo final.

Hay tecnología abandonada, procedimientos incompletos y una cantidad preocupante de documentos que nadie terminó de archivar.

# 2. Identidad del gremio

## 2.1. El Gremio de los Cartógrafos

El Gremio de los Cartógrafos no era una organización militar ni una civilización dedicada a conquistar territorios.

Su propósito era explorar y documentar.

Sus miembros desempeñaban diferentes funciones:

- **Cartógrafos:** exploraban regiones y registraban rutas, asentamientos y recursos.
- **Archivistas:** desarrollaban sistemas de catalogación y mantenían los registros.
- **Ingenieros:** diseñaban conectores, mecanismos y redes de señal.
- **Expedicionarios:** transportaban suministros y utilizaban las herramientas desarrolladas por el gremio.
- **Custodios:** mantenían los almacenes y administraban los recursos de cada puesto.

La organización era descentralizada. Cada región podía desarrollar sus propios procedimientos y adaptar las tecnologías disponibles.

Por esta razón existen diferentes generaciones de herramientas, documentos contradictorios y prototipos que nunca llegaron a estandarizarse.

## 2.2. Filosofía tecnológica

El gremio no utilizaba magia para resolver problemas de almacenamiento o transporte.

Sus tecnologías dependían de materiales físicos, señales de redstone, mecanismos y procedimientos reproducibles.

Una herramienta podía ser extraordinariamente avanzada, pero debía tener una explicación técnica dentro de las reglas del mundo.

Principios fundamentales:

1. Todo dispositivo necesita componentes físicos.
2. Toda conexión necesita un medio de transmisión.
3. Todo archivo tiene límites de capacidad.
4. Toda operación de almacenamiento debe tener un destino identificable.
5. La recuperación de recursos nunca debe implicar su duplicación.
6. Los procedimientos de emergencia tienen costes y limitaciones.

La tecnología puede ser experimental, pero no omnipotente.

# 3. El Registro de Campo del Archivista

**Nombre en inglés:** *Archivist's Field Ledger*.

El libro es el principal documento de referencia del mod y representa la reconstrucción progresiva del conocimiento del gremio.

No es un objeto mágico ni contiene una conciencia. Es un registro técnico que incorpora información a medida que el archivista identifica materiales, reconstruye dispositivos y utiliza sus herramientas.

## 3.1. Obtención

La obtención del libro combina descubrimiento y progresión, sin depender del azar.

Propuesta:

1. Cuando el jugador recoge cualquier libro vanilla por primera vez, se desbloquea el advancement correspondiente.
2. El advancement desbloquea la receta del Archivist's Field Ledger.
3. El libro se fabrica utilizando materiales sencillos:
   - Un libro.
   - Un saco de tinta.
   - Un lingote de cobre.
   - Polvo de redstone.

El jugador puede comenzar a utilizar Arcanum Storage en cualquier momento de su partida, incluso en mundos muy avanzados.

No es necesario encontrar una estructura, conseguir una página especial o depender de un cofre de loot.

## 3.2. Reconstrucción progresiva

El libro comienza con información incompleta.

A medida que el jugador obtiene materiales, fabrica componentes y utiliza dispositivos, recupera nuevas páginas.

El sistema distingue tres estados:

- **Desconocido:** no existen suficientes referencias para identificar la tecnología.
- **Parcialmente recuperado:** se han identificado algunos componentes o procedimientos.
- **Recuperado:** la documentación está disponible.

El conocimiento adquirido es permanente. Perder o consumir un material no elimina una página ya recuperada.

La documentación puede revelar pistas sobre recetas antes de que el jugador reúna todos sus ingredientes.

## 3.3. Estructura del libro

El libro tiene dos secciones principales:

### Manual

Contiene información práctica sobre las herramientas y sus procedimientos.

Se desbloquea principalmente mediante descubrimientos de materiales, fabricación y utilización de dispositivos.

### Anales

Contiene documentos históricos, anotaciones personales, informes de expedición y observaciones de antiguos miembros del gremio.

Se desbloquea mediante hitos y, opcionalmente, mediante documentos encontrados durante la exploración.

La información necesaria para utilizar el mod nunca depende de encontrar loot.

**El Manual enseña a utilizar el archivo. Los Anales explican quién lo construyó.**

## 3.4. Índice propuesto del Manual

1. **El primer registro:** introducción al gremio, al archivista y al sistema de documentación.
2. **El primer sello:** Storage Connector y Storage Display.
3. **Construir el archivo:** cables, conexiones, direcciones y cofres.
4. **Leer el índice:** búsqueda, extracción, resultados, páginas y procedencia.
5. **Los estuches de campaña:** shulkers y funcionamiento de la Shulker Press.
6. **El diezmo:** inscripción de nodos, costes y capacidad.
7. **Reservas personales:** End Pouch y acceso al ender chest.
8. **Protocolos de recuperación:** Arcanum Binding y Archive Cache.
9. **Límites del archivo:** profundidad, capacidad, conexiones y rendimiento.
10. **Solución de problemas:** desconexiones, inventarios, listados y operaciones rechazadas.

Los capítulos correspondientes a características todavía no implementadas no deben presentarse como instrucciones disponibles.

## 3.5. Índice propuesto de los Anales

1. El Gremio de los Cartógrafos.
2. Los primeros puestos de avituallamiento.
3. La guerra de los relés.
4. Las generaciones de sellos.
5. El diezmo y la administración de los archivos.
6. Los archivos abandonados.
7. Las reservas de campaña.
8. El protocolo de recuperación.
9. Notas de campo de antiguos archivistas.
10. El último registro.

# 4. El sistema de almacenamiento

El archivo físico es el resultado de varias generaciones de investigación del gremio.

Su propósito es localizar y recuperar suministros almacenados en distintos cofres sin tener que inspeccionarlos individualmente.

## 4.1. Storage Connector

El Storage Connector es el punto de origen de una red de almacenamiento.

Actúa como unidad central de conexión y administra los dispositivos asociados.

Su construcción utiliza componentes de redstone, especialmente repetidores y comparadores.

El gremio comenzó utilizando componentes convencionales de ingeniería de redstone. Con el tiempo desarrolló conectores especializados para mejorar la distribución y lectura de señales.

## 4.2. Storage Display

El Storage Display es la interfaz física del archivo.

Permite consultar el índice, buscar materiales y extraer recursos de los almacenes conectados.

Es la representación visible del sistema de catalogación.

El display no almacena objetos ni constituye un inventario independiente. Es una interfaz de consulta.

## 4.3. Archive Cable

El Archive Cable transporta las señales necesarias entre el Storage Connector y los contenedores asociados.

Permite construir redes con diferentes recorridos, esquinas y ramificaciones.

Los cables no transportan objetos físicamente. Tampoco tienen comportamiento de redstone convencional.

Su función es establecer y mantener la conexión lógica del archivo.

La longitud del cable no representa un límite de capacidad: la red está limitada por los nodos que puede registrar el conector.

## 4.4. Organización de la red

El Storage Connector dispone de cinco caras utilizables:

- Norte.
- Sur.
- Este.
- Oeste.
- Inferior.

Cada cara admite hasta cuatro cofres.

La capacidad máxima de una red es de veinte cofres.

Los cofres pueden ser simples o dobles. Un cofre doble cuenta como un único nodo.

El sistema es local: todos los cofres deben estar conectados mediante cables al mismo conector.

No existe acceso inalámbrico ni un sistema de canales remotos.

## 4.5. El principio de extracción

Una de las reglas fundamentales del archivo es que el índice solamente permite extraer objetos.

No acepta depósitos.

El gremio consideraba peligroso permitir que un sistema automatizado eligiera arbitrariamente el destino de un objeto.

Un material depositado en el cofre equivocado podía desaparecer dentro de una red de suministros y resultar prácticamente imposible de localizar.

Por esta razón, el archivo permite consultar y recuperar recursos, pero exige que los depósitos se realicen directamente en el cofre correspondiente.

Nota de un antiguo archivista:

> «El archivo entrega. No acepta donaciones sin destino. Si no sabes dónde guardar algo, el problema no es del índice.»

# 5. La catalogación de shulkers

Las cajas de shulker eran conocidas como **estuches de campaña**.

Permitían transportar suministros de forma compacta y protegerlos durante expediciones prolongadas.

El gremio desarrolló procedimientos para consultar su contenido sin necesidad de abrir cada estuche.

## 5.1. Lectura de contenidos

El ledger puede identificar los objetos almacenados dentro de shulkers colocadas en los cofres conectados.

La lectura tiene una profundidad limitada.

Los contenedores anidados más allá del segundo nivel se consideran sellados y no se inspeccionan.

Esta limitación evita que el índice tenga que recorrer estructuras de almacenamiento indefinidamente.

El jugador puede recuperar una shulker completa aunque sus contenidos no sean legibles.

## 5.2. Shulker Press

La Shulker Press es una herramienta especializada en reorganizar suministros.

Permite agrupar objetos equivalentes y redistribuirlos entre estuches de campaña.

Su propósito no es crear recursos, sino mejorar la distribución del espacio.

La prensa trabaja sobre un único cofre y necesita que el archivo tenga exactamente un cofre conectado.

Los estuches con nombre personalizado o lore se consideran propiedad identificada y quedan excluidos del procedimiento.

Tampoco se modifican objetos sueltos ni shulkers anidadas.

La prensa genera una vista previa antes de reorganizar el contenido y exige una confirmación explícita.

Una modificación del cofre entre el análisis y la confirmación invalida la operación.

Nota de mantenimiento:

> «Una caja con nombre tiene dueño. La prensa no discute con las etiquetas.»

# 6. El diezmo de inscripción

**Estado: propuesta de mecánica. No implementada en la versión 0.2.1.**

El gremio mantenía un procedimiento administrativo para registrar nuevos nodos en sus archivos.

Cada cofre incorporado incrementaba el trabajo necesario para mantener el índice actualizado.

El diezmo era la contribución necesaria para inscribir nuevos nodos y financiar los materiales utilizados en la calibración del sistema.

No era un impuesto territorial ni una recompensa para los administradores. Era un coste técnico de ampliación.

## 6.1. Justificación técnica

Un archivo puede parecer pequeño desde fuera, pero su contenido potencial es considerable.

Un cofre doble tiene 54 espacios. Si cada espacio contiene una shulker completa, y cada shulker almacena 27 stacks de 64 objetos, su capacidad teórica alcanza:

54 × 27 × 64 = 93.312 objetos por cofre doble.

Una red de veinte cofres dobles podría representar:

20 × 93.312 = 1.866.240 objetos.

Esta cifra representa una capacidad teórica máxima, suponiendo que todos los objetos puedan apilarse en grupos de 64.

El coste de mantener un archivo no depende únicamente de su tamaño físico. También depende de la cantidad de contenedores que debe inspeccionar y de la información que necesita indexar.

El diezmo representa el coste de ampliar esa infraestructura.

## 6.2. Regla de inscripción

La propuesta consiste en permitir cuatro cofres gratuitos por red.

A partir del quinto, cada nuevo nodo requiere un pago en esmeraldas.

El coste se inspira en la secuencia de Fibonacci:

1, 1, 2, 3, 5, 8, 13, 21...

Cada nuevo nodo tiene un coste superior al anterior.

El pago se realiza una sola vez al inscribir el cofre.

El diezmo no se cobra por cada consulta, extracción o reinicio del juego.

## 6.3. Propuesta inicial de costes

| Número de cofre | Coste propuesto |
|---:|---:|
| 1–4 | Gratis |
| 5 | 1 esmeralda |
| 6 | 1 esmeralda |
| 7 | 2 esmeraldas |
| 8 | 3 esmeraldas |
| 9 | 5 esmeraldas |
| 10 | 8 esmeraldas |
| 11 | 13 esmeraldas |
| 12 | 21 esmeraldas |
| 13 | 34 esmeraldas |
| 14 | 55 esmeraldas |
| 15 | 89 esmeraldas |
| 16 | 144 esmeraldas |
| 17 | 233 esmeraldas |
| 18 | 377 esmeraldas |
| 19 | 610 esmeraldas |
| 20 | 987 esmeraldas |

Esta tabla es una referencia matemática, no un balance definitivo.

La secuencia completa suma 2.583 esmeraldas para registrar los dieciséis cofres posteriores a los cuatro gratuitos.

Antes de implementar el sistema se debe evaluar si el coste final resulta razonable para una partida de supervivencia.

También se puede utilizar una versión escalada de Fibonacci, conservando el crecimiento progresivo sin exigir cantidades desproporcionadas.

## 6.4. Principios del diezmo

1. Los primeros cuatro cofres siempre son gratuitos.
2. El pago se realiza antes de registrar un nuevo nodo.
3. Un nodo ya inscrito no vuelve a pagar.
4. Desconectar temporalmente un cofre no elimina su inscripción.
5. El coste depende del número de inscripciones de la red, no del orden de conexión física.
6. El pago nunca debe consumir objetos del cofre que se está registrando.
7. La operación debe ser transaccional: si el registro falla, el pago no se pierde.
8. El coste debe ser visible antes de confirmar la inscripción.

El diezmo es una restricción económica y de progresión. No sustituye los límites técnicos, la optimización del índice ni las medidas de protección contra redes excesivamente costosas.

Nota de un administrador:

> «Cuatro cofres no requieren autorización. El quinto necesita una firma. El sexto necesita dos. Nadie recuerda quién aprobó esta política.»

# 7. Reservas personales: End Pouch

Los expedicionarios necesitaban acceder a sus reservas personales sin tener que transportar constantemente un cofre de ender.

El gremio desarrolló el **End Pouch**, un dispositivo portátil que permite abrir el inventario personal del ender chest.

A diferencia del archivo colectivo, el End Pouch no consulta los almacenes de una red.

Accede exclusivamente a las reservas personales del propietario.

Su fabricación requiere materiales asociados al End, por lo que representa una tecnología de expedición avanzada.

La receta utiliza:

- Un ender chest.
- Chorus fruits.
- Cuero.
- Hilo.
- Ojos de ender.
- Una shulker shell.

El dispositivo es reutilizable y no se consume durante su utilización normal.

La complejidad de su construcción refleja el esfuerzo necesario para desarrollar un acceso portátil a una reserva dimensional.

Nota de campo:

> «El equipaje es una responsabilidad. Poder dejarlo en otro lugar y seguir accediendo a él es una mejora considerable.»

# 8. Arcanum Binding

**Arcanum Binding** es un encantamiento experimental desarrollado para el End Pouch.

Su propósito era preservar el equipamiento esencial de un expedicionario cuando este moría durante una misión.

No evita la muerte ni conserva automáticamente todas las pertenencias.

Activa un procedimiento de recuperación de emergencia.

## 8.1. Protocolo de recuperación

Cuando un expedicionario muere llevando un End Pouch con Arcanum Binding directamente en su inventario, el dispositivo se consume y sella su equipamiento de combate en un **Archive Cache**.

El protocolo protege:

- Armadura.
- Barra rápida.
- Mano secundaria.

El resto del inventario se comporta normalmente.

Los objetos afectados por Curse of Vanishing no pueden recuperarse.

El Archive Cache es una caja de shulker identificada y se deposita en el ender chest del jugador siempre que exista espacio.

Si el ender chest está lleno, el sistema intenta entregar el archivo al jugador cuando reaparece.

El contenido debe recuperarse manualmente. El protocolo no equipa automáticamente los objetos.

Cuando el último objeto es retirado y se cierra la caja colocada, el contenedor desaparece.

## 8.2. Coste y rareza

Arcanum Binding no puede obtenerse mediante una mesa de encantamientos.

El gremio lo consideraba un procedimiento demasiado especializado para incorporarlo a sus herramientas convencionales.

Su aplicación requiere exactamente 31 niveles de experiencia en un yunque.

Los bibliotecarios de nivel maestro pueden ofrecer el libro encantado a cambio de 64 esmeraldas y un libro, con una probabilidad reducida de obtener la oferta.

La rareza representa la dificultad de reproducir el procedimiento.

## 8.3. Limitaciones

- Solo un End Pouch activa el protocolo por muerte.
- El dispositivo debe estar directamente en el inventario.
- Un pouch almacenado dentro de una shulker no se detecta.
- Un pouch sin el encantamiento se comporta normalmente.
- Si no existe equipamiento que proteger, el pouch no se consume.
- Con keepInventory activado no se aplica penalización ni se consume el dispositivo.
- Una caché pendiente puede perderse si el servidor se reinicia mientras el jugador todavía está muerto.

Nota de un antiguo expedicionario:

> «El protocolo no salva al explorador. Salva aquello que el explorador no debería perder.»

# 9. Los Anales del Gremio

Los Anales no son una historia lineal.

Son documentos recuperados de diferentes épocas y puestos de expedición.

Pueden contener observaciones contradictorias, anotaciones personales, informes de mantenimiento y decisiones administrativas.

## 9.1. La guerra de los relés

Durante las primeras expediciones, el gremio dependía de componentes de redstone convencionales.

Los repetidores permitían distribuir señales, pero presentaban limitaciones cuando las instalaciones crecían.

Los ingenieros desarrollaron posteriormente conectores especializados y sistemas de cableado propios.

La transición no fue inmediata. Algunos puestos siguieron utilizando sistemas antiguos durante años.

> «Los repetidores prestaban la señal. Nosotros aprendimos a fabricar el sello. Los ingenieros de redstone todavía consideran que fue una decisión innecesaria.»

## 9.2. Los archivos abandonados

Los puestos de avituallamiento no desaparecieron todos al mismo tiempo.

Algunos fueron abandonados por falta de mantenimiento. Otros quedaron aislados cuando las rutas dejaron de utilizarse.

Sus cofres permanecieron llenos, pero los registros se deterioraron.

Los dispositivos que todavía funcionan son una prueba de que el gremio desarrolló sistemas capaces de sobrevivir a sus propios administradores.

> «El último encargado dejó tres cajas sin etiquetar y una nota que decía que volvería después del invierno. El invierno terminó hace mucho.»

## 9.3. El problema de las etiquetas

Los archivistas descubrieron que los expedicionarios personalizaban sus cajas con nombres, marcas y anotaciones.

Esto generaba conflictos durante los procedimientos de reorganización.

Finalmente se estableció que cualquier contenedor personalizado debía considerarse propiedad identificada y quedar fuera de los procesos automáticos.

> «No reorganices aquello que no puedes identificar. La última vez que alguien ignoró esta norma, reorganizó también el equipaje del supervisor.»

## 9.4. El último registro

El último documento no tiene que explicar por qué desapareció el gremio.

Puede ser una nota sencilla de un archivista que terminó su trabajo y dejó las instalaciones preparadas para quien viniera después.

No debe revelar una gran catástrofe ni introducir un enemigo final.

La historia puede terminar sin resolver completamente el destino de sus integrantes.

> «El archivo está operativo. Los registros están incompletos, pero los suministros siguen donde deben estar. Si alguien encuentra estas instalaciones, que las utilice. Para eso fueron construidas.»

# 10. Cómo el lore justifica las mecánicas

| Mecánica | Justificación narrativa |
|---|---|
| Extracción exclusiva | El archivo entrega recursos, pero no acepta depósitos sin destino. |
| Storage Connector | Unidad central de registro de la red. |
| Storage Display | Interfaz física para consultar el índice. |
| Archive Cable | Medio de transmisión de señales. |
| Cinco caras utilizables | Diseño físico del conector. |
| Máximo de 20 cofres | Capacidad de registro de una unidad de archivo. |
| Cuatro cofres gratuitos | Inscripción básica incluida en la instalación. |
| Diezmo creciente | Coste de calibración y mantenimiento del índice. |
| Shulkers indexadas | Estuches de campaña cuyo contenido puede inspeccionarse. |
| Profundidad máxima | Los contenedores demasiado profundos se consideran sellados. |
| Límite de 3000 fichas | Capacidad finita del índice. |
| Shulker Press | Herramienta de reorganización de suministros. |
| Etiquetas ignoradas | Los contenedores personalizados tienen propietario identificado. |
| End Pouch | Acceso portátil a las reservas personales. |
| Arcanum Binding | Protocolo de recuperación de equipamiento. |
| Archive Cache | Contenedor físico de recuperación. |

# 11. Ganchos de expansión

Todas las futuras funcionalidades deben encajar en alguna de estas categorías.

## 11.1. Nuevos adaptadores

Barriles, hornos y otros contenedores vanilla.

Cada material requiere un adaptador compatible.

No todos los contenedores deben funcionar con el mismo sello.

## 11.2. Pestañas de índice

Filtros por categoría, tipo de objeto, origen o nodo.

Son mejoras de consulta y no deben modificar el funcionamiento básico del almacenamiento.

## 11.3. Espejos de archivo

Dispositivos que permiten consultar redes a distancia.

Su funcionamiento debe tener un coste, una limitación de señal o una infraestructura adicional.

La señal puede copiarse, pero no transmitirse sin consecuencias.

## 11.4. Talleres de archivo

Sistemas experimentales para fabricar objetos utilizando recursos almacenados en la red.

Esta tecnología debe considerarse una generación posterior y no una función implícita del catálogo actual.

## 11.5. Sellos de acceso

Sistemas de permisos para instalaciones compartidas y servidores multijugador.

Permiten establecer quién puede consultar, modificar o administrar un archivo.

## 11.6. Archivos de ultramar

Instalaciones ubicadas en otras dimensiones.

Pueden utilizar conectores y protocolos diferentes, pero deben mantener una relación tecnológica con los sistemas existentes.

## 11.7. Archivos corruptos

Estructuras opcionales que contienen instalaciones dañadas, documentos incompletos y mecanismos que deben repararse.

No deben requerir combate obligatorio.

Su función principal es ampliar la exploración y revelar fragmentos de la historia.

# 12. Identidad visual

La identidad visual debe transmitir tecnología antigua, documentación y herramientas de expedición.

Paleta principal:

- Cuero oscuro.
- Pergamino.
- Latón y cobre.
- Slate.
- Teal como acento tecnológico.
- Rojo redstone como indicador de señal o advertencia.

Elementos recurrentes:

- Rosas de los vientos.
- Esquinas metálicas.
- Fichas de catálogo.
- Diagramas técnicos.
- Sellos y etiquetas.
- Anotaciones manuscritas.
- Símbolos de orientación.

El Storage Display y el Archivist's Field Ledger deben compartir lenguaje visual.

La interfaz del libro debe utilizar dos páginas:

- Izquierda: índice, capítulos y navegación.
- Derecha: contenido, diagramas e ilustraciones.

El libro debe disponer de navegación por páginas, cierre y búsqueda opcional.

El contenido debe estar separado del código y preparado para traducciones mediante archivos JSON por idioma (`en_us`, `es_es`).

# 13. Voz y tono

El lenguaje de Arcanum Storage debe ser:

- Práctico.
- Seco.
- Técnico cuando sea necesario.
- Ligeramente irónico.
- Burocrático sin resultar aburrido.
- Coherente con documentos escritos por diferentes personas.

No debe utilizar épica grandilocuente ni presentar al jugador como un héroe predestinado.

El humor debe surgir de las observaciones de los archivistas, los problemas administrativos y las limitaciones absurdamente específicas de algunos procedimientos.

Ejemplos:

> «Un archivo no es una caja grande. Es una promesa de que encontrarás lo que buscas.»

> «El diezmo no es un impuesto. Es la tinta que mantiene legible el índice.»

> «Si el ledger dice que está en el norte, mira el sello. Si el sello dice N, no discutas con él.»

> «La capacidad del archivo es limitada. La paciencia del administrador también.»

> «No hemos perdido el inventario. Simplemente no hemos conseguido encontrarlo.»

> «El comité recomienda no superar la capacidad establecida. El comité también recomienda no consultar al comité durante las expediciones.»

# 14. Principios para futuras versiones

Estas reglas son obligatorias para mantener la coherencia del proyecto.

1. **El gremio es descentralizado y experimental.** Las diferentes regiones pueden utilizar tecnologías distintas.
2. **Cada versión puede representar una nueva generación.** Las mejoras sustituyen o perfeccionan procedimientos anteriores.
3. **Las mecánicas deben tener una explicación física.** Nada de magia que resuelva arbitrariamente los problemas de almacenamiento.
4. **El jugador no es especial.** Es un archivista que reconstruye tecnología existente.
5. **El conocimiento práctico nunca debe depender exclusivamente del azar.** El loot aporta historia, no bloquea funcionalidades.
6. **Las tecnologías deben conservar sus limitaciones.** Una herramienta especializada no debe convertirse en una solución universal.
7. **Los recursos no se crean ni se destruyen arbitrariamente.** Las operaciones deben respetar el inventario y sus contenidos.
8. **El humor debe ser discreto.** Las notas de campo pueden tener personalidad, pero no deben convertir el mod en una parodia.
9. **Las características futuras no deben presentarse como implementadas.** El lore puede anticiparlas, pero la documentación práctica debe reflejar la versión real.
10. **La historia no necesita una conclusión definitiva.** El destino del gremio puede permanecer parcialmente desconocido.

---

# 15. Decisiones de diseño pendientes

| Decisión | Estado |
|---|---|
| Nombre del gremio: Gremio de los Cartógrafos | Aceptado |
| Libro crafteable y desbloqueado al recoger un libro vanilla | Propuesto |
| Actualización progresiva mediante descubrimientos | Propuesto |
| Manual separado de los Anales | Aceptado |
| Loot de cartógrafo como fuente de documentos adicionales | Propuesto |
| Diezmo a partir del quinto cofre | Propuesto |
| Cuatro cofres gratuitos | Propuesto |
| Esmeraldas como moneda del diezmo | Preferido |
| Fibonacci pura como progresión de costes | Pendiente de balance |
| Bloques de esmeralda como unidad de pago | Descartado inicialmente |
| Diamantes como moneda alternativa | No recomendado |
| Estructura propia de Puesto de Archivo | Pospuesta |
| Inyección de documentos en casas de cartógrafo | Propuesta para una versión futura |

## Nota final de diseño

Arcanum Storage no pretende ser una solución mágica para guardar objetos.

Es una reconstrucción de infraestructura desarrollada por una organización que entendía que almacenar, clasificar y recuperar recursos eran problemas diferentes.

El archivo debe sentirse útil, limitado y deliberadamente diseñado.

Su historia no se cuenta mediante grandes acontecimientos, sino mediante herramientas que todavía funcionan, documentos que explican por qué fueron construidas y anotaciones de personas que probablemente tenían demasiado trabajo administrativo.

**El gremio desapareció. El archivo permanece.**