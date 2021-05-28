package io.github.reconsolidated.BedWars.inventoryShop.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class DisableMoving implements Listener {

    private final Inventory inv;

    public DisableMoving(Inventory inv) {
        this.inv = inv;
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getClickedInventory() != inv) return;
        event.setCancelled(true);
    }
}
