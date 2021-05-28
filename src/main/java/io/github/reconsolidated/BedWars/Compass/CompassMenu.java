package io.github.reconsolidated.BedWars.Compass;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Compass.Listeners.CompassMenuItemClickListener;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.inventoryShop.CustomItemStack;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.DisableDraging;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.DisableMoving;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.ZombieMenuItemClickListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Class responsible for the BedWars shop.
 * Creating an instance opens a shop menu for the player passed as the 2nd parameter.
 * TEST
 */

public class CompassMenu implements Listener {

    private final BedWars plugin;
    private Inventory inv;

    private Participant participant;
    private int timerID;
    private String color;
    private Player player;

    public CompassMenu(BedWars plugin, Player player, String type) {
        this.plugin = plugin;
        this.color = "WHITE";
        Participant p = plugin.getParticipant(player);
        if (p == null) return;
        this.inv = Bukkit.createInventory(null, 54, "Sklep drużynowy");
        this.color = p.getColor();
        this.participant = p;
        this.player = player;
        addItems(type);
        fillEmptySpace();
        registerEvents();
        player.openInventory(inv);

    }


    public void addItems(String type) {
        this.inv.clear();
        switch (type){
            case "main" ->{
                this.inv.setItem(1, CustomItemStack.createCustomItemStack(Material.EMERALD, 1, Material.DIAMOND, -1, "cmp", new ArrayList<>(), player));
                this.inv.setItem(2, CustomItemStack.createCustomItemStack(Material.COMPASS, 1, Material.DIAMOND, -1, "cmp", new ArrayList<>(), player));
            }
            case "comms" -> {
                this.inv.setItem(1, CustomItemStack.createCustomItemStack(Material.SHIELD, 1, Material.DIAMOND, -1, "cmp", new ArrayList<>(), player));
                this.inv.setItem(2, CustomItemStack.createCustomItemStack(Material.STONE_SWORD, 1, Material.DIAMOND, -1, "cmp", new ArrayList<>(), player));
                this.inv.setItem(3, CustomItemStack.createCustomItemStack(Material.EMERALD, 1, Material.DIAMOND, -1, "cmp", new ArrayList<>(), player));
                this.inv.setItem(4, CustomItemStack.createCustomItemStack(Material.DIAMOND, 1, Material.DIAMOND, -1, "cmp", new ArrayList<>(), player));
                this.inv.setItem(9, CustomItemStack.createCustomItemStack(Material.ARROW, 1, Material.IRON_INGOT, -1, "back", new ArrayList<>(), player));

            }
            case "finder" ->{
                if (plugin.getTeams().size() == 4){
                    this.inv.setItem(1, CustomItemStack.createCustomItemStack(Material.BLUE_WOOL, 1, Material.EMERALD, 2, "cmp", new ArrayList<>(), player));
                    this.inv.setItem(2, CustomItemStack.createCustomItemStack(Material.RED_WOOL, 1, Material.EMERALD, 2, "cmp", new ArrayList<>(), player));
                    this.inv.setItem(3, CustomItemStack.createCustomItemStack(Material.YELLOW_WOOL, 1, Material.EMERALD, 2, "cmp", new ArrayList<>(), player));
                    this.inv.setItem(4, CustomItemStack.createCustomItemStack(Material.GREEN_WOOL, 1, Material.EMERALD, 2, "cmp", new ArrayList<>(), player));
                    this.inv.setItem(5, CustomItemStack.createCustomItemStack(Material.ARROW, 1, Material.IRON_INGOT, -1, "back", new ArrayList<>(), player));
                }
                else{
                    this.inv.setItem(1, CustomItemStack.createCustomItemStack(Material.BLUE_WOOL, 1, Material.EMERALD, 2, "cmp", new ArrayList<>(), player));
                    this.inv.setItem(2, CustomItemStack.createCustomItemStack(Material.RED_WOOL, 1, Material.EMERALD, 2, "cmp", new ArrayList<>(), player));
                    this.inv.setItem(3, CustomItemStack.createCustomItemStack(Material.YELLOW_WOOL, 1, Material.EMERALD, 2, "cmp", new ArrayList<>(), player));
                    this.inv.setItem(4, CustomItemStack.createCustomItemStack(Material.GREEN_WOOL, 1, Material.EMERALD, 2, "cmp", new ArrayList<>(), player));
                    this.inv.setItem(5, CustomItemStack.createCustomItemStack(Material.LIGHT_BLUE_WOOL, 1, Material.EMERALD, 2, "cmp", new ArrayList<>(), player));
                    this.inv.setItem(6, CustomItemStack.createCustomItemStack(Material.GRAY_WOOL, 1, Material.EMERALD, 2, "cmp", new ArrayList<>(), player));
                    this.inv.setItem(7, CustomItemStack.createCustomItemStack(Material.PURPLE_WOOL, 1, Material.EMERALD, 2, "cmp", new ArrayList<>(), player));
                    this.inv.setItem(8, CustomItemStack.createCustomItemStack(Material.WHITE_WOOL, 1, Material.EMERALD, 2, "cmp", new ArrayList<>(), player));
                    this.inv.setItem(9, CustomItemStack.createCustomItemStack(Material.ARROW, 1, Material.IRON_INGOT, -1, "back", new ArrayList<>(), player));

                }

            }
        }

    }


    private void fillEmptySpace() {
        for (int i = 0; i < inv.getSize(); i++) {
            if (this.inv.getItem(i) == null || this.inv.getItem(i).getType() == Material.AIR) {
                // this.inv.setItem(i, new Space_Filler());
            }
        }
    }

    private void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(new DisableMoving(inv), plugin);
        plugin.getServer().getPluginManager().registerEvents(new DisableDraging(inv), plugin);
        plugin.getServer().getPluginManager().registerEvents(new CompassMenuItemClickListener(plugin,this), plugin);
    }

    private void setTiles(Material material) {
        ItemStack[] itemArray = new ItemStack[inv.getSize()];
        int counter = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null){
                if (item.getType() == Material.GRAY_STAINED_GLASS_PANE || item.getType() == Material.RED_STAINED_GLASS_PANE || item.getType() == Material.GREEN_STAINED_GLASS_PANE) {
                    itemArray[counter++] = new ItemStack(material, 1);
                } else {
                    itemArray[counter++] = item;
                }
            }
            else{
                counter++;
            }
        }
        inv.setContents(itemArray);
    }

    public void setRedTiles() {
        setTiles(Material.RED_STAINED_GLASS_PANE);
    }

    public void setGreenTiles() {
        setTiles(Material.GREEN_STAINED_GLASS_PANE);
    }

    public void setNormalTiles() {
        ItemStack[] itemArray = new ItemStack[inv.getSize()];
        int counter = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null){
                if (item.getType() == Material.RED_STAINED_GLASS_PANE || item.getType() == Material.GREEN_STAINED_GLASS_PANE) {
                    itemArray[counter++] = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
                } else {
                    itemArray[counter++] = item;
                }
            }
            else{
                counter++;
            }

        }
        inv.setContents(itemArray);
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public int getTimerID() {
        return timerID;
    }

    public void setTimerID(int timerID) {
        this.timerID = timerID;
    }
}
