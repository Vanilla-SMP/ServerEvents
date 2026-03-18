package com.serverevents.localization;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Set;

public class Localization {
    private static final String DEFAULT_LANGUAGE = "ru";
    private static final String BASE_PATH = "messages.";
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("ru", "en");

    private final JavaPlugin plugin;
    private final FileConfiguration mainConfig;
    private final FileConfiguration languageConfig;
    private final FileConfiguration fallbackLanguageConfig;
    private String language;

    public Localization(JavaPlugin plugin) {
        this.plugin = plugin;
        this.mainConfig = plugin.getConfig();
        this.language = normalizeLanguage(mainConfig.getString("localization.language", DEFAULT_LANGUAGE));
        this.languageConfig = loadLanguageFile(language);
        this.fallbackLanguageConfig = loadLanguageFile(DEFAULT_LANGUAGE);
        this.mainConfig.options().copyDefaults(true);
    }

    public String get(String key) {
        String current = getFromMainConfig(BASE_PATH, language, key);
        if (current != null) {
            return current;
        }

        current = languageConfig.getString(BASE_PATH + key);
        if (current != null) {
            return current;
        }

        String fallback = getFromMainConfig(BASE_PATH, DEFAULT_LANGUAGE, key);
        if (fallback != null) {
            return fallback;
        }

        fallback = fallbackLanguageConfig.getString(BASE_PATH + key);
        if (fallback != null) {
            return fallback;
        }

        plugin.getLogger().warning("Missing localization key: " + key);
        return key;
    }

    public String getLanguage() {
        return language;
    }

    private String normalizeLanguage(String raw) {
        if (raw == null) {
            return DEFAULT_LANGUAGE;
        }
        String normalized = raw.trim().toLowerCase();
        if (SUPPORTED_LANGUAGES.contains(normalized)) {
            return normalized;
        }
        plugin.getLogger().warning("Unsupported localization language '" + raw + "', fallback to '" + DEFAULT_LANGUAGE + "'.");
        return DEFAULT_LANGUAGE;
    }

    private FileConfiguration loadLanguageFile(String lang) {
        String fileName = "lang_" + lang + ".yml";
        plugin.saveResource(fileName, false);
        File file = new File(plugin.getDataFolder(), fileName);
        return YamlConfiguration.loadConfiguration(file);
    }

    private String getFromMainConfig(String base, String lang, String key) {
        return mainConfig.getString("localization." + base + lang + "." + key);
    }
}
