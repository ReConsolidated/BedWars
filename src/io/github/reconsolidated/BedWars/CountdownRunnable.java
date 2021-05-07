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
        this.countdown = 0;
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
            countdown++; // Adding 1 every second
            ssb.scoreboard.resetScores(lastScore);
            int timeTillNextEvent = plugin.getNextEventTime() - countdown;
            if (timeTillNextEvent%60 > 9){
                lastScore = (plugin.getNextEventName() + " za: 0" + ((timeTillNextEvent - timeTillNextEvent%60)/60) + ":" + timeTillNextEvent%60);
            }
            else{
                lastScore = (plugin.getNextEventName() + " za: 0" + ((timeTillNextEvent - timeTillNextEvent%60)/60)+ ":0" + timeTillNextEvent%60);
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