package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.Teams.Team;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DragonRunnable extends BukkitRunnable {
    private final BedWars plugin;
    private Location destination = null;
    private final int teamID;
    private EnderDragon dragon;

    public DragonRunnable(BedWars plugin, Team team, EnderDragon dragon){
        this.plugin = plugin;
        this.teamID = team.ID;
        this.dragon = dragon;
    }

    @Override
    public void run() {
        if (destination == null || dragon.getLocation().distanceSquared(destination) < 10){
            for (Participant p : plugin.getParticipants()){
                if (p.getTeam().ID == teamID) continue;
                destination = p.getPlayer().getLocation().clone();
            }
        }
        if (destination == null) return;

        // OD TEGO MOMENTU NIE MUSISZ RUSZAC
        Location dir = destination.clone().subtract(dragon.getLocation().clone());
        Vector vdir = dir.toVector();
        vdir = vdir.normalize();
        vdir = vdir.multiply(0.3);
        Location dragonNewLocation = dragon.getLocation().clone().add(vdir);
        vdir.multiply(-1);
        dragonNewLocation.setDirection(vdir);
        dragon.teleport(dragonNewLocation);
    }
}
