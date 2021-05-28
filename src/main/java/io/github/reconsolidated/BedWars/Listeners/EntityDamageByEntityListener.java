package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.CustomEntities.CustomIronGolem;
import io.github.reconsolidated.BedWars.CustomEntities.CustomSilverFish;
import io.github.reconsolidated.BedWars.CustomSpectator.MakeArmorsInvisible;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

                if (p1.team == p2.team){
                    event.setCancelled(true);
                    return;
                }

                p1.player.removePotionEffect(PotionEffectType.INVISIBILITY);
                p2.player.removePotionEffect(PotionEffectType.INVISIBILITY);
                MakeArmorsInvisible.sendOutArmorPacket(p1.player);
                MakeArmorsInvisible.sendOutArmorPacket(p2.player);
                p1.setLastHitBy(p2);
            }

            if (event.getDamager() instanceof Projectile){
                Projectile projectile = (Projectile) event.getDamager();
                if (projectile.getShooter() instanceof Player){
                    Participant p2 = plugin.getParticipant((Player) projectile.getShooter());
                    if (p2 != null){
                        if (p1.team.ID == p2.team.ID && !p1.player.equals(p2.player)){
                            event.setCancelled(true);
                            return;
                        }
                        p1.player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        p2.player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        MakeArmorsInvisible.sendOutArmorPacket(p1.player);
                        MakeArmorsInvisible.sendOutArmorPacket(p2.player);
                        p1.setLastHitBy(p2);
                    }
                }
            }

            if (event.getDamager() instanceof TNTPrimed){
                event.setDamage(4);
            }

            if (event.getDamager() instanceof IronGolem){
                for (CustomIronGolem golem : plugin.getGolems()){
                    if (event.getDamager().getEntityId() == golem.getBukkitEntity().getEntityId()){
                        p1.setLastHitBy(golem.owner);
                    }
                }
            }

            if (event.getDamager() instanceof Silverfish){
                for (CustomSilverFish sf : plugin.getSilverFish()){
                    if (event.getDamager().getEntityId() == sf.getBukkitEntity().getEntityId()){
                        p1.setLastHitBy(sf.owner);
                    }
                }
            }

            if (p1.player.getHealth() - event.getFinalDamage() <= 0){
                event.setCancelled(true);
                p1.player.playSound(p1.player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 10, 1);
                p1.onDeath();
            }



        }

    }
}
