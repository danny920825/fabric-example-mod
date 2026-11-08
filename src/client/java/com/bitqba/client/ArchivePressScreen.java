package com.bitqba.client;

import com.bitqba.screen.ArchivePressScreenHandler;
import com.bitqba.storage.ShulkerPress;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ArchivePressScreen extends HandledScreen<ArchivePressScreenHandler> {
    private static final Identifier TEXTURE =
            Identifier.of("arcanum_storage", "textures/gui/storage_press.png");

    private static final int GOLD = 0xFFD6A64B;
    private static final int GOLD_BRIGHT = 0xFFE9D18D;
    private static final int TEXT_MAIN = 0xFFF2F0E6;
    private static final int TEXT_DIM = 0xFF9FB0B8;
    private static final int TEXT_WARN = 0xFFE0A33C;
    private static final int TEXT_ERROR = 0xFFE0705C;
    private static final int TEXT_OK = 0xFF8FD18A;

    private static final int INFO_X = 12;
    private static final int STATUS_X = 12;
    private static final int STATUS_Y = 122;
    private static final int STATUS_WIDTH = 152;

    private ButtonWidget analyzeButton;
    private ButtonWidget organizeButton;
    private boolean confirmPending;
    private ShulkerPress.Status lastStatus;

    public ArchivePressScreen(ArchivePressScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 176;
        backgroundHeight = 184;
    }

    @Override
    protected void init() {
        super.init();
        confirmPending = false;
        lastStatus = null;

        analyzeButton = addDrawableChild(ButtonWidget.builder(
                        Text.translatable("gui.arcanum_storage.press.analyze"), button -> {
                            confirmPending = false;
                            clickButton(ArchivePressScreenHandler.BUTTON_ANALYZE);
                        })
                .dimensions(x + 10, y + 146, 76, 18)
                .tooltip(Tooltip.of(Text.translatable("gui.arcanum_storage.press.analyze_hint")))
                .build());

        organizeButton = addDrawableChild(ButtonWidget.builder(
                        Text.translatable("gui.arcanum_storage.press.organize"), button -> onOrganize())
                .dimensions(x + 90, y + 146, 76, 18)
                .tooltip(Tooltip.of(Text.translatable("gui.arcanum_storage.press.organize_hint")))
                .build());
        organizeButton.active = false;
    }

    private void onOrganize() {
        if (!handler.canApply()) return;
        if (!confirmPending) {
            confirmPending = true;
            return;
        }
        confirmPending = false;
        clickButton(ArchivePressScreenHandler.BUTTON_ORGANIZE);
    }

    private void clickButton(int id) {
        if (client != null && client.interactionManager != null) {
            client.interactionManager.clickButton(handler.syncId, id);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        updateButtons();
        super.render(context, mouseX, mouseY, deltaTicks);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void updateButtons() {
        if (analyzeButton == null || organizeButton == null) return;

        ShulkerPress.Status status = handler.getStatus();
        if (status != lastStatus) {
            lastStatus = status;
            confirmPending = false;
        }

        boolean canApply = handler.canApply();
        if (!canApply) confirmPending = false;

        analyzeButton.active = handler.canAnalyze();
        organizeButton.active = canApply;
        organizeButton.setMessage(confirmPending
                ? Text.translatable("gui.arcanum_storage.press.confirm")
                : Text.translatable("gui.arcanum_storage.press.organize"));
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F,
                backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, title, 10, 6, GOLD_BRIGHT, true);

        if (handler.isLinked()) {
            context.drawText(textRenderer, chestText(), INFO_X, 40, GOLD, true);
            context.drawText(textRenderer,
                    Text.translatable("gui.arcanum_storage.press.shulkers",
                            handler.getEligible(), handler.getIgnored()),
                    INFO_X, 50, TEXT_MAIN, true);

            context.drawText(textRenderer,
                    Text.translatable("gui.arcanum_storage.press.stacks",
                            handler.getStacksBefore(), handler.getStacksAfter()),
                    INFO_X, 68, TEXT_MAIN, true);
            context.drawText(textRenderer,
                    Text.translatable("gui.arcanum_storage.press.groups", handler.getGroups()),
                    INFO_X, 78, TEXT_MAIN, true);

            context.drawText(textRenderer,
                    Text.translatable("gui.arcanum_storage.press.empty",
                            handler.getEmptyBefore(), handler.getEmptyAfter()),
                    INFO_X, 96, TEXT_MAIN, true);
            context.drawText(textRenderer, freedText(), INFO_X, 106, freedColor(), true);
        }

        ShulkerPress.Status status = handler.getStatus();
        context.drawWrappedTextWithShadow(textRenderer, statusText(status), STATUS_X, STATUS_Y,
                STATUS_WIDTH, statusColor(status));

        Text footer = confirmPending
                ? Text.translatable("gui.arcanum_storage.press.confirm_hint")
                : Text.translatable("gui.arcanum_storage.press.footer");
        context.drawCenteredTextWithShadow(textRenderer, footer, backgroundWidth / 2, 170,
                confirmPending ? GOLD_BRIGHT : TEXT_DIM);
    }

    private Text chestText() {
        return handler.isLinked()
                ? Text.translatable("gui.arcanum_storage.press.chest_linked")
                : Text.translatable("gui.arcanum_storage.press.chest_none");
    }

    private Text freedText() {
        if (handler.getStatus() == ShulkerPress.Status.APPLIED) {
            return Text.translatable("gui.arcanum_storage.press.freed_result", handler.getEmptied());
        }
        if (handler.getStatus() == ShulkerPress.Status.READY && handler.getEmptyAfter() == 0) {
            return Text.translatable("gui.arcanum_storage.press.no_free");
        }
        return Text.translatable("gui.arcanum_storage.press.freed_preview", handler.getEmptied());
    }

    private int freedColor() {
        if (handler.getStatus() == ShulkerPress.Status.READY && handler.getEmptyAfter() == 0) {
            return TEXT_WARN;
        }
        return handler.getEmptied() > 0 ? TEXT_OK : TEXT_DIM;
    }

    private Text statusText(ShulkerPress.Status status) {
        if (status == ShulkerPress.Status.READY && !handler.canApply()) {
            return Text.translatable("gui.arcanum_storage.press.status.not_analyzed");
        }
        return switch (status) {
            case NO_NETWORK -> Text.translatable("gui.arcanum_storage.press.status.no_network");
            case NO_CHEST -> Text.translatable("gui.arcanum_storage.press.status.no_chest");
            case MULTIPLE_CHESTS -> Text.translatable("gui.arcanum_storage.press.status.multiple_chests");
            case NO_SHULKERS -> Text.translatable("gui.arcanum_storage.press.status.no_shulkers");
            case ALL_EMPTY -> Text.translatable("gui.arcanum_storage.press.status.all_empty");
            case ALREADY_ORGANIZED -> Text.translatable("gui.arcanum_storage.press.status.already");
            case READY -> Text.translatable("gui.arcanum_storage.press.status.ready");
            case APPLIED -> Text.translatable("gui.arcanum_storage.press.status.applied");
            case CHANGED -> Text.translatable("gui.arcanum_storage.press.status.changed");
            case FAILED -> Text.translatable("gui.arcanum_storage.press.status.failed");
        };
    }

    private int statusColor(ShulkerPress.Status status) {
        return switch (status) {
            case READY -> handler.canApply() ? TEXT_OK : TEXT_DIM;
            case APPLIED -> TEXT_OK;
            case NO_NETWORK, NO_CHEST, MULTIPLE_CHESTS, CHANGED, FAILED -> TEXT_ERROR;
            case NO_SHULKERS, ALL_EMPTY, ALREADY_ORGANIZED -> TEXT_WARN;
        };
    }
}
