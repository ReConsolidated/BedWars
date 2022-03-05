package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.PopupTower;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class BlockPlaceListener implements Listener {
    private BedWars plugin;
    public BlockPlaceListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if (event.getBlock().getLocation().getBlockY() > 100){
            event.setBuild(false);
            event.setCancelled(true);
            return;
        }
        if (event.getBlock().getLocation().getBlockX() > plugin.getGameBorderBiggestX()
                || event.getBlock().getLocation().getBlockZ() > plugin.getGameBorderBiggestZ()
                || event.getBlock().getLocation().getBlockX() < plugin.getGameBorderSmallestX()
                || event.getBlock().getLocation().getBlockZ() < plugin.getGameBorderSmallestZ()){
            event.setBuild(false);
            event.setCancelled(true);
            return;
        }
        Player player = event.getPlayer();
        Participant p = plugin.getParticipant(player);
        if (p == null)
            return;

        if (event.getBlockPlaced().getType().equals(Material.WATER)){
            if (event.getBlockPlaced().getLocation().distance(p.getTeam().getBedLocation()) > 30){
                p.getPlayer().sendMessage(ChatColor.RED + "Nie możesz wylać wody tak daleko od bazy.");
                event.setBuild(false);
                event.setCancelled(true);
                return;
            }
            event.getPlayer().getInventory().remove(Material.BUCKET);
        }


        if (event.getBlockPlaced().getType().equals(Material.TNT)){
            event.setBuild(false);
            event.setCancelled(true);
            removeItemFromInventory(event.getPlayer(), new ItemStack(Material.TNT, 1));
            double x = event.getBlockPlaced().getLocation().getBlockX() + 0.5;
            double z = event.getBlockPlaced().getLocation().getBlockZ() + 0.5;

            TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(
                    new Location(player.getWorld(), x, event.getBlockPlaced().getLocation().getBlockY(), z),
                    EntityType.PRIMED_TNT);
            tnt.setVelocity(new Vector(0, 0.2, 0));
            tnt.setFuseTicks(50);
            tnt.setSource(player);
        }

        if (event.getBlockPlaced().getType().equals(Material.CHEST)){
            event.setCancelled(true);
            removeItemFromInventory(event.getPlayer(), new ItemStack(Material.CHEST, 1));

            Location center = event.getBlockPlaced().getLocation();
            Vector direction = event.getPlayer().getLocation().subtract(center).getDirection();

            PopupTower.build(center, direction, Material.getMaterial(p.getColor() + "_WOOL"));

        }
    }

    private static void removeItemFromInventory(Player player, ItemStack cost) {
        int amount = cost.getAmount();
        for (int i = 0; i<player.getInventory().getContents().length; i++){
            ItemStack item = player.getInventory().getContents()[i];
            if (item == null)
                continue;
            if (item.getType().equals(cost.getType())){
                while (item.getAmount() > 0 && amount > 0){
                    item.setAmount(item.getAmount()-1);
                    amount--;
                }
                if (item.getAmount() == 0){
                    player.getInventory().remove(item);
                    i--;
                }
            }
            if (amount == 0)
                break;
        }
    }


}
