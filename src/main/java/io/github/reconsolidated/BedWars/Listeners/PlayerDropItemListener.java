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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static io.github.reconsolidated.BedWars.Participant.unbreakable;

public class PlayerDropItemListener implements Listener {
    private BedWars plugin;

    public PlayerDropItemListener(BedWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Participant p = plugin.getParticipant(event.getPlayer());
        if (isPickaxe(event.getItemDrop().getItemStack())
        || isAxe(event.getItemDrop().getItemStack())
        || event.getItemDrop().getItemStack().getType().equals(Material.SHEARS)){
            event.setCancelled(true);
            return;
        }
        if (event.getItemDrop().getItemStack().getType().equals(Material.WOODEN_SWORD)){
            event.setCancelled(true);
            return;
        }

        plugin.checkSwords(event.getPlayer());

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
