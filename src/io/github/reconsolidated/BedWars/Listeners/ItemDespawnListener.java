package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.ScoreScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class ItemDespawnListener implements Listener {
    private BedWars plugin;

    public ItemDespawnListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event){
        event.setCancelled(true);
    }

}
