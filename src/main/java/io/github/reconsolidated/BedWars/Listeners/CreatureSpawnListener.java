package io.github.reconsolidated.BedWars.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnListener implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event){
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)){
            event.setCancelled(true);
        }
    }
}
