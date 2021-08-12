package io.github.reconsolidated.BedWars.inventoryShop.buyMethods;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.inventoryShop.NbtWrapper;
import io.github.reconsolidated.BedWars.inventoryShop.VillagerMenu;
import io.github.reconsolidated.BedWars.inventoryShop.ZombieMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class ZombieBuy {
    public static void item(BedWars plugin, Player player, ItemStack item, ZombieMenu menu) {
        ItemStack cost = getCost(item);
        if (cost == null){
            player.sendMessage("Something unexpected happened, please contact the server administrator.");
        }
        if (canBuy(plugin, player, item)) {
            charge(player, cost);
            giveItem(plugin, player, item);
            success(plugin, player, menu);
        }
        else {

            fail(plugin, player, menu);
        }
    }

    private static void giveItem(BedWars plugin, Player player, ItemStack item) {
        Participant p = plugin.getParticipant(player);
        if (p != null){
            if (item.getType().equals(Material.IRON_SWORD)){
                p.getTeam().upgradeSharp();
                return;
            }
            if (item.getType().equals(Material.IRON_CHESTPLATE)){
                p.getTeam().upgradeProt();
                return;
            }
            if (item.getType().equals(Material.GOLDEN_PICKAXE)){
                p.getTeam().upgradeHaste();
                return;
            }
            if (item.getType().equals(Material.FURNACE)){
                p.getTeam().upgradeResources();
                return;
            }
            if (item.getType().equals(Material.BEACON)){
                p.getTeam().setHealPool();
                return;
            }
            if (item.getType().equals(Material.DRAGON_EGG)){
                p.getTeam().addDragon();
                return;
            }
            if (item.getType().equals(Material.TRIPWIRE_HOOK)
                    || item.getType().equals(Material.FEATHER)
                    || item.getType().equals(Material.REDSTONE_TORCH)
                    || item.getType().equals(Material.FEATHER)
                    || item.getType().equals(Material.IRON_PICKAXE)
                    || item.getType().equals(Material.FEATHER)){
                p.getTeam().addTrap(item.getType());
                return;
            }
        }
    }



    private static void success(BedWars plugin, Player player, ZombieMenu menu) {
        Bukkit.getServer().getScheduler().cancelTask(menu.getTimerID());
        menu.setGreenTiles();
        menu.setTimerID(Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, menu::setNormalTiles, 20L));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 3, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6F, 5);
    }

    private static void fail(BedWars plugin, Player player, ZombieMenu menu) {
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

    private static boolean canBuy(BedWars plugin, Player player, ItemStack item) {
        ItemStack cost = getCost(item);
        int amount = cost.getAmount();
        boolean canBuy = false;
        for (ItemStack items : player.getInventory().getContents()){
            if (items == null)
                continue;
            if (items.getType().equals(cost.getType())){
                amount -= items.getAmount();
                if (amount <= 0)
                    canBuy = true;
            }
        }
        if (!canBuy){
            player.sendMessage(ChatColor.RED + "Brakuje ci " + amount + "x DIAMENT żeby to kupić");
        }
        Participant p = plugin.getParticipant(player);
        if (p == null) return false;
        if (item.getType().equals(Material.IRON_SWORD)){
            if (p.getTeam().sharpLevel >= 1){
                player.sendMessage(ChatColor.RED + "Twoja drużyna ma już dodatkowe obrażenia do mieczy.");
                return false;
            }
        }
        if (item.getType().equals(Material.IRON_CHESTPLATE)){
            if (p.getTeam().protLevel >= 4){
                player.sendMessage(ChatColor.RED + "Twoja drużyna ma już najwyższy poziom zbroi.");
                return false;
            }
        }
        if (item.getType().equals(Material.GOLDEN_PICKAXE)){
            if (p.getTeam().hasteLevel >= 2){
                player.sendMessage(ChatColor.RED + "Twoja drużyna ma najwyzszy poziom pośpiechu.");
                return false;
            }
        }
        if (item.getType().equals(Material.FURNACE)){
            if (p.getTeam().resourcesLevel >= 4){
                player.sendMessage(ChatColor.RED + "Twoja drużyna ma już najwyższy poziom spawnera.");
                return false;
            }
        }
        if (item.getType().equals(Material.BEACON)){
            if (p.getTeam().hasHealPool()){
                player.sendMessage(ChatColor.RED + "Twoja drużyna ma już pole ochronne.");
                return false;
            }
        }
        if (item.getType().equals(Material.DRAGON_EGG)){
            if (p.getTeam().dragons == 2){
                player.sendMessage(ChatColor.RED + "Nie możesz kupić więcej smoków.");
                return false;
            }
        }
        if (item.getType().equals(Material.TRIPWIRE_HOOK)
                || item.getType().equals(Material.FEATHER)
                || item.getType().equals(Material.REDSTONE_TORCH)
                || item.getType().equals(Material.FEATHER)
                || item.getType().equals(Material.IRON_PICKAXE)
                || item.getType().equals(Material.FEATHER)){
            if (!p.getTeam().canAddTrap()){
                player.sendMessage(ChatColor.RED + "Masz już maksymalną liczbę pułapek.");
                return false;
            }
            for (ItemStack trapItem : p.getTeam().getTrapItems()){
                if (trapItem.getType().equals(item.getType())){
                    player.sendMessage(ChatColor.RED + "Masz już taką pułapkę.");
                    return false;
                }
            }

        }

        return canBuy;//Main.game.getParticipant(player).getMoney() >= cost;
    }

}
