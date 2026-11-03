# Changelog

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
