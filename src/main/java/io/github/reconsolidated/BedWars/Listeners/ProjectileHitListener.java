package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;

public class ProjectileHitListener implements Listener {
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

        if (event.getEntity() instanceof Fireball) {
            event.getEntity().getWorld().createExplosion(shooter.getPlayer(), event.getEntity().getLocation(), 2, false, true);
            // (Entity) event.getEntity().getShooter()
        }

        if (event.getHitEntity() instanceof Player){
            Participant playerHit = plugin.getParticipant((Player) event.getHitEntity());
            if (playerHit.getTeam() != null && shooter.getTeam() != null){
                if (playerHit.getTeam().ID == shooter.getTeam().ID){
                    event.getEntity().remove();
                    return;
                }
            }
        }

        if (event.getEntityType().equals(EntityType.SNOWBALL)){
            Participant p = plugin.getParticipant((Player) event.getEntity().getShooter());
            Silverfish sf = (Silverfish) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.SILVERFISH);
            sf.setCustomName(p.getChatColor() + "Silverfish");
            sf.setCustomNameVisible(true);
            sf.getPersistentDataContainer().set(new NamespacedKey(plugin, "team_id"), PersistentDataType.INTEGER, shooter.getTeam().ID);
            sf.getPersistentDataContainer().set(new NamespacedKey(plugin, "owner_name"), PersistentDataType.STRING, p.getPlayer().getName());
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
