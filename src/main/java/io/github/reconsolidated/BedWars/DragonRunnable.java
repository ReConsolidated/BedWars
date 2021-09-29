package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.Teams.Team;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class DragonRunnable extends BukkitRunnable {
    private final BedWars plugin;
    private Location destination = null;
    private final int teamID;
    private final EnderDragon dragon;

    public DragonRunnable(BedWars plugin, Team team, EnderDragon dragon){
        this.plugin = plugin;
        this.teamID = team.ID;
        this.dragon = dragon;
    }

    @Override
    public void run() {
        if (!dragon.getLocation().getChunk().isLoaded()){
            dragon.getLocation().getChunk().load();
        }
        if (destination == null || dragon.getLocation().distanceSquared(destination) < 3){
            for (Participant p : plugin.getParticipants()){
                if (p.getTeam().ID == teamID) continue;
                Random random = new Random();
                if (destination != null && random.nextBoolean()) continue;

                if (random.nextBoolean()){
                    destination = p.getPlayer().getLocation().clone();
                }
                else{
                    destination = p.getPlayer().getLocation().clone().add(
                            random.nextInt(150)-75,
                            random.nextInt(50)-5,
                            random.nextInt(150)-75);
                }

            }
        }
        if (destination == null) return;

        Location dir = destination.clone().subtract(dragon.getLocation().clone());
        Vector vdir = dir.toVector();
        vdir = vdir.normalize();
        Location dragonNewLocation = dragon.getLocation().clone().add(vdir);
        vdir.multiply(-1);
        dragonNewLocation.setDirection(vdir);
        dragon.teleport(dragonNewLocation);
    }
}
