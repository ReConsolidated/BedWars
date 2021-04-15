package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.ScoreScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;

public class EntityDamageListener implements Listener {
    private ScoreScoreboard scoreboard;
    private ArrayList<Participant> participants;
    public EntityDamageListener(ScoreScoreboard scoreboard, ArrayList<Participant> participants){
        this.scoreboard = scoreboard;
        this.participants = participants;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event){

        if (event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
            Player player = (Player) event.getEntity();
            for (int i = 0; i<participants.size(); i++){
                Participant p = participants.get(i);
                if (p.player.equals(player)){
                    if (player.getHealth() - event.getFinalDamage() <= 0){
                        event.setCancelled(true);
                        p.onDeath();
                    }
                }
            }

        }
    }
}
