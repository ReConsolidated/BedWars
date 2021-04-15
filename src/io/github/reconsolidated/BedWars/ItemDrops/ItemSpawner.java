package io.github.reconsolidated.BedWars.ItemDrops;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

public class ItemSpawner {
    private ItemStack item;  // Item spawned by spawner
    private int period;   // Spawns per minute
    private Location location;
    private BedWars plugin;
    private TextLine counterTextLine;
    private int team;

    public ItemSpawner(BedWars plugin, ItemStack item, int period, Location location, int team){
        this.plugin = plugin;
        this.item = item;
        this.period = period;
        this.location = location;
        this.team = team;

        ItemStack floatingItem = null;
        boolean hasHologram = false;
        ChatColor color = ChatColor.WHITE;
        String itemName = "";
        switch (item.getType()) {
            case EMERALD -> {
                floatingItem = new ItemStack(Material.EMERALD_BLOCK);
                hasHologram = true;
                itemName = "Emerald";
                color = ChatColor.GREEN;
            }
            case DIAMOND -> {
                floatingItem = new ItemStack(Material.DIAMOND_BLOCK);
                hasHologram = true;
                itemName = "Diament";
                color = ChatColor.AQUA;
            }
        }
        counterTextLine = null;
        if (hasHologram){
            Hologram hologram = HologramsAPI.createHologram(plugin, location.clone().add(0, 3, 0));
            hologram.appendTextLine(color + itemName);
            counterTextLine = hologram.appendTextLine(ChatColor.RED + "Pojawi się za: " + period);
            hologram.appendTextLine("");
            hologram.appendItemLine(floatingItem); // TODO zamienić na latający item (na hypixelu są one większe)
        }


    }

    public void start(){
        new ItemSpawnerRunnable().runTaskTimer(plugin, 0, 20);;
    }

    public void setPeriod(int f){
        this.period = f;
    }

    public int getPeriod(){
        return this.period;
    }

    private class ItemSpawnerRunnable extends BukkitRunnable {
        private int counter;

        @Override
        public void run() {
            counter++;
            if (counter >= period){
                double x_offset = 0.5;
                if (location.getBlockX() < 0){
                    x_offset = -0.5;
                }
                double z_offset = 0.5;
                if (location.getBlockZ() < 0){
                    z_offset = -0.5;
                }
                Item dropped = location.getWorld().dropItemNaturally(location.clone().add(x_offset, 0, z_offset), item);
                dropped.setVelocity(new Vector(0,0,0));
                counter = 0;
            }
            if (counterTextLine != null)
                counterTextLine.setText(ChatColor.GREEN + "Pojawi się za: " + (period-counter));

        }
    }
}
