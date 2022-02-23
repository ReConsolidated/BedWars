package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.jediscommunicator.JedisCommunicator;
import org.bukkit.scheduler.BukkitRunnable;

public class JedisRunnable extends BukkitRunnable {
    private final BedWars plugin;
    private final JedisCommunicator communicator;

    public JedisRunnable(BedWars plugin, JedisCommunicator communicator) {
        this.plugin = plugin;
        this.communicator = communicator;

        runTaskTimer(plugin, 0L, 10L);
    }

    @Override
    public void run() {
        int maxPartySize = plugin.getTEAM_SIZE();
        if (plugin.getPartiesCount() >= plugin.getTEAMS_COUNT()) {
            maxPartySize = 0;
        }
        communicator.setServerInfo(plugin.getServerName(), plugin.isOpen(), plugin.getParticipants().size(), plugin.getMaxPlayers(), maxPartySize, "bedwars" + plugin.getTEAM_SIZE());
    }
}
