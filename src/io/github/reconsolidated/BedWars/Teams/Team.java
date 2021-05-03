package io.github.reconsolidated.BedWars.Teams;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.ItemDrops.ItemSpawner;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.Teams.Traps.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

    public void updateEnchants(){
        for (Participant p : members){
            if (p.team.hasteLevel > 0){
                p.player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999, hasteLevel));
            }

            if (protLevel > 0){
                for (ItemStack s : p.player.getInventory().getArmorContents()){
                    if (s != null)
                        s.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protLevel);
                }
            }
            if (sharpLevel > 0){
                for (ItemStack s : p.player.getInventory()){
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
        // TODO
        for (ItemSpawner spawner : plugin.getSpawners()){
            if (spawner.getTeamID() == ID){
                if (spawner.getItem().getType().equals(Material.IRON_INGOT)
                || spawner.getItem().getType().equals(Material.GOLD_INGOT)){
                    if (resourcesLevel == 1){
                        spawner.setPeriod((int) (spawner.getBasePeriod() * 1.5));
                    }
                    if (resourcesLevel == 2){
                        spawner.setPeriod((int) (spawner.getBasePeriod() * 2));
                    }
                    if (resourcesLevel == 3){
                        spawner.setPeriod((int) (spawner.getBasePeriod() * 2));
                    }
                    if (resourcesLevel == 4){
                        spawner.setPeriod((int) (spawner.getBasePeriod() * 4));
                    }
                }

                if (spawner.getItem().getType().equals(Material.EMERALD)){
                    if (resourcesLevel > 2){
                        spawner.setPeriod(300);
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
}
