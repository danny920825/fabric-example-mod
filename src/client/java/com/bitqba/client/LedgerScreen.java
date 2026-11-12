package com.bitqba.client;

import com.bitqba.ledger.LedgerView;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Two-page field ledger: the practical Manual on the left tab and the
 * historical Annals on the right tab. Visible text comes from language keys.
 */
public final class LedgerScreen extends Screen {
    private static final int INK = 0xFF3B2A16;
    private static final int INK_DIM = 0xFF6B5A42;
    private static final int GOLD_DARK = 0xFF8A6A2F;
    private static final int GREEN_DARK = 0xFF3F6B3A;
    private static final int RED_DARK = 0xFF8A3A2F;

    private final LedgerView view;
    private final int tab;
    private String chapterId;
    private String entryId;
    private String analId;
    private int page;

    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;
    private int listX;
    private int leftW;
    private int detailX;
    private int detailY;
    private int detailW;

    public LedgerScreen(LedgerView view) {
        this(view, 0, null, null, null, 0);
    }

    private LedgerScreen(LedgerView view, int tab, String chapterId, String entryId, String analId, int page) {
        super(Text.translatable("gui.arcanum_storage.ledger.title"));
        this.view = view;
        this.tab = tab == 1 ? 1 : 0;
        this.chapterId = chapterId;
        this.entryId = entryId;
        this.analId = analId;
        this.page = Math.max(0, page);
        resolveSelection();
    }

    private void resolveSelection() {
        if (view.chapters.isEmpty()) {
            chapterId = null;
            entryId = null;
        } else {
            LedgerView.Chapter chapter = findChapter(chapterId);
            if (chapter == null) chapter = view.chapters.get(0);
            chapterId = chapter.id;
            if (findEntry(chapter, entryId) == null) {
                entryId = chapter.entries.isEmpty() ? null : chapter.entries.get(0).id;
            }
        }
        if (view.anals.isEmpty()) {
            analId = null;
        } else if (findAnal(analId) == null) {
            analId = view.anals.get(0).id;
        }
    }

    @Override
    protected void init() {
        super.init();

        panelW = Math.min(width - 16, 430);
        panelH = Math.min(height - 16, 236);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
        listX = panelX + 10;
        leftW = Math.max(82, Math.min(126, panelW / 3));

        int tabY = panelY + 30;
        addDrawableChild(ButtonWidget.builder(
                        Text.translatable("gui.arcanum_storage.ledger.tab.manual"),
                        button -> switchTab(0))
                .dimensions(panelX + 10, tabY, 62, 16)
                .build());
        addDrawableChild(ButtonWidget.builder(
                        Text.translatable("gui.arcanum_storage.ledger.tab.anales"),
                        button -> switchTab(1))
                .dimensions(panelX + 76, tabY, 62, 16)
                .build());
        addDrawableChild(ButtonWidget.builder(
                        Text.translatable("gui.arcanum_storage.ledger.close"),
                        button -> close())
                .dimensions(panelX + panelW - 60, tabY, 50, 16)
                .build());

        if (tab == 0) {
            initChapters();
        }

        initList();
        initPageButtons();
    }

    private void initChapters() {
        int count = Math.min(4, view.chapters.size());
        if (count == 0) return;

        int gap = 4;
        int available = panelW - 20 - (count - 1) * gap;
        int buttonWidth = Math.max(48, Math.min(72, available / count));
        int chapterX = panelX + 10;

        for (int index = 0; index < count; index++) {
            LedgerView.Chapter chapter = view.chapters.get(index);
            addDrawableChild(ButtonWidget.builder(
                            Text.translatable(chapter.titleKey),
                            button -> switchChapter(chapter.id))
                    .dimensions(chapterX, panelY + 50, buttonWidth, 16)
                    .build());
            chapterX += buttonWidth + gap;
        }
    }

