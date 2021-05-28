package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.ScoreScoreboard;
import io.github.reconsolidated.BedWars.Teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class BlockBreakListener implements Listener {
    private BedWars plugin;
    public BlockBreakListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Participant p = plugin.getParticipant(player);
        if (p == null)
            return;
        if (event.getBlock().getBlockData() instanceof Bed){
            event.setDropItems(false);
            ArrayList<Team> teams = plugin.getTeams();
            for (int i = 0; i<teams.size(); i++){
                if (teams.get(i).getBedLocation().distance(event.getBlock().getLocation()) < 3){
                    if (p.team == teams.get(i)){
                        event.setCancelled(true);
                        p.player.sendMessage(ChatColor.RED + "Nie możesz zniszczyć swojego łóżka idioto");
                    }
                    else{
                        teams.get(i).onBedDestroy();
                        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Zniszczono łóżko drużyny: " + teams.get(i).getChatColor() + teams.get(i).getName());
                    }

                }
            }
        }
    }
}
