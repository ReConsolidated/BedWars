package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.PostgreDB.DatabaseConnector;
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

        double eloSum = 0;

        for (Participant p : participants) {
            double elo = getPlayerElo(p.getPlayer().getName());
            if (elo == 0) {
                throw new Exception("Couldn't connect to database, can't calculate ranked match outcome");
            }
            eloSum += elo;
            participantsElo.put(p, elo);
        }


        double averageElo = eloSum / participants.size();

        for (Participant p : participantsElo.keySet()) {
            double elo = participantsElo.get(p);
            double difference = averageElo - elo;

            int place = p.getPlace();
            int teamsNumber = plugin.getTEAMS_COUNT();

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
}
