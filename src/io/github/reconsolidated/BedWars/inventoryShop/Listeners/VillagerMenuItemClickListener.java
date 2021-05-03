package io.github.reconsolidated.BedWars.inventoryShop.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.inventoryShop.NbtWrapper;
import io.github.reconsolidated.BedWars.inventoryShop.VillagerMenu;
import io.github.reconsolidated.BedWars.inventoryShop.buyMethods.Buy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class VillagerMenuItemClickListener implements Listener {

    private final VillagerMenu menu;
    private final BedWars plugin;
    public VillagerMenuItemClickListener(BedWars plugin, VillagerMenu menu) {
        this.menu = menu;
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (!(event.getClickedInventory() == (menu.getInventory()))) { return; }
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || item.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        Player player = (Player) event.getWhoClicked();
        String category = NbtWrapper.getNBTTag("category", item);
        if (category.length() > 1){
            new VillagerMenu(plugin, player, category);
        }
        else{
            Buy.item(plugin, player, item, menu);
        }


    }
}
