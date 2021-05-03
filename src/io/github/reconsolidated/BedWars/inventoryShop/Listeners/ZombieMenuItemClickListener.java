package io.github.reconsolidated.BedWars.inventoryShop.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.inventoryShop.NbtWrapper;
import io.github.reconsolidated.BedWars.inventoryShop.VillagerMenu;
import io.github.reconsolidated.BedWars.inventoryShop.ZombieMenu;
import io.github.reconsolidated.BedWars.inventoryShop.buyMethods.Buy;
import io.github.reconsolidated.BedWars.inventoryShop.buyMethods.ZombieBuy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ZombieMenuItemClickListener implements Listener {

    private final ZombieMenu menu;
    private final BedWars plugin;
    public ZombieMenuItemClickListener(BedWars plugin, ZombieMenu menu) {
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
        if (category.equals("traps")){
            menu.addItems("traps");
        }
        else if (category.equals("back")){
            menu.addItems("diamond");
        }
        else {
            ZombieBuy.item(plugin, player, item, menu);
            menu.addItems("diamond");
        }
    }
}
