package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.PostgreDB.DatabaseConnector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankedHandler {
    private final static Map<String, Double> playerElo = new HashMap<>();
    private final static Map<String, Integer> playerGamesPlayed = new HashMap<>();

    public static void onSetup() {
        playerElo.clear();
        playerGamesPlayed.clear();
    }

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
        }


        double averageElo = eloSum / participants.size();

        for (Participant p : participantsElo.keySet()) {
            double elo = participantsElo.get(p);

            int place = p.getTeam().getPlace();
            if (place == 0) {
                throw new RuntimeException("Place was 0 for team: " + p.getTeam().getName());
            }
            int teamsNumber = BedWars.getInstance().getTEAMS_COUNT();


            double baseValue = getBaseValue(place, teamsNumber);
            double teamBeds = p.getTeam().getBedsDestroyed();
            double teamKills = p.getTeam().getFinalKills();
            double rankFlexibility = getRankFlexibility(gamesPlayed.get(p));

            double eloValue = getEloValue(elo, averageElo, place <= teamsNumber/2);

            if (finalKills == 0) {
                finalKills = 1;
            }
            if (bedsDestroyed == 0) {
                bedsDestroyed = 1;
            }
            double eloGain = rankFlexibility * eloValue * (teamKills / finalKills * 10
                    * teamsNumber / 4 + teamBeds / bedsDestroyed * 10 * teamsNumber / 4 + baseValue);

            elo += eloGain;
            setPlayerElo(p.getPlayer().getName(), elo);
            setStatsPostMatch(p.getPlayer().getName(), place, p.getFinalKills(), p.getDeaths(), p.getKills(), p.getBedsDestroyed());

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

            Bukkit.getLogger().info("Stare ELO gracza: " + p.getPlayer().getName() + ": " + (elo-eloGain));
            Bukkit.getLogger().info("Nowe ELO gracza: " + p.getPlayer().getName() + ": " + elo);


            String message = "&6---------------------------------------\n&7\n";

            message += "&7Zabójstwa: &6" + p.getKills();
            message += "\n&7Final Kille: &6" + p.getFinalKills();
            message += "\n&7Zniszczone łóżka: &6" + p.getBedsDestroyed();
            message += "\n&7Miejsce: &6" + place;
            message += "\n&7\n&7Zdobyto &6&l%d &7punktów rankingowych!".formatted((int) eloGain);
            int games = gamesPlayed.get(p);
            if (games < 5) {
                if (5-games == 1) {
                    message += "\n&7Zagraj jeszcze &d%d &7grę, aby poznać swoją &6rangę.\n".formatted(5-games);
                } else {
                    message += "\n&7Zagraj jeszcze &d%d &7gry, aby poznać swoją &6rangę.\n".formatted(5-games);
                }
            } else {
                message += "\n&bTwoja aktualna &6&lranga &bto &7&l%s, &6&l%d &bpunktów.\n".formatted(getString(getRankDisplayName(elo, games)), getPointsLeft(elo));
            }
            message += "&7\n&6---------------------------------------";
            sendNotificationAndMessage(p.getPlayer(), message);
        }
    }

    private static String getString(Component textComponent) {
        TextComponent component = (TextComponent) textComponent;
        return component.content();
    }

    private static void sendNotificationAndMessage(Player player, String message) {
        BedWars.getInstance().getCommunicator().sendNotification(player.getName(), message);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
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
        if (teamsCount == 2) {
            if (place == 1) return 8;
            if (place == 2) return -10;
        }
        else if (teamsCount == 4) {
            if (place == 1) return 8;
            if (place == 2) return 4;
            if (place == 3) return -6;
            if (place == 4) return -12;
        }
        else if (teamsCount == 8) {
            if (place == 1) return 8;
            if (place == 2) return 6;
            if (place == 3) return 4;
            if (place == 4) return 2;
            if (place == 5) return -3;
            if (place == 6) return -6;
            if (place == 7) return -9;
            if (place == 8) return -12;
        } else {
            throw new RuntimeException("TeamsCount is not 2, 4 or 8, can't handle ranked");
        }
        throw new RuntimeException("Incorrect place: " + place);
    }

    private static void setPlayerGamesPlayed(String name, int games) {
        playerGamesPlayed.put(name, games);

        if (DatabaseConnector.getSql() == null) {
            Bukkit.getLogger().warning("Database is not connected.");
            return;
        }

        try {
            Statement statement = DatabaseConnector.getSql().createStatement();

            String sql = "UPDATE bedwars_ranked SET games_played=" + games
                    + " WHERE player_name='" + name + "' AND queue_name='" + BedWars.getInstance().getQueueName() + "';";
            statement.executeUpdate(sql);

            sql = "INSERT INTO bedwars_ranked (player_name, queue_name, games_played) VALUES  ('%s', '%s', %d) ON CONFLICT DO NOTHING;"
                    .formatted(name, BedWars.getInstance().getQueueName(), games);

            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static int getPlayerGamesPlayed(String name) {
        if (playerGamesPlayed.containsKey(name)) {
            return playerGamesPlayed.get(name);
        }


        if (DatabaseConnector.getSql() == null) {
            Bukkit.getLogger().warning("Database is not connected.");
            return -1;
        }

        try {
            Statement statement = DatabaseConnector.getSql().createStatement();

            String sql = "SELECT games_played FROM bedwars_ranked " +
                    "WHERE player_name='%s' AND queue_name='%s';".formatted(name, BedWars.getInstance().getQueueName());
            statement.executeQuery(sql);
            ResultSet result = statement.getResultSet();
            if (result.next()) {
                int gamesPlayed = result.getInt("games_played");
                playerGamesPlayed.put(name, gamesPlayed);
                return gamesPlayed;
            }
            return 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }


    public static double getPlayerElo(String playerName) {
        if (playerElo.containsKey(playerName)) {
            return playerElo.get(playerName);
        }

        if (DatabaseConnector.getSql() == null) {
            Bukkit.getLogger().warning("Database is not connected.");
            return 0;
        }

        try {
            Statement statement = DatabaseConnector.getSql().createStatement();

            String sql = "SELECT elo FROM bedwars_ranked " +
                    "WHERE player_name='%s' AND queue_name='%s';".formatted(playerName, BedWars.getInstance().getQueueName());
            statement.executeQuery(sql);
            ResultSet result = statement.getResultSet();
            if (result.next()) {
                double elo = result.getDouble("elo");
                playerElo.put(playerName, elo);
                return elo;
            }
            return 1000;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    private static void setPlayerElo(String playerName, double ELO) {
        playerElo.put(playerName, ELO);

        if (DatabaseConnector.getSql() == null) {
            Bukkit.getLogger().warning("Database is not connected.");
            return;
        }

        try {
            Statement statement = DatabaseConnector.getSql().createStatement();

            String sql = "UPDATE bedwars_ranked SET elo=" + ELO
                    + " WHERE player_name='%s' AND queue_name='%s';".formatted(playerName, BedWars.getInstance().getQueueName());
            statement.executeUpdate(sql);

            sql = "INSERT INTO bedwars_ranked (player_name, queue_name, elo) VALUES  ('%s', '%s', %f) ON CONFLICT DO NOTHING;"
                    .formatted(playerName, BedWars.getInstance().getQueueName(), ELO);

            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    /**
     *
     Record with playerName=playerName and queueName=plugin.getQueueName() must exist in database before using this
     */
    private static void setStatsPostMatch(String playerName, int place, int finalKills, int deaths, int kills, int bedsDestroyed) {
        if (DatabaseConnector.getSql() == null) {
            Bukkit.getLogger().warning("Database is not connected.");
            return;
        }

        try {
            Statement statement = DatabaseConnector.getSql().createStatement();

            int streak = getPlayerStreak(playerName);
            int teams = BedWars.getInstance().getTEAMS_COUNT();
            if (place <= BedWars.getInstance().getPlayersOnStart()/BedWars.getInstance().getTEAM_SIZE()/2) {
                if (streak >= 0) {
                    streak++;
                }
                if (streak < 0) {
                    streak = 1;
                }
            } else {
                if (streak <= 0) {
                    streak--;
                }
                if (streak > 0) {
                    streak = -1;
                }
            }


            String sql = """
            UPDATE bedwars_ranked SET games_played=games_played+1, streak=%d, final_kills=final_kills+%d,
             deaths=deaths+%d, kills=kills+%d, beds_destroyed=beds_destroyed+%d, sum_of_places=sum_of_places+%d
             WHERE player_name='%s' AND queue_name='%s';
            """.formatted(streak, finalKills, deaths, kills, bedsDestroyed, place, playerName, BedWars.getInstance().getQueueName());
            statement.executeUpdate(sql);

            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static int getPlayerStreak(String playerName) {
        if (DatabaseConnector.getSql() == null) {
            Bukkit.getLogger().warning("Database is not connected.");
            return 0;
        }

        try {
            Statement statement = DatabaseConnector.getSql().createStatement();

            String sql = "SELECT streak FROM bedwars_ranked " +
                    "WHERE player_name='%s' AND queue_name='%s';".formatted(playerName, BedWars.getInstance().getQueueName());
            statement.executeQuery(sql);
            ResultSet result = statement.getResultSet();
            if (result.next()) {
                return result.getInt("streak");
            }
            return 1000;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    public static TextComponent getRankPrefix(double elo, int gamesPlayed) {
        return Component.text("[").color(TextColor.color(166,163,154))
                .append(
                        RankedHandler.getRankDisplayName(elo, gamesPlayed).decorate(TextDecoration.BOLD)
                                .append(Component.text("] ").decoration(TextDecoration.BOLD, false)
                                        .color(TextColor.color(166,163,154))));
    }


    public static int getPointsLeft(double elo) {
        if (elo < 500) {
            return 0;
        }
        if (elo > 2000) {
            return (int) elo - 2000;
        }
        return (int) elo % 100;
    }


    public static TextComponent getRankDisplayName(double elo, int gamesPlayed) {
        TextColor bronzeColor = TextColor.color(176, 141, 87);
        TextColor silverColor = TextColor.color(192, 192, 192);
        TextColor goldColor = TextColor.color(218, 165, 32);
        TextColor lapisColor = TextColor.color(70, 115, 219);
        TextColor diamondColor = TextColor.color(37, 194, 178);
        TextColor titanColor = TextColor.color(217, 95, 213);
        if (elo == 0 || gamesPlayed < 5) {
            return Component.text("Brak rangi");
        }
        if (elo < 600) {
            return Component.text("Brąz I").color(bronzeColor);
        }
        if (elo < 700) {
            return Component.text("Brąz II").color(bronzeColor);
        }
        if (elo < 800) {
            return Component.text("Brąz III").color(bronzeColor);
        }
        if (elo < 900) {
            return Component.text("Srebro I").color(silverColor);
        }
        if (elo < 1000) {
            return Component.text("Srebro II").color(silverColor);
        }
        // 1000-1100 : silver 3, starting rank
        if (elo < 1100) {
            return Component.text("Srebro III").color(silverColor);
        }
        if (elo < 1200) {
            return Component.text("Złoto I").color(goldColor);
        }
        if (elo < 1300) {
            return Component.text("Złoto II").color(goldColor);
        }
        if (elo < 1400) {
            return Component.text("Złoto III").color(goldColor);
        }
        if (elo < 1500) {
            return Component.text("Lapis I").color(lapisColor);
        }
        if (elo < 1600) {
            return Component.text("Lapis II").color(lapisColor);
        }
        if (elo < 1700) {
            return Component.text("Lapis III").color(lapisColor);
        }
        if (elo < 1800) {
            return Component.text("Diament I").color(diamondColor);
        }
        if (elo < 1900) {
            return Component.text("Diament II").color(diamondColor);
        }
        if (elo < 2000) {
            return Component.text("Diament III").color(diamondColor);
        }
        return Component.text("Tytan").color(titanColor);
    }

    public static TextComponent getShortRankDisplayName(double elo, int gamesPlayed) {
        TextColor bronzeColor = TextColor.color(176, 141, 87);
        TextColor silverColor = TextColor.color(192, 192, 192);
        TextColor goldColor = TextColor.color(218, 165, 32);
        TextColor lapisColor = TextColor.color(70, 115, 219);
        TextColor diamondColor = TextColor.color(37, 194, 178);
        TextColor titanColor = TextColor.color(217, 95, 213);
        if (elo == 0 || gamesPlayed < 5) {
            return Component.text("");
        }
        if (elo < 600) {
            return Component.text("B I").color(bronzeColor);
        }
        if (elo < 700) {
            return Component.text("B II").color(bronzeColor);
        }
        if (elo < 800) {
            return Component.text("B III").color(bronzeColor);
        }
        if (elo < 900) {
            return Component.text("S I").color(silverColor);
        }
        if (elo < 1000) {
            return Component.text("S II").color(silverColor);
        }
        // 1000-1100 : silver 3, starting rank
        if (elo < 1100) {
            return Component.text("S III").color(silverColor);
        }
        if (elo < 1200) {
            return Component.text("Z I").color(goldColor);
        }
        if (elo < 1300) {
            return Component.text("Z II").color(goldColor);
        }
        if (elo < 1400) {
            return Component.text("Z III").color(goldColor);
        }
        if (elo < 1500) {
            return Component.text("L I").color(lapisColor);
        }
        if (elo < 1600) {
            return Component.text("L II").color(lapisColor);
        }
        if (elo < 1700) {
            return Component.text("L III").color(lapisColor);
        }
        if (elo < 1800) {
            return Component.text("D I").color(diamondColor);
        }
        if (elo < 1900) {
            return Component.text("D II").color(diamondColor);
        }
        if (elo < 2000) {
            return Component.text("D III").color(diamondColor);
        }
        return Component.text("Tytan").color(titanColor);
    }
}
