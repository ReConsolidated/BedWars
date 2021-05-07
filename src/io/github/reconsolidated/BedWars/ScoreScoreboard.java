package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.Teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ScoreScoreboard extends BukkitRunnable {
    public Objective objective;
    private ArrayList<Team> teams;
    private ScoreboardManager manager;
    public Scoreboard scoreboard;


    public ScoreScoreboard(ArrayList<Team> teams){
        this.teams = teams;

        manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        objective = scoreboard.registerNewObjective("Drużyny", "dummy", "Score");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    }

    public void registerPlayers(){
        for (Team t : teams){
            for (Participant p : t.members){
                p.player.setScoreboard(scoreboard);
            }
        }
    }

    @Override
    public void run() {
        scoreboard.getObjective("Drużyny").setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "BEDWARS");
        Score funFactScore = scoreboard.getObjective("Drużyny").getScore(ChatColor.GREEN + "ttv/bartiki94");
        funFactScore.setScore(8);
        Score spaceScore = scoreboard.getObjective("Drużyny").getScore("    ");
        spaceScore.setScore(7);

        // on Score 6 there is the countdown runnable

        spaceScore = scoreboard.getObjective("Drużyny").getScore(" ");
        spaceScore.setScore(4);
        spaceScore = scoreboard.getObjective("Drużyny").getScore("      ");
        spaceScore.setScore(-1);
        String isAlive = "  ✓ ";

        for (int i = 0; i<teams.size(); i++){
            Team t = teams.get(i);
            scoreboard.resetScores(t.lastScoreboardString);
            if (t.isBedAlive()){
                isAlive = "  ✓     ";
            }
            else{
                int alive = 0;
                for (Participant p : t.members){
                    if (p.player.isOnline() && p.player.getGameMode().equals(GameMode.SURVIVAL)){
                        alive++;
                    }
                }
                if (alive == 0) isAlive = ChatColor.RED + "  ✖     ";
                if (alive == 1) isAlive = ChatColor.GREEN + "  1     ";
                if (alive == 2) isAlive = ChatColor.GREEN + "  2     ";
                if (alive == 3) isAlive = ChatColor.GREEN + "  3     ";
                if (alive == 4) isAlive = ChatColor.GREEN + "  4     ";
            }
            String name = switch (i) {
                case 0 -> t.getChatColor() +  " Delta:" + ChatColor.GREEN + ChatColor.BOLD + isAlive;
                case 1 -> t.getChatColor() +  " Gamma:" + ChatColor.GREEN + ChatColor.BOLD + isAlive;
                case 2 -> t.getChatColor() +  " Beta:" + ChatColor.GREEN + ChatColor.BOLD + isAlive;
                default -> t.getChatColor() + " Alfa:" + ChatColor.GREEN + ChatColor.BOLD + isAlive;
            };

            t.lastScoreboardString = name;
            Score teamScore = scoreboard.getObjective("Drużyny").getScore(name);
            teamScore.setScore(i);
        }
    }
}
