package io.github.reconsolidated.BedWars.inventoryShop;

import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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

import static io.github.reconsolidated.BedWars.inventoryShop.buyMethods.Buy.canAfford;

public class CustomItemStack {
    private static YamlConfiguration customConfig;

    public static ItemStack createCustomItemStack(Material material, int amount, Material costMaterial, int cost, String category, List<String> lore, Player player){
        ItemStack itemStack = new ItemStack(material, amount);
        itemStack = NbtWrapper.setNBTTag("category", category, itemStack);
        if (cost > 0){
            itemStack = NbtWrapper.setNBTTag("cost_amount", Integer.toString(cost), itemStack);
            itemStack = NbtWrapper.setNBTTag("cost_material", costMaterial.toString(), itemStack);

            ItemMeta meta = itemStack.getItemMeta();
            String name = "";
            if (category.equals("diamond") || category.equals("traps") || category.equals("back")){
                meta.setLore(getDiamondLore(material, itemStack));
                name = getDiamondName(itemStack);
            }
            else{
                meta.setLore(getLore(material, itemStack, player, new ItemStack(costMaterial, cost)));
                name = getName(itemStack);
            }

            if (player != null && canAfford(player, new ItemStack(costMaterial, cost))){
                meta.setDisplayName(ChatColor.GREEN + name);
            }
            else if (player != null){
                meta.setDisplayName(ChatColor.RED + name);
            }
            itemStack.setItemMeta(meta);
        }
        else{
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + getCategoryName(itemStack));
            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    private static String getCategoryName(ItemStack item){
        loadCustomConfig("category_item_names_pl");
        if (customConfig.contains(item.getType().toString())) {
            String name = ChatColor
                    .translateAlternateColorCodes('&', (String) customConfig.get(item.getType().toString()));
            return name;
        }
        else{
            Bukkit.broadcastMessage("Nie znaleziono nazwy przedmiotu: " + item.getType().toString());
            return "NIEZNANY PRZEDMIOT";
        }
    }

    private static String getDiamondName(ItemStack item){
        loadCustomConfig("diamond_item_names_pl");
        if (customConfig.contains(item.getType().toString())) {
            String name = ChatColor
                    .translateAlternateColorCodes('&', (String) customConfig.get(item.getType().toString()));
            return name;
        }
        else{
            Bukkit.broadcastMessage("Nie znaleziono nazwy przedmiotu: " + item.getType().toString());
            return "NIEZNANY PRZEDMIOT";
        }
    }

    private static String getName(ItemStack item){
        loadCustomConfig("item_names_pl");
        if (customConfig.contains(item.getType().toString())) {
            String name = ChatColor
                    .translateAlternateColorCodes('&', (String) customConfig.get(item.getType().toString()));
            return name;
        }
        else{
            Bukkit.broadcastMessage("Nie znaleziono nazwy przedmiotu: " + item.getType().toString());
            return "NIEZNANY PRZEDMIOT";
        }
    }


    public static ItemStack createCustomPotion(PotionType type, int amount, Material costMaterial, int cost, String category, Player player){
        ItemStack itemStack = new ItemStack(Material.POTION, amount);
        itemStack = NbtWrapper.setNBTTag("category", category, itemStack);
        if (cost > 0){
            itemStack = NbtWrapper.setNBTTag("cost_amount", Integer.toString(cost), itemStack);
            itemStack = NbtWrapper.setNBTTag("cost_material", costMaterial.toString(), itemStack);

            PotionMeta jumpMeta = (PotionMeta) itemStack.getItemMeta();
            jumpMeta.setBasePotionData(new PotionData(type, false, false));
            jumpMeta.setLore(getLore(Material.POTION, itemStack, player, new ItemStack(costMaterial, cost)));

            if (player != null && canAfford(player, new ItemStack(costMaterial, cost))){
                jumpMeta.setDisplayName(ChatColor.GREEN + itemStack.getType().name());
            }
            else if (player != null){
                jumpMeta.setDisplayName(ChatColor.RED + itemStack.getType().name());
            }

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

    private static List<String> getLore(Material material, ItemStack item, Player player, ItemStack cost){
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
        result.add("");
        if (canAfford(player, cost)){
            result.add(ChatColor.GREEN + "Kliknij, żeby kupić!");
        }
        else{
            result.add(ChatColor.RED + "Nie możesz tego kupić.");
        }
        return result;
    }

    private static YamlConfiguration loadCustomConfig(String name){
        BedWars plugin = (BedWars) Bukkit.getPluginManager().getPlugin("BedWars");
        File customConfigFile = new File(plugin.getDataFolder(), name + ".yml");
        if (!customConfigFile.exists()) {
            try{
                customConfigFile.createNewFile();
                plugin.saveResource(name+".yml", true);
            }
            catch (IOException e){
                Bukkit.broadcastMessage("Nie udalo sie wczytac pliku konfiguracyjnego: " + name);
            }

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
