package com.bitqba.item;

import com.bitqba.ArcanumStorageMod;
import com.bitqba.block.ModBlocks;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public final class ModItems {
    public static final Item END_POUCH = register("end_pouch", EndPouchItem::new);
    public static final Item FIELD_LEDGER = register("field_ledger", FieldLedgerItem::new);
    public static final Item ENDER_DISPLAY = register("ender_display", EnderDisplayItem::new);

    private ModItems() {}

    private static Item register(String name, Function<Item.Settings, Item> factory) {
        Identifier id = Identifier.of(ArcanumStorageMod.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item.Settings settings = new Item.Settings().registryKey(key).maxCount(1);
        return Registry.register(Registries.ITEM, key, factory.apply(settings));
    }

    public static void register() {
        // Static registration above; this method exists to make the call order explicit.
    }
}
