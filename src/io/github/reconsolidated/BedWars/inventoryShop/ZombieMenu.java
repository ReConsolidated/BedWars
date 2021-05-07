package io.github.reconsolidated.BedWars.inventoryShop;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.VillagerMenuItemClickListener;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.DisableDraging;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.DisableMoving;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.ZombieMenuItemClickListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;

/**
 * Class responsible for the BedWars shop.
 * Creating an instance opens a shop menu for the player passed as the 2nd parameter.
 * TEST
 */

public class ZombieMenu implements Listener {

    private final BedWars plugin;
    private Inventory inv;

    private Participant participant;
    private int timerID;
    private String color;
    private Player player;

    public ZombieMenu(BedWars plugin, Player player, String type) {
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
    private int getArmorCost(){
        return (int) Math.pow(2, participant.team.protLevel + 1);
    }

    private int getHasteCost(){
        return (int) Math.pow(2, participant.team.hasteLevel + 1);
    }

    private int getResourcesCost(){
        return 2*(participant.team.resourcesLevel + 1);
    }

    public void addItems(String type) {
        this.inv.clear();
        switch (type){
            case "diamond" ->{
                this.inv.setItem(1, CustomItemStack.createCustomItemStack(Material.IRON_SWORD, 1, Material.DIAMOND, 4, "diamond", new ArrayList<>(), player));
                this.inv.setItem(2, CustomItemStack.createCustomItemStack(Material.IRON_CHESTPLATE, 1, Material.DIAMOND, getArmorCost(), "diamond", new ArrayList<>(), player));
                this.inv.setItem(3, CustomItemStack.createCustomItemStack(Material.GOLDEN_PICKAXE, 1, Material.IRON_INGOT, getHasteCost(), "diamond", new ArrayList<>(), player));
                this.inv.setItem(4, CustomItemStack.createCustomItemStack(Material.FURNACE, 1, Material.DIAMOND, getResourcesCost(), "diamond", new ArrayList<>(), player));
                this.inv.setItem(5, CustomItemStack.createCustomItemStack(Material.BEACON, 1, Material.DIAMOND, 1, "diamond", new ArrayList<>(), player));
                this.inv.setItem(6, CustomItemStack.createCustomItemStack(Material.DRAGON_EGG, 1, Material.DIAMOND, 5, "diamond", new ArrayList<>(), player));
                this.inv.setItem(7, CustomItemStack.createCustomItemStack(Material.LEATHER, 1, Material.DIAMOND, -1, "traps", new ArrayList<>(), player));

                this.inv.setItem(21, participant.team.getTrapItems().get(0));
                this.inv.setItem(22, participant.team.getTrapItems().get(1));
                this.inv.setItem(23, participant.team.getTrapItems().get(2));
            }
            case "traps" ->{
                this.inv.setItem(1, CustomItemStack.createCustomItemStack(Material.TRIPWIRE_HOOK, 1, Material.DIAMOND, 1, "diamond", new ArrayList<>(), player));
                this.inv.setItem(2, CustomItemStack.createCustomItemStack(Material.FEATHER, 1, Material.DIAMOND, 1, "diamond", new ArrayList<>(), player));
                this.inv.setItem(3, CustomItemStack.createCustomItemStack(Material.REDSTONE_TORCH, 1, Material.DIAMOND, 1, "diamond", new ArrayList<>(), player));
                this.inv.setItem(4, CustomItemStack.createCustomItemStack(Material.IRON_PICKAXE, 1, Material.DIAMOND, 1, "diamond", new ArrayList<>(), player));
                this.inv.setItem(5, CustomItemStack.createCustomItemStack(Material.ARROW, 1, Material.IRON_INGOT, -1, "back", new ArrayList<>(), player));
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
        plugin.getServer().getPluginManager().registerEvents(new ZombieMenuItemClickListener(plugin,this), plugin);
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
