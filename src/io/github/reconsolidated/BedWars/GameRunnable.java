package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.ItemDrops.ItemSpawner;
import io.github.reconsolidated.BedWars.Teams.Team;
import org.bukkit.*;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class GameRunnable extends BukkitRunnable {
    private BedWars plugin;
    private int counter;
    private ArrayList<Participant> participants;
    private ArrayList<ItemSpawner> spawners;


    public GameRunnable(BedWars plugin) {
        this.plugin = plugin;
        this.counter = 0;

        this.participants = plugin.getParticipants();
        this.spawners = plugin.getSpawners();
    }

    @Override
    public void run() {
        if (counter < 5){
            for (Participant p : participants){
                p.player.playSound(p.player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 10, 1);
                p.player.sendTitle("Start za " + (5-counter), "", 0, 21, 0);
            }
        }
        // POCZĄTEK GRY
        if (counter == 5){
            for (int i = 0; i<participants.size(); i++){
                if (participants.get(i).team != null)
                    participants.get(i).onStart();
            }
            for (int i = 0; i<spawners.size(); i++){
                spawners.get(i).start();
            }
        }

        if (counter == 65){
            for (int i = 0; i<spawners.size(); i++){
                if (spawners.get(i).getItem().getType().equals(Material.DIAMOND)){
                    spawners.get(i).setPeriod(plugin.getConfig().getInt("DIAMOND_II"));
                }
            }
        }
        if (counter == 125){
            for (int i = 0; i<spawners.size(); i++){
                if (spawners.get(i).getItem().getType().equals(Material.DIAMOND)){
                    spawners.get(i).setPeriod(plugin.getConfig().getInt("DIAMOND_III"));
                }
            }
        }
        if (counter == 185){
            for (int i = 0; i<spawners.size(); i++){
                if (spawners.get(i).getTeamID() == -1 && spawners.get(i).getItem().getType().equals(Material.EMERALD)){
                    spawners.get(i).setPeriod(plugin.getConfig().getInt("EMERALD_II"));
                }
            }
        }
        if (counter == 245){
            for (int i = 0; i<spawners.size(); i++){
                if (spawners.get(i).getItem().getType().equals(Material.DIAMOND)){
                    spawners.get(i).setPeriod(plugin.getConfig().getInt("DIAMOND_IV"));
                }
            }
        }
        if (counter == 844){
            for (int i = 0; i<spawners.size(); i++){
                if (spawners.get(i).getTeamID() == -1 && spawners.get(i).getItem().getType().equals(Material.EMERALD)){
                    spawners.get(i).setPeriod(plugin.getConfig().getInt("EMERALD_III"));
                }
            }
        }
        if (counter == 1443){
            for (Team t : plugin.getTeams()){
                for (int i = t.getBedLocation().getBlockX()-2; i<t.getBedLocation().getBlockX()+2; i++){
                    for (int j = t.getBedLocation().getBlockY()-2; j<t.getBedLocation().getBlockY()+2; j++){
                        for (int k = t.getBedLocation().getBlockZ()-2; k<t.getBedLocation().getBlockZ()+2; k++){
                            if (t.getBedLocation().getWorld().getBlockAt(i, j, k).getBlockData() instanceof Bed){
                                t.getBedLocation().getWorld().getBlockAt(i, j, k).setType(Material.AIR);
                            }
                        }
                    }
                }
                t.onBedDestroy();
            }
            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE +
                    "Wszystkie łóżka " + ChatColor.BOLD + ""
                    + ChatColor.GOLD + "ZNISZCZONE!");

        }
        if (counter == 20){
            for (Team t : plugin.getTeams()){
                boolean isAnyoneAlive = false;
                for (Participant m : t.members){
                    if (m.player.isOnline() && m.player.getGameMode().equals(GameMode.SURVIVAL)){
                        isAnyoneAlive = true;
                    }
                }
                if (isAnyoneAlive){
                    for (int i = 0; i<t.dragons; i++){
                        t.getBedLocation().getWorld().spawnEntity(t.getBedLocation(), EntityType.ENDER_DRAGON);
                    }
                }
            }
        }

        //
        counter++;
    }

    public String getNextEventName(){
        if (counter < 5) return "Start gry";
        if (counter < 65) return ChatColor.AQUA + "Diamenty II" + ChatColor.WHITE;
        if (counter < 125) return ChatColor.AQUA + "Diamenty III" + ChatColor.WHITE;
        if (counter < 185) return ChatColor.GREEN + "Emeraldy II" + ChatColor.WHITE;
        if (counter < 245) return ChatColor.AQUA + "Diamenty IV" + ChatColor.WHITE;
        if (counter < 844) return ChatColor.GREEN + "Emeraldy III" + ChatColor.WHITE;
        if (counter < 1443) return ChatColor.GOLD + "Łóżka znikną" + ChatColor.WHITE;
        if (counter < 2042) return ChatColor.MAGIC + "Apokalipsa" + ChatColor.WHITE;
        return "Koniec";
    }

    public int getNextEventTime(){
        if (counter < 5) return 5;
        if (counter < 65) return 65;
        if (counter < 125) return 125;
        if (counter < 185) return 185;
        if (counter < 245) return 245;
        if (counter < 844) return 844;
        if (counter < 1443) return 1443;
        if (counter < 2042) return 2042;
        return 0;
    }
}