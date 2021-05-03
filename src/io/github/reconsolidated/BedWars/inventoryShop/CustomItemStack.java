package io.github.reconsolidated.BedWars.inventoryShop;

import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CustomItemStack {
    private static YamlConfiguration customConfig;

    public static ItemStack createCustomItemStack(Material material, int amount, Material costMaterial, int cost, String category, List<String> lore){
        ItemStack itemStack = new ItemStack(material, amount);
        itemStack = NbtWrapper.setNBTTag("category", category, itemStack);
        if (cost > 0){
            itemStack = NbtWrapper.setNBTTag("cost_amount", Integer.toString(cost), itemStack);
            itemStack = NbtWrapper.setNBTTag("cost_material", costMaterial.toString(), itemStack);

            ItemMeta meta = itemStack.getItemMeta();
            if (category.equals("diamond") || category.equals("traps") || category.equals("back"))
                meta.setLore(getDiamondLore(material, itemStack));
            else
                meta.setLore(getLore(material, itemStack));
            itemStack.setItemMeta(meta);
        }


        return itemStack;
    }

    public static ItemStack createCustomPotion(PotionType type, int amount, Material costMaterial, int cost, String category){
        ItemStack itemStack = new ItemStack(Material.POTION, amount);
        itemStack = NbtWrapper.setNBTTag("category", category, itemStack);
        if (cost > 0){
            itemStack = NbtWrapper.setNBTTag("cost_amount", Integer.toString(cost), itemStack);
            itemStack = NbtWrapper.setNBTTag("cost_material", costMaterial.toString(), itemStack);

            PotionMeta jumpMeta = (PotionMeta) itemStack.getItemMeta();
            jumpMeta.setBasePotionData(new PotionData(type, false, false));
            jumpMeta.setLore(getLore(Material.POTION, itemStack));
            itemStack.setItemMeta(jumpMeta);
        }


        return itemStack;
    }

    private static List<String> getDiamondLore(Material material, ItemStack item){
        if (customConfig == null){
            loadCustomConfig("diamond_item_lores");
        }
        loadCustomConfig("diamond_item_lores");
        List<String> result = new ArrayList<String>();
        if (customConfig.contains(material.toString())) {
            String[] lore = ChatColor
                    .translateAlternateColorCodes('&', (String) customConfig.get(material.toString()))
                    .split(";");
            result.addAll(Arrays.asList(lore));
        }
        else{
            Bukkit.broadcastMessage("Nie znaleziono opisu przedmiotu: " + material.toString());
        }
        return result;
    }

    private static List<String> getLore(Material material, ItemStack item){
        if (customConfig == null){
            loadCustomConfig("item_lores");
        }
        loadCustomConfig("item_lores");
        List<String> result = new ArrayList<String>();
        if (material == Material.BOW){
            if (item.getEnchantments().containsKey(Enchantment.ARROW_KNOCKBACK)){
                String[] lore = ChatColor
                        .translateAlternateColorCodes('&', (String) customConfig.get("BOW_2"))
                        .split(";");
                result.addAll(Arrays.asList(lore));
            }
            else if (item.getEnchantments().containsKey(Enchantment.ARROW_DAMAGE)){
                String[] lore = ChatColor
                        .translateAlternateColorCodes('&', (String) customConfig.get("BOW_1"))
                        .split(";");
                result.addAll(Arrays.asList(lore));
            }
            else {
                String[] lore = ChatColor
                        .translateAlternateColorCodes('&', (String) customConfig.get("BOW"))
                        .split(";");
                result.addAll(Arrays.asList(lore));
            }
        }
        else if (customConfig.contains(material.toString())) {
            String[] lore = ChatColor
                    .translateAlternateColorCodes('&', (String) customConfig.get(material.toString()))
                    .split(";");
            result.addAll(Arrays.asList(lore));
        }
        else if(material == Material.POTION){
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            if (meta.getBasePotionData().getType() == PotionType.INVISIBILITY){
                String[] lore = ChatColor
                        .translateAlternateColorCodes('&', (String) customConfig.get("POTION_INVISIBILITY"))
                        .split(";");
                result.addAll(Arrays.asList(lore));
            }
            if (meta.getBasePotionData().getType() == PotionType.JUMP){
                String[] lore = ChatColor
                        .translateAlternateColorCodes('&', (String) customConfig.get("POTION_JUMP"))
                        .split(";");
                result.addAll(Arrays.asList(lore));
            }
            if (meta.getBasePotionData().getType() == PotionType.INVISIBILITY){
                String[] lore = ChatColor
                        .translateAlternateColorCodes('&', (String) customConfig.get("POTION_SPEED"))
                        .split(";");
                result.addAll(Arrays.asList(lore));
            }
        }
        else{
            Bukkit.broadcastMessage("Nie znaleziono opisu przedmiotu: " + material.toString());
        }
        return result;
    }

    private static YamlConfiguration loadCustomConfig(String name){
        BedWars plugin = (BedWars) Bukkit.getPluginManager().getPlugin("BedWars");
        File customConfigFile = new File(plugin.getDataFolder(), name + ".yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            plugin.saveResource(name+".yml", false);
        }
        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return customConfig;
    }
}
