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
import org.bukkit.block.data.type.TNT;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftTNTPrimed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
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
    }
}
