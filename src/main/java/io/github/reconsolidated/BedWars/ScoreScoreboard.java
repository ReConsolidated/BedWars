package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.Teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
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
    private Participant owner;
    private BedWars plugin;
    private String[] lastScoreboardStrings;

    public ScoreScoreboard(BedWars plugin, ArrayList<Team> teams, Participant owner){
        this.teams = teams;
        this.owner = owner;
        this.plugin = plugin;
        lastScoreboardStrings = new String[teams.size()];
        for (int i = 0; i<teams.size(); i++){
            lastScoreboardStrings[i] = "--";
        }
        manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        objective = scoreboard.registerNewObjective("Drużyny", "dummy", "Score");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        scoreboard.getObjective("Drużyny").setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "BEDWARS");
        Score funFactScore = scoreboard.getObjective("Drużyny").getScore(ChatColor.GREEN + "grypciocraft.pl");
        funFactScore.setScore(12);
        Score spaceScore = scoreboard.getObjective("Drużyny").getScore("    ");
        spaceScore.setScore(11);

        // on Score 10 there is the countdown runnable

        spaceScore = scoreboard.getObjective("Drużyny").getScore(" ");
        spaceScore.setScore(9);
        spaceScore = scoreboard.getObjective("Drużyny").getScore("      ");
        spaceScore.setScore(-1);

        owner.getPlayer().setScoreboard(scoreboard);


        // CREATING SCOREBOARD TEAMS
        org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.registerNewTeam("ALL_PLAYERS");
        for (Player bukkitPlayer : Bukkit.getOnlinePlayers()){
            scoreboardTeam.addEntry(bukkitPlayer.getName());
        }
        scoreboardTeam.setCanSeeFriendlyInvisibles(false);
        scoreboardTeam.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE,
                org.bukkit.scoreboard.Team.OptionStatus.NEVER);



        new CountdownRunnable(plugin, this).runTaskTimer(plugin, 0L, 20L);

    }

    public void registerPlayer(Player player){
        player.setScoreboard(scoreboard);
    }


    @Override
    public void run() {
        String isAlive = "  ✓ ";

        for (int i = 0; i<teams.size(); i++){
            Team t = teams.get(i);
            if (t.isBedAlive()){
                isAlive = "  ✓     ";
            }
            else{
                int alive = 0;
                for (Participant p : t.members){
                    if (p.getPlayer().isOnline() && (p.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || p.isRespawning()) ){
                        alive++;
                    }
                }
                if (alive == 0) isAlive = ChatColor.RED + "  ✖     ";
                if (alive == 1) isAlive = ChatColor.GREEN + "  1     ";
                if (alive == 2) isAlive = ChatColor.GREEN + "  2     ";
                if (alive == 3) isAlive = ChatColor.GREEN + "  3     ";
                if (alive == 4) isAlive = ChatColor.GREEN + "  4     ";
            }
            String name = t.getChatColor() +  " " + t.getName()
                    + getStringIfIsInTeam(i, ChatColor.GRAY + " (ty)") + ":"
                    + ChatColor.GREEN + ChatColor.BOLD + isAlive;

            if (t.ID != i && lastScoreboardStrings[t.ID].equals(name)){
                continue;
            }
            if (t.ID == i && lastScoreboardStrings[t.ID].equals(name)){
                continue;
            }

            if (t.ID != i){
                scoreboard.resetScores(lastScoreboardStrings[t.ID]);
                lastScoreboardStrings[t.ID] = name;
            }
            else{
                scoreboard.resetScores(lastScoreboardStrings[t.ID]);
                lastScoreboardStrings[t.ID] = name;
            }

            Score teamScore = scoreboard.getObjective("Drużyny").getScore(name);
            teamScore.setScore(7-i);
        }


    }
    private String getStringIfIsInTeam(int teamID, String s){
        if (teamID == owner.getTeam().ID) return s;
        return "";
    }
}
