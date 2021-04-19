package io.github.reconsolidated.BedWars;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Score;

public class BridgeEggRunnable extends BukkitRunnable {
    private int counter;
    private BedWars plugin;
    private Egg egg;
    private Location lastLocation;
    public BridgeEggRunnable(BedWars plugin, Egg egg) {
        this.plugin = plugin;
        this.egg = egg;
        counter = 0;
    }

    @Override
    public void run() {
        counter++;
        if (counter == 60){
            this.cancel();
        }
        if (egg.getCustomName() != null){
            this.cancel();
        }
        if (lastLocation != null)
            new EggRunnable(lastLocation.clone()).runTaskLater(plugin, 10L);

        lastLocation = egg.getLocation().clone().add(0, -2, 0);
    }

    private static class EggRunnable extends BukkitRunnable{
        private final Location lastLocation;
        public EggRunnable(Location lastLocation){
            this.lastLocation = lastLocation;
        }
        @Override
        public void run() {
            if (lastLocation != null && lastLocation.getBlock().getType().equals(Material.AIR)){
                lastLocation.getBlock().setType(Material.WHITE_WOOL);
                if (lastLocation.clone().add(1, 0, 0).getBlock().getType().equals(Material.AIR)){
                    lastLocation.clone().add(1, 0, 0).getBlock().setType(Material.WHITE_WOOL);
                }
                if (lastLocation.clone().add(0, 0, 1).getBlock().getType().equals(Material.AIR)){
                    lastLocation.clone().add(0, 0, 1).getBlock().setType(Material.WHITE_WOOL);
                }
                if (lastLocation.clone().add(1, 0, 1).getBlock().getType().equals(Material.AIR)){
                    lastLocation.clone().add(1, 0, 1).getBlock().setType(Material.WHITE_WOOL);
                }
            }
        }
    }
}

