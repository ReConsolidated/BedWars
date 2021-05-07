package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.ItemDrops.ItemSpawner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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
                if (spawners.get(i).getTeamID() == -1 && spawners.get(i).getItem().getType().equals(Material.DIAMOND)){
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

        //
        counter++;
    }

    public String getNextEventName(){
        if (counter < 5) return "Start gry";
        if (counter < 65) return "Diamenty II";
        if (counter < 125) return "Diamenty III";
        if (counter < 185) return "Emeraldy II";
        if (counter < 245) return "Diamenty IV";
        return "Nie wiem co dalej";
    }

    public int getNextEventTime(){
        if (counter < 5) return 5;
        if (counter < 65) return 65;
        if (counter < 125) return 125;
        if (counter < 185) return 185;
        if (counter < 245) return 245;
        return 0;
    }
}