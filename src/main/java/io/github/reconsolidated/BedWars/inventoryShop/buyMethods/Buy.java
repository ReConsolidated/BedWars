package io.github.reconsolidated.BedWars.inventoryShop.buyMethods;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.inventoryShop.NbtWrapper;
import io.github.reconsolidated.BedWars.inventoryShop.VillagerMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

import static io.github.reconsolidated.BedWars.Participant.unbreakable;

public class Buy {
    public static void item(BedWars plugin, Player player, ItemStack item, VillagerMenu menu) {
        ItemStack cost = getCost(item);
        if (cost == null){
            player.sendMessage("Something unexpected happened, please contact the server administrator.");
            return;
        }
        if (isArmor(item)){
            switch (player.getInventory().getBoots().getType()){
                case DIAMOND_BOOTS:
                    player.sendMessage(ChatColor.RED + "Masz już najlepszą zbroję.");
                    fail(plugin, player, menu);
                    return;
                case IRON_BOOTS:
                    if (item.getType().equals(Material.IRON_BOOTS)){
                        player.sendMessage(ChatColor.RED + "Masz już taką zbroję.");
                        fail(plugin, player, menu);
                        return;
                    }
                    if (item.getType().equals(Material.CHAINMAIL_BOOTS)){
                        player.sendMessage(ChatColor.RED + "Masz już lepszą zbroję.");
                        fail(plugin, player, menu);
                        return;
                    }
                    break;
                case CHAINMAIL_BOOTS:
                    if (item.getType().equals(Material.CHAINMAIL_BOOTS)){
                        player.sendMessage(ChatColor.RED + "Masz już taką zbroję.");
                        fail(plugin, player, menu);
                        return;
                    }
                    break;
            }
        }
        if (isPickaxe(item)){
            Participant p = plugin.getParticipant(player);
            if (p.isPickaxeMaxed()){
                fail(plugin, player, menu);
                player.sendMessage(ChatColor.RED + "Masz już najlepszy kilof.");
                return;
            }
        }
        if (isAxe(item)){
            Participant p = plugin.getParticipant(player);
            if (p.isAxeMaxed()){
                fail(plugin, player, menu);
                player.sendMessage(ChatColor.RED + "Masz już najlepszą siekierę.");
                return;
            }
        }
        if (item.getType().equals(Material.SHEARS)){
            Participant p = plugin.getParticipant(player);
            if (p.hasShears()){
                fail(plugin, player, menu);
                player.sendMessage(ChatColor.RED + "Masz już nożyce.");
                return;
            }
        }
        if (hasFullInventory(player)){
            player.sendMessage(ChatColor.RED + "Masz pełny ekwipunek.");
            fail(plugin, player, menu);
            return;
        }
        if (canAfford(player, cost)) {
            charge(player, cost);
            giveItem(plugin, player, item);
            success(plugin, player, menu, item);
            menu.addItems(menu.getCurrentCategory());
        }
        else {
            int needed = getNeededAmount(player, cost);
            if (cost.getType().equals(Material.IRON_INGOT)){
                player.sendMessage(ChatColor.RED + "Brakuje ci " + needed + "x ŻELAZO żeby to kupić");
            }
            if (cost.getType().equals(Material.GOLD_INGOT)){
                player.sendMessage(ChatColor.RED + "Brakuje ci " + needed + "x ZŁOTO żeby to kupić");
            }
            if (cost.getType().equals(Material.DIAMOND)){
                player.sendMessage(ChatColor.RED + "Brakuje ci " + needed + "x DIAMENT żeby to kupić");
            }
            if (cost.getType().equals(Material.EMERALD)){
                player.sendMessage(ChatColor.RED + "Brakuje ci " + needed + "x SZMARAGD żeby to kupić");
            }

            fail(plugin, player, menu);
        }
    }

    private static boolean hasFullInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()){
            if (item == null || item.getType().equals(Material.AIR)){
                return false;
            }
        }
        return true;
    }

    private static void giveItem(BedWars plugin, Player player, ItemStack item) {
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
        if (hasFullInventory(player)){
            player.sendMessage(ChatColor.RED + "Masz pełny ekwipunek.");
            return;
        }
        Participant p = plugin.getParticipant(player);
        if (p != null){
            if (isPickaxe(item)){
                p.upgradePickaxe();
                p.getTeam().updateEnchants();
                return;
            }
            if (isAxe(item)){
                p.upgradeAxe();
                p.getTeam().updateEnchants();
                return;
            }
            if (item.getType().equals(Material.SHEARS)){
                p.upgradeShears();
                p.getTeam().updateEnchants();
                return;
            }
            if (item.getType().equals(Material.STONE_SWORD)
                    || item.getType().equals(Material.IRON_SWORD)
                    || item.getType().equals(Material.DIAMOND_SWORD)){
                charge(player, new ItemStack(Material.WOODEN_SWORD));
                player.getInventory().addItem(unbreakable(newItem));
                p.getTeam().updateEnchants();
                return;
            }

        }

        if (newItem.getType().equals(Material.BOW)){
            newItem = unbreakable(newItem);
        }

        player.getInventory().addItem(newItem);
    }

    private static boolean isPickaxe(ItemStack item){
        if (item.getType().equals(Material.WOODEN_PICKAXE)
        || item.getType().equals(Material.STONE_PICKAXE)
        || item.getType().equals(Material.IRON_PICKAXE)
        || item.getType().equals(Material.GOLDEN_PICKAXE)
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
            armor[0] = unbreakable(new ItemStack(Material.CHAINMAIL_BOOTS));
            armor[1] = unbreakable(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        }
        if (armorType == Material.IRON_BOOTS) {
            armor[0] = unbreakable(new ItemStack(Material.IRON_BOOTS));
            armor[1] = unbreakable(new ItemStack(Material.IRON_LEGGINGS));
        }
        if (armorType == Material.DIAMOND_BOOTS) {
            armor[0] = unbreakable(new ItemStack(Material.DIAMOND_BOOTS));
            armor[1] = unbreakable(new ItemStack(Material.DIAMOND_LEGGINGS));
        }

        Participant p = plugin.getParticipant(player);
        if (p != null){
            p.getTeam().updateEnchants();
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

    private static void success(BedWars plugin, Player player, VillagerMenu menu, ItemStack item) {
        Bukkit.getServer().getScheduler().cancelTask(menu.getTimerID());
        menu.setGreenTiles();
        menu.setTimerID(Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, menu::setNormalTiles, 20L));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 3, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6F, 5);
        player.sendMessage(ChatColor.GREEN + "Zakupiono " + ChatColor.GOLD + item.getItemMeta().getDisplayName());
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
        boolean hasEmptySlot = false;
        for (ItemStack its : player.getInventory().getContents()){
            if (its == null || its.getType().equals(Material.AIR)){
                hasEmptySlot = true;
            }
        }
        if (!hasEmptySlot) return false;

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

    public static int getNeededAmount(Player player, ItemStack cost) {
        int amount = cost.getAmount();
        for (ItemStack items : player.getInventory().getContents()){
            if (items == null)
                continue;
            if (items.getType().equals(cost.getType())){
                amount -= items.getAmount();
                if (amount <= 0)
                    return 0;
            }
        }
        return amount;
    }

}
