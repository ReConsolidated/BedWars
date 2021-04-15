package io.github.reconsolidated.BedWars.inventoryShop;

import net.minecraft.server.v1_16_R2.NBTTagCompound;
import net.minecraft.server.v1_16_R2.NBTTagString;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NbtWrapper {
    public static ItemStack setNBTTag(String tagName, String value, ItemStack itemStack){
        net.minecraft.server.v1_16_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tagCompound = nmsStack.getOrCreateTag();
        tagCompound.setString(tagName, value);
        itemStack = CraftItemStack.asBukkitCopy(nmsStack);
        return itemStack;
    }

    public static String getNBTTag(String key, ItemStack itemStack){
        net.minecraft.server.v1_16_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tagCompound = nmsStack.getTag();
        if (tagCompound == null)
            return null;
        return tagCompound.getString(key);
    }
}
