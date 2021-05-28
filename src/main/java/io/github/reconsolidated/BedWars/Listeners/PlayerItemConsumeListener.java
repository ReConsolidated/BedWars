package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.CustomSpectator.MakeArmorsInvisible;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static io.github.reconsolidated.BedWars.inventoryShop.buyMethods.Buy.charge;

public class PlayerItemConsumeListener implements Listener {
    private BedWars plugin;

    public PlayerItemConsumeListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        if (event.getItem().getType().equals(Material.MILK_BUCKET)){
            Participant p = plugin.getParticipant(player);
            p.setTrapInvincibility(30);
        }

        if (event.getItem().getType().equals(Material.POTION)){
            PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
            switch (meta.getBasePotionData().getType()){
                case INVISIBILITY ->{
                    event.setCancelled(true);
                    MakeArmorsInvisible.sendOutNoArmorPacket(player);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600, 0));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY))
                                MakeArmorsInvisible.sendOutArmorPacket(player);
                        }
                    }.runTaskLater(plugin, 601L);
                    charge(event.getPlayer(), event.getItem());
                }
                case JUMP -> {
                    event.setCancelled(true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 900, 4));
                    charge(event.getPlayer(), event.getItem());
                }
                case SPEED -> {
                    event.setCancelled(true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 900, 1));
                    charge(event.getPlayer(), event.getItem());
                }

            }
        }
    }
}
