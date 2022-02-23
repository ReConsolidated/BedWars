package io.github.reconsolidated.BedWars.PostgreDB;

import io.github.reconsolidated.BedWars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseFunctions {


    private static PlayerGlobalDataDomain getPlayerDomain(UUID uuid){
        if (DatabaseConnector.getSql() == null) {
            Bukkit.getLogger().warning("Database is not connected.");
            return null;
        }

        try {
            Statement statement = DatabaseConnector.getSql().createStatement();

            String sql = "SELECT * FROM playerglobaldata " +
                    "WHERE uuid='" + uuid.toString() + "';";
            statement.executeQuery(sql);
            ResultSet result = statement.getResultSet();
            if (result.next()) {
                return new PlayerGlobalDataDomain(uuid, result.getInt("grypciocoins"), result.getInt("experience"), result.getInt("inGameTime"));
            }
            return null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    private static void savePlayerDomain(BedWars plugin, PlayerGlobalDataDomain domain){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (DatabaseConnector.getSql() == null) {
                    Bukkit.getLogger().warning("Database is not connected.");
                    return;
                }
                try {
                    Statement statement = DatabaseConnector.getSql().createStatement();

                    String sql = "UPDATE playerglobaldata SET experience=" + domain.getExperience()
                            + ", grypciocoins=" + domain.getGrypcioCoins() + ", ingametime=" + domain.getInGameTime() + " WHERE uuid='" + domain.getUuid() + "';";
                    int rowsAffected = statement.executeUpdate(sql);
                    Bukkit.getLogger().info("Executed SQL: " + sql + ", rows affected: " + rowsAffected);

                    sql = "INSERT INTO playerglobaldata (uuid, grypciocoins, experience, ingametime) " +
                            "VALUES ('" + domain.getUuid() + "', '" + domain.getGrypcioCoins() + "'," +
                            " '" + domain.getExperience() + "', '" + domain.getInGameTime() + "') " +
                            "ON CONFLICT DO NOTHING;";

                    rowsAffected = statement.executeUpdate(sql);
                    statement.close();

                    Bukkit.getLogger().info("Executed SQL: " + sql + ", rows affected: " + rowsAffected);

                    Bukkit.getLogger().warning("Updated player data: " +
                            Bukkit.getPlayer(domain.getUuid()) + ",  gold: " + domain.getGrypcioCoins() + ", exp: "
                            + domain.getExperience());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        }.runTaskAsynchronously(plugin);
    }


    public static void addGold(BedWars plugin, Player player, int amount){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (DatabaseConnector.getSql() == null) {
                    Bukkit.getLogger().warning("Database is not connected.");
                    return;
                }
                try {
                    Statement statement = DatabaseConnector.getSql().createStatement();
                    int gold = getPlayerGold(player);

                    String sql = "UPDATE playerglobaldata SET grypciocoins=" + (gold+amount) + " WHERE uuid='" + player.getUniqueId() + "';";
                    statement.executeUpdate(sql);

                    sql = "INSERT INTO playerglobaldata (uuid, grypciocoins, experience, ingametime) " +
                            "VALUES ('" + player.getUniqueId() + "', '" + amount + "'," +
                            " '" + 0 + "', '" + 0 + "') " +
                            "ON CONFLICT DO NOTHING;";

                    statement.executeUpdate(sql);
                    statement.close();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        }.runTaskAsynchronously(plugin);
    }

    public static void chargeGold(BedWars plugin, Player player, int amount) {
        addGold(plugin, player, -1 * amount);
    }

    public static void addExperience(BedWars plugin, Player player, int amount){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (DatabaseConnector.getSql() == null) {
                    Bukkit.getLogger().warning("Database is not connected.");
                    return;
                }
                try {
                    Statement statement = DatabaseConnector.getSql().createStatement();
                    int experience = getPlayerExperience(player);

                    String sql = "UPDATE playerglobaldata SET experience=" + (experience+amount) + " WHERE uuid='" + player.getUniqueId() + "';";
                    statement.executeUpdate(sql);

                    sql = "INSERT INTO playerglobaldata (uuid, grypciocoins, experience, ingametime) " +
                            "VALUES ('" + player.getUniqueId() + "', '" + 0 + "'," +
                            " '" + amount + "', '" + 0 + "') " +
                            "ON CONFLICT DO NOTHING;";

                    statement.executeUpdate(sql);
                    statement.close();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        }.runTaskAsynchronously(plugin);
    }

    private static int getPlayerExperience(Player player) {
        PlayerGlobalDataDomain domain = getPlayerDomain(player.getUniqueId());
        if (domain == null) return 0;
        return domain.getExperience();
    }

    public static int getPlayerGold(Player player){
        PlayerGlobalDataDomain domain = getPlayerDomain(player.getUniqueId());
        if (domain == null) return 0;
        return domain.getGrypcioCoins();
    }


    private static final Map<UUID, Integer> playerExpMap = new HashMap<>();

    public static String getPlayerLevelProgress(Player player, boolean forced) {
        PlayerGlobalDataDomain domain = getPlayerDomain(player.getUniqueId());
        if (domain == null) return "0/200";
        int exp = domain.getExperience();
        int startExp = exp;
        if (!forced && playerExpMap.containsKey(player.getUniqueId()) && playerExpMap.get(player.getUniqueId()) == exp) return null;

        int value = 200;
        while (exp >= 0){
            value += 270;
            exp -= value;
        }
        String result = "" + (value+exp) + "/" + value;
        playerExpMap.put(player.getUniqueId(), startExp);
        return result;
    }

    public static int getPlayerLevel(Player player) {
        PlayerGlobalDataDomain domain = getPlayerDomain(player.getUniqueId());
        if (domain == null) return 0;
        int exp = domain.getExperience();
        int level = 1;
        int value = 200;
        while (exp >= 0){
            exp -= value;
            value += 270;
            level++;
        }
        return level-1;
    }

    public static double getGoldMultiplier(Player player){
        if (player.isOp()) return 2;
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()){
            switch (perm.getPermission().toLowerCase()){
                case "admin", "mvip" -> {
                    return 2;
                }
                case "svip" -> {
                    return 1.5;
                }
                case "vip" -> {
                    return 1.25;
                }
            }
        }
        return 1;
    }

    public static void setPlayerMeta(Player player, String metaKey, String metaValue) {
        if (DatabaseConnector.getSql() == null) {
            Bukkit.getLogger().warning("Database is not connected.");
            return;
        }

        try {
            Statement statement = DatabaseConnector.getSql().createStatement();

            String sql = "DELETE FROM playermetadata " +
                            "WHERE playername='" + player.getName() + "' AND metakey='" + metaKey + "';";
            statement.executeUpdate(sql);

            sql = "INSERT into playermetadata \n" +
                            "(playername, metakey, metavalue)\n" +
                            "values ('" + player.getName() + "', '" + metaKey + "'," +
                            " '" + metaValue + "');";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


}
