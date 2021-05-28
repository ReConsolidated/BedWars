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
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class PlayerPickupArrowListener implements Listener {
    private BedWars plugin;

    public PlayerPickupArrowListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPickupArrow(PlayerPickupArrowEvent event){
        Participant p = plugin.getParticipant(event.getPlayer());
        if (p == null || p.isDead() || p.isSpectating()){
            event.setCancelled(true);
        }
    }
}
