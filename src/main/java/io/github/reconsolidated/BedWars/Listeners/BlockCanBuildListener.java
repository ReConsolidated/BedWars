package io.github.reconsolidated.BedWars.Listeners;


import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCanBuildEvent;

import java.util.ArrayList;

public class BlockCanBuildListener implements Listener {
    private BedWars plugin;
    public BlockCanBuildListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockCanBuildEvent(BlockCanBuildEvent event){
        ArrayList<Entity> entities = new ArrayList<>(event.getBlock().getWorld().getNearbyEntities(event.getBlock().getLocation(), 1, 1, 1));
        for (Entity e : entities){
            if (e instanceof Player){
                Participant p = plugin.getParticipant((Player) e);
                if (p == null) continue;
                if (p.isSpectating()){
                    event.setBuildable(true);
                }
                else{
                    if (event.getBlock().getX() == p.getPlayer().getLocation().getBlockX()
                    && event.getBlock().getY() == p.getPlayer().getLocation().getBlockY()
                    && event.getBlock().getZ() == p.getPlayer().getLocation().getBlockZ()){
                        event.setBuildable(false);
                        return;
                    }

                }
            }
        }
    }
}
