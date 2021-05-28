package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.CustomEntities.CustomIronGolem;
import io.github.reconsolidated.BedWars.CustomEntities.CustomSilverFish;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.Bukkit;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class EntityTargetListener implements Listener {
    private BedWars plugin;

    public EntityTargetListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event){
        if (event.getEntity() instanceof Silverfish || event.getEntity() instanceof IronGolem){
            LivingEntity le = (LivingEntity) event.getEntity();
            if (event.getTarget() instanceof Player){
                Participant p = plugin.getParticipant((Player) event.getTarget());
                if (p != null && p.team != null){
                    for (CustomIronGolem golem : plugin.getGolems()){
                        if (le.getEntityId() == golem.getBukkitEntity().getEntityId()){
                            if (golem.teamID == p.team.ID){
                                event.setCancelled(true);
                            }
                        }
                    }
                    for (CustomSilverFish sf : plugin.getSilverFish()){
                        if (le.getEntityId() == sf.getBukkitEntity().getEntityId()){
                            if (sf.teamID == p.team.ID){
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            };
        }
    }
}
