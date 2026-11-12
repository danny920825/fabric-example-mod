package com.bitqba.client;

import com.bitqba.network.StorageSearchPayload;
import com.bitqba.screen.StorageScreenHandler;
import com.bitqba.storage.StorageNetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

public class StorageDisplayScreen extends HandledScreen<StorageScreenHandler> {
    private static final Identifier TEXTURE =
            Identifier.of("arcanum_storage", "textures/gui/storage_display.png");

    private static final int CATALOG_X = 8;
    private static final int CATALOG_Y = 33;
    private static final int COLUMNS = 9;
    private static final int ROWS = 5;

    private static final int GOLD = 0xFFD6A64B;
    private static final int GOLD_BRIGHT = 0xFFE9D18D;
    private static final int TEXT_MAIN = 0xFFF2F0E6;
    private static final int TEXT_DIM = 0xFF9FB0B8;
    private static final int TEXT_WARN = 0xFFE0A33C;
    private static final int TEXT_ERROR = 0xFFE0705C;
    private static final int DOT_OFF = 0xFFB4473C;

    private TextFieldWidget searchField;
    private ButtonWidget sortButton;
    private String lastQuery = "";
    private int lastSort = -1;

    public StorageDisplayScreen(StorageScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 176;
        backgroundHeight = 238;
    }

    @Override
    protected void init() {
        super.init();

        searchField = new TextFieldWidget(textRenderer, x + 11, y + 19, 137, 10,
                Text.translatable("gui.arcanum_storage.search"));
        searchField.setMaxLength(64);
        searchField.setDrawsBackground(false);
        searchField.setTextShadow(false);
        searchField.setPlaceholder(Text.translatable("gui.arcanum_storage.search"));
        searchField.setText(lastQuery);
        searchField.setChangedListener(query -> {
            lastQuery = query;
            ClientPlayNetworking.send(new StorageSearchPayload(query));
        });
        addDrawableChild(searchField);

        addDrawableChild(ButtonWidget.builder(Text.literal("x"), button -> {
                    searchField.setText("");
                    searchField.setFocused(true);
                })
                .dimensions(x + 152, y + 17, 16, 12)
                .tooltip(Tooltip.of(Text.translatable("gui.arcanum_storage.clear_search")))
                .build());

        addDrawableChild(ButtonWidget.builder(Text.literal("\u2039"), button -> clickButton(StorageScreenHandler.BUTTON_PREVIOUS_PAGE))
                .dimensions(x + 7, y + 126, 20, 16)
                .tooltip(Tooltip.of(Text.translatable("gui.arcanum_storage.previous_page")))
                .build());

        sortButton = addDrawableChild(ButtonWidget.builder(Text.empty(), button -> clickButton(StorageScreenHandler.BUTTON_SORT))
                .dimensions(x + 31, y + 126, 38, 16)
                .tooltip(Tooltip.of(Text.translatable("gui.arcanum_storage.sort_hint")))
                .build());
        updateSortLabel(true);

        addDrawableChild(ButtonWidget.builder(Text.literal("\u203A"), button -> clickButton(StorageScreenHandler.BUTTON_NEXT_PAGE))
                .dimensions(x + 149, y + 126, 20, 16)
                .tooltip(Tooltip.of(Text.translatable("gui.arcanum_storage.next_page")))
                .build());
    }

