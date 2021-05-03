package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.CustomIronGolem.CustomIronGolem;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.Teams.Team;
import io.github.reconsolidated.BedWars.inventoryShop.buyMethods.Buy;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class PlayerInteractListener implements Listener {
    private BedWars plugin;

    public PlayerInteractListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Participant p = plugin.getParticipant(event.getPlayer());
        if (p == null) return;

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getMaterial().equals(Material.POLAR_BEAR_SPAWN_EGG)){
            event.setCancelled(true);
            Location location = event.getClickedBlock().getLocation().clone().add(0, 2, 0);
            CustomIronGolem golem = new CustomIronGolem(EntityTypes.IRON_GOLEM, ((CraftWorld) location.getWorld()).getHandle());
            golem.spawn(event.getPlayer(), location);
            golem.setCustomName(Integer.toString(p.team.ID));
            golem.setCustomNameVisible(true);
            Buy.charge(p.player, new ItemStack(Material.POLAR_BEAR_SPAWN_EGG));
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.getClickedBlock() != null
                && event.getClickedBlock().getState() instanceof Chest){
            Bukkit.broadcastMessage("Skrzynia kliknieta.");
            int closestTeamID = getClosestTeamID(event.getClickedBlock().getLocation());
            Bukkit.broadcastMessage("" + closestTeamID);
            if (closestTeamID != p.team.ID){
                event.setCancelled(true);
            }
        }

        if (event.getMaterial().equals(Material.FIRE_CHARGE)){
            event.setCancelled(true);
            p.player.launchProjectile(Fireball.class, p.player.getEyeLocation().getDirection());
            Buy.charge(p.player, new ItemStack(Material.FIRE_CHARGE));
        }

    }

    private int getClosestTeamID(Location location){
        int currentSmallest = 1000000;
        int closestTeamID = -1;
        for (Team team : plugin.getTeams()){
            int distance = (int) team.getSpawnLocation().distanceSquared(location);
            if (distance < currentSmallest){
                currentSmallest = distance;
                closestTeamID = team.ID;
            }
        }
        return closestTeamID;
    }
}
