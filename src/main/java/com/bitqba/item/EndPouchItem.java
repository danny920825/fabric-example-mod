package com.bitqba.item;

import com.bitqba.enchantment.ModEnchantments;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import java.util.function.Consumer;

/**
 * A portable door to the player's ender chest.
 *
 * <p>Right-click while holding it, or right-click it in the player inventory, to
 * open the same 27 slots as a vanilla Ender Chest. The pouch is never consumed by
 * normal use; only the Arcanum Binding death cache consumes it.</p>
 */
public class EndPouchItem extends Item {
    public EndPouchItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            openEnderChest(user);
        }
        return ActionResult.SUCCESS;
    }

    /**
     * Opens the pouch when it is right-clicked in the player inventory, mirroring
     * how bundles respond to inventory clicks. Only empty-cursor right clicks on
     * a slot owned by the player are claimed, so normal inventory clicks still work.
     */
    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType,
            PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT || !otherStack.isEmpty()) return false;
        if (!(slot.inventory instanceof PlayerInventory)) return false;
        if (!player.getEntityWorld().isClient()) {
            openEnderChest(player);
        }
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent,
            Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.arcanum_storage.end_pouch.tooltip").formatted(Formatting.GRAY));
        if (ModEnchantments.hasArcanumBinding(stack)) {
            textConsumer.accept(Text.translatable("item.arcanum_storage.end_pouch.bound")
                    .formatted(Formatting.GOLD));
        }
    }

    private static void openEnderChest(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        EnderChestInventory inventory = player.getEnderChestInventory();
        serverPlayer.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, playerEntity) ->
                        GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, inventory),
                Text.translatable("container.enderchest")));
    }
}
