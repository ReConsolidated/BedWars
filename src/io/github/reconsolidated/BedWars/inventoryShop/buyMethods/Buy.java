package io.github.reconsolidated.BedWars.inventoryShop.buyMethods;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
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
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Buy {
    public static void item(BedWars plugin, Player player, ItemStack item, VillagerMenu menu) {
        ItemStack cost = getCost(item);
        if (cost == null){
            player.sendMessage("Something unexpected happened, please contact the server administrator.");
            return;
        }
        if (canAfford(player, cost)) {
            charge(player, cost);
            giveItem(plugin, player, item);
            success(plugin, player, menu);
            menu.addItems("main");
        } else {
            fail(plugin, player, menu);
        }
    }

    private static void giveItem(BedWars plugin, Player player, ItemStack item) {
        List<String> lore = new ArrayList<String>();
        ItemStack newItem = new ItemStack(item.getType(), item.getAmount());
        newItem.addUnsafeEnchantments(item.getEnchantments());
        if (item.getType().equals(Material.POTION)){
            item.getItemMeta().setLore(Collections.emptyList());
            newItem.setItemMeta(item.getItemMeta());
        }
        if (isArmor(item)){
            buyArmor(plugin, player, item);
            return;
        }
        Participant p = plugin.getParticipant(player);
        if (p != null){
            if (isPickaxe(item)){
                p.upgradePickaxe();
                p.team.updateEnchants();
                return;
            }
            if (isAxe(item)){
                p.upgradeAxe();
                p.team.updateEnchants();
                return;
            }
            if (item.getType().equals(Material.SHEARS)){
                p.upgradeShears();
                p.team.updateEnchants();
                return;
            }
            if (item.getType().equals(Material.STONE_SWORD)
                    || item.getType().equals(Material.IRON_SWORD)
                    || item.getType().equals(Material.DIAMOND_SWORD)){
                charge(player, new ItemStack(Material.WOODEN_SWORD));
                player.getInventory().addItem(newItem);
                p.team.updateEnchants();
                return;
            }

        }

        player.getInventory().addItem(newItem);
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

    private static void buyArmor(BedWars plugin, Player player, ItemStack item) {
        Material armorType = item.getType();
        ItemStack[] armor = player.getInventory().getArmorContents();
        if (armorType == Material.CHAINMAIL_BOOTS) {
            armor[0] = new ItemStack(Material.CHAINMAIL_BOOTS);
            armor[1] = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        }
        if (armorType == Material.IRON_BOOTS) {
            armor[0] = new ItemStack(Material.IRON_BOOTS);
            armor[1] = new ItemStack(Material.IRON_LEGGINGS);
        }
        if (armorType == Material.DIAMOND_BOOTS) {
            armor[0] = new ItemStack(Material.DIAMOND_BOOTS);
            armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        }

        Participant p = plugin.getParticipant(player);
        if (p != null){
            p.team.updateEnchants();
        }

        player.getInventory().setArmorContents(armor);
    }

    private static boolean isArmor(ItemStack item) {
        Material itemMaterial = item.getType();
        ArrayList<Material> armorMaterials = new ArrayList<>();
        armorMaterials.add(Material.LEATHER_BOOTS);
        armorMaterials.add(Material.CHAINMAIL_BOOTS);
        armorMaterials.add(Material.IRON_BOOTS);
        armorMaterials.add(Material.GOLDEN_BOOTS);
        armorMaterials.add(Material.DIAMOND_BOOTS);
        armorMaterials.add(Material.NETHERITE_BOOTS);
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

    public static void charge(Player player, ItemStack cost) {
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

    public static boolean canAfford(Player player, ItemStack cost) {
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
