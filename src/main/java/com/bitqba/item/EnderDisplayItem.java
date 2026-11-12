package com.bitqba.item;

import com.bitqba.ledger.LedgerService;
import com.bitqba.screen.StorageScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * The Ender Display. A portable item (not a block): right-click it in your hand
 * or in your inventory to browse your own ender chest and the shulker boxes
 * stored inside it. It is never placed in the world.
 */
public class EnderDisplayItem extends Item {
    public EnderDisplayItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            openEnderDisplay(user);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType,
            PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT || !otherStack.isEmpty()) return false;
        if (!(slot.inventory instanceof PlayerInventory)) return false;
        if (!player.getEntityWorld().isClient()) {
            openEnderDisplay(player);
        }
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent,
            Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.arcanum_storage.ender_display.tooltip")
                .formatted(Formatting.GRAY));
    }

    private static void openEnderDisplay(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        LedgerService.onEnderDisplayUsed(serverPlayer);
        BlockPos pos = player.getBlockPos();
        serverPlayer.openHandledScreen(new ExtendedScreenHandlerFactory<>() {
            @Override
            public StorageScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity playerEntity) {
                return new StorageScreenHandler(syncId, inventory, pos, true);
            }

            @Override
            public Text getDisplayName() {
                return Text.translatable("container.arcanum_storage.ender_display");
            }

            @Override
            public BlockPos getScreenOpeningData(ServerPlayerEntity ignored) {
                return pos;
            }
        });
    }
}
