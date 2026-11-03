package com.bitqba.client;

import com.bitqba.network.StorageSearchPayload;
import com.bitqba.screen.StorageScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class StorageDisplayScreen extends HandledScreen<StorageScreenHandler> {
    private TextFieldWidget searchField;
    private String lastQuery = "";

    public StorageDisplayScreen(StorageScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 176;
        backgroundHeight = 266;
        playerInventoryTitleY = 178;
    }

    @Override
    protected void init() {
        super.init();
        searchField = new TextFieldWidget(textRenderer, x + 8, y + 16, 160, 14,
                Text.translatable("gui.storage_display.search"));
        searchField.setMaxLength(64);
        searchField.setPlaceholder(Text.translatable("gui.storage_display.search"));
        searchField.setText(lastQuery);
        searchField.setChangedListener(query -> {
            lastQuery = query;
            ClientPlayNetworking.send(new StorageSearchPayload(query));
        });
        addDrawableChild(searchField);

        addDrawableChild(ButtonWidget.builder(Text.literal("‹"), button -> {
            if (client != null && client.interactionManager != null) {
                client.interactionManager.clickButton(handler.syncId, 0);
            }
        }).dimensions(x + 8, y + 145, 22, 16).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("›"), button -> {
            if (client != null && client.interactionManager != null) {
                client.interactionManager.clickButton(handler.syncId, 1);
            }
        }).dimensions(x + 146, y + 145, 22, 16).build());
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.fill(x, y, x + backgroundWidth, y + backgroundHeight, 0xFF20242A);
        context.fill(x + 4, y + 2, x + backgroundWidth - 4, y + 31, 0xFF111519);
        context.fill(x + 4, y + 33, x + backgroundWidth - 4, y + 144, 0xFF30363D);
        context.fill(x + 4, y + 174, x + backgroundWidth - 4, y + backgroundHeight - 4, 0xFF30363D);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, title, 8, 5, 0xFFF2F2F2, false);

        Text connections = Text.translatable("gui.storage_display.connections", handler.getConnectionCount());
        context.drawText(textRenderer, connections, 34, 149, 0xFFA8DADC, false);

        String page = (handler.getPage() + 1) + " / " + handler.getPageCount();
        context.drawText(textRenderer, page, backgroundWidth - 34 - textRenderer.getWidth(page), 149,
                0xFFFFFFFF, false);

        Text message = statusMessage();
        context.drawCenteredTextWithShadow(textRenderer, message, backgroundWidth / 2, 165, 0xFFFFD166);

        context.drawText(textRenderer, Text.translatable("container.inventory"), 8, playerInventoryTitleY,
                0xFFE0E0E0, false);
    }

    private Text statusMessage() {
        if (handler.getConnectionCount() == 0) {
            return Text.translatable("gui.storage_display.empty.no_connections");
        }
        String query = searchField == null ? "" : searchField.getText();
        if (handler.getEntryCount() == 0) {
            return query.isBlank()
                    ? Text.translatable("gui.storage_display.empty.no_items")
                    : Text.translatable("gui.storage_display.empty.no_results");
        }
        if (handler.isDepthLimited()) {
            return Text.translatable("gui.storage_display.depth_limited");
        }
        return Text.translatable("gui.storage_display.extract_only");
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (searchField != null && searchField.isFocused()) {
            if (input.key() == GLFW.GLFW_KEY_ESCAPE) return super.keyPressed(input);
            if (searchField.keyPressed(input)) return true;
            if (client != null && client.options.inventoryKey.matchesKey(input)) return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (searchField != null && searchField.isFocused()) return searchField.charTyped(input);
        return super.charTyped(input);
    }
}
