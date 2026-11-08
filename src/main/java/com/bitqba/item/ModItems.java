package com.bitqba.item;

import com.bitqba.ArcanumStorageMod;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModItems {
    public static final Item END_POUCH = register("end_pouch");

    private ModItems() {}

    private static Item register(String name) {
        Identifier id = Identifier.of(ArcanumStorageMod.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        return Registry.register(Registries.ITEM, key,
                new EndPouchItem(new Item.Settings().registryKey(key).maxCount(1)));
    }

    public static void register() {
        // Static registration above; this method exists to make the call order explicit.
    }
}
