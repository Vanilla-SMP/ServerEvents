package com.serverevents.listeners;

import com.serverevents.ServerEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.util.UUID;

public class JoinWelcomeListener implements Listener {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private final ServerEvents plugin;

    public JoinWelcomeListener(ServerEvents plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null || !player.isOnline()) {
                return;
            }

            Location effectLocation = player.getLocation().add(0.0, 1.0, 0.0);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.MASTER, 1.0f, 1.0f);
            player.spawnParticle(Particle.ELDER_GUARDIAN, effectLocation, 1, 0.0, 0.0, 0.0, 0.0);

            Component title = MINI_MESSAGE.deserialize(plugin.getLocalization().get("welcome.title"));
            Component subtitle = MINI_MESSAGE.deserialize(plugin.getLocalization().get("welcome.subtitle"));
            Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500));
            player.showTitle(Title.title(title, subtitle, times));
        }, 60L);
    }
}
