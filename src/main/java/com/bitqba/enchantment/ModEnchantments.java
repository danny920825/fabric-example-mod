package com.bitqba.enchantment;

import com.bitqba.ArcanumStorageMod;
import com.bitqba.item.ModItems;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/**
 * The Arcanum Binding: a rare, librarian-only enchantment that binds the End Pouch,
 * so it cannot be lost when the player dies.
 *
 * <p>The enchantment itself is data-driven
 * ({@code data/arcanum_storage/enchantment/arcanum_binding.json}). This class only
 * exposes its registry key and the helpers used by the death and trading hooks.</p>
 */
public final class ModEnchantments {
    public static final RegistryKey<Enchantment> ARCANUM_BINDING = RegistryKey.of(
            RegistryKeys.ENCHANTMENT, Identifier.of(ArcanumStorageMod.MOD_ID, "arcanum_binding"));

    private ModEnchantments() {}

    /**
     * Returns whether the stack is an End Pouch carrying Arcanum Binding.
     */
    public static boolean isBoundPouch(ItemStack stack) {
        return !stack.isEmpty() && stack.isOf(ModItems.END_POUCH) && hasArcanumBinding(stack);
    }

    /**
     * Returns whether the stack carries Arcanum Binding, including enchanted books
     * (whose enchantments live in the stored-enchantments component).
     */
    public static boolean hasArcanumBinding(ItemStack stack) {
        if (stack.isEmpty()) return false;

        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
        for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
            if (entry.matchesKey(ARCANUM_BINDING)) return true;
        }
        return false;
    }
}
