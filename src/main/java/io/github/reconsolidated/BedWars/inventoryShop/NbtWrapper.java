package io.github.reconsolidated.BedWars.inventoryShop;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class NbtWrapper {
    public static ItemStack setNBTTag(String tagName, String value, ItemStack itemStack){
        Plugin plugin = Bukkit.getPluginManager().getPlugin("BedWars");
        ItemMeta meta = itemStack.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, tagName), PersistentDataType.STRING, value);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static String getNBTTag(String key, ItemStack itemStack){
        Plugin plugin = Bukkit.getPluginManager().getPlugin("BedWars");
        return itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
    }
}
