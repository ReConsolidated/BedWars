package io.github.reconsolidated.BedWars.CustomEntities;

import io.github.reconsolidated.BedWars.Participant;
import net.minecraft.server.v1_16_R2.EntitySilverfish;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftSilverfish;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CustomSilverFish extends EntitySilverfish {
    public int teamID;
    public Participant owner;

    public CustomSilverFish(EntityTypes<? extends EntitySilverfish> entitytypes, World world) {
        super(entitytypes, world);
    }

    public void spawn(int teamID, Location location, Participant owner){
        CraftSilverfish sf = ((CraftWorld)location.getWorld()).addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        sf.setCustomName(owner.getChatColor() + "Silverfish");
        this.setPosition(location.getX(), location.getY(), location.getZ());
        this.teamID = teamID;
        this.owner = owner;
    }


}
