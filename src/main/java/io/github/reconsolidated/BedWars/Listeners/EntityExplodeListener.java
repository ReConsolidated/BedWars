package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener implements Listener {
    private BedWars plugin;

    public EntityExplodeListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent event){
        if (event.getEntity() instanceof TNTPrimed) {
            event.getEntity().getLocation().createExplosion(2, false, true);
        }
    }
}
