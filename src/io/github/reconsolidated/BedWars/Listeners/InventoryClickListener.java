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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {
    private BedWars plugin;

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
            if (event.getCurrentItem() != null && isPickaxe(event.getCurrentItem())){
                event.setCancelled(true);
            }
        }
    }

    private static boolean isPickaxe(ItemStack item){
        if (item.getType().equals(Material.WOODEN_PICKAXE)
                || item.getType().equals(Material.STONE_PICKAXE)
                || item.getType().equals(Material.IRON_PICKAXE)
                || item.getType().equals(Material.DIAMOND_PICKAXE)){
            return true;
        }
        return false;
    }

    private static boolean isAxe(ItemStack item){
        if (item.getType().equals(Material.WOODEN_AXE)
                || item.getType().equals(Material.STONE_AXE)
                || item.getType().equals(Material.IRON_AXE)
                || item.getType().equals(Material.DIAMOND_AXE)){
            return true;
        }
        return false;
    }
}
