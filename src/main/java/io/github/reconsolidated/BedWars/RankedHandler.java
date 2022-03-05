package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.PostgreDB.DatabaseConnector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankedHandler {
    public static void handleRanked(BedWars plugin) throws Exception {
        List<Participant> participants = (List<Participant>) plugin.getParticipants().clone();
        participants.addAll(plugin.getInactiveParticipants());

        Map<Participant, Double> participantsElo = new HashMap<>();
        Map<Participant, Integer> gamesPlayed = new HashMap<>();

        double eloSum = 0;
        int finalKills = 0;
        int bedsDestroyed = 0;

        for (Participant p : participants) {
            double elo = getPlayerElo(p.getPlayer().getName());
            if (elo == 0) {
                throw new Exception("Couldn't connect to database, can't calculate ranked match outcome");
            }
            participantsElo.put(p, elo);

            eloSum += elo;
            finalKills += p.getFinalKills();
            bedsDestroyed += p.getBedsDestroyed();

            int games = getPlayerGamesPlayed(p.getPlayer().getName());
            if (games == -1) {
                throw new Exception("Couldn't connect to database, can't calculate ranked match outcome");
            }
            gamesPlayed.put(p, games);
            setPlayerGamesPlayed(p.getPlayer().getName(), games+1);
        }


        double averageElo = eloSum / participants.size();

        for (Participant p : participantsElo.keySet()) {
            double elo = participantsElo.get(p);

            int place = p.getPlace();
            int teamsNumber = plugin.getTEAMS_COUNT();

            double baseValue = getBaseValue(place, teamsNumber);
            double teamBeds = p.getTeam().getBedsDestroyed();
            double teamKills = p.getTeam().getFinalKills();
            double rankFlexibility = getRankFlexibility(gamesPlayed.get(p));

            double eloValue = getEloValue(elo, averageElo, place <= teamsNumber/2);

            double eloGain = rankFlexibility * eloValue * (teamKills / finalKills * 10
                    * teamsNumber / 4 + teamBeds / bedsDestroyed * 10 * teamsNumber / 4 + baseValue);

            elo += eloGain;
            setPlayerElo(p.getPlayer().getName(), elo);

            Bukkit.getLogger().info("Gracz: " + p.getPlayer().getName());
            Bukkit.getLogger().info("baseValue: " + baseValue);
            Bukkit.getLogger().info("place: " + place);
            Bukkit.getLogger().info("teamsNumber: " + teamsNumber);
            Bukkit.getLogger().info("teamKills: " + teamKills);
            Bukkit.getLogger().info("finalKills: " + finalKills);
            Bukkit.getLogger().info("teamBeds: " + teamBeds);
            Bukkit.getLogger().info("beds: " + bedsDestroyed);
            Bukkit.getLogger().info("rankFlexibility: " + rankFlexibility);
            Bukkit.getLogger().info("averageElo: " + averageElo);
            Bukkit.getLogger().info("elo: " + elo);

            Bukkit.getLogger().info("Nowe ELO gracza: " + p.getPlayer().getName() + ": " + elo);
            p.getPlayer().sendMessage(p.getPlayer().getName(), "Twoje nowe ELO: " + elo);
            plugin.getCommunicator().sendNotification(p.getPlayer().getName(), "Twoje nowe ELO: " + elo);
        }
    }

    private static double getEloValue(double elo, double averageElo, boolean gainedPoints) {
        if (gainedPoints) {
            return averageElo/elo;
        } else {
            return elo/averageElo;
        }
    }

    private static double getRankFlexibility(int gamesPlayed) {
        if (gamesPlayed < 0) {
            throw new RuntimeException("Games played cannot be less than 0! Value: " + gamesPlayed);
        }
        if (gamesPlayed == 0) {
            return 2.5;
        }
        return Math.max(1, 1/Math.log10(gamesPlayed+2));
    }

    private static double getBaseValue(int place, double teamsCount) {
        if (place <= teamsCount/2) {
            return 4 * teamsCount/place;
        } else {
            return -4 * teamsCount/(teamsCount-place+1);
        }
    }

    private static void setPlayerGamesPlayed(String name, int games) {
        if (DatabaseConnector.getSql() == null) {
            Bukkit.getLogger().warning("Database is not connected.");
            return;
        }

        try {
            Statement statement = DatabaseConnector.getSql().createStatement();

            String sql = "UPDATE bedwars_ranked SET games_played=" + games
                    + " WHERE playername='" + name + "';";
            statement.executeUpdate(sql);

            sql = "INSERT INTO bedwars_ranked (playername, games_played) VALUES  ('" + name + "', '" + games + "')" +
                    "ON CONFLICT DO NOTHING;";

            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static int getPlayerGamesPlayed(String name) {
        if (DatabaseConnector.getSql() == null) {
            Bukkit.getLogger().warning("Database is not connected.");
            return -1;
        }

        try {
            Statement statement = DatabaseConnector.getSql().createStatement();

            String sql = "SELECT * FROM bedwars_ranked " +
                    "WHERE playername='" + name + "';";
            statement.executeQuery(sql);
            ResultSet result = statement.getResultSet();
            if (result.next()) {
                return result.getInt("games_played");
            }
            return 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }


    private static double getPlayerElo(String playerName) {
        if (DatabaseConnector.getSql() == null) {
            Bukkit.getLogger().warning("Database is not connected.");
            return 0;
        }

        try {
            Statement statement = DatabaseConnector.getSql().createStatement();

            String sql = "SELECT * FROM bedwars_ranked " +
                    "WHERE playername='" + playerName + "';";
            statement.executeQuery(sql);
            ResultSet result = statement.getResultSet();
            if (result.next()) {
                return result.getDouble("elo");
            }
            return 1000;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    private static void setPlayerElo(String playerName, double ELO) {
        if (DatabaseConnector.getSql() == null) {
            Bukkit.getLogger().warning("Database is not connected.");
            return;
        }

        try {
            Statement statement = DatabaseConnector.getSql().createStatement();

            String sql = "UPDATE bedwars_ranked SET elo=" + ELO
                    + " WHERE playername='" + playerName + "';";
            statement.executeUpdate(sql);

            sql = "INSERT INTO bedwars_ranked (playername, elo) VALUES  ('" + playerName + "', '" + ELO + "')" +
                    "ON CONFLICT DO NOTHING;";

            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public static TextComponent getRankDisplayName(double elo) {
        TextColor bronzeColor = TextColor.color(176, 141, 87);
        TextColor silverColor = TextColor.color(192, 192, 192);
        TextColor goldColor = TextColor.color(218, 165, 32);
        TextColor lapisColor = TextColor.color(70, 115, 219);
        TextColor diamondColor = TextColor.color(37, 194, 178);
        TextColor titanColor = TextColor.color(217, 95, 213);
        // 1000-1100 : silver 3
        if (elo < 1100) {
            return Component.text("Srebro III").color(silverColor);
        }
        if (elo < 1200) {
            return Component.text("Złoto I").color(goldColor);
        }
        if (elo < 1300) {
            return Component.text("Złoto II").color(goldColor);
        }
        return Component.text("Brak rangi");
    }

    public static TextComponent getShortRankDisplayName(double elo) {
        TextColor bronzeColor = TextColor.color(176, 141, 87);
        TextColor silverColor = TextColor.color(192, 192, 192);
        TextColor goldColor = TextColor.color(218, 165, 32);
        TextColor lapisColor = TextColor.color(70, 115, 219);
        TextColor diamondColor = TextColor.color(37, 194, 178);
        TextColor titanColor = TextColor.color(217, 95, 213);
        // 1000-1100 : silver 3
        if (elo < 1100) {
            return Component.text("S III").color(silverColor);
        }
        if (elo < 1200) {
            return Component.text("Z I").color(silverColor);
        }
        if (elo < 1300) {
            return Component.text("Z II").color(goldColor);
        }
        return Component.text("");
    }
}
