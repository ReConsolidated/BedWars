package io.github.reconsolidated.BedWars.inventoryShop.buyMethods;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.inventoryShop.NbtWrapper;
import io.github.reconsolidated.BedWars.inventoryShop.VillagerMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Buy {
    public static void item(BedWars plugin, Player player, ItemStack item, VillagerMenu menu) {

        ItemStack cost = getCost(item);
        if (cost == null){
            player.sendMessage("Something unexpected happened, please contact the server administrator.");
        }
        if (canAfford(player, cost)) {
            charge(player, cost);
            giveItem(player, item);
            success(plugin, player, menu);
        } else {
            fail(plugin, player, menu);
        }

    }

    private static void giveItem(Player player, ItemStack item) {
        List<String> lore = new ArrayList<String>();
        ItemMeta meta = item.getItemMeta();
        // removing cost line from lore
        if (meta.getLore() != null){
            for (String loreStr : meta.getLore()) {
                if (!loreStr.contains("Cost")) {
                    lore.add(lore.size(), loreStr);
                }
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        player.getInventory().addItem(item);
    }

    private static void buyArmor(Player player, ItemStack item) {
        Material armorType = item.getType();
//        if (armorType == Material.LEATHER_CHESTPLATE) {
//            new Full_Leather_Armor().equip(player);
//        } else if (armorType == Material.CHAINMAIL_CHESTPLATE) {
//            new Full_Chain_Armor().equip(player);
//        } else if (armorType == Material.IRON_CHESTPLATE) {
//            new Full_Iron_Armor().equip(player);
//        } else if (armorType == Material.GOLDEN_CHESTPLATE) {
//            new Full_Gold_Armor().equip(player);
//        } else if (armorType == Material.DIAMOND_CHESTPLATE) {
//            new Full_Diamond_Armor().equip(player);
//        } else if (armorType == Material.NETHERITE_CHESTPLATE) {
//            int level = item.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
//            new Full_Netherite_Armor(level).equip(player);
//        }
    }

    private static boolean isArmor(ItemStack item) {
        Material itemMaterial = item.getType();
        ArrayList<Material> armorMaterials = new ArrayList<>();
        armorMaterials.add(Material.LEATHER_CHESTPLATE);
        armorMaterials.add(Material.CHAINMAIL_CHESTPLATE);
        armorMaterials.add(Material.IRON_CHESTPLATE);
        armorMaterials.add(Material.GOLDEN_CHESTPLATE);
        armorMaterials.add(Material.DIAMOND_CHESTPLATE);
        armorMaterials.add(Material.NETHERITE_CHESTPLATE);
        for (Material armor : armorMaterials) {
            if (itemMaterial == armor) {
                return true;
            }
        }
        return false;
    }

    private static void success(BedWars plugin, Player player, VillagerMenu menu) {
        Bukkit.getServer().getScheduler().cancelTask(menu.getTimerID());
        menu.setGreenTiles();
        menu.setTimerID(Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, menu::setNormalTiles, 20L));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 3, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6F, 5);
    }

    private static void fail(BedWars plugin, Player player, VillagerMenu menu) {
        Bukkit.getServer().getScheduler().cancelTask(menu.getTimerID());
        menu.setRedTiles();
        menu.setTimerID(Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, menu::setNormalTiles, 20L));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 3, 1);
    }

    /*
        Gets cost from the last lore of item
    */
    private static ItemStack getCost(ItemStack item) {
        int amount = Integer.MAX_VALUE;
        String nbtTag = NbtWrapper.getNBTTag("cost_amount", item);
        if (nbtTag == null) return null;
        try {
            amount = Integer.parseInt(nbtTag);
        }
        catch (NumberFormatException e){
            return null;
        }

        String matName = NbtWrapper.getNBTTag("cost_material", item);
        if (matName == null) return null;
        
        if (Material.getMaterial(matName) == null)
            return null;

        return new ItemStack(Material.getMaterial(matName), amount);
    }

    private static void charge(Player player, ItemStack cost) {
        int amount = cost.getAmount();
        for (int i = 0; i<player.getInventory().getContents().length; i++){
            ItemStack item = player.getInventory().getContents()[i];
            if (item == null)
                continue;
            if (item.getType().equals(cost.getType())){
                while (item.getAmount() > 0 && amount > 0){
                    item.setAmount(item.getAmount()-1);
                    amount--;
                }
                if (item.getAmount() == 0){
                    player.getInventory().remove(item);
                    i--;
                }
            }
            if (amount == 0)
                break;
        }
    }

    private static boolean canAfford(Player player, ItemStack cost) {
        int amount = cost.getAmount();
        for (ItemStack items : player.getInventory().getContents()){
            if (items == null)
                continue;
            if (items.getType().equals(cost.getType())){
                amount -= items.getAmount();
                if (amount <= 0)
                    return true;
            }
        }
        return false;//Main.game.getParticipant(player).getMoney() >= cost;
    }

}
