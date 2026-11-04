# Changelog

## 0.1.1 — interface and link polish

### Added

- Orientation letters **N/S/E/W** on the four sides of the Storage Connector, matching the side names shown by the screen ("Origin: North", "Origin: South", and so on).
- Item tooltips when hovering a catalog result, like in vanilla containers.
- Sorting by natural order, item name or total count. The sort button shows the active mode.
- Page navigation with the mouse wheel and with the left/right and Page Up/Page Down keys.
- An origin line on hover, including whether the stack is inside a shulker box.

### Changed

- Compact 176×238 interface with a 9×5 catalog: 45 results per page, so the whole screen fits at common GUI scales.
- The connector now sends a level 15 signal to correctly wired repeaters, and only while a display is attached. The repeater lights up to confirm the link. Its delay and vanilla redstone behavior are untouched.
- Catalog listings are capped at 3000 entries, with a warning when the list is truncated.
- Refined header, slot wells and status band; removed the redundant "Inventory" label.

### Fixed

- Catalog tooltips were missing on 1.21.9 because `HandledScreen` no longer draws them automatically.
- Corrected the repeater/`FACING` direction semantics so the origin side matches the physical side of the connector.

### Compatibility

- Minecraft Java 1.21.9
- Fabric Loader 0.19.2+
- Fabric API 0.134.1+1.21.9+
- Java 21

## 0.1.0-beta — first release

### Added

- **Storage Connector** and **Storage Display** blocks with their recipes, loot tables, models, textures and English/Spanish translations.
- A connector network with up to four sides. Each side uses a repeater as a direction marker and a vanilla chest behind it.
- Search by visible item name or registry ID, with pagination.
- Indexing of single and double chests and of the shulker boxes stored inside them.
- Extraction with left-click (full stack), right-click (one item) and shift-click (as much as fits in the player inventory).
- Empty states for no connections, no items and no search results, a depth-limit warning, and a connection counter.

### Safety

- The catalog is extraction-only. Cursor deposits, shift-click deposits and every other insertion route were removed.
- Every extraction resolves the connector, display, chest, slot, root stack and shulker path again on the server. Stale or disconnected references are refused.
- Items inside shulker boxes are updated in place. Extracting the last content item leaves the empty shulker; extracting the shulker itself moves the whole box with its contents.
- Container depth is capped at two nested levels and the limit is shown in the interface.
- If an item disappears between listing and extraction, nothing is removed and the list refreshes.

### Changed

- The mod no longer forces connected repeaters to stay powered or to use a one-tick delay. Repeaters keep their vanilla redstone behavior; the connector and the interface show the network state instead.
- The connector's active texture and light now only indicate that a display is attached.

### Compatibility

- Minecraft Java 1.21.9
- Fabric Loader 0.19.2+
- Fabric API 0.134.1+1.21.9+
- Java 21
