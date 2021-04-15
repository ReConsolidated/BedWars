package io.github.reconsolidated.BedWars.inventoryShop.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class DisableDraging implements Listener {

    private final Inventory inv;

    public DisableDraging(Inventory inv) {
        this.inv = inv;
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryDragEvent event) {
        if (event.getInventory() == inv) {
            event.setCancelled(true);
        }
    }
}
