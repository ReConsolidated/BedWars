package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.CustomSpectator.CustomSpectator;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.Party.PartyDataManager;
import io.github.reconsolidated.BedWars.Party.PartyDomain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

import static io.github.reconsolidated.BedWars.DataBase.LobbyConnection.ServerStateManager.sendServerState;

public class PlayerJoinListener implements Listener {
    private ArrayList<Participant> participants;
    private BedWars plugin;

    public PlayerJoinListener(BedWars plugin){
        this.participants = plugin.getParticipants();
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if (plugin.hasStarted){
            plugin.vanishPlayer(player);
            event.setJoinMessage(null);
            return;
        }

        // Updating serverStateDomain if there is a new party
        PartyDomain party = PartyDataManager.getParty(event.getPlayer());
        if (party != null) {
            if (party.getOwner().equalsIgnoreCase(event.getPlayer().getName())){
                plugin.setPartiesCount(plugin.getPartiesCount()+1);
            }
        }
        sendServerState(plugin);

        Participant p = plugin.getInactiveParticipant(player);
        if (p != null){
            p.setPlayer(player);
            plugin.restoreParticipant(p);
            if (p.isDead()){
                CustomSpectator.setSpectator(plugin, player);
            }
            else{
                p.onRespawn();
            }
            p.getScoreboard().registerPlayer(p.getPlayer());

        }
        else{
            player.teleport(plugin.world.getSpawnLocation());

            for (Player p2 : Bukkit.getOnlinePlayers()){
                player.hidePlayer(plugin, p2);
                p2.hidePlayer(plugin, player);
            }
            for (Player p2 : Bukkit.getOnlinePlayers()){
                player.showPlayer(plugin, p2);
                p2.showPlayer(plugin, player);
            }

            if (!plugin.hasStarted && plugin.getMaxPlayers() > Bukkit.getOnlinePlayers().size()){
                p = new Participant(player, plugin);
                participants.add(p);
                event.setJoinMessage(ChatColor.YELLOW + player.getName()
                        + " dołączył (" + ChatColor.AQUA + participants.size()
                        + ChatColor.YELLOW + "/" + ChatColor.AQUA
                        + (plugin.getTEAMS_COUNT() * plugin.getTEAM_SIZE())
                        + ChatColor.YELLOW + ").");
                player.setInvulnerable(true);
            }
            else{
                CustomSpectator.setSpectator(plugin, player);
            }
        }

    }
}
