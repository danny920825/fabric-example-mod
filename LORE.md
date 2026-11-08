# Lore de Arcanum Storage — El Archivo de los Cartógrafos

Documento de trabajo. Define la identidad narrativa del mod y las reglas para ampliarlo sin contradecir lo ya contado.

## Premisa

El **Gremio de los Cartógrafos** exploró el mundo y levantó puestos de avituallamiento para sus expediciones. Cada puesto guardaba suministros en cofres y cajas de shulker, y los catalogaba con un **ledger**: un índice físico que respondía a sellos de latón y redstone.

El gremio se dispersó. Sus puestos quedaron abandonados, pero algunos ledgers todavía responden. El jugador es un **archivista de campo**: encuentra un registro, reconstruye la red y vuelve a poner en orden los almacenes olvidados.

No hay elegido, profecía ni enemigo final. Hay infraestructura recuperada, reglas de archivo y una historia contada en notas al margen.

## Principios para no romper el lore

1. **El gremio era descentralizado y experimental.** Cada región usaba sellos, recetas y métodos distintos. Eso permite rediseñar mecánicas y añadir módulos sin retcon.
2. **Cada versión es una generación de sellos o un módulo recuperado.** Lo nuevo no niega lo viejo: lo mejora o lo sustituye dentro de la ficción.
3. **La mecánica se explica con una regla del archivo.** Si algo es local, costoso o limitado, el ledger tiene una razón burocrática o física.
4. **El jugador no es especial.** Es un técnico curioso. La historia se cuenta con documentos, no con misiones obligatorias.
5. **Nada de magia que resuelva el almacenamiento.** Todo es latón, redstone, tinta y señal. La tecnología puede ser rara, pero es reproducible.

## Glosario

- **Ledger / Libro Mayor:** el índice de la red. Físicamente es la pantalla; portátilmente, el libro.
- **Sello:** un conector. Enlaza un cofre con el ledger.
- **Nodo:** cada cofre enlazado. La red es una constelación de nodos.
- **Diezmo:** coste de inscribir un nodo nuevo. Paga tinta, señal y latón.
- **Prensa:** el organizador de shulkers. Compacta suministros y libera estuches vacíos.
- **Capacidad:** límite de nodos y de fichas que el ledger puede sostener.
- **Profundidad:** nivel de cajas dentro de cajas que el ledger puede leer.
- **Extracción:** única operación de salida. El archivo entrega, pero no acepta depósitos sin destino.

## El libro: Registro de Campo del Archivista

**Nombre en inglés:** *Archivist's Field Ledger*.

### Cómo se obtiene (recomendación)

Híbrido de tres capas, para no depender del azar ni regalar toda la historia:

1. **Crafteable desde el principio.** Barato y sin RNG:
   - Libro + saco de tinta + lingote de cobre + polvo de redstone.
   - Así el jugador siempre puede consultar cómo construir la red.
2. **Copias y páginas en cofres de cartógrafo.** Inyección de loot en las casas de cartógrafo de aldea: una copia del registro o una **Página de Archivo**. Aporta descubrimiento sin bloquear el tutorial.
3. **Capítulos por hitos.** El libro se amplía al colocar el primer sello, enlazar el primer cofre, organizar la primera shulker, alcanzar 8 nodos, etc. El ledger "absorbe" lo que aprende.

No recomiendo que el libro solo se encuentre: si el jugador no lo encuentra, el mod no se explica. Tampoco que sea solo crafteable: se pierde la exploración y el tono de archivo recuperado.

### Estructura

- **Manual:** capítulos prácticos siempre disponibles.
- **Anales:** capítulos de lore, desbloqueados por hitos o páginas encontradas.

### Manual (índice propuesto)

1. **El primer sello** — qué hace el mod, cómo craftear el libro, el conector y la pantalla.
2. **Construir el archivo** — cofres, repetidores (generación provisional), letras N/S/E/W y enlace.
3. **Leer el índice** — búsqueda, extracción, tooltips, orden, origen y páginas.
4. **La prensa de estuches** — organizador de shulkers, qué es una etiqueta, vista previa y cajas vacías.
5. **Los sellos de red** — conectores directos, diezmo, capacidad y límites.
6. **Solución de problemas** — sin conexiones, sin resultados, profundidad, listado truncado, inventario lleno.

