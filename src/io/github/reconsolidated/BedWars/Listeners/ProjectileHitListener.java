package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.CustomEntities.CustomSilverFish;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.ScoreScoreboard;
import net.minecraft.server.v1_16_R2.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;

public class ProjectileHitListener implements Listener {
    private ScoreScoreboard myScoreboard;
    private ArrayList<Participant> participants;
    private BedWars plugin;

    public ProjectileHitListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        if (!(event.getEntity().getShooter() instanceof Player)){
            return;
        }
        Participant shooter = plugin.getParticipant((Player) event.getEntity().getShooter());
        if (shooter == null) return;

        if (event.getHitEntity() instanceof Player){
            Participant playerHit = plugin.getParticipant((Player) event.getHitEntity());
            if (playerHit.team != null && shooter.team != null){
                if (playerHit.team.ID == shooter.team.ID){
                    event.getEntity().remove();
                    return;
                }
            }

        }

        if (event.getEntityType().equals(EntityType.SNOWBALL)){
            CustomSilverFish sf = new CustomSilverFish(EntityTypes.SILVERFISH, ((CraftWorld) event.getEntity().getLocation().getWorld()).getHandle());
            sf.spawn(shooter.team.ID, event.getEntity().getLocation());
            plugin.addSilverfish(sf);
            event.getEntity().remove();
            return;
        }

        if (event.getEntityType().equals(EntityType.EGG)){
            Egg egg = (Egg) event.getEntity();
            event.getEntity().remove();
        }

    }
}
