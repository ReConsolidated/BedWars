package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    private BedWars plugin;

    public PlayerMoveListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        if (event.getPlayer().getLocation().getBlockY() < 0){
            Participant p = plugin.getParticipant(event.getPlayer());
            if (p == null) return;
            p.onDeath();
        }


    }
}
