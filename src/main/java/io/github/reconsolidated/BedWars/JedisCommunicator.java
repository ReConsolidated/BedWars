package io.github.reconsolidated.BedWars;


import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JedisCommunicator {

    private final String GAMEMODE_NAME = "BedWars";
//
    private final BedWars plugin;

    private final String serverName;

    private int taskId = 0;

    Jedis jedis = new Jedis("redis");

    public JedisCommunicator(BedWars plugin) {
        this.plugin = plugin;
        this.serverName = getNewServerName();
        if (serverName == null) return;
        run();
    }


    private void run() {
        taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Map<String, String> serverInfo = new HashMap<String, String>();
            serverInfo.put("ip", getContainerId());
            serverInfo.put("status", "" + plugin.hasStarted);
            serverInfo.put("maxPlayers", plugin.getServer().getMaxPlayers() + "");
            serverInfo.put("currentPlayers", plugin.getServer().getOnlinePlayers().size() + "");
            serverInfo.put("port", plugin.getServer().getPort() + "");
            serverInfo.put("time", 0 + "");
            jedis.hmset(serverName + "", serverInfo);
            jedis.expire(serverName, 30);
        }, 0L, 10L);
    }

    public void remove() {
        this.plugin.getServer().getScheduler().cancelTask(taskId);
        this.jedis.del(this.serverName);
    }

    private String getNewServerName() {
        try{
            List<String> servers = jedis.lrange(GAMEMODE_NAME + "Servers", 0, -1);
            String name = "";

            do {
                String uuid = RandomStringUtils.randomAlphanumeric(4);
                name = "MIN" + uuid.toLowerCase(Locale.ROOT);
            } while (servers.contains(name));

            jedis.rpush(GAMEMODE_NAME + "Servers", name);
            return name;
        } catch (JedisConnectionException e){
            Bukkit.getLogger().warning("[BedWars REDIS] Jedis is not connected.");
            return null;
        }
    }

    public String getServerName() {
        return serverName;
    }

    private String getContainerId() {
        try {
            String cmd = "env";
            Runtime run = Runtime.getRuntime();
            Process pr = run.exec(cmd);
            pr.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "";
            while ((line = buf.readLine()) != null) {
                if (line.contains("HOSTNAME")) {
                    return line.split("=")[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }
}
