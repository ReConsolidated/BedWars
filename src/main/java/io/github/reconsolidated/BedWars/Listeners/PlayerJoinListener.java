package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.CustomSpectator.CustomSpectator;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.Party.PartyDataManager;
import io.github.reconsolidated.BedWars.Party.PartyDomain;
import io.github.reconsolidated.BedWars.RankedHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;

import java.util.List;


public class PlayerJoinListener implements Listener {
    private BedWars plugin;

    public PlayerJoinListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        List<Participant> participants = plugin.getParticipants();
        if (plugin.hasStarted){
            event.setJoinMessage(null);
            if (player.hasPermission("moderator")){
                plugin.vanishPlayer(player);
                player.teleport(plugin.getSpawnLocation());
            } else {
                player.kickPlayer(ChatColor.RED + "Spróbuj ponownie za chwilę...");
            }
            return;
        }

        // Updating serverStateDomain if there is a new party
        PartyDomain party = PartyDataManager.getParty(event.getPlayer());
        if (party != null) {
            if (party.getOwner().equalsIgnoreCase(event.getPlayer().getName())){
                plugin.setPartiesCount(plugin.getPartiesCount()+1);
            }
        }

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
                player.hidePlayer(p2);
                p2.hidePlayer(player);
            }
            for (Player p2 : Bukkit.getOnlinePlayers()){
                player.showPlayer(p2);
                p2.showPlayer(player);
            }

            double elo = RankedHandler.getPlayerElo(player.getName());
            int gamesPlayed = RankedHandler.getPlayerGamesPlayed(player.getName());
            TextComponent shortRankDisplayName = (TextComponent) RankedHandler.getRankDisplayName(elo, gamesPlayed).decorate(TextDecoration.BOLD);

            if (shortRankDisplayName.content().length() > 0) {
                player.displayName(Component.text("[").color(TextColor.color(166,163,154))
                        .append(
                                shortRankDisplayName
                                        .append(Component.text("] ").color(TextColor.color(166,163,154))
                                                .append(Component.text(player.getName()).color(TextColor.color(255, 255, 255))))));
            } else {
                player.displayName(Component.text(player.getName()).color(TextColor.color(255, 255, 255)));
            }


            if (!plugin.hasStarted && plugin.getMaxPlayers() > Bukkit.getOnlinePlayers().size()){
                p = new Participant(player, plugin);
                participants.add(p);
                event.joinMessage(player.displayName().append(Component.text(ChatColor.YELLOW + " dołączył (" + ChatColor.AQUA + participants.size()
                        + ChatColor.YELLOW + "/" + ChatColor.AQUA
                        + (plugin.getTEAMS_COUNT() * plugin.getTEAM_SIZE())
                        + ChatColor.YELLOW + ").")));

                player.setPlayerListName(player.getName());
                player.getInventory().clear();
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                player.getEnderChest().clear();
                player.setGameMode(GameMode.ADVENTURE);
                player.setFireTicks(0);
                player.setHealth(player.getMaxHealth());
            }
            else{
                CustomSpectator.setSpectator(plugin, player);
            }
        }


    }
}
