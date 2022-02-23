package io.github.reconsolidated.BedWars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class Vanish implements Listener {
    private List<Player> vanishedPlayers;
    private final Plugin plugin;

    public Vanish(Plugin plugin){
        vanishedPlayers = new ArrayList<>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void clean() {
        vanishedPlayers = new ArrayList<>();
    }

    public void vanishPlayer(Player player){
        if (vanishedPlayers.contains(player)){
            for (Player p2 : Bukkit.getOnlinePlayers()){
                p2.showPlayer(plugin, player);
            }
            vanishedPlayers.remove(player);
            player.sendMessage(ChatColor.GREEN + "Wyłączono vanisha");
            return;
        }
        vanishedPlayers.add(player);
        for (Player p2 : Bukkit.getOnlinePlayers()){
            p2.hidePlayer(plugin, player);
        }
        player.setGameMode(GameMode.CREATIVE);
        player.sendMessage(ChatColor.GREEN + "Włączono vanisha");
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        vanishedPlayers.remove(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        for (Player player : vanishedPlayers){
            event.getPlayer().hidePlayer(plugin, player);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event){
        for (Player player : vanishedPlayers){
            if (event.getEntity().getUniqueId().equals(player.getUniqueId())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        for (Player player : vanishedPlayers){
            if (event.getPlayer().getUniqueId().equals(player.getUniqueId())){
                event.setCancelled(true);
            }
        }
    }
}
