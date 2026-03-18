package com.serverevents.discord;

import com.serverevents.localization.Localization;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class DiscordNotifier {
    private static final String API_URL = "https://discord.com/api/v10/channels/%s/messages";

    private final DiscordConfig config;
    private final Plugin plugin;
    private final Localization localization;

    public DiscordNotifier(DiscordConfig config, Plugin plugin, Localization localization) {
        this.config = config;
        this.plugin = plugin;
        this.localization = localization;
    }

    public void notifyEventStarted(String eventName, String eventDetails) {
        if (!config.isEnabled()) return;

        String description = eventDetails != null && !eventDetails.isBlank()
            ? eventName + "\n" + eventDetails
            : eventName;

        String payload = buildPayload(description);
        String url = String.format(API_URL, config.getChannelId());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sendRequest(url, payload));
    }

    private String buildPayload(String description) {
        String escaped = description.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        String title = localization.get("discord.event_started_title")
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n");
        return "{\"embeds\":[{\"title\":\"" + title + "\","
            + "\"description\":\"" + escaped + "\","
            + "\"color\":16766720}]}";
    }

    private void sendRequest(String url, String payload) {
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bot " + config.getBotToken());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            if (code != 200 && code != 201 && code != 204) {
                plugin.getLogger().warning(localization.get("discord.failed_http") + " " + code);
            }
            conn.disconnect();
        } catch (Exception e) {
            plugin.getLogger().warning(localization.get("discord.error_sending") + " " + e.getMessage());
        }
    }
}
