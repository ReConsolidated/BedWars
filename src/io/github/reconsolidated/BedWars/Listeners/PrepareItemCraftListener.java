package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class PrepareItemCraftListener implements Listener {
    private BedWars plugin;

    public PrepareItemCraftListener(BedWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        event.getInventory().setResult(new ItemStack(Material.AIR));
    }

}