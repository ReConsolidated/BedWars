package io.github.reconsolidated.BedWars.Teams;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.ItemDrops.ItemSpawner;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.Teams.Traps.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Bed;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Team {
    private Location bedLocation;
    private Location spawnLocation;

    private BedWars plugin;

    private boolean isBedAlive = true;

    public int protLevel = 0;
    public int resourcesLevel = 0;
    public int sharpLevel = 0;
    public int hasteLevel = 0;
    public int dragons = 1;
    public int ID;


    private boolean hasHealPool = false;

    public HashSet<Participant> members;
    public LinkedList<Trap> traps;

    public Team(Location bedLocation, Location spawnLocation, int ID, BedWars plugin){
        this.bedLocation = bedLocation;
        this.spawnLocation = spawnLocation;
        this.ID = ID;
        this.plugin = plugin;
        new TeamBase(plugin, ID, spawnLocation).runTaskTimer(plugin, 0L, 20L);
        members = new HashSet<>();
        traps = new LinkedList<>();
    }

    public String getName(){
        switch (ID){
            case 0 -> {
                return "ALFA";
            }
            case 1 -> {
                return "BETA";
            }
            case 2 -> {
                return "GAMMA";
            }
            case 3 -> {
                return "DELTA";
            }
            case 4 -> {
                return "EPSILON";
            }
            case 5 -> {
                return "JOTA";
            }
            case 6 -> {
                return "KAPPA";
            }
            case 7 -> {
                return "LAMBDA";
            }
        }
        return "NIE MA TEAMU TAKIEGO LOOOL";
    }

    public ChatColor getChatColor(){
        switch (ID){
            case 0 -> {return ChatColor.BLUE;}
            case 1 -> {return ChatColor.RED;}
            case 2 -> {return ChatColor.YELLOW;}
            case 3 -> {return ChatColor.GREEN;}
            case 4 -> {return ChatColor.AQUA;}
            case 5 -> {return ChatColor.DARK_GRAY;}
            case 6 -> {return ChatColor.LIGHT_PURPLE;}
            case 7 -> {return ChatColor.WHITE;}
        }
        return ChatColor.BLACK;
    }

    public ArrayList<ItemStack> getTrapItems(){
        ArrayList<ItemStack> trapItems = new ArrayList<>();
        for (Trap trap : traps){
            trapItems.add(trap.getItemStack());
        }
        while (trapItems.size() < 3){
            trapItems.add(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, trapItems.size()+1));
        }
        return trapItems;
    }

    public void updateEnchants(){
        for (Participant p : members){
            if (p.getTeam().hasteLevel > 0){
                p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999, hasteLevel-1));
            }

            if (protLevel > 0){
                for (ItemStack s : p.getPlayer().getInventory().getArmorContents()){
                    if (s != null)
                        s.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protLevel);
                }
            }
            if (sharpLevel > 0){
                for (ItemStack s : p.getPlayer().getInventory()){
                    if (s == null) continue;
                    if (s.getType().equals(Material.WOODEN_SWORD)
                            || s.getType().equals(Material.STONE_SWORD)
                            || s.getType().equals(Material.IRON_SWORD)
                            || s.getType().equals(Material.DIAMOND_SWORD)){
                        s.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, sharpLevel);
                    }
                }
            }
        }
    }


    public boolean hasHealPool(){
        return hasHealPool;
    }

    public void updateItemSpawner(){
        for (ItemSpawner spawner : plugin.getSpawners()){
            if (spawner.getTeamID() == ID){
                if (spawner.getItem().getType().equals(Material.IRON_INGOT)
                || spawner.getItem().getType().equals(Material.GOLD_INGOT)){
                    if (resourcesLevel == 1){
                        spawner.setPeriod((int) (spawner.getBasePeriod() / 1.5));
                    }
                    if (resourcesLevel == 2){
                        spawner.setPeriod((int) (spawner.getBasePeriod() / 2));
                    }
                    if (resourcesLevel == 3){
                        spawner.setPeriod((int) (spawner.getBasePeriod() / 2));
                    }
                    if (resourcesLevel == 4){
                        spawner.setPeriod((int) (spawner.getBasePeriod() / 4));
                    }
                }

                if (spawner.getItem().getType().equals(Material.EMERALD)){
                    if (resourcesLevel > 2){
                        spawner.setPeriod(600);
                    }
                }
            }
        }
    }

    public void upgradeSharp(){
        sharpLevel = 1;
        updateEnchants();
    }

    public void upgradeHaste(){
        hasteLevel = Math.min(hasteLevel+1, 2);
        updateEnchants();
    }

    public void upgradeProt(){
        protLevel = Math.min(protLevel+1, 4);
        updateEnchants();
    }

    public void upgradeResources(){
        resourcesLevel = Math.min(resourcesLevel + 1, 4);
        updateItemSpawner();
    }

    public void setHealPool(){
        hasHealPool = true;
    }

    public void addDragon(){
        dragons = 2;
    }

    public boolean hasTrap(){
        return traps.size() > 0;
    }

    public void addTrap(Material material){
        if (material.equals(Material.TRIPWIRE_HOOK)){
            traps.add(new BlindAndSlowTrap());
        }
        if (material.equals(Material.FEATHER)) {
            traps.add(new CounterOffensiveTrap());
        }
        if (material.equals(Material.REDSTONE_TORCH)) {
            traps.add(new AlarmTrap());
        }
        if (material.equals(Material.IRON_PICKAXE)) {
            traps.add(new MinerTrap());
        }
    }

    public boolean canAddTrap(){
        return traps.size() < 3;
    }

    public void addMember(Participant p){
        members.add(p);
    }

    public Location getBedLocation(){
        return bedLocation;
    }
    public Location getSpawnLocation(){
        return spawnLocation;
    }
    public boolean isBedAlive(){
        return isBedAlive;
    }

    public void onBedDestroy(){
        this.isBedAlive = false;
    }

    public void destroyBed() {
        for (int i = getBedLocation().getBlockX()-2; i<getBedLocation().getBlockX()+2; i++){
            for (int j = getBedLocation().getBlockY()-2; j<getBedLocation().getBlockY()+2; j++){
                for (int k = getBedLocation().getBlockZ()-2; k<getBedLocation().getBlockZ()+2; k++){
                    if (getBedLocation().getWorld().getBlockAt(i, j, k).getBlockData() instanceof Bed){
                        getBedLocation().getWorld().getBlockAt(i, j, k).getDrops().clear();
                        getBedLocation().getWorld().getBlockAt(i, j, k).setType(Material.AIR);
                    }
                }
            }
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Entity e : getBedLocation().getWorld().getNearbyEntities(getBedLocation(), 2, 2 ,2 )){
                if (e instanceof Item){
                    e.remove();
                }
            }
        }, 5L);
        onBedDestroy();
    }

    public int getBedsDestroyed() {
        int beds = 0;
        for (Participant p : members) {
            beds += p.getBedsDestroyed();
        }
        return beds;
    }

    public int getFinalKills() {
        int kills = 0;
        for (Participant p : members) {
            kills += p.getFinalKills();
        }
        return kills;
    }
}
