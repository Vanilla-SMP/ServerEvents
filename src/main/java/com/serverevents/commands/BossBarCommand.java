package com.serverevents.commands;

import com.serverevents.ServerEvents;
import com.serverevents.preferences.BossBarPreferences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BossBarCommand implements CommandExecutor {
    private final ServerEvents plugin;
    private final BossBarPreferences preferences;

    public BossBarCommand(ServerEvents plugin, BossBarPreferences preferences) {
        this.plugin = plugin;
        this.preferences = preferences;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text(plugin.getLocalization().get("bossbar.only_players"), NamedTextColor.RED));
            return true;
        }

        preferences.toggle(player.getUniqueId());
        boolean enabled = preferences.isEnabled(player.getUniqueId());

        if (enabled) {
            if (plugin.getEventManager().getCurrentEvent() != null) {
                plugin.getEventManager().getCurrentEvent().onPlayerJoin(player);
            }
            player.sendMessage(Component.text(plugin.getLocalization().get("bossbar.enabled"), NamedTextColor.GREEN));
        } else {
            if (plugin.getEventManager().getCurrentEvent() != null) {
                plugin.getEventManager().getCurrentEvent().getBossBar().removePlayer(player);
            }
            player.sendMessage(Component.text(plugin.getLocalization().get("bossbar.disabled"), NamedTextColor.RED));
        }

        return true;
    }
}
