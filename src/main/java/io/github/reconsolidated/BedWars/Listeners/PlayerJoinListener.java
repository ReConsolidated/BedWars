package io.github.reconsolidated.BedWars.Listeners;


import io.github.reconsolidated.BedWars.AfterGameEnd.Items;
import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.CustomSpectator.CustomSpectator;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.Party.PartyDataManager;
import io.github.reconsolidated.BedWars.RankedHandler;
import io.github.reconsolidated.BedWars.Scoreboards.LobbyScoreboard;
import io.github.reconsolidated.jediscommunicator.Party;
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
        plugin.getJedisRunnable().run();

        Player player = event.getPlayer();

        player.setMaximumNoDamageTicks(20);
        List<Participant> participants = plugin.getParticipants();
        if (plugin.hasStarted){
            event.setJoinMessage(null);

            boolean isInactive = false;
            for (Participant in : plugin.getInactiveParticipants()) {
                if (in.getPlayer().getName().equalsIgnoreCase(player.getName())) {
                    isInactive = true;
                }
            }
            if (!isInactive && player.hasPermission("moderator")){
                plugin.vanishPlayer(player);
                player.teleport(plugin.getSpawnLocation());
                return;
            } else if (!isInactive) {
                player.kickPlayer(ChatColor.RED + "Spróbuj ponownie za chwilę...");
                return;
            }

        }

        // Updating serverStateDomain if there is a new party
        Party party = PartyDataManager.getParty(event.getPlayer());
        if (party != null) {
            Bukkit.getLogger().info("[DEBUG] party gracza %s:".formatted(event.getPlayer().getName()) + party.getAllMembers());

            if (party.getOwner().equalsIgnoreCase(event.getPlayer().getName())){
                plugin.setPartiesCount(plugin.getPartiesCount()+1);
            }
        } else {
            Bukkit.getLogger().info("[DEBUG] party gracza %s jest null".formatted(event.getPlayer().getName()));
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
            p.setPrefixes();

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


            if (!plugin.hasStarted && plugin.getMaxPlayers() >= Bukkit.getOnlinePlayers().size()){
                p = new Participant(player, plugin);
                new LobbyScoreboard(plugin, player);
                participants.add(p);
                event.joinMessage(player.displayName().append(Component.text(ChatColor.YELLOW + " dołączył (" + ChatColor.AQUA + participants.size()
                        + ChatColor.YELLOW + "/" + ChatColor.AQUA
                        + (plugin.getTEAMS_COUNT() * plugin.getTEAM_SIZE())
                        + ChatColor.YELLOW + ").")));

                player.getInventory().clear();
                player.getInventory().setItem(8, Items.getLeaveItem());
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
