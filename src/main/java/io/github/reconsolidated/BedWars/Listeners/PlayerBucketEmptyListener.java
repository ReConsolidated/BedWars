package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        Player player = event.getPlayer();
        Participant p = plugin.getParticipant(player);
        if (p == null) return;
        if (event.getBlock().getLocation().distance(p.getTeam().getBedLocation()) > 30){
            p.getPlayer().sendMessage(ChatColor.RED + "Nie możesz wylać wody tak daleko od bazy.");
            event.setCancelled(true);
            return;
        }
        if (BedWars.guard.isProtected(event.getBlock().getLocation())) {
            p.getPlayer().sendMessage(ChatColor.RED + "Nie możesz tego tu postawić!");
            event.setCancelled(true);
            return;
        }

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
