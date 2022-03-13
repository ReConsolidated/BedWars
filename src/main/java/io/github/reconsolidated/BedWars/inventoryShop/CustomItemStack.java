package io.github.reconsolidated.BedWars.inventoryShop;

import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static io.github.reconsolidated.BedWars.inventoryShop.buyMethods.Buy.canAfford;

public class CustomItemStack {
    private static YamlConfiguration customConfig;
    private static YamlConfiguration diamondLoreConfig;
    private static YamlConfiguration diamondNamesConfig;
    private static YamlConfiguration compassLoreConfig;
    private static YamlConfiguration compassNamesConfig;
    private static YamlConfiguration namesConfig;
    private static YamlConfiguration loreConfig;
    private static YamlConfiguration categoryNamesConfig;

    public static ItemStack createCustomItemStack(Material material, int amount, Material costMaterial, int cost, String category, List<String> lore, Player player){
        ItemStack itemStack = new ItemStack(material, amount);

        changeSwordDamage(itemStack);


        itemStack = NbtWrapper.setNBTTag("category", category, itemStack);
        if (cost > 0){
            itemStack = NbtWrapper.setNBTTag("cost_amount", Integer.toString(cost), itemStack);
            itemStack = NbtWrapper.setNBTTag("cost_material", costMaterial.toString(), itemStack);

            ItemMeta meta = itemStack.getItemMeta();
            String name = "";
            if (category.equals("diamond") || category.equals("traps") || category.equals("back") || category.equals("not_clickable")){
                meta.setLore(getDiamondLore(material, itemStack));
                name = getDiamondName(itemStack);
            }
            else if (category.equals("cmp")){
                meta.setLore(getCompassLore(material, itemStack));
                name = getCompassName(itemStack);
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
            if (category.equals("not_clickable")){
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(ChatColor.AQUA + getDiamondName(itemStack));
                meta.setLore(getDiamondLore(itemStack.getType(), itemStack));
                itemStack.setItemMeta(meta);
            }
            else{
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(ChatColor.AQUA + getCategoryName(itemStack));
                itemStack.setItemMeta(meta);
            }

        }

        return itemStack;
    }

    private static void changeSwordDamage(ItemStack item) {
//        if (item.getType() == Material.STONE_SWORD) {
//            ItemMeta meta = item.getItemMeta();
//            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
//                    new AttributeModifier(UUID.randomUUID(),"old_damage", 6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
//            item.setItemMeta(meta);
//        }
//        if (item.getType() == Material.IRON_SWORD) {
//            ItemMeta meta = item.getItemMeta();
//            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
//                    new AttributeModifier(UUID.randomUUID(),"old_damage", 7, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
//            item.setItemMeta(meta);
//        }
//        if (item.getType() == Material.DIAMOND_SWORD) {
//            ItemMeta meta = item.getItemMeta();
//            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
//                    new AttributeModifier(UUID.randomUUID(),"old_damage", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
//            item.setItemMeta(meta);
//        }
    }

    private static String getCompassName(ItemStack item){
        if (compassNamesConfig == null){
            compassNamesConfig = loadCustomConfig("compass_item_names_pl");
        }

        if (compassNamesConfig.contains(item.getType().toString())) {
            String name = ChatColor
                    .translateAlternateColorCodes('&', (String) compassNamesConfig.get(item.getType().toString()));
            return name;
        }
        else{
            Bukkit.broadcastMessage("(Compass names) Nie znaleziono nazwy przedmiotu: " + item.getType().toString());
            return "NIEZNANY PRZEDMIOT";
        }
    }

    private static List<String> getCompassLore(Material material, ItemStack item){
        if (compassLoreConfig == null){
            compassLoreConfig = loadCustomConfig("compass_item_lores");
        }
        List<String> result = new ArrayList<String>();
        if (compassLoreConfig.contains(material.toString())) {
            String[] lore = ChatColor
                    .translateAlternateColorCodes('&', (String) compassLoreConfig.get(material.toString()))
                    .split(";");
            result.addAll(Arrays.asList(lore));
        }
        else{
            Bukkit.broadcastMessage("(Compass Lore) Nie znaleziono opisu przedmiotu: " + material.toString());
        }
        return result;
    }

    private static String getCategoryName(ItemStack item){
        if (categoryNamesConfig == null){
            categoryNamesConfig = loadCustomConfig("category_item_names_pl");
        }

        if (categoryNamesConfig.contains(item.getType().toString())) {
            String name = ChatColor
                    .translateAlternateColorCodes('&', (String) categoryNamesConfig.get(item.getType().toString()));
            return name;
        }
        else{
            Bukkit.broadcastMessage("(Category name) Nie znaleziono nazwy przedmiotu: " + item.getType().toString());
            return "NIEZNANY PRZEDMIOT";
        }
    }

    private static String getDiamondName(ItemStack item){
        if (diamondNamesConfig == null){
            diamondNamesConfig = loadCustomConfig("diamond_item_names_pl");
        }

        if (diamondNamesConfig.contains(item.getType().toString())) {
            String name = ChatColor
                    .translateAlternateColorCodes('&', (String) diamondNamesConfig.get(item.getType().toString()));
            return name;
        }
        else{
            Bukkit.broadcastMessage("(Diamond names) Nie znaleziono nazwy przedmiotu: " + item.getType().toString());
            return "NIEZNANY PRZEDMIOT";
        }
    }

    private static String getName(ItemStack item){
        if (namesConfig == null){
            namesConfig = loadCustomConfig("item_names_pl");
        }

        Material material = item.getType();
        if(material.equals(Material.POTION)){
            PotionMeta meta = (PotionMeta) item.getItemMeta();

            if (meta.hasCustomEffect(PotionEffectType.INVISIBILITY) || meta.getBasePotionData().getType().equals(PotionType.INVISIBILITY)){
                return ChatColor
                        .translateAlternateColorCodes('&', (String) namesConfig.get("POTION_INVISIBILITY"));
            }
            else if (meta.getBasePotionData().getType().equals(PotionType.JUMP)){
                return ChatColor
                        .translateAlternateColorCodes('&', (String) namesConfig.get("POTION_JUMP"));
            }
            else {
                return ChatColor
                        .translateAlternateColorCodes('&', (String) namesConfig.get("POTION_SPEED"));
            }
        }
        else if (namesConfig.contains(item.getType().toString())) {
            String name = ChatColor
                    .translateAlternateColorCodes('&', (String) namesConfig.get(item.getType().toString()));
            return name;
        }
        else{
            Bukkit.broadcastMessage("(Names) Nie znaleziono nazwy przedmiotu: " + item.getType().toString());
            return "NIEZNANY PRZEDMIOT";
        }
    }


    public static ItemStack createCustomPotion(PotionType type, int amount, Material costMaterial, int cost, String category, Player player){
        ItemStack itemStack = new ItemStack(Material.POTION, amount);
        itemStack = NbtWrapper.setNBTTag("category", category, itemStack);
        if (cost > 0){
            itemStack = NbtWrapper.setNBTTag("cost_amount", Integer.toString(cost), itemStack);
            itemStack = NbtWrapper.setNBTTag("cost_material", costMaterial.toString(), itemStack);

            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            meta.setBasePotionData(new PotionData(type, false, false));
            itemStack.setItemMeta(meta);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            meta.setLore(getLore(Material.POTION, itemStack, player, new ItemStack(costMaterial, cost)));

            if (player != null && canAfford(player, new ItemStack(costMaterial, cost))){
                meta.setDisplayName(ChatColor.GREEN + getName(itemStack));
            }
            else if (player != null){
                meta.setDisplayName(ChatColor.RED + getName(itemStack));
            }

            itemStack.setItemMeta(meta);
        }


        return itemStack;
    }

    private static List<String> getDiamondLore(Material material, ItemStack item){
        if (diamondLoreConfig == null){
            diamondLoreConfig = loadCustomConfig("diamond_item_lores");
        }
        List<String> result = new ArrayList<String>();
        if (diamondLoreConfig.contains(material.toString())) {
            String[] lore = ChatColor
                    .translateAlternateColorCodes('&', (String) diamondLoreConfig.get(material.toString()))
                    .split(";");
            result.addAll(Arrays.asList(lore));
        }
        else{
            Bukkit.broadcastMessage("(Diamond Lore) Nie znaleziono opisu przedmiotu: " + material.toString());
        }
        return result;
    }

    private static List<String> getLore(Material material, ItemStack item, Player player, ItemStack cost){
        if (loreConfig == null){
            loreConfig = loadCustomConfig("item_lores");
        }
        List<String> result = new ArrayList<String>();
        if (material.equals(Material.BOW)){
            if (cost.getAmount() == 6){
                String[] lore = ChatColor
                        .translateAlternateColorCodes('&', (String) loreConfig.get("BOW_2"))
                        .split(";");
                result.addAll(Arrays.asList(lore));
            }
            else if (cost.getAmount() == 24){
                String[] lore = ChatColor
                        .translateAlternateColorCodes('&', (String) loreConfig.get("BOW_1"))
                        .split(";");
                result.addAll(Arrays.asList(lore));
            }
            else {
                String[] lore = ChatColor
                        .translateAlternateColorCodes('&', (String) loreConfig.get("BOW"))
                        .split(";");
                result.addAll(Arrays.asList(lore));
            }
        }
        else if (loreConfig.contains(material.toString())) {
            String[] lore = ChatColor
                    .translateAlternateColorCodes('&', (String) loreConfig.get(material.toString()))
                    .split(";");
            result.addAll(Arrays.asList(lore));
        }
        else if(material.equals(Material.POTION)){
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            if (meta.hasCustomEffect(PotionEffectType.INVISIBILITY) || meta.getBasePotionData().getType().equals(PotionType.INVISIBILITY)){
                String[] lore = ChatColor
                        .translateAlternateColorCodes('&', (String) loreConfig.get("POTION_INVISIBILITY"))
                        .split(";");
                result.addAll(Arrays.asList(lore));
            }
            else if (meta.getBasePotionData().getType().equals(PotionType.JUMP)){
                String[] lore = ChatColor
                        .translateAlternateColorCodes('&', (String) loreConfig.get("POTION_JUMP"))
                        .split(";");
                result.addAll(Arrays.asList(lore));
            }
            else {
                String[] lore = ChatColor
                        .translateAlternateColorCodes('&', (String) loreConfig.get("POTION_SPEED"))
                        .split(";");
                result.addAll(Arrays.asList(lore));
            }
        }
        else{
            Bukkit.broadcastMessage("(Lore) Nie znaleziono opisu przedmiotu: " + material.toString());
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
