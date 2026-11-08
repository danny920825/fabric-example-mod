# Arcanum Storage

Arcanum Storage is a Fabric mod for **Minecraft Java 1.21.9**. It adds a physical storage terminal that searches the contents of up to twenty vanilla chests across a cable network, including the shulker boxes stored inside them, and an Archive Connector that organizes unlabeled shulker boxes with the Shulker Press. It also adds the End Pouch, a portable door to the player's ender chest, and the Arcanum Binding enchantment that turns the pouch into a one-use death cache for the player's combat gear.

**The catalog is extraction-only.** It lets you search and take items out. It never accepts items: to store something, open the specific chest and place the item there. This keeps every deposit unambiguous and avoids the terminal silently choosing a chest or shulker for you.

All the crafting tables above are for version 0.2.1.

## Requirements

- Minecraft Java 1.21.9
- Fabric Loader 0.19.2 or newer
- Fabric API 0.134.1+1.21.9 or a compatible build
- Java 21

## Installation

1. Install Fabric Loader for Minecraft 1.21.9.
2. Put [Fabric API](https://modrinth.com/mod/fabric-api) in the `mods` folder.
3. Put the Arcanum Storage JAR (`arcanum-storage-0.2.1.jar`) in the same `mods` folder.
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

- **Archive Connector:** one copper ingot, one redstone dust, one shulker shell and one piston.

  ```
  C R C
  R S R
  C P C
  ```

  `C` = copper ingot, `R` = redstone dust, `S` = shulker shell, `P` = piston. Since 0.2.1 this connector is joined to the cable network with an **Archive Cable** instead of sitting against a chest directly, and it opens the Shulker Press.

- **Archive Cable:** two copper ingots and one redstone dust.

  ```
  C R C
  ```

  `C` = copper ingot, `R` = redstone dust. Yields four cables.

- **End Pouch:** one ender chest, two chorus fruits, two leather, two string, two ender eyes and one shulker shell.

  ```
  L E L
  C H C
  S Y S
  ```

  `L` = leather, `E` = ender chest, `C` = chorus fruit, `H` = shulker shell, `S` = string, `Y` = ender eye.

  This is the most expensive recipe in the mod. The chorus fruit and the shulker shell only grow inside the outer islands of the End, so the pouch can only be crafted after the dragon has been defeated. The pouch opens the player's ender chest.

- **Storage Display:** four gold ingots, four iron ingots, one diamond and two Ender Eyes.

  ```
  G I G
  E D E
  G I G
  ```

  `G` = gold ingot, `I` = iron ingot, `E` = Ender Eye, `D` = diamond.

### Building the network

The **Storage Connector** is the root of the network and the **Storage Display** always sits on its top face. The other five faces (north, south, east, west and below) can feed a cable branch:

1. Place the **Storage Connector**.
2. Place the **Storage Display** directly on top of it.
3. Run **Archive Cables** out of any of the other five faces. Cables connect to each other, to the connector and to chests, so you can route them in any shape, including loops and corners. The amount of cable is unlimited.
4. Place a **vanilla chest** so that it touches any cable connected to the connector. Single and double chests both work, and a double chest counts as one chest.

Each of the five root faces supports up to **four chests**, for a total of **20 chests per network**. Extra chests touching a full branch are ignored; the connector's screen shows the number of chests that are actually linked.

The network is local to its cable: there is no remote access, channel system or wireless range. The cables carry no items and have no redstone behavior. Right-click the **Storage Display** to open the catalog.

The **Archive Connector** joins the cable network through an **Archive Cable** instead of sitting against a chest directly. The press only processes the network when it holds **exactly one chest** (a double chest counts as one). If the network has more than one chest, the Shulker Press interface turns off and Analyze/Organize are disabled.

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

### Organizing with the Shulker Press

Right-click the **Archive Connector** to open the Shulker Press. The press only works on unlabeled shulker boxes stored in the single chest of the cable network.

1. Press **Analyze** to build a preview. The summary shows the eligible shulkers, the ignored labeled ones, the stacks before and after regrouping, the number of groups and the shulkers that will be freed.
2. Press **Organize**. The button turns into **Confirm** for a second click, so nothing moves before you confirm.
3. The server revalidates the chest snapshot, writes the new distribution and frees the empty shulkers. If the chest changed in between, nothing is moved.

The press groups items with the same type and components, starting with the group that has the largest total count, and redistributes the stacks into the shulkers. Shulkers with a custom name or lore are ignored completely, loose items and nested shulkers are not touched.

The Shulker Press is a tool for organizing one chest at a time; it does not organize whole networks yet.

### Creative tab

All the mod blocks and the End Pouch live in their own creative tab, **Arcanum Storage**, instead of the Redstone tab. The tab icon is the **Storage Display**, the face of the mod.

### Using the ender chest

Open the End Pouch by right-clicking it while holding it, or by right-clicking its slot in the player inventory. It opens the same 27-slot ender chest the player already has, without placing any block. The pouch is not consumed by normal use; only the Arcanum Binding death cache consumes it. It is only craftable after the dragon has been defeated (see the End Pouch recipe above).

### Arcanum Binding

**Arcanum Binding** is a rare enchantment made only for the End Pouch. It turns the pouch into a **one-use death cache**: when you die, the pouch is consumed and seals your combat gear in an **Archive Cache**.

- It cannot be rolled at an enchanting table.
- Applying an enchanted book to an End Pouch at an anvil costs exactly **31 experience levels**, one level above the enchanting table's maximum of 30.
- **Master librarians** (level 5) may sell the enchanted book for **64 emeralds and 1 book**. The offer is rare (about a 10% chance when a librarian reaches master level), has a single use per restock and always costs the same.
- On death, if the pouch is directly in the inventory, the pouch is destroyed and these items are sealed into the cache: **armor, hotbar and offhand** (up to 14 stacks). The **27 backpack slots** (the rest of the main inventory) drop normally at the death point.
- The **Archive Cache** is a named shulker box (`Archive Cache`, lore `Sealed by the Arcanum Binding. It vanishes when emptied.`). It is placed in the player's **ender chest**. If the ender chest is full, the cache goes to the respawned player's inventory; if that is also full, it drops at the respawn point.
- The cache is a normal shulker box. Open it and re-equip the items; it does not restore the gear automatically. When you take its last item and close it, the placed cache disappears, so empty labeled shulkers never pile up. The cache item itself can be carried or stored until you place it.
- With `keepInventory` on no penalty applies: the pouch and the gear are kept and nothing is consumed.
- An unenchanted pouch, and a bound pouch with nothing to seal, drop normally. Only one bound pouch activates the cache per death; any other pouch drops normally.
- Items with Curse of Vanishing still vanish and are never sealed.
- The pouch must be directly in the inventory. A pouch stored inside a shulker box is not detected; the whole shulker is treated as a normal item.
- The pouch itself is not consumed while the enchantment is idle and can still be used normally.

### Tools to break the blocks

- The **Storage Connector**, **Storage Display** and **Archive Connector** are pickaxe blocks. Breaking them without a pickaxe is very slow and drops nothing.
- The Storage Connector and Storage Display can be mined with any pickaxe.
- The **Archive Cable** is soft: it can be broken by hand and always drops itself.
- The **Archive Connector** has iron-block hardness (5.0) and requires a **diamond pickaxe or better** (netherite works too). Silk Touch is **not** required; a normal pickaxe drops the block.

## Supported storage and limits

- Vanilla **single and double chests** only.
- **Shulker boxes stored inside those chests** are indexed recursively, up to two nested container levels. Contents deeper than that are not listed; the screen shows a warning and the containing shulker can still be taken as an item.
- Taking a shulker box takes the whole box, including its contents. Taking its last content item leaves the empty shulker in place.
- Ender chests, trapped chests, barrels, hoppers and inventories from other mods are not included.
- The network is local to the cable: the connector walks its five usable faces and every cable reachable from them. There is no remote access, channel system or wireless range.
- A network holds at most **20 chests**: four per lateral face plus four below. Cable length and cable count are unlimited.
- Before every extraction, the server re-checks the connector, the display, the chest, the slot and the container path. If something changed or disconnected, the operation is refused and the list is refreshed.
- A listing is capped at 3000 entries; the screen warns when the cap is reached.

## Known limitations for 0.2.1

- No deposits from the catalog. Open the chosen chest to store items.
- The Shulker Press only organizes one chest per connector and only unlabeled shulkers; it does not organize whole networks yet, and it turns off when the network holds more than one chest.
- No payment or tithe for linking chests yet; the cable network is free while the feature is being tested.
- The End Pouch opens the ender chest and is only craftable after the dragon has been defeated. The network itself is local and holds at most 20 chests.
- Arcanum Binding only triggers from a bound pouch directly in the inventory. A pouch stored inside a shulker box is not detected, and only one bound pouch activates the cache per death; any additional pouch drops normally.
- If the ender chest is full at the moment of death, the Archive Cache is held until the player respawns and is then placed in the respawn inventory (or dropped at the respawn point if that is also full). A server restart while the player is still dead discards this pending cache.
- No category filters or item-location column in the catalog yet. The origin face is shown on hover.
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
