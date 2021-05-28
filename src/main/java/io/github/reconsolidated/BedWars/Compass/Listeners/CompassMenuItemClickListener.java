package io.github.reconsolidated.BedWars.Compass.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Compass.CompassMenu;
import io.github.reconsolidated.BedWars.Compass.OnItemClick;
import io.github.reconsolidated.BedWars.inventoryShop.NbtWrapper;
import io.github.reconsolidated.BedWars.inventoryShop.ZombieMenu;
import io.github.reconsolidated.BedWars.inventoryShop.buyMethods.ZombieBuy;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CompassMenuItemClickListener implements Listener {

    private final CompassMenu menu;
    private final BedWars plugin;
    public CompassMenuItemClickListener(BedWars plugin, CompassMenu menu) {
        this.menu = menu;
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)){
            Player player = (Player) event.getClickedInventory().getViewers().get(0);
            if (event.getClickedInventory().equals(player.getInventory())){
                if (player.getOpenInventory().getTopInventory().equals(menu.getInventory())){
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (!(event.getClickedInventory() == (menu.getInventory()))) { return; }
        if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)){
            event.setCancelled(true);
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || item.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        Player player = (Player) event.getWhoClicked();

        String category = NbtWrapper.getNBTTag("category", item);
        if (item.getType().equals(Material.EMERALD)){
            menu.addItems("comms");
        }
        else if (item.getType().equals(Material.COMPASS)){
            menu.addItems("finder");
        }
        else if (category.equals("back")){
            menu.addItems("main");
        }
        else if(category.equals("not_clickable")){
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 10, 1);
        }
        else {
            OnItemClick.item(plugin, player, item, menu);
            player.closeInventory();
        }
    }
}
