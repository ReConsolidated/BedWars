package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.ItemDrops.ItemSpawner;
import org.bukkit.Bukkit;
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
            Bukkit.broadcastMessage("Gra rozpoczyna się za " + (5-counter) + " sekund(y)");
        }
        // POCZĄTEK GRY
        if (counter == 5){
            for (int i = 0; i<participants.size(); i++){
                participants.get(i).player.teleport(participants.get(i).team.getSpawnLocation());
            }
            for (int i = 0; i<spawners.size(); i++){
                spawners.get(i).start();
            }
        }
        //
        counter++;
    }
}