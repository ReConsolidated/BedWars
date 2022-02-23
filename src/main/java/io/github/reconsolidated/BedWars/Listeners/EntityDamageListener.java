package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class EntityDamageListener implements Listener {
    private BedWars plugin;
    public EntityDamageListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntity() instanceof Villager || event.getEntity() instanceof Zombie){
            event.setCancelled(true);
        }
        if (event.getEntity() instanceof Player){
            Participant p = plugin.getParticipant((Player) event.getEntity());

            if (p == null || p.isSpectating() || p.getTeam() == null){
                event.setCancelled(true);
                return;
            }
        }
        if (event.getEntity() instanceof Player && !(event instanceof EntityDamageByEntityEvent)
                && !(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))) {
            Player player = (Player) event.getEntity();
            List<Participant> participants = plugin.getParticipants();
            for (int i = 0; i<participants.size(); i++){
                Participant p = participants.get(i);
                if (p.getPlayer().equals(player)){
                    if (player.getHealth() - event.getFinalDamage() <= 0){
                        event.setCancelled(true);
                        p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 10, 1);
                        p.onDeath();
                    }
                }
            }

        }
    }
}
