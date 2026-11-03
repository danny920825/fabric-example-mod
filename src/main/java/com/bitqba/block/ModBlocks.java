package com.bitqba.block;

import com.bitqba.StorageDisplayMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    public static final Block STORAGE_CONNECTOR = register("storage_connector", new StorageConnectorBlock(connectorSettings()));
    public static final Block STORAGE_DISPLAY = register("storage_display", new StorageDisplayBlock(settings("storage_display", 3.0f)));

    private ModBlocks() {}

    private static AbstractBlock.Settings settings(String name, float hardness) {
        Identifier id = Identifier.of(StorageDisplayMod.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        return AbstractBlock.Settings.create().registryKey(key).strength(hardness).requiresTool();
    }

    private static AbstractBlock.Settings connectorSettings() {
        Identifier id = Identifier.of(StorageDisplayMod.MOD_ID, "storage_connector");
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        return AbstractBlock.Settings.create().registryKey(key).strength(4.0f).requiresTool()
                .luminance(state -> state.get(StorageConnectorBlock.SCREEN_ATTACHED) ? 12 : 0);
    }

    private static Block register(String name, Block block) {
        Identifier id = Identifier.of(StorageDisplayMod.MOD_ID, name);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Registry.register(Registries.BLOCK, blockKey, block);
        Registry.register(Registries.ITEM, itemKey, new BlockItem(block, new Item.Settings().registryKey(itemKey)));
        return block;
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.add(STORAGE_CONNECTOR);
            entries.add(STORAGE_DISPLAY);
        });
        StorageDisplayMod.LOGGER.info("Storage display blocks registered");
    }
}
