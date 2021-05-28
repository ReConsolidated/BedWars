package io.github.reconsolidated.BedWars.CustomSpectator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;


public class MakeArmorsInvisible {
    private BedWars plugin;
    public MakeArmorsInvisible(BedWars plugin){
        this.plugin = plugin;
    }

    public static void sendOutNoArmorPacket(Player player){
        for (Player p2 : Bukkit.getOnlinePlayers()){
            if (p2.equals(player)) continue;
            PacketContainer pc = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);

            pc.getIntegers().write(0, player.getEntityId());

            ArrayList<Pair<EnumWrappers.ItemSlot, ItemStack>> eq = new ArrayList<>();
            eq.add(0, new Pair<>(EnumWrappers.ItemSlot.HEAD, new ItemStack(Material.AIR)));
            eq.add(0, new Pair<>(EnumWrappers.ItemSlot.CHEST, new ItemStack(Material.AIR)));
            eq.add(0, new Pair<>(EnumWrappers.ItemSlot.LEGS, new ItemStack(Material.AIR)));
            eq.add(0, new Pair<>(EnumWrappers.ItemSlot.FEET, new ItemStack(Material.AIR)));
            eq.add(0, new Pair<>(EnumWrappers.ItemSlot.MAINHAND, new ItemStack(Material.AIR)));
            eq.add(0, new Pair<>(EnumWrappers.ItemSlot.OFFHAND, new ItemStack(Material.AIR)));

            pc.getSlotStackPairLists().write(0, eq);

            try{
                ProtocolLibrary.getProtocolManager().sendServerPacket(p2, pc);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public static void sendOutArmorPacket(Player player){
        for (Player p2 : Bukkit.getOnlinePlayers()){
            if (p2.equals(player)) continue;
            PacketContainer pc = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);

            pc.getIntegers().write(0, player.getEntityId());

            ArrayList<Pair<EnumWrappers.ItemSlot, ItemStack>> eq = new ArrayList<>();
            eq.add(0, new Pair<>(EnumWrappers.ItemSlot.HEAD, player.getInventory().getHelmet()));
            eq.add(0, new Pair<>(EnumWrappers.ItemSlot.CHEST, player.getInventory().getChestplate()));
            eq.add(0, new Pair<>(EnumWrappers.ItemSlot.LEGS, player.getInventory().getLeggings()));
            eq.add(0, new Pair<>(EnumWrappers.ItemSlot.FEET, player.getInventory().getBoots()));
            eq.add(0, new Pair<>(EnumWrappers.ItemSlot.MAINHAND, player.getInventory().getItemInMainHand()));
            eq.add(0, new Pair<>(EnumWrappers.ItemSlot.OFFHAND, player.getInventory().getItemInOffHand()));

            pc.getSlotStackPairLists().write(0, eq);

            try{
                ProtocolLibrary.getProtocolManager().sendServerPacket(p2, pc);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void run(){
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, ListenerPriority.NORMAL,
                        PacketType.Play.Server.ENTITY_EQUIPMENT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {

                        int entityID = event.getPacket().getIntegers().getValues().get(0);
                        Player player = null;
                        for (Player p: Bukkit.getOnlinePlayers()){
                            if (entityID == p.getEntityId()){
                                player = p;
                            }
                        }
                        if (player == null){
                            return;
                        }
                        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                            event.setCancelled(true);
                        }
                    }
                });
    }
}
