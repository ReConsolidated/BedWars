package io.github.reconsolidated.BedWars;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class IronGolemRunnable extends BukkitRunnable {
    private final BedWars plugin;

    public IronGolemRunnable(BedWars plugin) {
        this.plugin = plugin;
        runTaskTimer(plugin, 10L, 1L);
    }

    @Override
    public void run() {
        for (IronGolem golem : plugin.getGolems()) {
            if (golem.getTarget() == null || golem.getTarget().getType().equals(EntityType.ZOMBIE)) {
                setTarget(plugin, golem);
            }
        }
    }

    public static void setTarget(BedWars plugin, IronGolem golem) {
        double currentDistanceSq = 999999;
        LivingEntity currentEntity = null;
        int golemTeam = getTeam(plugin, golem);

        for (Entity e : golem.getNearbyEntities(15, 6, 15)) {
            if (e instanceof Player || e instanceof IronGolem) {
                if (e instanceof Player) {
                    Player player = (Player) e;
                    if (player.getGameMode() != GameMode.SURVIVAL) {
                        continue;
                    }
                }
                int otherTeam = getTeam(plugin, e);
                if (golemTeam != otherTeam) {
                    double distanceSq = golem.getLocation().distanceSquared(e.getLocation());
                    if (distanceSq < currentDistanceSq) {
                        currentEntity = (LivingEntity) e;
                    }
                }
            }
        }

        if (currentEntity != null) {
            golem.setTarget(currentEntity);
        }
    }

    public static int getTeam(BedWars plugin, Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Participant p = plugin.getParticipant(player);
            if (p != null && p.getTeam() != null) {
                return p.getTeam().ID;
            } else {
                return -1;
            }
        }


        Integer teamID = entity.getPersistentDataContainer().get(new NamespacedKey(plugin, "team_id"), PersistentDataType.INTEGER);
        if (teamID == null) {
            return -1;
        }
        return teamID;
    }
}
