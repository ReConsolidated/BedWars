package io.github.reconsolidated.BedWars.inventoryShop;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustomItemStack {

    public static ItemStack createCustomItemStack(Material material, int amount, Material costMaterial, int cost, String category, List<String> lore){
        ItemStack itemStack = new ItemStack(material, amount);
        itemStack = NbtWrapper.setNBTTag("category", category, itemStack);
        if (cost > 0){
            itemStack = NbtWrapper.setNBTTag("cost_amount", Integer.toString(cost), itemStack);
            itemStack = NbtWrapper.setNBTTag("cost_material", costMaterial.toString(), itemStack);

            ItemMeta meta = itemStack.getItemMeta();
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }


        return itemStack;
    }

}
