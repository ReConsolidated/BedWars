package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.ScoreScoreboard;
import io.github.reconsolidated.BedWars.inventoryShop.VillagerMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

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

        if (event.getRightClicked().getType().equals(EntityType.VILLAGER)){
            new VillagerMenu(plugin, player, "main");
        }
    }
}
