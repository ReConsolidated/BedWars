package io.github.reconsolidated.BedWars;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;

public class ScoreScoreboard extends BukkitRunnable {
    public Objective objective;
    private ArrayList<Participant> participants;
    private ScoreboardManager manager;
    public Scoreboard scoreboard;


    ScoreScoreboard(ArrayList<Participant> participants){
        this.participants = participants;

        manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        objective = scoreboard.registerNewObjective("Score", "dummy", "Score");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int i = 0; i<participants.size(); i++){
            participants.get(i).player.setScoreboard(scoreboard);
        }


    }

    @Override
    public void run() {
        Score spaceScore = scoreboard.getObjective("Score").getScore("    ");
        spaceScore.setScore(-1);

        participants.sort(Participant.scoreComparator);
        int bestScore = 0;
        for (int i = 0; i<participants.size(); i++){
            Participant p = participants.get(i);
            Score score = scoreboard.getObjective("Score").getScore(p.player.getName());
            score.setScore(p.currentScore);
            if (p.currentScore > bestScore){
                bestScore = p.currentScore;
            }
        }

        spaceScore = scoreboard.getObjective("Score").getScore("      ");
        spaceScore.setScore(bestScore+1);



    }
}
