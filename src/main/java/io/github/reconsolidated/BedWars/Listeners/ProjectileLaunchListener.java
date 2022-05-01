package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.BridgeEggRunnable;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ProjectileLaunchListener implements Listener {
    private BedWars plugin;
    public ProjectileLaunchListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileThrown(ProjectileLaunchEvent event){
        if (!(event.getEntity().getShooter() instanceof Player)){
            return;
        }
        Participant shooter = plugin.getParticipant((Player) event.getEntity().getShooter());
        if (shooter == null) return;

        if (event.getEntityType().equals(EntityType.EGG)){
            BukkitRunnable rn = new BridgeEggRunnable(plugin, (Egg) event.getEntity(), shooter);
            rn.runTaskTimer(plugin, 3L, 1L);
        }

    }

}
