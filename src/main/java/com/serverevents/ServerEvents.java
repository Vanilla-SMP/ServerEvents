package com.serverevents;

import com.serverevents.commands.BossBarCommand;
import com.serverevents.discord.DiscordConfig;
import com.serverevents.discord.DiscordNotifier;
import com.serverevents.events.EventManager;
import com.serverevents.listeners.JoinWelcomeListener;
import com.serverevents.localization.Localization;
import com.serverevents.preferences.BossBarPreferences;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerEvents extends JavaPlugin {
    private EventManager eventManager;
    private BossBarPreferences bossBarPreferences;
    private Localization localization;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        saveDefaultConfig();
        localization = new Localization(this);
        bossBarPreferences = new BossBarPreferences(getDataFolder());
        eventManager = new EventManager(this, bossBarPreferences);

        DiscordConfig discordConfig = new DiscordConfig(getConfig());
        if (discordConfig.isEnabled()) {
            eventManager.setDiscordNotifier(new DiscordNotifier(discordConfig, this, localization));
            getLogger().info(localization.get("logs.discord_enabled"));
        }

        eventManager.startEventCycle();
        getServer().getPluginManager().registerEvents(new JoinWelcomeListener(this), this);
        getCommand("bossbar").setExecutor(new BossBarCommand(this, bossBarPreferences));
        getLogger().info(localization.get("logs.plugin_enabled"));
    }

    @Override
    public void onDisable() {
        if (eventManager != null) {
            eventManager.stopAll();
        }
        if (localization != null) {
            getLogger().info(localization.get("logs.plugin_disabled"));
        } else {
            getLogger().info("ServerEvents disabled!");
        }
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public BossBarPreferences getBossBarPreferences() {
        return bossBarPreferences;
    }

    public Localization getLocalization() {
        return localization;
    }
}
