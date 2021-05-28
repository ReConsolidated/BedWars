package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerBucketEmptyListener implements Listener {
    private BedWars plugin;

    public PlayerBucketEmptyListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event){
        event.getPlayer().getInventory().remove(event.getItemStack());
        event.getPlayer().getInventory().remove(Material.BUCKET);
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().getInventory().remove(event.getItemStack());
                event.getPlayer().getInventory().remove(Material.BUCKET);
            }
        }.runTaskLater(plugin, 1L);
    }
}
