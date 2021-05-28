package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.CustomSpectator.CustomSpectator;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.ScoreScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class PlayerQuitListener implements Listener {
    private BedWars plugin;

    public PlayerQuitListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        event.setQuitMessage("");
        Participant p = plugin.getParticipant(player);
        if (p == null) return;
        if (plugin.hasStarted){
            if (!p.hasLost){
                p.onDeath();
                plugin.setParticipantInactive(p);
                event.setQuitMessage(p.getChatColor() + p.player.getName() + ChatColor.YELLOW + " wyszedł z gry.");
            }
        }
        else{
            plugin.getParticipants().remove(p);
            event.setQuitMessage(ChatColor.YELLOW +
                    player.getName() + " wyszedł ("
                    + ChatColor.AQUA + plugin.getParticipants().size()
                    + ChatColor.YELLOW + "/" + ChatColor.AQUA + "8" + ChatColor.YELLOW + ").");
        }
    }
}
