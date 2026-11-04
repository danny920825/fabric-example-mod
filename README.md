# Storage Display

Storage Display is a Fabric mod for **Minecraft Java 1.21.9**. It adds a physical storage terminal that searches the contents of up to four nearby vanilla chests, including the shulker boxes stored inside them.

**The catalog is extraction-only.** It lets you search and take items out. It never accepts items: to store something, open the specific chest and place the item there. This keeps every deposit unambiguous and avoids the terminal silently choosing a chest or shulker for you.

## Requirements

- Minecraft Java 1.21.9
- Fabric Loader 0.19.2 or newer
- Fabric API 0.134.1+1.21.9 or a compatible build
- Java 21

## Installation

1. Install Fabric Loader for Minecraft 1.21.9.
2. Put [Fabric API](https://modrinth.com/mod/fabric-api) in the `mods` folder.
3. Put the Storage Display JAR (`storage-display-0.1.1.jar`) in the same `mods` folder.
4. Start the game with the Fabric profile.

## How to use it

### Crafting

- **Storage Connector:** four redstone blocks, four repeaters and one comparator.

  ```
  R P R
  P C P
  R P R
  ```

  `R` = redstone block, `P` = repeater, `C` = comparator.

- **Storage Display:** four gold ingots, four iron ingots, one diamond and two Ender Eyes.

  ```
  G I G
  E D E
  G I G
  ```

  `G` = gold ingot, `I` = iron ingot, `E` = Ender Eye, `D` = diamond.

### Building the network

1. Place the **Storage Connector**.
2. Place the **Storage Display** directly on top of it. The connector lights up and uses its active texture while the display is attached.
3. On any of the four horizontal sides of the connector, place a **repeater** next to the connector with its output pointing away from the connector.
4. Place a **vanilla chest** directly behind that repeater, on the side the repeater points to.
5. Repeat on up to four sides. A double chest counts as one connection.

The repeater is a directional marker: the mod checks its facing and the chest behind it. While a display is attached, the connector sends a level 15 signal only to correctly wired repeaters, so the repeater lights up to confirm the link. Its delay and the rest of its vanilla redstone behavior are untouched. Each connector face shows the letter of its direction (**N**, **S**, **E** or **W**) so the origin labels on the screen match the physical block.

### Using the display

Open the Storage Display to see the network catalog.

- Search by the item's visible name or its registry ID.
- Hover a result to see its item tooltip and where it comes from ("Origin: North", plus "inside a shulker" when applicable).
- Use the arrow buttons, the mouse wheel or the left/right and Page Up/Page Down keys to change page.
- Use the sort button to switch between natural order, name and total count.
- **Left-click** a result to take a full stack.
- **Right-click** a result to take one item.
- **Shift-click** a result to move as much as fits into your inventory.
- The screen shows the number of connected chests, the current page, empty states and a depth-limit warning. Each page shows up to 45 results.

The catalog never accepts items with the cursor, shift-click or any other route. To store an item, open the chest itself.

## Supported storage and limits

- Vanilla **single and double chests** only.
- **Shulker boxes stored inside those chests** are indexed recursively, up to two nested container levels. Contents deeper than that are not listed; the screen shows a warning and the containing shulker can still be taken as an item.
- Taking a shulker box takes the whole box, including its contents. Taking its last content item leaves the empty shulker in place.
- Ender chests, trapped chests, barrels, hoppers and inventories from other mods are not included.
- The network is local: the connector checks its four sides. There is no remote access, channel system or wireless range.
- Before every extraction, the server re-checks the connector, the display, the chest, the slot and the container path. If something changed or disconnected, the operation is refused and the list is refreshed.
- A listing is capped at 3000 entries; the screen warns when the cap is reached.

## Known limitations for 0.1.1

- No deposits from the catalog. Open the chosen chest to store items.
- No organizer, category filters or item-location column yet. The origin side is shown on hover.
- No support for modded inventories or additional vanilla containers.
- Search rescans the connected chests; very large networks may need an index in a later version.

## Development

The project targets Java 21 and uses Gradle with Fabric Loom.

```powershell
.\gradlew.bat build
```

The release JAR and its sources are written to `build/libs/`. Run the development client with:

```powershell
.\gradlew.bat runClient
```

See [TODO.md](TODO.md) for the work route and [RELEASE_PLAN.md](RELEASE_PLAN.md) for the release scope and criteria. The 0.1 changes are summarized in [CHANGELOG.md](CHANGELOG.md).
