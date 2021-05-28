package io.github.reconsolidated.BedWars;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnRunnable extends BukkitRunnable {
    private int countdown;
    private BedWars plugin;
    private Participant p;

    public RespawnRunnable(BedWars plugin, int countdown, Participant p) {
        this.plugin = plugin;
        this.countdown = countdown;
        this.p = p;
    }

    @Override
    public void run() {
        countdown --; //Taking away 1 from countdown every 1 second
        if (countdown > 0){
            p.player.playSound(p.player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 10, 1);
            p.player.sendTitle(ChatColor.GREEN + "Odrodzisz się za: " + countdown,
                    "", 5, 20, 5);
        }

        if(countdown == 0) {
            p.onRespawn();
            this.cancel();
        }


    }
}