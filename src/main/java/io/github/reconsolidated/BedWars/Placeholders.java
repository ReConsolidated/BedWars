package io.github.reconsolidated.BedWars;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {
    private final BedWars plugin;

    public Placeholders(BedWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bedwars";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ReConsolidated";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        if(params.equalsIgnoreCase("name")) {
            return offlinePlayer == null ? null : offlinePlayer.getName(); // "name" requires the player to be valid
        }

        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            // %bedwars_rank-name-short
            String[] args = params.split("-");
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("rank")) {
                    if (args[1].equalsIgnoreCase("name")) {
                        if (args[2].equalsIgnoreCase("short")) {
                            double elo = RankedHandler.getPlayerElo(offlinePlayer.getName());
                            int gamesPlayed = RankedHandler.getPlayerGamesPlayed(offlinePlayer.getName());
                            return RankedHandler.getShortRankDisplayName(elo, gamesPlayed).content();
                        }
                    }
                }
            }
        }

        return null; // Placeholder is unknown by the Expansion

    }
}
