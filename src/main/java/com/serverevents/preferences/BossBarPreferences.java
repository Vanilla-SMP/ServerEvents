package com.serverevents.preferences;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarPreferences {
    private final Map<UUID, Boolean> preferences = new HashMap<>();
    private final File configFile;
    private final FileConfiguration config;

    public BossBarPreferences(File dataFolder) {
        this.configFile = new File(dataFolder, "bossbar-preferences.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        load();
    }

    private void load() {
        for (String key : config.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(key);
                preferences.put(playerId, config.getBoolean(key));
            } catch (IllegalArgumentException e) {
                // Ignore invalid UUIDs
            }
        }
    }

    private void save() {
        for (Map.Entry<UUID, Boolean> entry : preferences.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEnabled(UUID playerId) {
        return preferences.getOrDefault(playerId, true);
    }

    public void toggle(UUID playerId) {
        preferences.put(playerId, !isEnabled(playerId));
        save();
    }
}
