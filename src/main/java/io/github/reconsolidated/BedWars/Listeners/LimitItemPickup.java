package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class LimitItemPickup implements Listener {
    private final Set<UUID> playersOnCooldown = new HashSet<>();
    private final BedWars plugin;

    public LimitItemPickup(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event){
        if (!(event.getEntity() instanceof Player)){
            return;
        }
        Player player = (Player) event.getEntity();
        if (playersOnCooldown.contains(player.getUniqueId())){
            event.setCancelled(true);
            if ((new Random()).nextBoolean()){
                playersOnCooldown.remove(player.getUniqueId());
            }
            return;
        }
        playersOnCooldown.add(player.getUniqueId());
    }
}
