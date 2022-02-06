package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Compass.CompassMenu;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.Teams.Team;
import io.github.reconsolidated.BedWars.inventoryShop.buyMethods.Buy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.BrewingStand;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.IronGolem;
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
        if (event.getAction().equals(Action.PHYSICAL)){
            event.setCancelled(true);
            return;
        }

        Participant p = plugin.getParticipant(event.getPlayer());

        if (p == null || p.isSpectating() || p.getTeam() == null){
            event.setCancelled(true);
            return;
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if (event.getItem() != null && event.getItem().getType().equals(Material.COMPASS)){
                new CompassMenu(plugin, p.getPlayer(), "main");
            }
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getMaterial().equals(Material.POLAR_BEAR_SPAWN_EGG)){
            event.setCancelled(true);
            Location location = event.getClickedBlock().getLocation().clone().add(0, 2, 0);
            if (event.getClickedBlock().getLocation().distance(p.getTeam().getBedLocation()) > 50){
                p.getPlayer().sendMessage(ChatColor.RED + "Nie możesz stworzyć golema tak daleko od bazy.");
                return;
            }
            IronGolem golem = (IronGolem) location.getWorld().spawnEntity(location, EntityType.IRON_GOLEM);
            golem.setCustomName(p.getChatColor() + "Golem");
            golem.setCustomNameVisible(true);
            plugin.addGolem(golem);
            Buy.charge(p.getPlayer(), new ItemStack(Material.POLAR_BEAR_SPAWN_EGG));
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.getClickedBlock() != null
                && event.getClickedBlock().getState() instanceof Chest){
            int closestTeamID = getClosestTeamID(event.getClickedBlock().getLocation());
            if (closestTeamID != p.getTeam().ID){
                event.setCancelled(true);
            }
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && !p.getPlayer().isSneaking()
                && event.getClickedBlock() != null
                && event.getClickedBlock().getBlockData() instanceof Bed){
            event.setCancelled(true);
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.getClickedBlock() != null
                && (event.getClickedBlock().getBlockData() instanceof BrewingStand
                || event.getClickedBlock().getBlockData() instanceof TrapDoor
                || event.getClickedBlock().getBlockData() instanceof Furnace)){
            event.setCancelled(true);
        }

        if (event.getMaterial().equals(Material.FIRE_CHARGE)
                && (event.getAction().equals(Action.RIGHT_CLICK_AIR)
                || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
            event.setCancelled(true);
            p.getPlayer().launchProjectile(Fireball.class, p.getPlayer().getEyeLocation().getDirection());
            Buy.charge(p.getPlayer(), new ItemStack(Material.FIRE_CHARGE));
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
