package io.github.reconsolidated.BedWars;


import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Score;

public class CountdownRunnable extends BukkitRunnable {
    private int countdown; //This is how long(in secs) you want the countdown to be!
    private ScoreScoreboard ssb; //Making it so we can access the scoreboard board and objective o
    private boolean isGoing = false;
    private BedWars plugin;

    private String lastScore = "";

    public CountdownRunnable(BedWars plugin, ScoreScoreboard ssb, int countdown) {
        this.plugin = plugin;
        this.ssb = ssb;
        this.countdown = countdown;
        this.isGoing = false;
    }

    public void start(){
        isGoing = true;
    }

    public void stop(){
        isGoing = false;
    }

    @Override
    public void run() {
        if (isGoing){
            countdown --; //Taking away 1 from countdown every 1 second
            ssb.scoreboard.resetScores(lastScore);
            if (countdown%60 > 9){
                lastScore = ("Time left: 0" + ((countdown - countdown%60)/60) + ":" + countdown%60);
            }
            else{
                lastScore = ("Time left: 0" + ((countdown - countdown%60)/60)+ ":0" + countdown%60);
            }


            Score score = ssb.scoreboard.getObjective("Score").getScore(lastScore);
            score.setScore(-2); //Making it so after "Time:" it displays the int countdown(So how long it has left in seconds.)
            if(countdown == 0) {
                plugin.onGameEnd();
                this.cancel();
            }
        }

    }
}