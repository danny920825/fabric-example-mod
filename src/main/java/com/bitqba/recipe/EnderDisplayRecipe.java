package com.bitqba.recipe;

import com.bitqba.block.ModBlocks;
import com.bitqba.item.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

/**
 * Builds the Ender Display from an unenchanted End Pouch, a Storage Display,
 * an Archive Cable, a Storage Connector and an emerald block. A special recipe is
 * used because a normal ingredient cannot express "this pouch has no enchantments".
 */
public class EnderDisplayRecipe extends SpecialCraftingRecipe {
    public EnderDisplayRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        int pouch = 0;
        int display = 0;
        int cable = 0;
        int connector = 0;
        int emerald = 0;

        for (ItemStack stack : input.getStacks()) {
            if (stack.isEmpty()) continue;
            if (stack.getCount() != 1) return false;

            if (stack.isOf(ModItems.END_POUCH)) {
                if (!EnchantmentHelper.getEnchantments(stack).isEmpty()) return false;
                pouch++;
            } else if (stack.isOf(ModBlocks.STORAGE_DISPLAY.asItem())) {
                display++;
            } else if (stack.isOf(ModBlocks.ARCHIVE_CABLE.asItem())) {
                cable++;
            } else if (stack.isOf(ModBlocks.STORAGE_CONNECTOR.asItem())) {
                connector++;
            } else if (stack.isOf(Items.EMERALD_BLOCK)) {
                emerald++;
            } else {
                return false;
            }
        }

        return pouch == 1 && display == 1 && cable == 1 && connector == 1 && emerald == 1;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return new ItemStack(ModItems.ENDER_DISPLAY);
    }

    @Override
    public RecipeSerializer<? extends SpecialCraftingRecipe> getSerializer() {
        return ModRecipes.ENDER_DISPLAY;
    }
}