    private void initList() {
        detailY = contentTop();
        detailX = listX + leftW + 8;
        detailW = Math.max(80, panelX + panelW - 10 - detailX);

        int perPage = perPage();
        int start = page * perPage;

        if (tab == 0) {
            List<LedgerView.Entry> entries = currentEntries();
            for (int index = 0; index < perPage && start + index < entries.size(); index++) {
                LedgerView.Entry entry = entries.get(start + index);
                addDrawableChild(ButtonWidget.builder(entryLabel(entry), button -> selectEntry(entry.id))
                        .dimensions(listX, detailY + index * 18, leftW, 16)
                        .build());
            }
        } else {
            for (int index = 0; index < perPage && start + index < view.anals.size(); index++) {
                LedgerView.Anal anal = view.anals.get(start + index);
                addDrawableChild(ButtonWidget.builder(analLabel(anal), button -> selectAnal(anal.id))
                        .dimensions(listX, detailY + index * 18, leftW, 16)
                        .build());
            }
        }

        int pages = pageCount();
        if (page >= pages) {
            page = pages - 1;
        }
    }

    private void initPageButtons() {
        if (pageCount() <= 1) return;

        int buttonY = panelY + panelH - 22;
        addDrawableChild(ButtonWidget.builder(
                        Text.translatable("gui.arcanum_storage.ledger.prev"),
                        button -> changePage(-1))
                .dimensions(listX, buttonY, 20, 16)
                .build());
        addDrawableChild(ButtonWidget.builder(
                        Text.translatable("gui.arcanum_storage.ledger.next"),
                        button -> changePage(1))
                .dimensions(listX + 24, buttonY, 20, 16)
                .build());
    }

    private int contentTop() {
        return panelY + (tab == 0 ? 70 : 52);
    }

    private int perPage() {
        int listBottom = panelY + panelH - 26;
        return Math.max(1, Math.min(5, (listBottom - contentTop()) / 18));
    }

    private int pageCount() {
        int size = tab == 0 ? currentEntries().size() : view.anals.size();
        return Math.max(1, (size + perPage() - 1) / perPage());
    }

    private List<LedgerView.Entry> currentEntries() {
        LedgerView.Chapter chapter = currentChapter();
        return chapter == null ? List.of() : chapter.entries;
    }

    private LedgerView.Chapter currentChapter() {
        return findChapter(chapterId);
    }

    private LedgerView.Chapter findChapter(String id) {
        if (id == null) return null;
        for (LedgerView.Chapter chapter : view.chapters) {
            if (id.equals(chapter.id)) return chapter;
        }
        return null;
    }

    private LedgerView.Entry findEntry(LedgerView.Chapter chapter, String id) {
        if (chapter == null || id == null) return null;
        for (LedgerView.Entry entry : chapter.entries) {
            if (id.equals(entry.id)) return entry;
        }
        return null;
    }

    private LedgerView.Entry currentEntry() {
        return findEntry(currentChapter(), entryId);
    }

    private LedgerView.Anal findAnal(String id) {
        if (id == null) return null;
        for (LedgerView.Anal anal : view.anals) {
            if (id.equals(anal.id)) return anal;
        }
        return null;
    }

    private LedgerView.Anal currentAnal() {
        return findAnal(analId);
    }

    private Text entryLabel(LedgerView.Entry entry) {
        String prefix = switch (entry.state) {
            case "complete" -> "+ ";
            case "partial" -> "~ ";
            default -> "- ";
        };
        return Text.literal(prefix).append(Text.translatable(entry.titleKey));
    }

    private Text analLabel(LedgerView.Anal anal) {
        return Text.literal(anal.unlocked ? "+ " : "- ").append(Text.translatable(anal.titleKey));
    }

    private void switchTab(int newTab) {
        open(newTab, chapterId, entryId, analId, 0);
    }

