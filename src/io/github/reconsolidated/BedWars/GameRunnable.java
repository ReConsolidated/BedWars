package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.ItemDrops.ItemSpawner;
import org.bukkit.Bukkit;
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


        //
        counter++;
    }
}