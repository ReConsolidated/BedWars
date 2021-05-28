package io.github.reconsolidated.BedWars.Listeners;


import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;


public class BlockIgniteListener implements Listener {
    private BedWars plugin;
    public BlockIgniteListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event){
        event.setCancelled(true);
    }



}
