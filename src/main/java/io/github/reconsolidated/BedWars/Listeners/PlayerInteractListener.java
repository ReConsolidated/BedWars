package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Compass.CompassMenu;
import io.github.reconsolidated.BedWars.CustomEntities.CustomIronGolem;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.Teams.Team;
import io.github.reconsolidated.BedWars.inventoryShop.buyMethods.Buy;
import net.minecraft.server.v1_16_R2.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.Bed;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.entity.Fireball;
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

        if (p == null || p.isSpectating() || p.team == null){
            event.setCancelled(true);
            return;
        }


        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if (event.getItem() != null && event.getItem().getType().equals(Material.COMPASS)){
                new CompassMenu(plugin, p.player, "main");
            }
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getMaterial().equals(Material.POLAR_BEAR_SPAWN_EGG)){
            event.setCancelled(true);
            Location location = event.getClickedBlock().getLocation().clone().add(0, 2, 0);
            CustomIronGolem golem = new CustomIronGolem(EntityTypes.IRON_GOLEM, ((CraftWorld) location.getWorld()).getHandle());
            golem.spawn(p.team.ID, location, p);
            plugin.addGolem(golem);
            Buy.charge(p.player, new ItemStack(Material.POLAR_BEAR_SPAWN_EGG));
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.getClickedBlock() != null
                && event.getClickedBlock().getState() instanceof Chest){
            int closestTeamID = getClosestTeamID(event.getClickedBlock().getLocation());
            if (closestTeamID != p.team.ID){
                event.setCancelled(true);
            }
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.getClickedBlock() != null
                && event.getClickedBlock().getState() instanceof Bed){
            event.setCancelled(true);
        }

        if (event.getMaterial().equals(Material.FIRE_CHARGE)
                && (event.getAction().equals(Action.RIGHT_CLICK_AIR)
                || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
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
