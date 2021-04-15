package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.ScoreScoreboard;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

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
                    if (Integer.toString(p.team.ID).equals(le.getCustomName())){
                        event.setCancelled(true);
                    }
                }
            };
        }
    }
}
