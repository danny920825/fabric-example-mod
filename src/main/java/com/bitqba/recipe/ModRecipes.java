package com.bitqba.recipe;

import com.bitqba.ArcanumStorageMod;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModRecipes {
    /** Special recipe: the End Pouch must be completely unenchanted. */
    public static final RecipeSerializer<EnderDisplayRecipe> ENDER_DISPLAY = Registry.register(
            Registries.RECIPE_SERIALIZER,
            Identifier.of(ArcanumStorageMod.MOD_ID, "ender_display"),
            new SpecialCraftingRecipe.SpecialRecipeSerializer<>(EnderDisplayRecipe::new));

    private ModRecipes() {}

    public static void register() {
        ArcanumStorageMod.LOGGER.info("Special recipes registered");
    }
}
