package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;


public class InventoryClickListener implements Listener {
    private final BedWars plugin;

    public InventoryClickListener(BedWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player){
            Participant p = plugin.getParticipant((Player)event.getWhoClicked());
            if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)){
                event.setCancelled(true);
            }

            if (p != null && (p.getPlayer().getOpenInventory().getType().equals(InventoryType.CHEST)
            || p.getPlayer().getOpenInventory().getType().equals(InventoryType.ENDER_CHEST))) {
                if (event.getCurrentItem() != null) {
                    if (isUndroppableItem(event.getCurrentItem())) {
                        event.setCancelled(true);
                    }
                }
                if (event.getCursor() != null) {
                    if (isUndroppableItem(event.getCursor())) {
                        event.setCancelled(true);
                    }
                }
                if (event.getHotbarButton() > -1) {
                    if (isUndroppableItem(p.getPlayer().getInventory().getItem(event.getHotbarButton()))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }


    private boolean isUndroppableItem(ItemStack item) {
        if (item == null) return false;
        return PlayerDropItemListener.isPickaxe(item)
                || PlayerDropItemListener.isAxe(item)
                || item.getType().equals(Material.COMPASS)
                || item.getType().equals(Material.WOODEN_SWORD)
                || item.getType().equals(Material.SHEARS);

    }




}
