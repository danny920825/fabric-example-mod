package com.bitqba.item;

import com.bitqba.ledger.LedgerService;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * The Archivist's Field Ledger. It is a personal reader, not a container: the
 * knowledge lives in the player, so the item can be shared or replaced without
 * losing a single page.
 */
public class FieldLedgerItem extends Item {
    public FieldLedgerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && user instanceof ServerPlayerEntity player) {
            LedgerService.openFromItem(player);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context,
            TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.arcanum_storage.field_ledger.tooltip")
                .formatted(Formatting.GRAY));
    }
}
