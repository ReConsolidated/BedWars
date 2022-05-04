package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.jediscommunicator.JedisCommunicator;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class RejoinRunnable extends BukkitRunnable {

    @Override
    public void run() {
        JedisCommunicator jedis = new JedisCommunicator();
        for (Participant p : BedWars.getInstance().getInactiveParticipants()) {
            if (p.getTeam().isPlaying()) {
                jedis.setRejoin(p.getPlayer().getName(), "" + Bukkit.getServer().getPort());
            }
        }
    }
}
