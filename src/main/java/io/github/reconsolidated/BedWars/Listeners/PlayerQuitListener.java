package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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
        if (p == null){
            event.setQuitMessage(null);
            return;
        }
        if (plugin.hasStarted){
            if (!p.hasLost()){
                p.onDeath();
                plugin.setParticipantInactive(p);
                event.setQuitMessage(p.getChatColor() + p.getPlayer().getName() + ChatColor.YELLOW + " wyszedł z gry.");
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
