package io.github.reconsolidated.BedWars.Listeners;

import org.bukkit.block.data.type.Bed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

public class BlockDropItemListener implements Listener {
    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event){
        if (event.getBlock().getBlockData() instanceof Bed){
            event.setCancelled(true);
        }
    }
}
