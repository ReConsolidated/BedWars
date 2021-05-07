package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.Teams.Team;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Participant {
    public Player player;
    private Participant lastHitBy;
    public int currentScore = 0;
    public int kills = 0;
    public Team team;

    private BedWars plugin;


    private int pickaxeLevel = 0;
    private int axeLevel = 0;
    private int shearsLevel = 0;

    private Color color = Color.WHITE;

    public Participant(Player player, BedWars plugin){
        this.player = player;
        this.plugin = plugin;
    }

    public void onStart(){
        player.setInvulnerable(false);
        player.teleport(team.getSpawnLocation());
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        getColor();
        ItemStack[] armor = new ItemStack[4];
        armor[0] = new ItemStack(Material.LEATHER_BOOTS);
        armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
        armor[3] = new ItemStack(Material.LEATHER_HELMET);

        for (int i = 0; i<4; i++){
            LeatherArmorMeta meta = (LeatherArmorMeta) armor[i].getItemMeta();
            meta.setColor(color);
            meta.setUnbreakable(true);
            armor[i].setItemMeta(meta);
        }

        player.getInventory().setArmorContents(armor);

        player.getInventory().addItem(unbreakable(new ItemStack(Material.WOODEN_SWORD)));
    }

    public void onRespawn(){
        if (pickaxeLevel != 0){
            pickaxeLevel = Math.max(1, pickaxeLevel-1);
        }
        if (axeLevel != 0){
            axeLevel = Math.max(1, axeLevel-1);
        }

        ItemStack[] armor = player.getInventory().getArmorContents();
        player.getInventory().clear();
        player.getInventory().setArmorContents(armor);
        player.getInventory().addItem(unbreakable(new ItemStack(Material.WOODEN_SWORD)));
        player.getInventory().addItem(unbreakable(getPickaxe()));
        player.getInventory().addItem(unbreakable(getAxe()));
        player.getInventory().addItem(unbreakable(getShears()));

        lastHitBy = null;
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setHealth(20);
        player.setFireTicks(0);
        player.getActivePotionEffects().clear();
        player.teleport(team.getSpawnLocation());
    }

    public Participant getLastHitBy(){
        return lastHitBy;
    }

    public void setLastHitBy(Participant p){
        lastHitBy = p;
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run(){
                lastHitBy = null;
            }
        }, 8 * 20L);
    }

    public void onDeath(){
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(player.getWorld().getSpawnLocation());

        if (getLastHitBy() != null){
            getLastHitBy().kills++;
            for (ItemStack item : player.getInventory().getContents()){
                if (item == null) continue;
                if (item.getType().equals(Material.IRON_INGOT)
                        || item.getType().equals(Material.GOLD_INGOT)
                        || item.getType().equals(Material.DIAMOND)
                        || item.getType().equals(Material.EMERALD)){
                    player.getInventory().remove(item);
                    getLastHitBy().player.getInventory().addItem(item);
                }
            }
            if (team.isBedAlive()){
                Bukkit.broadcastMessage(getLastHitBy().player.getDisplayName() + " zabił " + player.getDisplayName());
            }
            else{
                Bukkit.broadcastMessage(ChatColor.RED + "FINAL KILL!"
                        + getLastHitBy().getChatColor() + getLastHitBy().player.getDisplayName()
                        + ChatColor.RED + " zabił " + getChatColor() + player.getDisplayName());
            }

        }
        else{
            Bukkit.broadcastMessage(getChatColor() + player.getDisplayName() + " z jakiegoś powodu umarł XD");
        }

        if (team.isBedAlive()){
            new RespawnRunnable(plugin, 5, this).runTaskTimer(plugin, 0L, 20L);
        }
        else{
            onGameEnd();
        }
    }

    public void onGameEnd(){
        player.sendMessage("Frajer i bomba jesteś takie oro na łóżku było");
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(player.getLocation().getWorld().getSpawnLocation());
    }

    public ChatColor getChatColor(){
        switch(team.ID){
            case 0 -> {
                return ChatColor.BLUE;
            }
            case 1 -> {
                return ChatColor.RED;
            }
            case 2 -> {
                return ChatColor.YELLOW;
            }
            case 3 -> {
                return ChatColor.GREEN;
            }
        }
        return ChatColor.BLACK;
    }

    public String getColor(){
        if (team == null){
            return "WHITE";
        }
        switch(team.ID){
            case 0 -> {
                color = Color.BLUE;
                return "BLUE";
            }
            case 1 -> {
                color = Color.RED;
                return "RED";
            }
            case 2 -> {
                color = Color.YELLOW;
                return "YELLOW";
            }
            case 3 -> {
                color = Color.GREEN;
                return "GREEN";
            }
        }
        return "WHITE";
    }



    public void upgradePickaxe(){
        for (ItemStack item : player.getInventory()){
            if (item != null && getPickaxe() != null && item.getType() == getPickaxe().getType()){
                player.getInventory().removeItem(item);
            }
        }
        pickaxeLevel++;
        player.getInventory().addItem(unbreakable(getPickaxe()));
    }

    public void upgradeAxe(){
        for (ItemStack item : player.getInventory()){
            if (item != null && getAxe() != null && item.getType() == getAxe().getType()){
                player.getInventory().removeItem(item);
            }
        }
        axeLevel++;
        player.getInventory().addItem(unbreakable(getAxe()));
    }

    public void upgradeShears(){
        for (ItemStack item : player.getInventory()){
            if (item != null && getShears() != null && item.getType() == getShears().getType()){
                return;
            }
        }
        shearsLevel = 1;
        player.getInventory().addItem(unbreakable(getShears()));
    }

    public static ItemStack unbreakable(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (meta != null){
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }

        return item;
    }
    
    private ItemStack getPickaxe(){
        pickaxeLevel = Math.min(pickaxeLevel, 3);
        ItemStack item = new ItemStack(Material.AIR);
        if (pickaxeLevel == 1){
            item = new ItemStack(Material.WOODEN_PICKAXE);
        }
        if (pickaxeLevel == 2){
            item = new ItemStack(Material.IRON_PICKAXE);
        }
        if (pickaxeLevel == 3){
            item = new ItemStack(Material.DIAMOND_PICKAXE);
        }
        if (item.getItemMeta() != null){
            ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack getAxe(){
        axeLevel = Math.min(axeLevel, 3);
        ItemStack item = new ItemStack(Material.AIR);
        if (axeLevel == 1){
            item = new ItemStack(Material.WOODEN_AXE);
        }
        if (axeLevel == 2){
            item = new ItemStack(Material.IRON_AXE);
        }
        if (axeLevel == 3){
            item = new ItemStack(Material.DIAMOND_AXE);
        }
        if (item.getItemMeta() != null){
            ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack getShears(){
        if (shearsLevel == 0){
            return new ItemStack(Material.AIR);
        }
        else {
            ItemStack item = new ItemStack(Material.SHEARS);
            ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
            return item;
        }
    }

    public Map<Enchantment, Integer> getArmorEnchantments(){
        Map<Enchantment, Integer> map = new HashMap<>();
        if (team.protLevel > 0){
            map.put(Enchantment.PROTECTION_ENVIRONMENTAL, team.protLevel);
        }
        return map;
    }

    public static Comparator<Participant> scoreComparator = new Comparator<Participant>() {
        @Override
        public int compare(Participant o1, Participant o2) {
            return Integer.compare(o1.currentScore, o2.currentScore);
        }
    };





}
