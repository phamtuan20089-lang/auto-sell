package vn.autosell;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

public class AutoSellClient implements ClientModInitializer {
    private static KeyBinding openKey;
    private static long nextActionAt = 0L;
    private static long lastGuiClickAt = 0L;
    private static int commandCursor = 0;

    @Override
    public void onInitializeClient() {
        AutoSellConfig.get();
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autosell.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.autosell"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> tick(client));
    }

    private static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        while (openKey.wasPressed()) {
            client.setScreen(new AutoSellScreen());
        }

        AutoSellConfig config = AutoSellConfig.get();
        if (!config.enabled) return;

        long now = System.currentTimeMillis();

        // GUI selling: only clicks the configured slot in a matching container title.
        // This is opt-in because shop layouts differ between servers.
        if (config.guiMode && client.currentScreen instanceof net.minecraft.client.gui.screen.ingame.HandledScreen<?> handled) {
            String title = handled.getTitle().getString();
            if (title.toLowerCase().contains(config.guiTitleContains.toLowerCase())
                    && now - lastGuiClickAt >= Math.max(5, config.intervalSeconds) * 1000L
                    && client.interactionManager != null) {
                int slot = config.guiSellSlot;
                if (slot >= 0 && slot < client.player.currentScreenHandler.slots.size()) {
                    client.interactionManager.clickSlot(
                            client.player.currentScreenHandler.syncId,
                            slot,
                            0,
                            SlotActionType.PICKUP,
                            client.player
                    );
                    lastGuiClickAt = now;
                }
            }
            return;
        }

        if (!config.commandMode || client.currentScreen != null || now < nextActionAt) return;

        Map<String, Integer> counts = getSelectedInventoryCounts(client, config);
        if (counts.isEmpty()) return;

        var entries = counts.entrySet().stream().toList();
        if (commandCursor >= entries.size()) commandCursor = 0;
        Map.Entry<String, Integer> entry = entries.get(commandCursor++);
        String command = config.sellCommand
                .replace("{item}", entry.getKey())
                .replace("{count}", Integer.toString(entry.getValue()));

        if (client.getNetworkHandler() != null && !command.isBlank()) {
            String outgoing = command.startsWith("/") ? command.substring(1) : command;
            client.getNetworkHandler().sendChatCommand(outgoing);
            nextActionAt = now + Math.max(5, config.intervalSeconds) * 1000L;
        }
    }

    private static Map<String, Integer> getSelectedInventoryCounts(MinecraftClient client, AutoSellConfig config) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (ItemStack stack : client.player.getInventory().main) {
            if (stack.isEmpty()) continue;
            String id = Registries.ITEM.getId(stack.getItem()).toString();
            if (config.isSellable(id)) {
                counts.merge(id, stack.getCount(), Integer::sum);
            }
        }
        return counts;
    }
}
