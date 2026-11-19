package vn.autosell;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public class AutoSellConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static AutoSellConfig INSTANCE;

    public boolean enabled = false;
    public boolean commandMode = true;
    public boolean guiMode = false;
    public String sellCommand = "/sell {item}";
    public int intervalSeconds = 10;

    // GUI mode is intentionally off until the user configures these for their server.
    public String guiTitleContains = "Sell";
    public int guiSellSlot = 0;

    public Set<String> selectedItems = new LinkedHashSet<>();
    public Set<String> protectedItems = new LinkedHashSet<>();

    public static AutoSellConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    private static Path path() {
        return MinecraftClient.getInstance().runDirectory.toPath()
                .resolve("config").resolve("autosell.json");
    }

    public static AutoSellConfig load() {
        Path path = path();
        try {
            Files.createDirectories(path.getParent());
            if (Files.exists(path)) {
                try (Reader reader = Files.newBufferedReader(path)) {
                    AutoSellConfig loaded = GSON.fromJson(reader, AutoSellConfig.class);
                    if (loaded != null) return loaded;
                }
            }
        } catch (Exception e) {
            System.err.println("[AutoSell] Could not load config: " + e.getMessage());
        }
        AutoSellConfig config = new AutoSellConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Path path = path();
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(this, writer);
            }
        } catch (Exception e) {
            System.err.println("[AutoSell] Could not save config: " + e.getMessage());
        }
    }

    public boolean isSellable(String itemId) {
        return selectedItems.contains(itemId) && !protectedItems.contains(itemId);
    }
}
