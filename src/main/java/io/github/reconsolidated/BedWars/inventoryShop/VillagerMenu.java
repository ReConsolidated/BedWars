package io.github.reconsolidated.BedWars.inventoryShop;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.VillagerMenuItemClickListener;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.DisableDraging;
import io.github.reconsolidated.BedWars.inventoryShop.Listeners.DisableMoving;
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

public class VillagerMenu implements Listener {

    private final BedWars plugin;
    private Inventory inv;
    private Player player;
    private int timerID;
    private String color;
    private String currentCategory;

    public VillagerMenu(BedWars plugin, Player player, String type) {
        this.plugin = plugin;
        this.player = player;
        this.inv = Bukkit.createInventory(null, 54, "Sklep");
        this.color = "WHITE";
        Participant p = plugin.getParticipant(player);
        if (p != null){
            this.color = p.getColor();
        }

        addItems(type);
        registerEvents();
        player.openInventory(inv);

    }

    public String getCurrentCategory(){
        return currentCategory;
    }

    public void addItems(String type) {
        Participant p = plugin.getParticipant(player);
        if (p == null) return;
        currentCategory = type;
        inv.clear();
        switch (type){
            case "main" -> {
                this.inv.setItem(1, CustomItemStack.createCustomItemStack(Material.WHITE_TERRACOTTA, 1, Material.IRON_INGOT, -1, "blocks", new ArrayList<>(), player));
                this.inv.setItem(2, CustomItemStack.createCustomItemStack(Material.GOLDEN_SWORD, 1, Material.IRON_INGOT, -1, "melee", new ArrayList<>(), player));
                this.inv.setItem(3, CustomItemStack.createCustomItemStack(Material.CHAINMAIL_BOOTS, 1, Material.IRON_INGOT, -1, "armor", new ArrayList<>(), player));
                this.inv.setItem(4, CustomItemStack.createCustomItemStack(Material.STONE_PICKAXE, 1, Material.IRON_INGOT, -1, "tools", new ArrayList<>(), player));
                this.inv.setItem(5, CustomItemStack.createCustomItemStack(Material.BOW, 1, Material.IRON_INGOT, -1, "ranged", new ArrayList<>(), player));
                this.inv.setItem(6, CustomItemStack.createCustomItemStack(Material.BREWING_STAND, 1, Material.IRON_INGOT, -1, "potions", new ArrayList<>(), player));
                this.inv.setItem(7, CustomItemStack.createCustomItemStack(Material.TNT, 1, Material.IRON_INGOT, -1, "utility", new ArrayList<>(), player));
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.getMaterial(color + "_WOOL"), 16, Material.IRON_INGOT, 4, "", new ArrayList<>(), player));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.STONE_SWORD, 1, Material.IRON_INGOT, 10, "", new ArrayList<>(), player));
                this.inv.setItem(21, CustomItemStack.createCustomItemStack(Material.CHAINMAIL_BOOTS, 1, Material.IRON_INGOT, 40, "", new ArrayList<>(), player));
                if (p.getPickaxeLevel() == 0)
                    this.inv.setItem(22, CustomItemStack.createCustomItemStack(Material.WOODEN_PICKAXE, 1, Material.IRON_INGOT, 10, "", new ArrayList<>(), player));
                if (p.getPickaxeLevel() == 1)
                    this.inv.setItem(22, CustomItemStack.createCustomItemStack(Material.IRON_PICKAXE, 1, Material.IRON_INGOT, 10, "", new ArrayList<>(), player));
                if (p.getPickaxeLevel() == 2)
                    this.inv.setItem(22, CustomItemStack.createCustomItemStack(Material.GOLDEN_PICKAXE, 1, Material.GOLD_INGOT, 3, "", new ArrayList<>(), player));
                if (p.getPickaxeLevel() == 3)
                    this.inv.setItem(22, CustomItemStack.createCustomItemStack(Material.DIAMOND_PICKAXE, 1, Material.GOLD_INGOT, 6, "", new ArrayList<>(), player));


                this.inv.setItem(23, CustomItemStack.createCustomItemStack(Material.BOW, 1, Material.GOLD_INGOT, 12, "", new ArrayList<>(), player));
                this.inv.setItem(24, CustomItemStack.createCustomPotion(PotionType.INVISIBILITY, 1, Material.EMERALD, 2, "", player));
                this.inv.setItem(25, CustomItemStack.createCustomItemStack(Material.TNT, 1, Material.GOLD_INGOT, 4, "", new ArrayList<>(), player));
                this.inv.setItem(28, CustomItemStack.createCustomItemStack(Material.OAK_PLANKS, 16, Material.GOLD_INGOT, 4, "", new ArrayList<>(), player));
                this.inv.setItem(29, CustomItemStack.createCustomItemStack(Material.IRON_SWORD, 1, Material.GOLD_INGOT, 7, "", new ArrayList<>(), player));
                this.inv.setItem(30, CustomItemStack.createCustomItemStack(Material.IRON_BOOTS, 1, Material.GOLD_INGOT, 12, "", new ArrayList<>(), player));
                this.inv.setItem(31, CustomItemStack.createCustomItemStack(Material.SHEARS, 1, Material.IRON_INGOT, 20, "", new ArrayList<>(), player));
                this.inv.setItem(32, CustomItemStack.createCustomItemStack(Material.ARROW, 8, Material.GOLD_INGOT, 2, "", new ArrayList<>(), player));
                this.inv.setItem(33, CustomItemStack.createCustomPotion(PotionType.JUMP, 1, Material.EMERALD, 1, "", player));
                this.inv.setItem(34, CustomItemStack.createCustomItemStack(Material.WATER_BUCKET, 1, Material.GOLD_INGOT, 3, "", new ArrayList<>(), player));

                this.inv.setItem(37, CustomItemStack.createCustomItemStack(Material.getMaterial(color + "_TERRACOTTA"), 16, Material.IRON_INGOT, 12, "", new ArrayList<>(), player));
                this.inv.setItem(38, CustomItemStack.createCustomItemStack(Material.DIAMOND_SWORD, 1, Material.EMERALD, 4, "", new ArrayList<>(), player));
                this.inv.setItem(39, CustomItemStack.createCustomItemStack(Material.DIAMOND_BOOTS, 1, Material.EMERALD, 6, "", new ArrayList<>(), player));

                if (p.getAxeLevel() == 0)
                    this.inv.setItem(40, CustomItemStack.createCustomItemStack(Material.WOODEN_AXE, 1, Material.IRON_INGOT, 10, "", new ArrayList<>(), player));
                if (p.getAxeLevel() == 1)
                    this.inv.setItem(40, CustomItemStack.createCustomItemStack(Material.STONE_AXE, 1, Material.IRON_INGOT, 10, "", new ArrayList<>(), player));
                if (p.getAxeLevel() == 2)
                    this.inv.setItem(40, CustomItemStack.createCustomItemStack(Material.IRON_AXE, 1, Material.GOLD_INGOT, 3, "", new ArrayList<>(), player));
                if (p.getAxeLevel() == 3)
                    this.inv.setItem(40, CustomItemStack.createCustomItemStack(Material.DIAMOND_AXE, 1, Material.GOLD_INGOT, 6, "", new ArrayList<>(), player));

                ItemStack item = CustomItemStack.createCustomItemStack(Material.BOW, 1, Material.GOLD_INGOT, 24, "", new ArrayList<>(), player);
                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                this.inv.setItem(41, item);

                this.inv.setItem(42, CustomItemStack.createCustomPotion(PotionType.SPEED, 1, Material.EMERALD, 1, "", player));
                this.inv.setItem(43, CustomItemStack.createCustomItemStack(Material.GOLDEN_APPLE, 1, Material.GOLD_INGOT, 3, "", new ArrayList<>(), player));

            }
            case "blocks" -> {
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.getMaterial(color + "_WOOL"), 16, Material.IRON_INGOT, 12, "", new ArrayList<>(), player));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.getMaterial(color + "_TERRACOTTA"), 16, Material.IRON_INGOT, 12, "", new ArrayList<>(), player));
                this.inv.setItem(21, CustomItemStack.createCustomItemStack(Material.GLASS, 4, Material.IRON_INGOT, 12, "", new ArrayList<>(), player));
                this.inv.setItem(22, CustomItemStack.createCustomItemStack(Material.END_STONE, 12, Material.IRON_INGOT, 24, "", new ArrayList<>(), player));
                this.inv.setItem(23, CustomItemStack.createCustomItemStack(Material.LADDER, 16, Material.IRON_INGOT, 4, "", new ArrayList<>(), player));
                this.inv.setItem(24, CustomItemStack.createCustomItemStack(Material.OAK_PLANKS, 16, Material.GOLD_INGOT, 4, "", new ArrayList<>(), player));
                this.inv.setItem(25, CustomItemStack.createCustomItemStack(Material.OBSIDIAN, 4, Material.EMERALD, 4, "", new ArrayList<>(), player));
            }
            case "melee" -> {
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.STONE_SWORD, 1, Material.IRON_INGOT, 10, "", new ArrayList<>(), player));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.IRON_SWORD, 1, Material.GOLD_INGOT, 7, "", new ArrayList<>(), player));
                this.inv.setItem(21, CustomItemStack.createCustomItemStack(Material.DIAMOND_SWORD, 1, Material.EMERALD, 4, "", new ArrayList<>(), player));
                ItemStack item = CustomItemStack.createCustomItemStack(Material.STICK, 1, Material.GOLD_INGOT, 5, "", new ArrayList<>(), player);
                item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                this.inv.setItem(22, item);
            }
            case "armor" -> {
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.CHAINMAIL_BOOTS, 1, Material.IRON_INGOT, 40, "", new ArrayList<>(), player));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.IRON_BOOTS, 1, Material.GOLD_INGOT, 12, "", new ArrayList<>(), player));
                this.inv.setItem(21, CustomItemStack.createCustomItemStack(Material.DIAMOND_BOOTS, 1, Material.EMERALD, 6, "", new ArrayList<>(), player));
            }
            case "tools" -> {
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.SHEARS, 1, Material.IRON_INGOT, 20, "", new ArrayList<>(), player));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.WOODEN_PICKAXE, 1, Material.IRON_INGOT, 10, "", new ArrayList<>(), player));
                this.inv.setItem(21, CustomItemStack.createCustomItemStack(Material.WOODEN_AXE, 1, Material.IRON_INGOT, 10, "", new ArrayList<>(), player));
            }
            case "ranged" -> {
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.ARROW, 8, Material.GOLD_INGOT, 2, "", new ArrayList<>(), player));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.BOW, 1, Material.GOLD_INGOT, 12, "", new ArrayList<>(), player));
                ItemStack item = CustomItemStack.createCustomItemStack(Material.BOW, 1, Material.GOLD_INGOT, 24, "", new ArrayList<>(), player);
                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                this.inv.setItem(21, item);
                ItemStack item2 = CustomItemStack.createCustomItemStack(Material.BOW, 1, Material.EMERALD, 6, "", new ArrayList<>(), player);
                item2.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                item2.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
                this.inv.setItem(22, item2);
            }
            case "potions" -> {
                this.inv.setItem(19, CustomItemStack.createCustomPotion(PotionType.SPEED, 1, Material.EMERALD, 1, "", player));
                this.inv.setItem(20, CustomItemStack.createCustomPotion(PotionType.JUMP, 1, Material.EMERALD, 1, "", player));
                this.inv.setItem(21, CustomItemStack.createCustomPotion(PotionType.INVISIBILITY, 1, Material.EMERALD, 2, "", player));
            }
            case "utility" -> {
                this.inv.setItem(19, CustomItemStack.createCustomItemStack(Material.GOLDEN_APPLE, 1, Material.GOLD_INGOT, 3, "", new ArrayList<>(), player));
                this.inv.setItem(20, CustomItemStack.createCustomItemStack(Material.SNOWBALL, 1, Material.IRON_INGOT, 40, "", new ArrayList<>(), player));
                this.inv.setItem(21, CustomItemStack.createCustomItemStack(Material.POLAR_BEAR_SPAWN_EGG, 1, Material.IRON_INGOT, 120, "", new ArrayList<>(), player));
                this.inv.setItem(22, CustomItemStack.createCustomItemStack(Material.FIRE_CHARGE, 1, Material.IRON_INGOT, 40, "", new ArrayList<>(), player));
                this.inv.setItem(23, CustomItemStack.createCustomItemStack(Material.TNT, 1, Material.GOLD_INGOT, 4, "", new ArrayList<>(), player));
                this.inv.setItem(24, CustomItemStack.createCustomItemStack(Material.ENDER_PEARL, 1, Material.EMERALD, 4, "", new ArrayList<>(), player));
                this.inv.setItem(25, CustomItemStack.createCustomItemStack(Material.WATER_BUCKET, 1, Material.GOLD_INGOT, 3, "", new ArrayList<>(), player));
                this.inv.setItem(26, CustomItemStack.createCustomItemStack(Material.EGG, 1, Material.EMERALD, 2, "", new ArrayList<>(), player));
                this.inv.setItem(27, CustomItemStack.createCustomItemStack(Material.MILK_BUCKET, 1, Material.GOLD_INGOT, 4, "", new ArrayList<>(), player));
                this.inv.setItem(28, CustomItemStack.createCustomItemStack(Material.SPONGE, 4, Material.GOLD_INGOT, 3, "", new ArrayList<>(), player));
                this.inv.setItem(29, CustomItemStack.createCustomItemStack(Material.CHEST, 1, Material.IRON_INGOT, 24, "", new ArrayList<>(), player));
            }
        }
        fillEmptySpace();

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
        plugin.getServer().getPluginManager().registerEvents(new VillagerMenuItemClickListener(plugin,this), plugin);
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
