package vn.autosell;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.LinkedHashMap;
import java.util.Map;

public class AutoSellScreen extends Screen {
    private int page = 0;

    public AutoSellScreen() {
        super(Text.literal("AutoSell"));
    }

    @Override
    protected void init() {
        AutoSellConfig config = AutoSellConfig.get();
        int cx = width / 2;
        int y = 42;

        addDrawableChild(ButtonWidget.builder(
                Text.literal("AutoSell: " + (config.enabled ? "ON" : "OFF")),
                b -> { config.enabled = !config.enabled; config.save(); clearAndInit(); }
        ).dimensions(cx - 105, y, 210, 20).build());

        y += 25;
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Command mode: " + (config.commandMode ? "ON" : "OFF")),
                b -> { config.commandMode = !config.commandMode; config.save(); clearAndInit(); }
        ).dimensions(cx - 105, y, 210, 20).build());

        y += 23;
        addDrawableChild(ButtonWidget.builder(
                Text.literal("GUI mode: " + (config.guiMode ? "ON" : "OFF")),
                b -> { config.guiMode = !config.guiMode; config.save(); clearAndInit(); }
        ).dimensions(cx - 105, y, 210, 20).build());

        y += 28;
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Interval: " + config.intervalSeconds + "s (click to +5)"),
                b -> { config.intervalSeconds = config.intervalSeconds >= 60 ? 5 : config.intervalSeconds + 5; config.save(); clearAndInit(); }
        ).dimensions(cx - 105, y, 210, 20).build());

        y += 30;
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Previous items"),
                b -> { page = Math.max(0, page - 1); clearAndInit(); }
        ).dimensions(cx - 105, y, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Next items"),
                b -> { page++; clearAndInit(); }
        ).dimensions(cx + 5, y, 100, 20).build());

        y += 25;
        Map<String, Integer> inventory = inventoryItems();
        var entries = inventory.entrySet().stream().toList();
        int start = page * 8;
        for (int i = start; i < Math.min(start + 8, entries.size()); i++) {
            String id = entries.get(i).getKey();
            boolean selected = config.selectedItems.contains(id);
            boolean protectedItem = config.protectedItems.contains(id);
            String label = (selected ? "[SELL] " : "[SKIP] ") + id;
            if (protectedItem) label = "[SAFE] " + id;
            final String itemId = id;
            addDrawableChild(ButtonWidget.builder(
                    Text.literal(label),
                    b -> {
                        if (config.selectedItems.contains(itemId)) {
                            config.selectedItems.remove(itemId);
                        } else {
                            config.selectedItems.add(itemId);
                            config.protectedItems.remove(itemId);
                        }
                        config.save();
                        clearAndInit();
                    }
            ).dimensions(cx - 145, y, 290, 18).build());
            y += 20;
        }

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Close"),
                b -> close()
        ).dimensions(cx - 50, height - 28, 100, 20).build());
    }

    private Map<String, Integer> inventoryItems() {
        Map<String, Integer> items = new LinkedHashMap<>();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return items;
        for (ItemStack stack : client.player.getInventory().main) {
            if (!stack.isEmpty()) {
                String id = Registries.ITEM.getId(stack.getItem()).toString();
                items.merge(id, stack.getCount(), Integer::sum);
            }
        }
        return items;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 14, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Click an inventory item to add/remove it from the sell list."),
                width / 2, 29, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
