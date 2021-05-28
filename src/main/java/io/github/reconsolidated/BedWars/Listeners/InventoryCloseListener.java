package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static io.github.reconsolidated.BedWars.Participant.unbreakable;


public class InventoryCloseListener implements Listener {

    private final BedWars plugin;

    public InventoryCloseListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        plugin.checkSwords((Player) event.getPlayer());
    }
}
