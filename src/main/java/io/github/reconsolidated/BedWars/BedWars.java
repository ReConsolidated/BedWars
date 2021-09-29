package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.Chat.ChatMessageListener;
import io.github.reconsolidated.BedWars.CustomEntities.CustomIronGolem;
import io.github.reconsolidated.BedWars.CustomEntities.CustomSilverFish;
import io.github.reconsolidated.BedWars.CustomSpectator.CustomSpectator;
import io.github.reconsolidated.BedWars.CustomSpectator.MakeArmorsInvisible;
import io.github.reconsolidated.BedWars.CustomSpectator.StopSpectatorSounds;
import io.github.reconsolidated.BedWars.DataBase.LobbyConnection.ServerStateManager;
import io.github.reconsolidated.BedWars.DataBase.PlayerDataManager;
import io.github.reconsolidated.BedWars.ItemDrops.ItemSpawner;
import io.github.reconsolidated.BedWars.Listeners.*;
import io.github.reconsolidated.BedWars.Teams.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.github.reconsolidated.BedWars.CustomConfig.loadCustomConfig;
import static io.github.reconsolidated.BedWars.CustomConfig.saveCustomConfig;
import static io.github.reconsolidated.BedWars.DataBase.LobbyConnection.ServerStateManager.sendServerState;
import static io.github.reconsolidated.BedWars.Participant.unbreakable;

public class BedWars extends JavaPlugin implements Listener {

    public World world;
    public boolean hasStarted = false;

    @Getter
    private int TEAMS_COUNT = 4;
    @Getter
    private int TEAM_SIZE = 1;
    @Getter
    @Setter
    private int partiesCount = 0;

    private static String currentWorldName = "bedwars_ancient"; //

    private ArrayList<Participant> participants;
    private ArrayList<Participant> inactiveParticipants;
    private ArrayList<Team> teams;
    private ArrayList<ItemSpawner> spawners;
    private GameRunnable gameRunnable;

    private ArrayList<CustomIronGolem> golems = new ArrayList<>();
    private ArrayList<CustomSilverFish> silverFish = new ArrayList<>();

    public YamlConfiguration currentConfig;

    private JedisCommunicator jedis;

    @Getter
    private String serverName = "bedwars";


    // These variables should be initiated in onStart() method, based on teams' beds locations.
    @Getter
    private int gameBorderSmallestX = 9999;
    @Getter
    private int gameBorderSmallestZ = 9999;
    @Getter
    private int gameBorderBiggestX = -9999;
    @Getter
    private int gameBorderBiggestZ = -9999;

    @Override
    public void onEnable() {
        Commands commandsPlugin = new Commands(this);
        getServer().getPluginCommand("helpbedwars").setExecutor(commandsPlugin);
        getServer().getPluginCommand("bdstart").setExecutor(commandsPlugin);
        getServer().getPluginCommand("bdstop").setExecutor(commandsPlugin);
        getServer().getPluginCommand("itemspawner").setExecutor(commandsPlugin);
        getServer().getPluginCommand("newspawn").setExecutor(commandsPlugin);
        getServer().getPluginCommand("setspawn").setExecutor(commandsPlugin);
        getServer().getPluginCommand("setbed").setExecutor(commandsPlugin);
        getServer().getPluginCommand("newshop1").setExecutor(commandsPlugin);
        getServer().getPluginCommand("newshop2").setExecutor(commandsPlugin);
        getServer().getPluginCommand("release").setExecutor(commandsPlugin);
        getServer().getPluginCommand("team").setExecutor(commandsPlugin);
        getServer().getPluginCommand("d").setExecutor(commandsPlugin);
        getServer().getPluginCommand("all").setExecutor(commandsPlugin);
        getServer().getPluginCommand("w").setExecutor(commandsPlugin);
        getServer().getPluginManager().registerEvents(this, this);

        List<String> worlds = getConfig().getStringList("maps");
        TEAMS_COUNT = getConfig().getInt("team_number");
        TEAM_SIZE = getConfig().getInt("team_size");

        serverName = serverName + TEAM_SIZE;

        Random random = new Random();
        currentWorldName = worlds.get(random.nextInt(worlds.size())); // this will be random

        world = Bukkit.createWorld(new WorldCreator(currentWorldName));

        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setTicksPerMonsterSpawns(0);
        world.setMonsterSpawnLimit(128);
        world.setDifficulty(Difficulty.NORMAL);
        world.setFullTime(0);

        Bukkit.getScheduler().runTaskTimer(this, () ->{
            world.setFullTime(0);
        }, 20L, 20L);


        currentConfig = loadCustomConfig(currentWorldName);
        if (!currentConfig.contains(currentWorldName)){
            currentConfig.createSection(currentWorldName);
        }

        participants = new ArrayList<>();
        inactiveParticipants = new ArrayList<>();
        teams = new ArrayList<>();
        spawners = new ArrayList<>();

        gameRunnable = new GameRunnable(this);

        new MakeArmorsInvisible(this).run();
        new StopSpectatorSounds(this).run();


        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEntityListener(participants, this), this);
        getServer().getPluginManager().registerEvents(new PlayerItemConsumeListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupArrowListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityTargetListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityPickupItemListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockDropItemListener(), this);
        getServer().getPluginManager().registerEvents(new ProjectileHitListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileLaunchListener(this), this);
        getServer().getPluginManager().registerEvents(new PrepareItemCraftListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemDespawnListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerBucketEmptyListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockCanBuildListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatMessageListener(this), this);
        getServer().getPluginManager().registerEvents(new LimitItemPickup(this), this);
        getServer().getPluginManager().registerEvents(new CreatureSpawnListener(), this);

