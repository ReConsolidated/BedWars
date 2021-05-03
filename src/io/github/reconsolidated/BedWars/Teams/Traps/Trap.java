package io.github.reconsolidated.BedWars.Teams.Traps;

import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.entity.Entity;

import java.util.ArrayList;

public interface Trap {
    public void onTriggered(BedWars plugin, ArrayList<Entity> entities, int teamID);
}