    private void switchChapter(String id) {
        LedgerView.Chapter chapter = findChapter(id);
        String firstEntry = chapter == null || chapter.entries.isEmpty() ? null : chapter.entries.get(0).id;
        open(0, id, firstEntry, analId, 0);
    }

    private void selectEntry(String id) {
        open(0, chapterId, id, analId, page);
    }

    private void selectAnal(String id) {
        open(1, chapterId, entryId, id, page);
    }

    private void changePage(int delta) {
        int pages = pageCount();
        int target = Math.max(0, Math.min(pages - 1, page + delta));
        if (target != page) {
            open(tab, chapterId, entryId, analId, target);
        }
    }

    private void open(int newTab, String newChapter, String newEntry, String newAnal, int newPage) {
        if (client != null) {
            client.setScreen(new LedgerScreen(view, newTab, newChapter, newEntry, newAnal, newPage));
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);
        drawPanel(context);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        drawContent(context);
    }

    private void drawPanel(DrawContext context) {
        context.fill(panelX - 2, panelY - 2, panelX + panelW + 2, panelY + panelH + 2, 0xFF3A2A18);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xFFE9D8B0);
        context.fill(panelX + 6, panelY + 6, panelX + panelW - 6, panelY + panelH - 6, 0xFFF3E8CC);
        context.fill(panelX, panelY, panelX + panelW, panelY + 3, 0xFFD6A64B);
    }

    private void drawContent(DrawContext context) {
        context.drawText(textRenderer, Text.translatable("gui.arcanum_storage.ledger.title"),
                panelX + 10, panelY + 7, INK, false);
        context.drawText(textRenderer, Text.translatable("gui.arcanum_storage.ledger.subtitle"),
                panelX + 10, panelY + 18, INK_DIM, false);

        Text progress = Text.translatable("gui.arcanum_storage.ledger.progress", view.completed, view.total);
        context.drawText(textRenderer, progress,
                panelX + panelW - 10 - textRenderer.getWidth(progress), panelY + 18, INK_DIM, false);

        if (view.complete) {
            Text banner = Text.translatable("gui.arcanum_storage.ledger.complete_banner");
            context.drawText(textRenderer, banner,
                    panelX + (panelW - textRenderer.getWidth(banner)) / 2, panelY + panelH - 34,
                    GREEN_DARK, false);
        }

        if (pageCount() > 1) {
            Text pageText = Text.translatable("gui.arcanum_storage.ledger.page", page + 1, pageCount());
            context.drawText(textRenderer, pageText, listX + 50, panelY + panelH - 18, INK_DIM, false);
        }

        if (view.chapters.isEmpty() && view.anals.isEmpty()) {
            context.drawText(textRenderer, Text.translatable("gui.arcanum_storage.ledger.empty"),
                    detailX, detailY, INK_DIM, false);
            return;
        }

        if (tab == 0) {
            drawManualDetail(context);
        } else {
            drawAnalDetail(context);
        }
    }

    private void drawManualDetail(DrawContext context) {
        LedgerView.Entry entry = currentEntry();
        if (entry == null) {
            context.drawText(textRenderer, Text.translatable("gui.arcanum_storage.ledger.select"),
                    detailX, detailY, INK_DIM, false);
            return;
        }

        ItemStack icon = stack(entry.icon);
        if (!icon.isEmpty()) {
            context.drawItem(icon, detailX, detailY);
        }
        context.drawText(textRenderer, Text.translatable(entry.titleKey),
                detailX + 20, detailY + 4, INK, false);

        Text stateText = Text.translatable("gui.arcanum_storage.ledger.state_label",
                Text.translatable("gui.arcanum_storage.ledger.state." + entry.state));
        context.drawText(textRenderer, stateText, detailX, detailY + 20, stateColor(entry.state), false);

        boolean showRecipe = !entry.recipe.isEmpty();
        int textWidth = detailW - (showRecipe ? 60 : 0);
        Text body = Text.translatable("gui.arcanum_storage.ledger." + entry.id + "." + entry.state);
        context.drawWrappedText(textRenderer, body, detailX, detailY + 32,
                Math.max(70, textWidth), INK, false);

        if (!entry.missing.isEmpty()) {
            int y = detailY + 68;
            context.drawText(textRenderer,
                    Text.translatable("gui.arcanum_storage.ledger.missing"),
                    detailX, y, RED_DARK, false);
            y += 11;
            for (int index = 0; index < Math.min(3, entry.missing.size()); index++) {
                context.drawText(textRenderer, Text.literal("- ").append(factText(entry.missing.get(index))),
                        detailX, y, INK_DIM, false);
                y += 10;
            }
        }

        if (showRecipe) {
            int recipeX = detailX + detailW - 52;
            context.drawText(textRenderer, Text.translatable("gui.arcanum_storage.ledger.recipe"),
                    recipeX, detailY + 2, GOLD_DARK, false);
            drawRecipe(context, entry.recipe, recipeX, detailY + 14);
        }
    }

    private void drawAnalDetail(DrawContext context) {
        LedgerView.Anal anal = currentAnal();
        if (anal == null) {
            context.drawText(textRenderer, Text.translatable("gui.arcanum_storage.ledger.select"),
                    detailX, detailY, INK_DIM, false);
            return;
        }

        context.drawText(textRenderer, Text.translatable(anal.titleKey), detailX, detailY, INK, false);

        if (!anal.unlocked) {
            context.drawText(textRenderer, Text.translatable("gui.arcanum_storage.ledger.blocked"),
                    detailX, detailY + 14, RED_DARK, false);
            int y = detailY + 28;
            if (!anal.missing.isEmpty()) {
                context.drawText(textRenderer, Text.translatable("gui.arcanum_storage.ledger.missing"),
                        detailX, y, RED_DARK, false);
                y += 11;
                for (int index = 0; index < Math.min(3, anal.missing.size()); index++) {
                    context.drawText(textRenderer,
                            Text.literal("- ").append(factText(anal.missing.get(index))),
                            detailX, y, INK_DIM, false);
                    y += 10;
                }
            }
            return;
        }

        context.drawWrappedText(textRenderer, Text.translatable(anal.bodyKey),
                detailX, detailY + 14, detailW, INK, false);
    }

    private void drawRecipe(DrawContext context, List<List<String>> recipe, int x, int y) {
        int cell = 16;
        int gap = 1;
        for (int row = 0; row < Math.min(3, recipe.size()); row++) {
            List<String> items = recipe.get(row);
            for (int column = 0; column < Math.min(3, items.size()); column++) {
                int cellX = x + column * (cell + gap);
                int cellY = y + row * (cell + gap);
                context.fill(cellX, cellY, cellX + cell, cellY + cell, 0xFFD9C9A3);
                ItemStack stack = stack(items.get(column));
                if (!stack.isEmpty()) {
                    context.drawItem(stack, cellX, cellY);
                }
            }
        }
    }

    private static int stateColor(String state) {
        return switch (state) {
            case "complete" -> GREEN_DARK;
            case "partial" -> GOLD_DARK;
            default -> INK_DIM;
        };
    }

    private static ItemStack stack(String id) {
        Identifier identifier = Identifier.tryParse(id);
        if (identifier == null) return ItemStack.EMPTY;
        Item item = Registries.ITEM.get(identifier);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static Text factText(String fact) {
        int separator = fact.indexOf(':');
        String type = separator >= 0 ? fact.substring(0, separator) : fact;
        String target = separator >= 0 ? fact.substring(separator + 1) : fact;
        if (type.equals("event")) {
            return Text.translatable("gui.arcanum_storage.ledger.event." + target);
        }
        if (type.equals("item") || type.equals("craft") || type.equals("place")) {
            ItemStack stack = stack(target);
            if (!stack.isEmpty()) {
                return stack.getName();
            }
        }
        return Text.literal(target);
    }
}
