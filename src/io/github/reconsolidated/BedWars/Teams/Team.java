package io.github.reconsolidated.BedWars.Teams;

import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.Location;

import java.util.HashSet;

public class Team {
    private Location bedLocation;
    private Location spawnLocation;

    private boolean isBedAlive = true;

    public int protLevel = 0;
    public int sharpLevel = 0;
    public int ID;

    public HashSet<Participant> members;

    public Team(Location bedLocation, Location spawnLocation, int ID){
        this.bedLocation = bedLocation;
        this.spawnLocation = spawnLocation;
        this.ID = ID;
        members = new HashSet<>();
    }

    public void addMember(Participant p){
        members.add(p);
    }

    public Location getBedLocation(){
        return bedLocation;
    }
    public Location getSpawnLocation(){
        return spawnLocation;
    }
    public boolean isBedAlive(){
        return isBedAlive;
    }

    public void onBedDestroy(){
        this.isBedAlive = false;
    }
}
