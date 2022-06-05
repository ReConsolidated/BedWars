package io.github.reconsolidated.BedWars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class StartGameRunnable extends BukkitRunnable {
    private final BedWars plugin;
    private int counter;

    public StartGameRunnable(BedWars plugin){
        this.plugin = plugin;
        counter = 18000;
        this.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void run() {

        int maxPlayers = plugin.getTEAM_SIZE() * plugin.getTEAMS_COUNT();
        if (Bukkit.getOnlinePlayers().size() > 0){
            counter--;
        }
        if (Bukkit.getOnlinePlayers().size() >= 0.25 * maxPlayers && counter > 60){
            counter = 60;
        }
        if (Bukkit.getOnlinePlayers().size() >= 0.75 * maxPlayers && counter > 30){
            counter = 30;
        }
        if (Bukkit.getOnlinePlayers().size() >= maxPlayers && counter > 10){
            counter = 10;
        }

        if (counter <= 60 && counter > 5 && counter%10 == 0){
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Gra wystartuje za " + counter + " sekund.");
        }
        else if (counter == 5){
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Gra wystartuje za " + counter + " sekund.");
        }

        if (counter < 60 && Bukkit.getOnlinePlayers().size() < 0.25 * maxPlayers){
            Bukkit.broadcastMessage(ChatColor.RED + "Jest za mało graczy, przerywam odliczanie.");
            counter = 18000;
        }

        if (plugin.hasStarted){
            this.cancel();
        }

        if (counter <= 5){
            plugin.setOpen(false);
            plugin.onStart();
            this.cancel();
        } else {
            plugin.setOpen(true);
        }

    }
}
