package io.github.reconsolidated.BedWars.ItemDrops;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ItemSpawner {
    private ItemStack item;  // Item spawned by spawner
    private int period;   // spawn every period * 20 ms
    private Location location;
    private BedWars plugin;
    private TextLine counterTextLine;
    private final int team;
    private final int maxAmount;
    private final int basePeriod;

    public ItemSpawner(BedWars plugin, ItemStack item, int period, Location location, int team, int maxAmount){
        this.plugin = plugin;
        this.item = item;
        this.period = period;
        this.basePeriod = period;
        this.location = location;
        this.team = team;
        this.maxAmount = maxAmount;

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
        if (hasHologram && team == -1){
            Hologram hologram = HologramsAPI.createHologram(plugin, location.clone().add(0, 3, 0));
            hologram.appendTextLine(color + itemName);
            counterTextLine = hologram.appendTextLine(ChatColor.YELLOW + "Pojawi się za: " + ChatColor.RED + (int)(period/20));
            hologram.appendTextLine("");
            hologram.appendItemLine(floatingItem); // TODO zamienić na latający item (na hypixelu są one większe)
        }


    }

    public int getBasePeriod(){
        return basePeriod;
    }

    public ItemStack getItem(){
        return item;
    }

    public void start(){
        new ItemSpawnerRunnable().runTaskTimer(plugin, 0, 1);
    }

    public int getTeamID(){
        return team;
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
                counter = 0;
                int nearbyItems = 0;
                for (Entity e : location.getWorld().getNearbyEntities(location, 4, 4, 4)){
                    if (e instanceof Item){
                        Item it = (Item) e;
                        if (it.getItemStack().getType().equals(item.getType())){
                            nearbyItems += it.getItemStack().getAmount();
                        }
                    }
                }
                if (nearbyItems < maxAmount){
                    List<Player> playersNearby = new ArrayList<>();
                    for (Entity e : location.getWorld().getNearbyEntities(location, 2, 5, 2)){
                        if (e instanceof Player){
                            Player player = (Player) e;
                            if (player.getGameMode().equals(GameMode.SURVIVAL)){
                                playersNearby.add((Player) e);
                            }

                        }
                    }
                    if (playersNearby.size() == 0
                            || item.getType().equals(Material.EMERALD)
                            || item.getType().equals(Material.DIAMOND)){
                        Item dropped = location.getWorld().dropItem(location, item);
                        dropped.setVelocity(new Vector(0, 0, 0));
                        counter = 0;
                    }
                    else {
                        // if there are players nearby put items in their eq instead of dropping
                        for (Player player : playersNearby){
                            player.getInventory().addItem(item);
                            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 5, 0);
                        }
                        // set counter to a negative value, so that it takes  longer to regenerate
                        // after it gave away more items than it should have
                        // if period is 20, and 4 players took 
                        counter = period - playersNearby.size()  * period;
                    }
                }

            }
            if (counterTextLine != null){
                int showIn = (period-counter)/20;
                showIn = Math.max(showIn, 0);
                counterTextLine.setText(ChatColor.YELLOW + "Pojawi się za: " + ChatColor.RED + showIn);
            }


        }
    }
}
