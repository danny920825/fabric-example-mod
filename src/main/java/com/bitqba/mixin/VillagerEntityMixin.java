package com.bitqba.mixin;

import com.bitqba.enchantment.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * Adds the Arcanum Binding enchanted book to master librarians.
 *
 * <p>Vanilla only offers the enchantments in its trade tags, and its price
 * formula would never reach a full stack of emeralds. This hook adds a dedicated,
 * low-probability offer: one enchanted book for 64 emeralds and one book.</p>
 */
@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {
    private static final float ARCANUM_BINDING_TRADE_CHANCE = 0.10F;

    /** Guards against {@code getOffers()} re-entering {@code fillRecipes} on its first call. */
    private boolean arcanum$fillingRecipes;

    @Inject(method = "fillRecipes", at = @At("TAIL"))
    private void arcanum$addBindingTrade(CallbackInfo ci) {
        if (this.arcanum$fillingRecipes) return;
        this.arcanum$fillingRecipes = true;

        try {
            arcanum$addBindingTradeNow();
        } finally {
            this.arcanum$fillingRecipes = false;
        }
    }

    private void arcanum$addBindingTradeNow() {
        VillagerEntity self = (VillagerEntity) (Object) this;

        if (!self.getVillagerData().profession().matchesKey(VillagerProfession.LIBRARIAN)) return;
        if (self.getVillagerData().level() < 5) return;

        TradeOfferList offers = self.getOffers();
        for (TradeOffer offer : offers) {
            if (ModEnchantments.hasArcanumBinding(offer.getSellItem())) return;
        }

        if (self.getRandom().nextFloat() >= ARCANUM_BINDING_TRADE_CHANCE) return;

        Registry<Enchantment> enchantments = self.getEntityWorld()
                .getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT);
        RegistryEntry.Reference<Enchantment> binding = enchantments.getOrThrow(ModEnchantments.ARCANUM_BINDING);
        ItemStack book = EnchantmentHelper.getEnchantedBookWith(new EnchantmentLevelEntry(binding, 1));

        offers.add(new TradeOffer(
                new TradedItem(Items.EMERALD, 64),
                Optional.of(new TradedItem(Items.BOOK)),
                book,
                1,   // max uses: a single book per restock
                30,  // merchant experience
                0.0F // fixed price, no demand-based multiplier
        ));
    }
}
