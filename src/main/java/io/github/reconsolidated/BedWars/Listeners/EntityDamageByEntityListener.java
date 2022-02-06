package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.CustomSpectator.MakeArmorsInvisible;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;


public class EntityDamageByEntityListener implements Listener {
    private BedWars plugin;

    public EntityDamageByEntityListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player ){
            Participant p1 = plugin.getParticipant((Player) event.getEntity());
            if (p1 == null ) return;

            if (event.getDamager() instanceof Player){
                Participant p2 = plugin.getParticipant((Player) event.getDamager());

                if (p2 == null || p2.isSpectating()){
                    event.setCancelled(true);
                    return;
                }

                if (p1.getTeam() == p2.getTeam()){
                    event.setCancelled(true);
                    return;
                }

                p1.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
                p2.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
                MakeArmorsInvisible.sendOutArmorPacket(p1.getPlayer());
                MakeArmorsInvisible.sendOutArmorPacket(p2.getPlayer());
                p1.setLastHitBy(p2);
            }

            if (event.getDamager() instanceof Projectile){
                Projectile projectile = (Projectile) event.getDamager();
                if (projectile.getShooter() instanceof Player){
                    Participant p2 = plugin.getParticipant((Player) projectile.getShooter());
                    if (p2 != null){
                        if (p1.getTeam().ID == p2.getTeam().ID && !p1.getPlayer().equals(p2.getPlayer())){
                            event.setCancelled(true);
                            return;
                        }
                        p1.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
                        p2.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
                        MakeArmorsInvisible.sendOutArmorPacket(p1.getPlayer());
                        MakeArmorsInvisible.sendOutArmorPacket(p2.getPlayer());
                        p1.setLastHitBy(p2);
                    }
                }
            }

            if (event.getDamager() instanceof TNTPrimed){
                event.setDamage(4);
            }

            if (event.getDamager() instanceof IronGolem){
                IronGolem golem = (IronGolem) event.getDamager();
                String owner = golem.getPersistentDataContainer().get(new NamespacedKey(plugin, "owner_name"), PersistentDataType.STRING);
                if (owner != null) {
                    Participant p2 = plugin.getParticipant(owner);
                    if (p2 != null) {
                        p1.setLastHitBy(p2);
                    }
                }
            }

            if (event.getDamager() instanceof Silverfish){
                Silverfish sf = (Silverfish) event.getDamager();
                String owner = sf.getPersistentDataContainer().get(new NamespacedKey(plugin, "owner_name"), PersistentDataType.STRING);
                if (owner != null) {
                    Participant p2 = plugin.getParticipant(owner);
                    if (p2 != null) {
                        p1.setLastHitBy(p2);
                    }
                }
            }

            if (p1.getPlayer().getHealth() - event.getFinalDamage() <= 0){
                event.setCancelled(true);
                p1.getPlayer().playSound(p1.getPlayer().getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 10, 1);
                p1.onDeath();
            }
        }
    }
}
