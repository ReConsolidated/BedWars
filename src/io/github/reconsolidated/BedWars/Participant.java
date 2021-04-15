package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.Teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Comparator;

public class Participant {
    public Player player;
    public Participant lastOpponent;
    public int currentScore = 0;
    public int kills = 0;
    public Team team;

    private BedWars plugin;

    private Participant lastHitBy = null;

    public Participant(Player player, BedWars plugin){
        this.player = player;
        this.plugin = plugin;
    }

    public static Comparator<Participant> scoreComparator = new Comparator<Participant>() {
        @Override
        public int compare(Participant o1, Participant o2) {
            return Integer.compare(o1.currentScore, o2.currentScore);
        }
    };

    public Participant getLastHitBy(){
        return lastHitBy;
    }

    public void setLastHitBy(Participant p){
        lastHitBy = p;
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run(){
                lastHitBy = null;
            }
        }, 8 * 20L);
    }

    public void onDeath(){
        if (getLastHitBy() != null){
            getLastHitBy().kills++;
            Bukkit.broadcastMessage(getLastHitBy().player.getDisplayName() + " zabił " + player.getDisplayName());
        }
        else{
            Bukkit.broadcastMessage(player.getDisplayName() + " z jakiegoś powodu umarł XD");
        }

        if (team.isBedAlive()){
            player.setHealth(20);
            player.teleport(team.getSpawnLocation());
        }
        else{
            onGameEnd();
        }
    }

    public void onGameEnd(){
        player.sendMessage("Frajer i bomba jesteś takie oro na łóżku było");
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(player.getLocation().getWorld().getSpawnLocation());
    }



}
