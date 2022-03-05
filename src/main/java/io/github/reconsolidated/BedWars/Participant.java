package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.CustomSpectator.CustomSpectator;
import io.github.reconsolidated.BedWars.CustomSpectator.MakeArmorsInvisible;
import io.github.reconsolidated.BedWars.Teams.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Comparator;

public class Participant {
    @Getter
    @Setter
    private Player player;
    private Participant lastHitBy;

    @Getter
    @Setter
    private int kills = 0;

    @Getter
    @Setter
    private int finalKills = 0;

    @Getter
    @Setter
    private int bedsDestroyed = 0;

    private int deaths = 0;

    @Getter
    private int place = 100;

    @Getter
    @Setter
    private Team team;

    @Getter
    private Integer gameLoseTime = 0;

    @Getter
    @Setter
    private ScoreScoreboard scoreboard;
    private final BedWars plugin;
    private boolean hasLost = false;
    private int trapInvincibilityTime = 0;
    private int pickaxeLevel = 0;
    private int axeLevel = 0;
    private int shearsLevel = 0;
    private boolean isSpectating = false;
    private ItemStack[] inventoryBeforeSpectating = null;
    private boolean teamChat = false;
    private boolean isDead = false;
    private boolean isRespawning = false;
    private Color color = Color.WHITE;

    public Participant(Player player, BedWars plugin){
        this.player = player;
        this.plugin = plugin;
    }

    // onStart() initializes the player, teleports him to his bed and sets his inventory
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

    // When player dies or falls into the void
    public void onDeath(){
        deaths++;
        isDead = true;
        player.teleport(player.getWorld().getSpawnLocation());

        if (getLastHitBy() != null){
            if (!team.isBedAlive()){
                getLastHitBy().setFinalKills(getLastHitBy().getFinalKills()+1);
            }
            getLastHitBy().setKills(getLastHitBy().getKills() + 1);
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

        if (team != null && team.isBedAlive()){
            isRespawning = true;
            new RespawnRunnable(plugin, 5, this).runTaskTimer(plugin, 0L, 20L);
        }
        else{
            place = plugin.getTeamsLeft();
            hasLost = true;
            onGameEnd();
        }
    }

    // onRespawn() happens 5 seconds after players' death
    public void onRespawn(){
        isRespawning = false;
        if (pickaxeLevel != 0){
            pickaxeLevel = Math.max(1, pickaxeLevel-1);
        }
        if (axeLevel != 0){
            axeLevel = Math.max(1, axeLevel-1);
        }
        player.teleport(team.getSpawnLocation());
        CustomSpectator.endSpectator(plugin, player);
        player.setInvulnerable(true);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.setInvulnerable(false);
        }, 20L);

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

    // Player is in spectating mode after his death (for 5 seconds) or if he has lost the game.
    public void setIsSpectating(boolean value) {
        isSpectating = value;
    }
    public boolean isSpectating(){
        return isSpectating;
    }

    // Player that hit this participant most recently
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

    // When player has lost the game, but he can still spectate
    public void onGameEnd(){
        gameLoseTime = plugin.getGameTime();
        player.sendMessage(ChatColor.RED + "Twoje łóżko zostało zniszczone i już się nie odrodzisz.");
        CustomSpectator.setSpectator(plugin, player);
        player.teleport(player.getLocation().getWorld().getSpawnLocation());
    }

    // ChatColor and Color depend on the team, not really needed here, but it's handful
    public ChatColor getChatColor(){
        if (team != null){
            return team.getChatColor();
        }
        return ChatColor.BLACK;
    }

    public String getTerracottaColor() {
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
                return "DARK_GRAY";
            }
            case 6 -> {
                color = Color.PURPLE;
                return "LIGHT_PURPLE";
            }
            case 7 -> {
                color = Color.WHITE;
                return "WHITE";
            }
        }
        return "WHITE";
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

    // Those functions are shop-related. Player can upgrade his gear.
    // Also all of the gear is unbreakable, function is static because it is used project-wide
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
    // End of shop-related functions

    // The scoreboard is sorted with this comparator
    public static Comparator<Participant> scoreComparator = Comparator.comparingInt(Participant::getKills);

    // When a player is spectating he shouldn't have any items in inventory.
    // We save them in "inventoryBeforeSpectating" variable, and then restore after he has respawned
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

    // Returns true if player is currently dead.
    public boolean isDead() {
        return isDead;
    }

    // Returns true if the player is dead and will not respawn (has lost the game completely).
    public boolean hasLost(){
        return hasLost;
    }

    // Trap invincibility can be obtained by drinking milk
    public boolean isTrapInvincible(){
        return trapInvincibilityTime > 0;
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

    // Command "/team" switches you to the team chat.
    // If you text on the team chat, enemies can't see your messages, but
    // your teammates can.
    public boolean getTeamChat(){
        return teamChat;
    }
    public void setTeamChat(boolean teamChat) {
        this.teamChat = teamChat;
    }

    public boolean isRespawning() {
        return isRespawning;
    }


    public void setPlace(int i) {
        place = i;
    }
}
