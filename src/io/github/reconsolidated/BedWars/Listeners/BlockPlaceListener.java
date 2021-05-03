package io.github.reconsolidated.BedWars.Listeners;

import com.sk89q.worldedit.math.Vector2;
import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import net.minecraft.server.v1_16_R2.EntityLiving;
import net.minecraft.server.v1_16_R2.EntityTNTPrimed;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Ladder;
import org.bukkit.block.data.type.TNT;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftTNTPrimed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;


public class BlockPlaceListener implements Listener {
    private BedWars plugin;
    public BlockPlaceListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Participant p = plugin.getParticipant(player);
        if (p == null)
            return;

        if (event.getBlockPlaced().getType().equals(Material.TNT)){
            event.setCancelled(true);
            removeItemFromInventory(event.getPlayer(), new ItemStack(Material.TNT, 1));
            double x = event.getBlockPlaced().getLocation().getBlockX() + 0.5;
            double z = event.getBlockPlaced().getLocation().getBlockZ() + 0.5;

            TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(
                    new Location(player.getWorld(), x, event.getBlockPlaced().getLocation().getBlockY(), z),
                    EntityType.PRIMED_TNT);
            tnt.setVelocity(new Vector(0, 0.2, 0));

            // Change via NMS the source of the TNT by the player
            EntityLiving nmsEntityLiving = (EntityLiving)(((CraftLivingEntity) player).getHandle());
            EntityTNTPrimed nmsTNT = (EntityTNTPrimed) (((CraftTNTPrimed) tnt).getHandle());
            try {
                Field sourceField = EntityTNTPrimed.class.getDeclaredField("source");
                sourceField.setAccessible(true);
                sourceField.set(nmsTNT, nmsEntityLiving);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (event.getBlockPlaced().getType().equals(Material.CHEST)){
            event.setCancelled(true);
            removeItemFromInventory(event.getPlayer(), new ItemStack(Material.CHEST, 1));

            Location center = event.getBlockPlaced().getLocation();
            Vector direction = event.getPlayer().getLocation().subtract(center).getDirection();
            craftNormalize(direction);
            buildPopupTower(center, direction, Material.BLUE_WOOL);
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

    private void craftNormalize(Vector vec){
        double max_val = Math.max(vec.getX()*vec.getX(), vec.getZ()*vec.getZ());
        if (max_val == vec.getX()*vec.getX()){
            if (vec.getX() < 0)
                vec.setX(-1);
            else
                vec.setX(1);
            vec.setY(0);
            vec.setZ(0);
        }
        if (max_val == vec.getZ()*vec.getZ()){
            if (vec.getZ() < 0)
                vec.setZ(-1);
            else
                vec.setZ(1);
            vec.setY(0);
            vec.setX(0);
        }
    }

    private void buildPopupTower(Location center, Vector direction, Material material){
        Vector forward = direction.clone();
        Vector left = direction.clone().rotateAroundY(Math.PI /2);
        left.setX((int)left.getX());
        left.setY((int)left.getY());
        left.setZ((int)left.getZ());
        Location pos = center.clone();

        pos.add(forward);
        pos.add(forward);
        for (int i = 0; i<4; i++){

            center.getWorld().getBlockAt(pos).setType(material);
            pos.add(left);
            center.getWorld().getBlockAt(pos).setType(material);
            pos.subtract(forward);
            pos.add(left);
            center.getWorld().getBlockAt(pos).setType(material);
            pos.subtract(forward);
            center.getWorld().getBlockAt(pos).setType(material);
            pos.subtract(forward);
            pos.subtract(left);
            center.getWorld().getBlockAt(pos).setType(material);
            pos.subtract(left);
            if (i>1){
                center.getWorld().getBlockAt(pos).setType(material);
            }
            pos.subtract(left);
            center.getWorld().getBlockAt(pos).setType(material);
            pos.subtract(left);
            pos.add(forward);
            center.getWorld().getBlockAt(pos).setType(material);
            pos.add(forward);
            center.getWorld().getBlockAt(pos).setType(material);
            pos.add(forward);
            pos.add(left);
            center.getWorld().getBlockAt(pos).setType(material);

            pos.add(left);
            pos.add(new Vector(0, 1, 0));
        }

        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        pos.subtract(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);

        pos.subtract(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(forward);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);

        pos.subtract(forward);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(forward);
        pos.add(forward);
        pos.add(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(forward);
        pos.subtract(forward);
        pos.subtract(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(forward);
        center.getWorld().getBlockAt(pos).setType(material);

        pos.add(new Vector(0, 1, 0));

        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(forward);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        pos.add(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(forward);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        pos.subtract(forward);
        center.getWorld().getBlockAt(pos).setType(material);

        pos.add(new Vector(0, 1, 0));

        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(forward);
        pos.add(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(forward);
        pos.add(forward);
        pos.add(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        pos.add(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(left);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(forward);
        pos.subtract(left);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.subtract(forward);
        pos.subtract(forward);
        pos.subtract(forward);
        center.getWorld().getBlockAt(pos).setType(material);
        pos.add(left);
        pos.subtract(forward);
        center.getWorld().getBlockAt(pos).setType(material);

        pos = center.clone();
        pos.add(forward);
        for (int i = 0; i<5; i++){
            center.getWorld().getBlockAt(pos).setType(Material.LADDER);
            Ladder ladder = (Ladder) center.getWorld().getBlockAt(pos).getBlockData();
            if (forward.getX() == -1){
                ladder.setFacing(BlockFace.EAST);
            }
            if (forward.getX() == 1){
                ladder.setFacing(BlockFace.WEST);
            }
            if (forward.getZ() == -1){
                ladder.setFacing(BlockFace.SOUTH);
            }
            if (forward.getZ() == 1){
                ladder.setFacing(BlockFace.NORTH);
            }
            center.getWorld().getBlockAt(pos).setBlockData(ladder);
            pos.add(new Vector(0, 1, 0));

        }


    }


}
