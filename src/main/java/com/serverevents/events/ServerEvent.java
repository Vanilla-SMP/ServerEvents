package com.serverevents.events;

import com.serverevents.preferences.BossBarPreferences;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public abstract class ServerEvent {
    protected final String name;
    protected BossBar bossBar;
    protected long endTime;
    protected long startTime;
    protected BossBarPreferences preferences;

    public ServerEvent(String name) {
        this.name = name;
    }

    public void setPreferences(BossBarPreferences preferences) {
        this.preferences = preferences;
    }

    public abstract void start();
    public abstract void stop();
    public abstract void tick();
    public abstract void onPlayerJoin(Player player);
    public abstract String getDiscordDescription();

    public String getName() {
        return name;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
        this.startTime = System.currentTimeMillis();
    }

    protected long getRemainingSeconds() {
        return Math.max(0, (endTime - System.currentTimeMillis()) / 1000);
    }

    protected void updateBossBarProgress() {
        if (bossBar != null) {
            long totalMs = endTime - startTime;
            long remainingMs = Math.max(0, endTime - System.currentTimeMillis());
            double progress = totalMs > 0 ? (double) remainingMs / totalMs : 0.0;
            bossBar.setProgress(Math.min(1.0, Math.max(0.0, progress)));
        }
    }
}