    private void clickButton(int id) {
        if (client != null && client.interactionManager != null) {
            client.interactionManager.clickButton(handler.syncId, id);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        // Vanilla 1.21.9 no longer draws slot tooltips from HandledScreen#render,
        // so screens that want them must ask for them explicitly.
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F,
                backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        updateSortLabel(false);

        context.drawText(textRenderer, title, 10, 6, GOLD_BRIGHT, true);
        drawConnectionChip(context);
        drawStatus(context, mouseX, mouseY);

        Text page = Text.translatable("gui.arcanum_storage.page",
                handler.getPage() + 1, handler.getPageCount());
        context.drawCenteredTextWithShadow(textRenderer, page, 109, 130, TEXT_MAIN);
    }

    private void drawConnectionChip(DrawContext context) {
        int connections = handler.getConnectionCount();
        int dotColor;
        if (connections > 0) {
            double wave = (Math.sin(Util.getMeasuringTimeMs() / 420.0) + 1.0) / 2.0;
            int alpha = (int) (150 + wave * 105);
            dotColor = (alpha << 24) | (GOLD & 0xFFFFFF);
        } else {
            dotColor = DOT_OFF;
        }
        context.fill(150, 8, 154, 12, dotColor);

        Text count = Text.literal(Integer.toString(connections));
        context.drawText(textRenderer, count, 157, 6, connections > 0 ? TEXT_MAIN : TEXT_ERROR, true);
    }

    private void drawStatus(DrawContext context, int mouseX, int mouseY) {
        Text origin = hoveredOrigin(mouseX, mouseY);
        Text message;
        int color;
        if (origin != null) {
            message = origin;
            color = GOLD_BRIGHT;
        } else if (handler.getConnectionCount() == 0) {
            message = Text.translatable("gui.arcanum_storage.empty.no_connections");
            color = TEXT_ERROR;
        } else if (handler.isTruncated()) {
            message = Text.translatable("gui.arcanum_storage.truncated", StorageNetwork.MAX_ENTRIES);
            color = TEXT_WARN;
        } else if (handler.isDepthLimited()) {
            message = Text.translatable("gui.arcanum_storage.depth_limited");
            color = TEXT_WARN;
        } else if (handler.getEntryCount() == 0) {
            String query = searchField == null ? "" : searchField.getText();
            message = query.isBlank()
                    ? Text.translatable("gui.arcanum_storage.empty.no_items")
                    : Text.translatable("gui.arcanum_storage.empty.no_results");
            color = TEXT_DIM;
        } else {
            message = Text.translatable("gui.arcanum_storage.extract_only");
            color = GOLD;
        }
        context.drawCenteredTextWithShadow(textRenderer, message, backgroundWidth / 2, 145, color);
    }

    /** Shows where the hovered catalog stack comes from instead of the generic status line. */
    private Text hoveredOrigin(int mouseX, int mouseY) {
        int localX = mouseX - x;
        int localY = mouseY - y;
        if (localX < CATALOG_X || localY < CATALOG_Y
                || localX >= CATALOG_X + COLUMNS * 18 || localY >= CATALOG_Y + ROWS * 18) {
            return null;
        }
        int offsetX = localX - CATALOG_X;
        int offsetY = localY - CATALOG_Y;
        if (offsetX % 18 >= 16 || offsetY % 18 >= 16) return null;

        int slot = (offsetY / 18) * COLUMNS + (offsetX / 18);
        int code = handler.getOriginCode(slot);
        if (code <= 0) return null;
        return handler.originText(code);
    }

    private void updateSortLabel(boolean force) {
        if (sortButton == null) return;
        int mode = handler.getSortMode().ordinal();
        if (!force && mode == lastSort) return;
        lastSort = mode;
        Text label = switch (handler.getSortMode()) {
            case NAME -> Text.translatable("gui.arcanum_storage.sort.name");
            case COUNT -> Text.translatable("gui.arcanum_storage.sort.count");
            case NONE -> Text.translatable("gui.arcanum_storage.sort.order");
        };
        sortButton.setMessage(label);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        boolean overCatalog = mouseX >= x + 6 && mouseX < x + 170 && mouseY >= y + 31 && mouseY < y + 123;
        if (overCatalog && verticalAmount != 0) {
            clickButton(verticalAmount > 0 ? StorageScreenHandler.BUTTON_PREVIOUS_PAGE
                    : StorageScreenHandler.BUTTON_NEXT_PAGE);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (searchField != null && searchField.isFocused()) {
            if (input.isEscape()) return super.keyPressed(input);
            if (searchField.keyPressed(input)) return true;
            if (client != null && client.options.inventoryKey.matchesKey(input)) return true;
        } else {
            switch (input.key()) {
                case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_PAGE_UP -> {
                    clickButton(StorageScreenHandler.BUTTON_PREVIOUS_PAGE);
                    return true;
                }
                case GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_PAGE_DOWN -> {
                    clickButton(StorageScreenHandler.BUTTON_NEXT_PAGE);
                    return true;
                }
                default -> { }
            }
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (searchField == null) return super.charTyped(input);
        if (!searchField.isFocused()) {
            searchField.setFocused(true);
        }
        return searchField.charTyped(input) || super.charTyped(input);
    }
}
