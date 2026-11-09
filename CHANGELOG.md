# Changelog

## 1.0.0 — Field Ledger

First full release.

### Added

- **Field Ledger** (`field_ledger`): craftable field manual (book, ink sac, copper ingot, redstone dust) that opens a two-tab registry. Manual entries progress through Unknown/Partial/Complete and show missing requirements, recipe grids and item icons. Annals are historical documents unlocked by milestones.
- Knowledge is stored as hidden vanilla advancements, so it is permanent per player and survives losing the book, dying, reconnecting or reloading the world. No custom save format.
- The Manual covers every implemented mechanic and never requires loot, structures, RNG, death or item loss. Annals add optional history only.
- Visible achievement tree in the vanilla advancement screen, including the 1.0 capstone "The Registry is complete".
- The Field Ledger recipe is always craftable and is unlocked in the recipe book automatically when joining, including already-explored worlds.

### Changed

- Version, README, creative tab and JAR name updated to 1.0.0.

### Fixed

- Field Ledger item model, 16x16 texture and translations (English, Spanish Spain, Spanish Mexico).
- Removed invalid `//` comments from the three language files.
- Corrected craft fact advancements so crafting in a normal crafting table unlocks them.

### Compatibility

- Minecraft Java 1.21.9
- Fabric Loader 0.19.2+
- Fabric API 0.134.1+1.21.9+
- Java 21

## 0.2.1 — End Pouch and Archive Connector via cable

Second minor feature release.

### Added

- **Creative tab** for the mod: the new **Arcanum Storage** tab (chosen as the face of the mod) now holds the Storage Connector, the Storage Display, the Archive Cable, the Archive Connector and the End Pouch. It replaces the Redstone tab.
- **End Pouch** (`end_pouch`): a portable door to the player's ender chest. Right-click while holding it, or right-click it in the player inventory, opens the same ender chest. The pouch is not consumed by normal use.
- End Pouch recipe: leather, ender chest, chorus fruit, shulker shell, string, ender eyes and ender chests. The most expensive recipe in the mod; the chorus fruit and the shulker shell only exist in the outer End, so the pouch can only be crafted after the dragon has been defeated.
- **Arcanum Binding** (`arcanum_storage:arcanum_binding`): a rare enchantment exclusive to the End Pouch. It is never offered by the enchanting table; applying its enchanted book at an anvil costs 31 experience levels. Master librarians may sell the book for 64 emeralds and 1 book (about 10% chance when reaching master level, one use per restock).
- **Archive Cache** death mechanic: when a player dies with a bound End Pouch directly in the inventory, the pouch is consumed and the armor, hotbar and offhand are sealed into a named shulker box. The cache is placed in the player's ender chest; if it is full, the cache is placed in the respawn inventory, and if that is also full it drops at the respawn point. The 27 backpack slots drop normally. Unenchanted pouches, empty loadouts and `keepInventory` are unaffected, and Curse of Vanishing items still vanish. A placed cache removes itself once its last item is taken, so repeated deaths do not leave empty labeled shulker boxes behind.
- New tooltip line for a bound End Pouch, plus English, Spanish (Spain) and Spanish (Mexico) messages when the cache is stored or delivered.

### Changed

- **Project renamed to Arcanum Storage.** The visible name, creative tab and JAR (`arcanum-storage-0.2.1.jar`) now use the new brand, and the internal mod ID changed from `storage_display` to `arcanum_storage`. Blocks keep their names (Storage Connector, Storage Display, Archive Cable, Archive Connector), but their registry IDs now live under `arcanum_storage:`. Worlds created with previous versions will lose the old items and blocks.
- **Archive Connector** no longer attaches to a chest. It joins the **Archive Cable** network and the **Shulker Press** resolves the network's chest.
- The **Shulker Press** now turns off with a new `MULTIPLE_CHESTS` status and disables both buttons when the cable network holds more than one chest (a double chest counts as one).

### Compatibility

- Minecraft Java 1.21.9
- Fabric Loader 0.19.2+
- Fabric API 0.134.1+1.21.9+
- Java 21

## 0.1.2 — Archive Connector, Shulker Press and Archive Cable

First public release of the phase 2 and phase 3 feature sets.

**Phase 3 — Archive Cable and expanded network**

### Added

- **Archive Cable**, a new block that carries the storage network from the Storage Connector to chests. It connects in all six directions, uses a thin shape for outline and collision, and joins other cables, the connector and chests. Recipe: two copper ingots and one redstone dust, yielding four cables.
- Blockstate with multipart arms, core and arm models, block and item textures, loot table and English/Spanish translations for the cable.
- Networks now support up to **20 chests**: four per lateral face of the connector plus four below. The number of cables is unlimited.
- The cable graph is walked once per usable connector face, so loops and rings are allowed and each face can claim its own four chests without duplicating any.

### Changed

- **Repeaters no longer connect chests.** The cable is the only network medium; existing repeater-based links stop being detected.
- The Storage Connector no longer emits redstone power. Repeaters are now just a crafting ingredient, not a link marker.
- The catalog's origin label now includes the **Below** face for the downward branch.
- Version bumped to 0.1.2.

### Removed

- Repeater-based link detection (`isConnectedRepeater` and the connector's redstone output).

**Phase 2 — Archive Connector and Shulker Press**

### Added

- **Archive Connector**, a new block that links directly to a vanilla chest without repeaters. It has no redstone behavior of its own and only uses `FACING` plus a `LINKED` state for texture and light.
- **Shulker Press**, opened by right-clicking the Archive Connector. It organizes the unlabeled shulker boxes stored in the linked chest and frees the shulkers that become empty.
- The press groups matching items by item and components, sorts groups by total count descending (stable tie-break by item ID) and redistributes the stacks, starting with the largest group.
- A preview flow with an analysis step and a two-step confirmation before anything moves.
- A summary screen with eligible shulkers, ignored labeled shulkers, stacks before/after, groups and shulkers that will be freed, plus status states for no chest, no shulkers, all empty, already organized, ready, applied, changed and failed.
- The operation revalidates the full chest snapshot before writing and aborts if the chest changed between the analysis and the application; a failed write restores the original inventory.
- Recipe, loot table, blockstate, models, textures and English/Spanish translations for the Archive Connector and the press GUI.

### Fixed

- Block items now use the `block.` translation prefix, so their names resolve to "Storage Connector", "Storage Display" and "Archive Connector" instead of showing raw keys such as `item.storage_display.archive_connector`.
- Added the `minecraft:mineable/pickaxe` block tag for all three blocks. Without it, `requiresTool()` made the pickaxe count as a wrong tool: the blocks broke very slowly and dropped nothing even with Silk Touch.
- Added the `minecraft:needs_diamond_tool` tag for the Archive Connector, which now has iron-block hardness (5.0) and requires a diamond pickaxe or better. Silk Touch is no longer needed.

### Safety

- The press only reads and writes unlabeled shulker boxes stored directly in the chest. Shulkers with a custom name or lore are ignored completely, loose items and other chests are never touched.
- Nested shulkers are treated as opaque objects; the press never opens them.
- No item is created, duplicated or destroyed; components are preserved and stacks are regrouped only.

### Compatibility

- Minecraft Java 1.21.9
- Fabric Loader 0.19.2+
- Fabric API 0.134.1+1.21.9+
- Java 21

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
