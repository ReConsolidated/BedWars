package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class EntityPickupItemListener implements Listener {
    private BedWars plugin;

    public EntityPickupItemListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event){
        if (event.getEntity() instanceof Player){
            Participant p = plugin.getParticipant((Player) event.getEntity());
            if (p == null || p.isSpectating()){
                event.setCancelled(true);
            }
        }
        else{
            event.setCancelled(true);
        }
    }
}
