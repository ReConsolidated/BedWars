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
    private Participant shooter;
    private String color = "BLUE";

    public BridgeEggRunnable(BedWars plugin, Egg egg, Participant shooter) {
        this.plugin = plugin;
        this.egg = egg;
        this.shooter = shooter;
        counter = 0;

        this.color = shooter.getColor();
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
            new EggRunnable(lastLocation.clone(), color).runTaskLater(plugin, 10L);

        lastLocation = egg.getLocation().clone().add(0, -2, 0);
    }

    private static class EggRunnable extends BukkitRunnable{
        private final Location lastLocation;
        private final String color;
        public EggRunnable(Location lastLocation, String color){
            this.lastLocation = lastLocation;
            this.color = color;
        }
        @Override
        public void run() {
            if (lastLocation != null && lastLocation.getBlock().getType().equals(Material.AIR)){
                lastLocation.getBlock().setType(Material.getMaterial(color + "_WOOL"));
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

