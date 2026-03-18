package com.serverevents.discord;

import org.bukkit.configuration.file.FileConfiguration;

public class DiscordConfig {
    private final String botToken;
    private final String channelId;

    public DiscordConfig(FileConfiguration config) {
        this.botToken = config.getString("discord.bot-token", "");
        this.channelId = config.getString("discord.channel-id", "");
    }

    public boolean isEnabled() {
        return !botToken.isEmpty() && !botToken.equals("YOUR_BOT_TOKEN_HERE")
            && !channelId.isEmpty() && !channelId.equals("YOUR_CHANNEL_ID_HERE");
    }

    public String getBotToken() { return botToken; }
    public String getChannelId() { return channelId; }
}
