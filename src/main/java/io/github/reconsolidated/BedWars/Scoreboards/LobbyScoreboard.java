package io.github.reconsolidated.BedWars.Scoreboards;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.RankedHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.UUID;


public class LobbyScoreboard implements Listener {
    private final Scoreboard scoreboard;
    private final ArrayList<String> data;
    private final Player player;
    private final Objective objective;
    private final BedWars plugin;

    public LobbyScoreboard(BedWars plugin, Player player){
        this.plugin = plugin;
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        if (scoreboard.getObjective("main") == null){
            objective = scoreboard.registerNewObjective("main", "dummy", ChatColor.GREEN + "" + ChatColor.BOLD + "BedWars");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        else{
            objective = scoreboard.getObjective("main");
        }
        data = new ArrayList<>();
        player.setScoreboard(scoreboard);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        // From this point, the code is specific for this plugin and doesn't impact the scoreboard mechanics.
        // Od tego momentu są określone dane specyficzne dla tego pluginu, nie wpływające na działanie scoreboardu.
        for (Player p2 : Bukkit.getOnlinePlayers()) {
            assignPlayerTeam(scoreboard, p2);
        }


        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    HandlerList.unregisterAll(LobbyScoreboard.this);
                }
                update();
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> assignPlayerTeam(scoreboard, event.getPlayer()), 20L);
    }

    private void assignPlayerTeam(Scoreboard scoreboard, Player player) {
        org.bukkit.scoreboard.Team t = scoreboard.getTeam(player.getName());
        if (t == null) {
            t = scoreboard.registerNewTeam(player.getName());
        }
        double elo = RankedHandler.getPlayerElo(player.getName());
        int gamesPlayed = RankedHandler.getPlayerGamesPlayed(player.getName());
        t.prefix(RankedHandler.getRankPrefix(elo, gamesPlayed));
        t.addPlayer(player);
    }

    public void update(){
        set(1, "");
        set(2, ChatColor.GOLD + "Graczy: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size() + ChatColor.GRAY + "/"
                + ChatColor.WHITE + plugin.getMaxPlayers());
        set(3, "");
        set(4, ChatColor.GREEN + "Mapa: " + ChatColor.WHITE + plugin.world.getName());
        if (plugin.isRanked()) {
            set(5, ChatColor.DARK_AQUA + "Tryb: " + ChatColor.WHITE + "Rankingowy");
        } else {
            set(5, ChatColor.DARK_AQUA + "Tryb: " + ChatColor.WHITE + "Normalny");
        }
        set(6, "");
        set(7, ChatColor.GOLD + "" +  ChatColor.BOLD + "GRPMC.PL");
    }

    public void put(String newText){
        for (String text : data){
            if (newText.equalsIgnoreCase(text)){
                newText += " ";
                put(newText);
                return;
            }
        }
        data.add(newText);
        for (int i = 0; i<data.size(); i++){
            objective.getScore(data.get(i)).setScore(data.size() - i);
        }
    }

    public void set(int lineNumber, String newText){
        for (int i = 0; i<data.size(); i++){
            if (lineNumber == i) continue;
            String text = data.get(i);
            if (text.equalsIgnoreCase(newText)){
                set(lineNumber, newText + " ");
                return;
            }
        }
        if (lineNumber > data.size()){
            for (int i = 0; i<=(lineNumber - data.size()); i++){
                put("");
            }
            put(newText);
            return;
        }
        if (lineNumber == data.size()){
            put(newText);
            return;
        }
        String old = data.get(lineNumber);
        if (old.equals(newText)) return;
        data.set(lineNumber, newText);
        Score score = objective.getScore(old);
        int scoreValue = score.getScore();
        scoreboard.resetScores(old);
        objective.getScore(newText).setScore(scoreValue);
    }

}
