package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;


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

                if (p2 == null)
                    return;
                if (p1.team == p2.team){
                    event.setCancelled(true);
                    return;
                }
                p1.setLastHitBy(p2);
            }

            if (event.getDamager() instanceof TNTPrimed){
                event.setDamage(2);
            }

            if (p1.player.getHealth() - event.getFinalDamage() <= 0){
                p1.onDeath();
            }



        }

    }
}
