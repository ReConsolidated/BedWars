package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.inventoryShop.VillagerMenu;
import io.github.reconsolidated.BedWars.inventoryShop.ZombieMenu;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;

public class PlayerInteractEntityListener implements Listener {
    private ArrayList<Participant> participants;
    private BedWars plugin;

    public PlayerInteractEntityListener(ArrayList<Participant> participants, BedWars plugin){
        this.participants = participants;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Participant p = plugin.getParticipant(player);
        if (p == null || p.isSpectating() || p.team == null){
            event.setCancelled(true);
            return;
        }

        if (event.getRightClicked().getType().equals(EntityType.VILLAGER)){
            new VillagerMenu(plugin, player, "main");
        }
        if (event.getRightClicked().getType().equals(EntityType.ZOMBIE)){
            new ZombieMenu(plugin, player, "diamond");
        }
    }
}
