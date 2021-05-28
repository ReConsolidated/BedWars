package io.github.reconsolidated.BedWars;

import org.bukkit.Location;
import org.bukkit.Material;

public class SpawnDestroyer {
    public static void destroy(Location location){
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        for (int i = x-20; i<x+20; i++){
            for (int j = y-10; j<y+10; j++){
                for (int k = z-20; k<z+20; k++){
                    location.getWorld().getBlockAt(i, j, k).setType(Material.AIR);
                }
            }
        }
    }
}
