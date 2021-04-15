package io.github.reconsolidated.BedWars;

import com.sk89q.worldedit.bukkit.BukkitConfiguration;
import io.github.reconsolidated.BedWars.ItemDrops.ItemSpawner;
import io.github.reconsolidated.BedWars.Listeners.*;
import io.github.reconsolidated.BedWars.Teams.Team;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class BedWars extends JavaPlugin implements Listener {

    private World world;

    private final int TEAMS = 4;

    private static String currentWorldName = "world"; // TODO

    private ArrayList<Participant> participants;
    private ArrayList<Team> teams;
    private ArrayList<ItemSpawner> spawners;
    private ScoreScoreboard myScoreboard;
    private CountdownRunnable countdownRunnable;
    private GameRunnable gameRunnable;

    @Override
    public void onEnable() {
        Commands commandsPlugin = new Commands(this);
        getServer().getPluginCommand("help").setExecutor(commandsPlugin);
        getServer().getPluginCommand("start").setExecutor(commandsPlugin);
        getServer().getPluginCommand("stop").setExecutor(commandsPlugin);
        getServer().getPluginCommand("itemspawner").setExecutor(commandsPlugin);
        getServer().getPluginCommand("newspawn").setExecutor(commandsPlugin);
        getServer().getPluginCommand("setspawn").setExecutor(commandsPlugin);
        getServer().getPluginCommand("setbed").setExecutor(commandsPlugin);
        getServer().getPluginManager().registerEvents(this, this);


        world = Bukkit.getWorlds().get(0);

        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);

        participants = new ArrayList<>();
        teams = new ArrayList<>();
        spawners = new ArrayList<>();

        myScoreboard = new ScoreScoreboard(participants);
        myScoreboard.runTaskTimer(this, 0, 4);

        countdownRunnable = new CountdownRunnable(this, myScoreboard, 120);
        countdownRunnable.runTaskTimer(this, 0, 20);
        gameRunnable = new GameRunnable(this);


        getServer().getPluginManager().registerEvents(new PlayerJoinListener(myScoreboard, participants, this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEntityListener(participants, this), this);
        getServer().getPluginManager().registerEvents(new PlayerItemConsumeListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(myScoreboard, participants), this);
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityTargetListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this, participants), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileHitListener(this), this);
    }


    public void onStart(){
        countdownRunnable.start();

        for (int i = 0; i<TEAMS; i++){
            ConfigurationSection section = getConfig().getConfigurationSection(currentWorldName + "." + i);
            assert section != null : "Section isn't set.";
            teams.add(new Team(
                    (Location) section.get("bedLocation"),
                    (Location) section.get("spawnLocation"),
                    i
            ));
        }

        int spawnersCounter = getConfig().getConfigurationSection(currentWorldName).getInt("spawners_counter");
        for (int i = 1; i<=spawnersCounter; i++){
            ConfigurationSection section = getConfig().getConfigurationSection(currentWorldName + ".spawners." + i);
            assert section != null : "Section isn't set.";

            String type = (String) section.get("type");
            Integer period = (Integer) section.get("period");
            Location location = (Location) section.get("location");
            Integer team = (Integer) section.get("team");
            if (type == null || period == null || location == null || team == null){
                Bukkit.broadcastMessage("Nie udało się załadować informacji o spawnerze");
            }
            else{
                spawners.add(new ItemSpawner(this,
                        new ItemStack(Material.getMaterial(type)),
                        period,
                        location,
                        team
                ));
            }

        }

        for (int i = 0; i<participants.size(); i++){
            teams.get(i%TEAMS).addMember(participants.get(i));
            participants.get(i).team = teams.get(i%TEAMS);
        }

        gameRunnable.runTaskTimer(this, 0, 20);

    }
    public void onStop(){
        countdownRunnable.stop();
    }

    public void onGameEnd(){

    } //

    public void onItemSpawner(Player player, String itemName, int period, int team){
        if (Material.getMaterial(itemName) == null){
            player.sendMessage("Podano niepoprawną nazwę itemu!");
            return;
        }
        spawners.add(new ItemSpawner(
                this,
                new ItemStack(Material.getMaterial(itemName), 1),
                period,
                player.getLocation(),
                team));
        player.sendMessage("Pomyślnie utworzono spawner itemu");

        if (getConfig().getConfigurationSection(currentWorldName) == null){
            getConfig().createSection(currentWorldName).set("spawners_counter", 0);
        }
        if (!getConfig().getConfigurationSection(currentWorldName).contains("spawners_counter")){
            getConfig().getConfigurationSection(currentWorldName).set("spawners_counter", 0);
        }
        if (getConfig().getConfigurationSection(currentWorldName + ".spawners") == null){
            getConfig().createSection(currentWorldName + ".spawners");
        }

        int spawnersCounter = getConfig().getConfigurationSection(currentWorldName).getInt("spawners_counter");
        spawnersCounter++;
        getConfig().getConfigurationSection(currentWorldName).set("spawners_counter", spawnersCounter);

        ConfigurationSection section = getConfig().createSection(currentWorldName + ".spawners." + spawnersCounter);
        section.set("type", itemName);
        section.set("period", period);
        section.set("location", player.getLocation());
        player.sendMessage("Poprawnie ustawiono item spawner numer: " + spawnersCounter);
        saveConfig();

    }


    public void onSetSpawn(Player player, String teamNumber){
        if (getConfig().getConfigurationSection(currentWorldName + "." + teamNumber) == null){
            getConfig().createSection(currentWorldName + "." + teamNumber);
        }
        ConfigurationSection section = getConfig().getConfigurationSection(currentWorldName + "." + teamNumber);
        section.set("spawnLocation", player.getLocation());
        player.sendMessage("Poprawnie ustawiono spawn teamu: " + teamNumber);
        saveConfig();
    }

    public void onSetBed(Player player, String teamNumber){
        if (getConfig().getConfigurationSection(currentWorldName + "." + teamNumber) == null){
            getConfig().createSection(currentWorldName + "." + teamNumber);
        }
        ConfigurationSection section = getConfig().getConfigurationSection(currentWorldName + "." + teamNumber);
        section.set("bedLocation", player.getLocation());
        player.sendMessage("Poprawnie ustawiono lozko teamu: " + teamNumber);
        saveConfig();
    }

    public ArrayList<Team> getTeams(){
        return teams;
    }

    public Participant getParticipant(Player player){
        for (int i = 0; i<participants.size(); i++){
            if (participants.get(i).player.equals(player)){
                return participants.get(i);
            }
        }
        return null;
    }

    public ArrayList<Participant> getParticipants(){
        return this.participants;
    }

    public ArrayList<ItemSpawner> getSpawners(){
        return this.spawners;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event)
    {
        event.setFoodLevel(20);
    }


}