### Anales (índice propuesto)

1. **El gremio de los cartógrafos** — quiénes eran y por qué catalogaban.
2. **La guerra de los relés** — los repetidores fueron un préstamo de los ingenieros de redstone; los sellos propios son la solución del gremio.
3. **El diezmo** — por qué inscribir un nodo cuesta más cuanto más grande es la red.
4. **Los archivos abandonados** — puestos, ledgers rotos y páginas perdidas.
5. **Notas de campo** — voces de distintos archivistas, con ganchos para módulos futuros.

### Voz y tono

Práctico, seco, con humor de archivo. Frases cortas. Notas al margen. Nada de épica grandilocuente.

Ejemplos:

> "Un archivo no es una caja grande. Es una promesa de que encontrarás lo que buscas."

> "El diezmo no es un impuesto. Es la tinta que mantiene legible el índice."

> "Los repetidores prestaban la señal. Nosotros aprendimos a fabricar el sello."

> "Si el ledger dice que está en el norte, mira el sello. Si el sello dice N, no discutas con él."

### Interfaz del libro

- Pantalla de dos páginas con estética de pergamino, cuero oscuro y esquinas de latón.
- Columna izquierda: Manual / Anales. Derecha: texto e ilustraciones simples (iconos de objetos, diagramas de slots).
- Botones de página, cierre y, si se quiere, búsqueda.
- Contenido en JSON por idioma (`en_us`, `es_es`) para añadir capítulos sin tocar código.
- El libro no se consume ni hay que recraftearlo: se actualiza al descubrir capítulos.

## Cómo el lore justifica las mecánicas

| Mecánica | Explicación en el archivo |
|---|---|
| Solo extracción | "El archivo entrega, no acepta donaciones sin destino. Para depositar, abre el cofre y decide tú." |
| Shulkers indexadas | "Las cajas de shulker son estuches de campaña; el ledger lee su interior sin abrirlas." |
| Límite de profundidad | "Más allá del segundo sello, el contenido se considera sellado." |
| Tope de 3000 fichas | "El ledger tiene páginas finitas; cuando se llena, muestra solo las primeras fichas." |
| Red local | "La señal no viaja lejos sin un relé. El archivo es de campaña." |
| Diezmo creciente | "Cada nodo añade peso al índice; el diezmo paga la tinta y la señal que lo mantienen legible." |
| Capacidad máxima | "El ledger puede sostener un número finito de nodos; más allá, las fichas se corrompen." |
| Prensa de shulkers | "Compacta suministros para liberar estuches vacíos. No crea ni destruye nada." |
| Etiquetas ignoradas | "Una caja con nombre es una caja con dueño. La prensa no discute con las etiquetas." |

## Ganchos de expansión

- **Adaptadores de sello:** barriles, hornos y otros contenedores vanilla. "Cada material requiere un sello distinto."
- **Pestañas de índice:** filtros por categoría o por nodo.
- **Espejos de archivo:** acceso remoto caro y tardío. "La señal se copia, pero se degrada."
- **Talleres de archivo:** crafting desde la red. Mencionado como experimento incompleto, no prometido.
- **Sellos de acceso:** permisos para multijugador.
- **Archivos de ultramar:** otras dimensiones.
- **Archivos corruptos:** estructuras opcionales con nodos rotos y puzles, sin combate obligatorio.

La regla: todo lo nuevo es un **módulo** o una **generación** del gremio. Nunca una contradicción.

## Identidad visual

- Cuero oscuro, latón, slate, acentos teal y rojo redstone.
- Rosa de los vientos, esquinas metálicas, fichas de catálogo.
- El libro y la pantalla comparten paleta y tipografía.
- Las letras N/S/E/W del conector son parte de la identidad: el archivo siempre dice dónde está cada cosa.

## Decisiones pendientes

1. Nombre final del gremio: **Archivo de los Cartógrafos** (recomendado) o Gremio del Índice.
2. ¿Libro crafteable + loot de cartógrafo + hitos? (Recomendado sí.)
3. ¿Estructura propia "Puesto de Archivo" en 1.0 o solo inyección de loot en aldeas? (Recomendado empezar por loot; estructura en fase 4.)
4. ¿El libro se puede copiar en mesa de cartógrafo o con receta? (Recomendado receta simple para multijugador.)
