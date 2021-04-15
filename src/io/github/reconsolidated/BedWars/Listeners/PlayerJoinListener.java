package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.ScoreScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class PlayerJoinListener implements Listener {
    private ScoreScoreboard myScoreboard;
    private ArrayList<Participant> participants;
    private BedWars plugin;

    public PlayerJoinListener(ScoreScoreboard myScoreboard, ArrayList<Participant> participants, BedWars plugin){
        this.myScoreboard = myScoreboard;
        this.participants = participants;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();


        Participant p = new Participant(player, plugin);
        player.setScoreboard(myScoreboard.scoreboard);
        participants.add(p);
    }
}