        jedis = new JedisCommunicator(this);

        new StartGameRunnable(this);
        sendServerState(this, serverName, TEAM_SIZE, TEAMS_COUNT);
    }

    public void releasePlayer(Player player) {
        Participant p = this.getParticipant(player);
        if (p != null){
            p.getTeam().members.remove(p);
            getParticipants().remove(p);
        }

        for (Player p2 : Bukkit.getOnlinePlayers()){
            p2.hidePlayer(this, player);
            player.showPlayer(this, p2);
        }
    }

    @Override
    public void onDisable(){
        jedis.remove();
    }

    public void onStart(){
        ServerStateManager.removeServerFromList();
        Bukkit.getScheduler().runTaskAsynchronously(this, () ->{
            PlayerDataManager.fetchPlayersData(participants);
        });

        if (!currentConfig.contains(currentWorldName + ".shop")){
            currentConfig.createSection(currentWorldName + ".shop");
        }
        Object[] shopKeys = currentConfig.getConfigurationSection(currentWorldName + ".shop").getKeys(false).toArray();
        for (int i = 0; i<shopKeys.length; i++){
            Location location = (Location) currentConfig.getConfigurationSection(currentWorldName + ".shop").get((String) shopKeys[i] + ".location");
            Integer type = (Integer) currentConfig.getConfigurationSection(currentWorldName + ".shop").get((String) shopKeys[i] + ".type");
            if (type == 1){
                Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
                villager.setSilent(true);
                villager.setAI(false);
            }
            if (type == 2){
                location.getWorld().setDifficulty(Difficulty.NORMAL);
                Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
                zombie.setAdult();
                zombie.setRemoveWhenFarAway(false);
                zombie.setSilent(true);
                zombie.setAI(false);
            }
        }


        for (int i = 0; i< TEAMS_COUNT; i++){
            ConfigurationSection section = currentConfig.getConfigurationSection(currentWorldName + "." + i);
            if (section == null){
                Bukkit.broadcastMessage("Nie są ustawione spawny teamów");
                return;
            }
            Location bedLocation = (Location) section.get("bedLocation");
            gameBorderSmallestX = Math.min(gameBorderSmallestX, bedLocation.getBlockX() - 30);
            gameBorderSmallestZ = Math.min(gameBorderSmallestZ, bedLocation.getBlockZ() - 30);
            gameBorderBiggestX = Math.max(gameBorderBiggestX, bedLocation.getBlockX() + 30);
            gameBorderBiggestZ = Math.max(gameBorderBiggestZ, bedLocation.getBlockZ() + 30);

            teams.add(new Team(
                    bedLocation,
                    (Location) section.get("spawnLocation"),
                    i,
                    this
            ));

            try {
                Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("" + i);
            } catch (Exception ignored){

            }


        }

        int spawnersCounter = currentConfig.getConfigurationSection(currentWorldName).getInt("spawners_counter");
        for (int i = 1; i<=spawnersCounter; i++){
            ConfigurationSection section = currentConfig.getConfigurationSection(currentWorldName + ".spawners." + i);
            assert section != null : "Section isn't set.";

            String type = (String) section.get("type");
            Integer period = (Integer) section.get("period");
            Location location = (Location) section.get("location");
            Integer team = (Integer) section.get("team");
            Integer maxAmount = (Integer) section.get("maxAmount");
            if (type == null || period == null || location == null || team == null || maxAmount == null){
                Bukkit.getLogger().warning("Nie udało się załadować informacji o spawnerze");
            }
            else{
                spawners.add(new ItemSpawner(this,
                        new ItemStack(Material.getMaterial(type)),
                        period,
                        location,
                        team,
                        maxAmount
                ));
            }

        }

        gameRunnable.runTaskTimer(this, 0, 20);


    }

    public void onGameEnd(){
        Team winnerTeam = teams.get(0);
        for (Participant p : participants){
            if (!p.hasLost()){
                winnerTeam = p.getTeam();

            }
        }
        for (Participant p : participants){
            if (!p.hasLost()){
                PlayerDataManager.savePlayerData(this, p);
                p.getPlayer().sendTitle(ChatColor.GREEN + "Zwycięstwo!", "", 5, 100, 5);
            }
            else{
                p.getPlayer().sendTitle("", ChatColor.GOLD + "Wygrała drużyna " + winnerTeam.getChatColor() + winnerTeam.getName(), 5, 100, 5);
            }
            CustomSpectator.setSpectator(this, p.getPlayer());
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()){
                    player.kickPlayer("Koniec gry");
                }
                Bukkit.getServer().shutdown();
            }
        }.runTaskLater(this, 20*30);

    }

    public ArrayList<CustomIronGolem> getGolems(){
        return golems;
    }

    public ArrayList<CustomSilverFish> getSilverFish(){
        return silverFish;
    }

    public void addSilverfish(CustomSilverFish sf){
        silverFish.add(sf);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            silverFish.remove(sf);
            sf.killEntity();
        }, 20L * 15);
    }


    public void addGolem(CustomIronGolem golem){
        golems.add(golem);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            golems.remove(golem);
            golem.killEntity();
        }, 20L * 120);
    }

    public String getNextEventName(){
        if (gameRunnable == null) return "Jeszcze nie";
        return gameRunnable.getNextEventName();
    }

    public int getNextEventTime(){
        if (gameRunnable == null) return 0;
        return gameRunnable.getNextEventTime();
    }

    public void onItemSpawner(Player player, String itemName, int period, int maxAmount, int team){
        if (Material.getMaterial(itemName) == null){
            player.sendMessage("Podano niepoprawną nazwę itemu!");
            return;
        }
        spawners.add(new ItemSpawner(
                this,
                new ItemStack(Material.getMaterial(itemName), 1),
                period,
                player.getLocation(),
                team,
                maxAmount));
        player.sendMessage("Pomyślnie utworzono spawner itemu");

        if (currentConfig.getConfigurationSection(currentWorldName) == null){
            currentConfig.createSection(currentWorldName).set("spawners_counter", 0);
        }
        if (!currentConfig.getConfigurationSection(currentWorldName).contains("spawners_counter")){
            currentConfig.getConfigurationSection(currentWorldName).set("spawners_counter", 0);
        }
        if (currentConfig.getConfigurationSection(currentWorldName + ".spawners") == null){
            currentConfig.createSection(currentWorldName + ".spawners");
        }

        int spawnersCounter = currentConfig.getConfigurationSection(currentWorldName).getInt("spawners_counter");
        spawnersCounter++;
        currentConfig.getConfigurationSection(currentWorldName).set("spawners_counter", spawnersCounter);

        ConfigurationSection section = currentConfig.createSection(currentWorldName + ".spawners." + spawnersCounter);
        section.set("type", itemName);
        section.set("period", period);
        section.set("location", player.getLocation());
        section.set("team", team);
        section.set("maxAmount", maxAmount);
        player.sendMessage("Poprawnie ustawiono item spawner numer: " + spawnersCounter);
        saveCustomConfig(currentWorldName, currentConfig);

    }

    public void createShop1(Player player){
        saveShop(player.getLocation(), 1);
    }

    public void createShop2(Player player){
        saveShop(player.getLocation(), 2);
    }

    private void saveShop(Location location, int type){
        Random random = new Random();
        ConfigurationSection section;
        if (currentConfig.contains(currentWorldName + ".shop")){
            section = currentConfig.getConfigurationSection(currentWorldName + ".shop");
        }
        else{
            section = currentConfig.createSection(currentWorldName + ".shop");
        }
        int key = random.nextInt();
        section.set(key + ".location", location);
        section.set(key + ".type", type);
        saveCustomConfig(currentWorldName, currentConfig);
    }


    public void onSetSpawn(Player player, String teamNumber){
        if (currentConfig.getConfigurationSection(currentWorldName + "." + teamNumber) == null){
            currentConfig.createSection(currentWorldName + "." + teamNumber);
        }
        ConfigurationSection section = currentConfig.getConfigurationSection(currentWorldName + "." + teamNumber);
        section.set("spawnLocation", player.getLocation());
        player.sendMessage("Poprawnie ustawiono spawn teamu: " + teamNumber);
        saveCustomConfig(currentWorldName, currentConfig);
    }

    public void onSetBed(Player player, String teamNumber){
        if (currentConfig.getConfigurationSection(currentWorldName + "." + teamNumber) == null){
            currentConfig.createSection(currentWorldName + "." + teamNumber);
        }
        ConfigurationSection section = currentConfig.getConfigurationSection(currentWorldName + "." + teamNumber);
        section.set("bedLocation", player.getLocation());
        player.sendMessage("Poprawnie ustawiono lozko teamu: " + teamNumber);
        saveCustomConfig(currentWorldName, currentConfig);
    }

    public ArrayList<Team> getTeams(){
        return teams;
    }

    public Participant getParticipant(Player player){
        for (int i = 0; i<participants.size(); i++){
            if (participants.get(i).getPlayer().equals(player)){
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


    public Location getSpawnLocation() {
        return world.getSpawnLocation();
    }

    public void setParticipantInactive(Participant p) {
        participants.remove(p);
        inactiveParticipants.add(p);
    }

    public Participant getInactiveParticipant(Player player){
        for (int i = 0; i<inactiveParticipants.size(); i++){
            if (inactiveParticipants.get(i).getPlayer().getUniqueId().equals(player.getUniqueId())){
                return inactiveParticipants.get(i);
            }
        }
        return null;
    }

    public void restoreParticipant(Participant p) {
        inactiveParticipants.remove(p);
        participants.add(p);
    }

    public void checkSwords(Player player) {
        int swordsCount = 0;
        for (ItemStack itemStack : player.getInventory().getContents()){
            if (isSword(itemStack)){
                swordsCount++;
            }
        }
        if (swordsCount < 1){
            player.getInventory().addItem(unbreakable(new ItemStack(Material.WOODEN_SWORD)));
        }
        if (swordsCount > 1){
            player.getInventory().remove(Material.WOODEN_SWORD);
        }
    }

    private boolean isSword(ItemStack item){
        if (item == null) return false;
        if (item.getType().equals(Material.WOODEN_SWORD)
                || item.getType().equals(Material.STONE_SWORD)
                || item.getType().equals(Material.IRON_SWORD)
                || item.getType().equals(Material.DIAMOND_SWORD)){
            return true;
        }
        return false;
    }

    public void setTeamChat(Player player, boolean teamChat) {
        Participant p = getParticipant(player);
        if (p == null) return;
        p.setTeamChat(teamChat);
        if (teamChat){
            player.sendMessage(ChatColor.GREEN + "Teraz piszesz tylko do swojej drużyny.");
        }
        else{
            player.sendMessage(ChatColor.GREEN + "Teraz piszesz do wszystkich graczy.");
        }
    }
}
