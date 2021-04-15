package io.github.reconsolidated.BedWars.inventoryShop;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.MenuItemClickListener;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.DisableDraging;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.DisableMoving;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;

/**
 * Class responsible for the BedWars shop.
 * Creating an instance opens a shop menu for the player passed as the 2nd parameter.
 */

public class VillagerMenu implements Listener {

    private final BedWars plugin;
    private Inventory inv;
    private Player player;
    private int timerID;

    public VillagerMenu(BedWars plugin, Player player, String type) {
        this.plugin = plugin;
        this.player = player;
        this.inv = Bukkit.createInventory(null, 54, "Sklep");
        addItems(type);
        fillEmptySpace();
        registerEvents();
        player.openInventory(inv);
    }

    private void addItems(String type) {
        switch (type){
            case "main" -> {
                this.inv.setItem(1, CustomItemStack.createCustomItemStack(Material.RED_TERRACOTTA, 1, Material.IRON_INGOT, -1, "blocks", new ArrayList<>()));
                this.inv.setItem(2, CustomItemStack.createCustomItemStack(Material.GOLDEN_SWORD, 1, Material.IRON_INGOT, -1, "melee", new ArrayList<>()));
                this.inv.setItem(3, CustomItemStack.createCustomItemStack(Material.CHAINMAIL_BOOTS, 1, Material.IRON_INGOT, -1, "armor", new ArrayList<>()));
                this.inv.setItem(4, CustomItemStack.createCustomItemStack(Material.STONE_PICKAXE, 1, Material.IRON_INGOT, -1, "tools", new ArrayList<>()));
                this.inv.setItem(5, CustomItemStack.createCustomItemStack(Material.BOW, 1, Material.IRON_INGOT, -1, "ranged", new ArrayList<>()));
                this.inv.setItem(6, CustomItemStack.createCustomItemStack(Material.BREWING_STAND, 1, Material.IRON_INGOT, -1, "potions", new ArrayList<>()));
                this.inv.setItem(7, CustomItemStack.createCustomItemStack(Material.TNT, 1, Material.IRON_INGOT, -1, "utility", new ArrayList<>()));
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.WHITE_WOOL, 16, Material.IRON_INGOT, 4, "", new ArrayList<>()));
            }
            case "blocks" -> {
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.WHITE_TERRACOTTA, 16, Material.IRON_INGOT, 12, "", new ArrayList<>()));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.WHITE_TERRACOTTA, 16, Material.IRON_INGOT, 12, "", new ArrayList<>()));
                this.inv.setItem(21, CustomItemStack.createCustomItemStack(Material.GLASS, 4, Material.IRON_INGOT, 12, "", new ArrayList<>()));
                this.inv.setItem(22, CustomItemStack.createCustomItemStack(Material.END_STONE, 12, Material.IRON_INGOT, 24, "", new ArrayList<>()));
                this.inv.setItem(23, CustomItemStack.createCustomItemStack(Material.LADDER, 16, Material.IRON_INGOT, 4, "", new ArrayList<>()));
                this.inv.setItem(24, CustomItemStack.createCustomItemStack(Material.OAK_PLANKS, 16, Material.GOLD_INGOT, 4, "", new ArrayList<>()));
                this.inv.setItem(25, CustomItemStack.createCustomItemStack(Material.OBSIDIAN, 4, Material.EMERALD, 4, "", new ArrayList<>()));
            }
            case "melee" -> {
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.STONE_SWORD, 1, Material.IRON_INGOT, 10, "", new ArrayList<>()));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.IRON_SWORD, 1, Material.GOLD_INGOT, 7, "", new ArrayList<>()));
                this.inv.setItem(21, CustomItemStack.createCustomItemStack(Material.DIAMOND_SWORD, 1, Material.EMERALD, 4, "", new ArrayList<>()));
                ItemStack item = CustomItemStack.createCustomItemStack(Material.STICK, 1, Material.GOLD_INGOT, 5, "", new ArrayList<>());
                item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                this.inv.setItem(22, item);
            }
            case "armor" -> {
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.CHAINMAIL_BOOTS, 1, Material.IRON_INGOT, 40, "armor", new ArrayList<>()));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.IRON_BOOTS, 1, Material.GOLD_INGOT, 12, "armor", new ArrayList<>()));
                this.inv.setItem(21, CustomItemStack.createCustomItemStack(Material.DIAMOND_BOOTS, 1, Material.EMERALD, 6, "armor", new ArrayList<>()));
            }
            case "tools" -> {
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.SHEARS, 1, Material.IRON_INGOT, 20, "", new ArrayList<>()));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.WOODEN_PICKAXE, 1, Material.IRON_INGOT, 10, "", new ArrayList<>()));
                this.inv.setItem(21, CustomItemStack.createCustomItemStack(Material.WOODEN_AXE, 1, Material.IRON_INGOT, 10, "", new ArrayList<>()));
            }
            case "ranged" -> {
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.ARROW, 8, Material.GOLD_INGOT, 2, "", new ArrayList<>()));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.BOW, 1, Material.GOLD_INGOT, 12, "", new ArrayList<>()));
                ItemStack item = CustomItemStack.createCustomItemStack(Material.BOW, 1, Material.GOLD_INGOT, 24, "", new ArrayList<>());
                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                this.inv.setItem(21, item);
                ItemStack item2 = CustomItemStack.createCustomItemStack(Material.BOW, 1, Material.EMERALD, 6, "", new ArrayList<>());
                item2.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                item2.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
                this.inv.setItem(22, item2);
            }
            case "potions" -> {
                ItemStack speedPotion = CustomItemStack.createCustomItemStack(Material.POTION, 1, Material.EMERALD, 1, "", new ArrayList<>());
                PotionMeta speedMeta = (PotionMeta) speedPotion.getItemMeta();
                speedMeta.setBasePotionData(new PotionData(PotionType.SPEED, false, true));
                speedPotion.setItemMeta(speedMeta);
                this.inv.setItem(19, speedPotion);

                ItemStack jumpPotion = CustomItemStack.createCustomItemStack(Material.POTION, 1, Material.EMERALD, 1, "", new ArrayList<>());
                PotionMeta jumpMeta = (PotionMeta) jumpPotion.getItemMeta();
                jumpMeta.setBasePotionData(new PotionData(PotionType.JUMP, false, true));
                jumpPotion.setItemMeta(jumpMeta);
                this.inv.setItem(20, jumpPotion);

                ItemStack invisPotion = CustomItemStack.createCustomItemStack(Material.POTION, 1, Material.EMERALD, 2, "", new ArrayList<>());
                PotionMeta invisMeta = (PotionMeta) invisPotion.getItemMeta();
                invisMeta.setBasePotionData(new PotionData(PotionType.INVISIBILITY, false, false));
                invisPotion.setItemMeta(invisMeta);
                this.inv.setItem(21, invisPotion);
            }
            case "utility" -> {
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.GOLDEN_APPLE, 1, Material.GOLD_INGOT, 3, "", new ArrayList<>()));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.SNOWBALL, 1, Material.IRON_INGOT, 40, "", new ArrayList<>()));
                this.inv.setItem(21, CustomItemStack.createCustomItemStack(Material.POLAR_BEAR_SPAWN_EGG, 1, Material.IRON_INGOT, 120, "", new ArrayList<>()));
                this.inv.setItem(22, CustomItemStack.createCustomItemStack(Material.FIRE_CHARGE, 1, Material.IRON_INGOT, 40, "", new ArrayList<>()));
                this.inv.setItem(23, CustomItemStack.createCustomItemStack(Material.TNT, 1, Material.GOLD_INGOT, 4, "", new ArrayList<>()));
                this.inv.setItem(24, CustomItemStack.createCustomItemStack(Material.ENDER_PEARL, 1, Material.EMERALD, 4, "", new ArrayList<>()));
                this.inv.setItem(25, CustomItemStack.createCustomItemStack(Material.WATER_BUCKET, 1, Material.GOLD_INGOT, 3, "", new ArrayList<>()));
                this.inv.setItem(26, CustomItemStack.createCustomItemStack(Material.EGG, 1, Material.EMERALD, 2, "", new ArrayList<>()));
                this.inv.setItem(27, CustomItemStack.createCustomItemStack(Material.MILK_BUCKET, 1, Material.GOLD_INGOT, 4, "", new ArrayList<>()));
                this.inv.setItem(28, CustomItemStack.createCustomItemStack(Material.SPONGE, 4, Material.GOLD_INGOT, 3, "", new ArrayList<>()));
                this.inv.setItem(29, CustomItemStack.createCustomItemStack(Material.CHEST, 1, Material.IRON_INGOT, 24, "", new ArrayList<>()));
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
        plugin.getServer().getPluginManager().registerEvents(new MenuItemClickListener(plugin,this), plugin);
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
