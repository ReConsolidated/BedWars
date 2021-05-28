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

import java.util.ArrayList;

public class PlayerJoinListener implements Listener {
    private ArrayList<Participant> participants;
    private BedWars plugin;

    public PlayerJoinListener(BedWars plugin){
        this.participants = plugin.getParticipants();
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        Participant p = plugin.getInactiveParticipant(player);
        if (p != null){
            p.player = player;
            plugin.restoreParticipant(p);
            if (p.isDead()){
                CustomSpectator.setSpectator(plugin, player);
            }
            else{
                p.onRespawn();
            }
            p.scoreboard.registerPlayer(p.player);

        }
        else{
            player.teleport(plugin.world.getSpawnLocation());

            for (Player p2 : Bukkit.getOnlinePlayers()){
                player.hidePlayer(plugin, p2);
                p2.hidePlayer(plugin, player);
            }
            for (Player p2 : Bukkit.getOnlinePlayers()){
                player.showPlayer(plugin, p2);
                p2.showPlayer(plugin, player);
            }

            if (!plugin.hasStarted){
                p = new Participant(player, plugin);
                participants.add(p);
                event.setJoinMessage(ChatColor.YELLOW + player.getName()
                        + " dołączył (" + ChatColor.AQUA + participants.size()
                        + ChatColor.YELLOW + "/" + ChatColor.AQUA + "8"
                        + ChatColor.YELLOW + ").");
                player.setInvulnerable(true);
            }
            else{
                CustomSpectator.setSpectator(plugin, player);
            }
        }

    }

    @EventHandler
    public void onPrePlayerLogin(AsyncPlayerPreLoginEvent event){
//        if (plugin.hasStarted){
//            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, ChatColor.GRAY + "Gra się już zaczęła.");
//        }
    }
}
