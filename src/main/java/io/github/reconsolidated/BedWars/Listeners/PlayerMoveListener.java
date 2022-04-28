package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMoveListener implements Listener {
    private BedWars plugin;
    public static Map<UUID, Long> lastMove = new HashMap<>();

    public PlayerMoveListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        lastMove.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());

        if (event.getPlayer().getLocation().getBlockY() < 0){
            Participant p = plugin.getParticipant(event.getPlayer());
            if (p == null || p.isSpectating()) return;
            p.onDeath();
        }
    }

    public static boolean isAfk(Player player) {
        Long time = lastMove.get(player.getUniqueId());
        if (time == null) {
            time = 0L;
        }
        return time < System.currentTimeMillis() - 5000;
    }
}
