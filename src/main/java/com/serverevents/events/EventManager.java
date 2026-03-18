package com.serverevents.events;

import com.serverevents.ServerEvents;
import com.serverevents.discord.DiscordNotifier;
import com.serverevents.events.types.MobSpawnBoostEvent;
import com.serverevents.preferences.BossBarPreferences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class EventManager implements Listener {
    private static final int MAX_EVENTS_PER_DAY = 16;
    private static final long DAY_MS = 86_400_000L;
    private static final long MIN_DURATION_MS = 15 * 60_000L;
    private static final long MAX_DURATION_MS = 40 * 60_000L;

    private final ServerEvents plugin;
    private final BossBarPreferences preferences;
    private final Random random = new Random();
    private DiscordNotifier discordNotifier;

    private ServerEvent currentEvent;
    private BukkitTask tickTask;
    private BukkitTask scheduleTask;

    private long dayStartMs;
    private int eventsToday;

    public EventManager(ServerEvents plugin, BossBarPreferences preferences) {
        this.plugin = plugin;
        this.preferences = preferences;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void setDiscordNotifier(DiscordNotifier notifier) {
        this.discordNotifier = notifier;
    }

    public void startEventCycle() {
        resetDayCounters();

        tickTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (currentEvent != null) {
                currentEvent.tick();
                currentEvent.updateBossBarProgress();
            }
        }, 0, 100);

        scheduleNextEvent();
    }

    private void resetDayCounters() {
        dayStartMs = System.currentTimeMillis();
        eventsToday = 0;
    }

    private void scheduleNextEvent() {
        long elapsed = System.currentTimeMillis() - dayStartMs;
        if (elapsed >= DAY_MS) {
            resetDayCounters();
        }

        if (eventsToday >= MAX_EVENTS_PER_DAY) {
            long untilNextDay = DAY_MS - (System.currentTimeMillis() - dayStartMs);
            long delayTicks = Math.max(1, untilNextDay / 50);
            scheduleTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                resetDayCounters();
                scheduleNextEvent();
            }, delayTicks);
            return;
        }

        long remaining = DAY_MS - elapsed;
        int remainingSlots = MAX_EVENTS_PER_DAY - eventsToday;
        long avgGap = remaining / remainingSlots;
        long minGap = avgGap / 2;
        long maxGap = avgGap * 3 / 2;
        long gapMs = minGap + (long) (random.nextDouble() * (maxGap - minGap));

        long delayTicks = Math.max(1, gapMs / 50);
        scheduleTask = Bukkit.getScheduler().runTaskLater(plugin, this::startNextEvent, delayTicks);
    }

    private void startNextEvent() {
        long durationMs = MIN_DURATION_MS + (long) (random.nextDouble() * (MAX_DURATION_MS - MIN_DURATION_MS));
        eventsToday++;

        startRandomEvent(durationMs);

        long durationTicks = durationMs / 50;
        scheduleTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            stopCurrentEvent();
            scheduleNextEvent();
        }, durationTicks);
    }

    private void startRandomEvent(long durationMs) {
        currentEvent = new MobSpawnBoostEvent(plugin);
        currentEvent.setPreferences(preferences);
        currentEvent.setEndTime(System.currentTimeMillis() + durationMs);
        currentEvent.start();

        if (discordNotifier != null) {
            discordNotifier.notifyEventStarted(currentEvent.getName(), currentEvent.getDiscordDescription());
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            currentEvent.onPlayerJoin(player);
        }
    }

    public void stopCurrentEvent() {
        if (currentEvent != null) {
            currentEvent.stop();
            currentEvent = null;
        }
    }

    public void stopAll() {
        stopCurrentEvent();
        if (scheduleTask != null) { scheduleTask.cancel(); scheduleTask = null; }
        if (tickTask != null) { tickTask.cancel(); tickTask = null; }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (currentEvent == null) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (currentEvent != null) currentEvent.onPlayerJoin(event.getPlayer());
        }, 20);
    }

    public ServerEvent getCurrentEvent() {
        return currentEvent;
    }
}
