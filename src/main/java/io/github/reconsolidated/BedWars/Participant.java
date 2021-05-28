package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.CustomSpectator.CustomSpectator;
import io.github.reconsolidated.BedWars.CustomSpectator.MakeArmorsInvisible;
import io.github.reconsolidated.BedWars.Teams.Team;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
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
    public ScoreScoreboard scoreboard;

    private BedWars plugin;
    public boolean hasLost = false;
    private int trapInvincibilityTime = 0;
    private int pickaxeLevel = 0;
    private int axeLevel = 0;
    private int shearsLevel = 0;
    private boolean isSpectating = false;
    private ItemStack[] inventoryBeforeSpectating = null;

    private boolean isDead = false;

    private Color color = Color.WHITE;

    public Participant(Player player, BedWars plugin){
        this.player = player;
        this.plugin = plugin;
    }

    public void setIsSpectating(boolean value) {
        isSpectating = value;
    }

    public boolean isTrapInvincible(){
        return trapInvincibilityTime > 0;
    }


    public void onStart(){
        player.setPlayerListName(getChatColor() + player.getName() + ChatColor.WHITE);
        player.teleport(team.getSpawnLocation());
        player.setInvulnerable(false);
        CustomSpectator.endSpectator(plugin, player);

        player.getInventory().clear();
        player.setDisplayName(team.getChatColor() + player.getName());

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
        player.getInventory().setItem(8, new ItemStack(Material.COMPASS));

    }

    public boolean isSpectating(){
        return isSpectating;
    }

    public void onRespawn(){
        if (pickaxeLevel != 0){
            pickaxeLevel = Math.max(1, pickaxeLevel-1);
        }
        if (axeLevel != 0){
            axeLevel = Math.max(1, axeLevel-1);
        }
        player.teleport(team.getSpawnLocation());
        CustomSpectator.endSpectator(plugin, player);

        ItemStack[] armor = player.getInventory().getArmorContents();

        player.getInventory().clear();
        player.getInventory().setItem(8, new ItemStack(Material.COMPASS));
        player.getInventory().setArmorContents(armor);
        player.getInventory().addItem(unbreakable(new ItemStack(Material.WOODEN_SWORD)));
        player.getInventory().addItem(unbreakable(getPickaxe()));
        player.getInventory().addItem(unbreakable(getAxe()));
        player.getInventory().addItem(unbreakable(getShears()));

        lastHitBy = null;
        player.setFoodLevel(20);
        player.setExp(0);
        player.setHealth(20);
        player.setFireTicks(0);
        player.getActivePotionEffects().clear();
        MakeArmorsInvisible.sendOutArmorPacket(player);


        isDead = false;


        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.setFireTicks(0), 2L);
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
        isDead = true;
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
                Bukkit.broadcastMessage(ChatColor.RED + "FINAL KILL! "
                        + getLastHitBy().getChatColor() + getLastHitBy().player.getDisplayName()
                        + ChatColor.RED + " zabił " + getChatColor() + player.getDisplayName());
            }

        }
        else{
            Bukkit.broadcastMessage(getChatColor() + player.getDisplayName() + ChatColor.RED + " zginął.");
            for (ItemStack item : player.getInventory().getContents()){
                if (item == null) continue;
                if (item.getType().equals(Material.IRON_INGOT)
                        || item.getType().equals(Material.GOLD_INGOT)
                        || item.getType().equals(Material.DIAMOND)
                        || item.getType().equals(Material.EMERALD)){
                    player.getInventory().remove(item);
                }
            }
        }

        CustomSpectator.setSpectator(plugin, player);

        if (team.isBedAlive()){
            new RespawnRunnable(plugin, 5, this).runTaskTimer(plugin, 0L, 20L);
        }
        else{
            hasLost = true;
            onGameEnd();
        }
    }

    public void onGameEnd(){
        player.sendMessage(ChatColor.RED + "Twoje łóżko zostało zniszczone i już się nie odrodzisz.");
        CustomSpectator.setSpectator(plugin, player);
        player.teleport(player.getLocation().getWorld().getSpawnLocation());
    }

    public ChatColor getChatColor(){
        if (team != null){
            return team.getChatColor();
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
            case 4 -> {
                color = Color.AQUA;
                return "LIGHT_BLUE";
            }
            case 5 -> {
                color = Color.GRAY;
                return "GRAY";
            }
            case 6 -> {
                color = Color.PURPLE;
                return "PURPLE";
            }
            case 7 -> {
                color = Color.WHITE;
                return "WHITE";
            }
        }
        return "WHITE";
    }

    public boolean isPickaxeMaxed(){
        return pickaxeLevel == 4;
    }

    public boolean isAxeMaxed(){
        return axeLevel == 4;
    }

    public boolean hasShears(){
        return shearsLevel > 0;
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

    public int getPickaxeLevel() {
        return pickaxeLevel;
    }

    public int getAxeLevel(){
        return axeLevel;
    }
    
    private ItemStack getPickaxe(){
        pickaxeLevel = Math.min(pickaxeLevel, 4);
        ItemStack item = new ItemStack(Material.AIR);
        if (pickaxeLevel == 1){
            item = new ItemStack(Material.WOODEN_PICKAXE);
            item.addEnchantment(Enchantment.DIG_SPEED, 1);
        }
        if (pickaxeLevel == 2){
            item = new ItemStack(Material.IRON_PICKAXE);
            item.addEnchantment(Enchantment.DIG_SPEED, 2);
        }
        if (pickaxeLevel == 3){
            item = new ItemStack(Material.GOLDEN_PICKAXE);
            item.addEnchantment(Enchantment.DIG_SPEED, 3);
        }
        if (pickaxeLevel == 4){
            item = new ItemStack(Material.DIAMOND_PICKAXE);
            item.addEnchantment(Enchantment.DIG_SPEED, 3);
        }
        if (item.getItemMeta() != null){
            ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }


        return item;
    }

    private ItemStack getAxe(){
        axeLevel = Math.min(axeLevel, 4);
        ItemStack item = new ItemStack(Material.AIR);
        if (axeLevel == 1){
            item = new ItemStack(Material.WOODEN_AXE);
            item.addEnchantment(Enchantment.DIG_SPEED, 1);
        }
        if (axeLevel == 2){
            item = new ItemStack(Material.STONE_AXE);
            item.addEnchantment(Enchantment.DIG_SPEED, 2);
        }
        if (axeLevel == 3){
            item = new ItemStack(Material.IRON_AXE);
            item.addEnchantment(Enchantment.DIG_SPEED, 2);
        }
        if (axeLevel == 4){
            item = new ItemStack(Material.DIAMOND_AXE);
            item.addEnchantment(Enchantment.DIG_SPEED, 3);
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


    public void saveInventoryForSpectating() {
        if (inventoryBeforeSpectating == null){
            inventoryBeforeSpectating = player.getInventory().getContents();
            player.getInventory().clear();
        }
    }

    public void restoreInventoryAfterSpectating() {
        if (inventoryBeforeSpectating != null){
            player.getInventory().setContents(inventoryBeforeSpectating);
            inventoryBeforeSpectating = null;
        }

    }

    public boolean isDead() {
        return isDead;
    }

    public void setTrapInvincibility(int time_in_seconds) {
        trapInvincibilityTime = time_in_seconds;
        new BukkitRunnable() {
            @Override
            public void run() {
                trapInvincibilityTime--;
                if (trapInvincibilityTime == 0){
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }


}
