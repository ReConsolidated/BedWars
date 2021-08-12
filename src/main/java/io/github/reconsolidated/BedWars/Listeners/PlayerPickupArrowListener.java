package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupArrowEvent;

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
