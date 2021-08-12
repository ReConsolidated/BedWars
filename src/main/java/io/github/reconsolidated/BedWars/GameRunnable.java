package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.ItemDrops.ItemSpawner;
import io.github.reconsolidated.BedWars.Teams.Team;
import org.bukkit.*;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.EnderDragon;
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

        int teamsPlaying = 0;
        for (Team t : plugin.getTeams()){
            boolean isPlaying = false;
            for (Participant m : t.members){
                if (!m.hasLost() && m.getPlayer().isOnline()){
                    teamsPlaying++;
                    isPlaying = true;
                    break;
                }
            }
            if (counter > 5 && !isPlaying){
                t.destroyBed();
            }
        }

        if (plugin.hasStarted && (counter == 2600 || teamsPlaying <= 1)){
            plugin.onGameEnd();
            this.cancel();
        }


        if (counter < 5){
            for (Participant p : participants){
                p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 10, counter+2);
                p.getPlayer().sendTitle("Start za " + (5-counter), "", 0, 21, 0);
            }
        }
        // POCZĄTEK GRY
        if (counter == 5){
            plugin.hasStarted = true;

            SpawnDestroyer.destroy(plugin.getSpawnLocation());
            for (int i = 0; i<participants.size(); i++){
                int minPlayers = plugin.getTeams().get(0).members.size();
                int minPlayersTeamID = 0;
                for (int j = 0; j<plugin.getTeams().size(); j++){
                    if (minPlayers > plugin.getTeams().get(j).members.size()){
                        minPlayers = plugin.getTeams().get(j).members.size();
                        minPlayersTeamID = j;
                    }
                }

                plugin.getTeams().get(minPlayersTeamID).addMember(participants.get(i));
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("" + minPlayersTeamID).addEntry(participants.get(i).getPlayer().getDisplayName());
                participants.get(i).setTeam(plugin.getTeams().get(minPlayersTeamID));
                participants.get(i).onStart();
                participants.get(i).setScoreboard(new ScoreScoreboard(plugin, plugin.getTeams(), participants.get(i)));
                participants.get(i).getScoreboard().runTaskTimer(plugin, 0, 4);
            }
            for (int i = 0; i<spawners.size(); i++){
                spawners.get(i).start();
            }


        }

        if (counter == 365){
            for (int i = 0; i<spawners.size(); i++){
                if (spawners.get(i).getItem().getType().equals(Material.DIAMOND)){
                    spawners.get(i).setPeriod(plugin.currentConfig.getInt("DIAMOND_II"));
                }
            }
        }
        if (counter == 725){
            for (int i = 0; i<spawners.size(); i++){
                if (spawners.get(i).getItem().getType().equals(Material.DIAMOND)){
                    spawners.get(i).setPeriod(plugin.currentConfig.getInt("DIAMOND_III"));
                }
            }
        }
        if (counter == 1085){
            for (int i = 0; i<spawners.size(); i++){
                if (spawners.get(i).getTeamID() == -1 && spawners.get(i).getItem().getType().equals(Material.EMERALD)){
                    spawners.get(i).setPeriod(plugin.currentConfig.getInt("EMERALD_II"));
                }
            }
        }
        if (counter == 1445){
            for (int i = 0; i<spawners.size(); i++){
                if (spawners.get(i).getItem().getType().equals(Material.DIAMOND)){
                    spawners.get(i).setPeriod(plugin.currentConfig.getInt("DIAMOND_IV"));
                }
            }
        }
        if (counter == 1805){
            for (int i = 0; i<spawners.size(); i++){
                if (spawners.get(i).getTeamID() == -1 && spawners.get(i).getItem().getType().equals(Material.EMERALD)){
                    spawners.get(i).setPeriod(plugin.currentConfig.getInt("EMERALD_III"));
                }
            }
        }
        if (counter == 2165){
            for (Team t : plugin.getTeams()){
                t.destroyBed();
            }
            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE +
                    "Wszystkie łóżka " + ChatColor.BOLD + ""
                    + ChatColor.GOLD + "ZNISZCZONE!");

        }
        if (counter == 2525){
            for (Team t : plugin.getTeams()){
                boolean isAnyoneAlive = false;
                for (Participant m : t.members){
                    if (m.getPlayer().isOnline() && m.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
                        isAnyoneAlive = true;
                    }
                }
                if (isAnyoneAlive){
                    for (int i = 0; i<t.dragons; i++){
                        EnderDragon ed = (EnderDragon) t.getBedLocation().getWorld().spawnEntity(t.getBedLocation(), EntityType.ENDER_DRAGON);
                        ed.setPhase(EnderDragon.Phase.values()[0]);
                    }
                }
            }
        }

        //
        counter++;
    }

    public String getNextEventName(){
        if (counter < 5) return "Start gry";
        if (counter < 360 +5) return ChatColor.AQUA + "Diamenty II" + ChatColor.WHITE;
        if (counter < 360*2+5) return ChatColor.AQUA + "Diamenty III" + ChatColor.WHITE;
        if (counter < 360*3+5) return ChatColor.GREEN + "Emeraldy II" + ChatColor.WHITE;
        if (counter < 360*4+5) return ChatColor.AQUA + "Diamenty IV" + ChatColor.WHITE;
        if (counter < 360*5+5) return ChatColor.GREEN + "Emeraldy III" + ChatColor.WHITE;
        if (counter < 360*6+5) return ChatColor.GOLD + "Łóżka znikną" + ChatColor.WHITE;
        if (counter < 360*7+5) return ChatColor.BLACK + "Apokalipsa" + ChatColor.WHITE;
        return ChatColor.GOLD + "Koniec" + ChatColor.WHITE;
    }

    public int getNextEventTime(){
        if (counter < 5) return 5;
        if (counter < 360 +5) return 360 +5;
        if (counter < 360*2+5) return 360*2+5;
        if (counter < 360*3+5) return 360*3+5;
        if (counter < 360*4+5) return 360*4+5;
        if (counter < 360*5+5) return 360*5+5;
        if (counter < 360*6+5) return 360*6+5;
        if (counter < 360*7+5) return 360*7+5;
        if (counter < 360*8+5) return 360*8+5;
        if (counter < 360*9+5) return 360*9+5;
        return 0;
    }
}