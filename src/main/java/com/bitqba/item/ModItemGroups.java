package com.bitqba.item;

import com.bitqba.ArcanumStorageMod;
import com.bitqba.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** The mod's own creative tab, kept out of the Redstone tab. */
public final class ModItemGroups {
    public static final RegistryKey<ItemGroup> STORAGE = RegistryKey.of(
            RegistryKeys.ITEM_GROUP, Identifier.of(ArcanumStorageMod.MOD_ID, "storage"));

    private ModItemGroups() {}

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, STORAGE, FabricItemGroup.builder()
                // The Storage Display is the face of the mod: it is the terminal the
                // player sees and the block that opens the catalog.
                .displayName(Text.translatable("itemGroup.arcanum_storage"))
                .icon(() -> new ItemStack(ModBlocks.STORAGE_DISPLAY))
                .build());

        ItemGroupEvents.modifyEntriesEvent(STORAGE).register(entries -> {
            entries.add(ModBlocks.STORAGE_DISPLAY);
            entries.add(ModItems.ENDER_DISPLAY);
            entries.add(ModBlocks.STORAGE_CONNECTOR);
            entries.add(ModBlocks.ARCHIVE_CABLE);
            entries.add(ModBlocks.ARCHIVE_CONNECTOR);
            entries.add(ModItems.END_POUCH);
            entries.add(ModItems.FIELD_LEDGER);
        });
    }
}
