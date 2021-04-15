package io.github.reconsolidated.BedWars.CustomIronGolem;

import io.github.reconsolidated.BedWars.Participant;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CustomIronGolem extends EntityIronGolem {

    public CustomIronGolem(EntityTypes<? extends EntityIronGolem> entitytypes, World world) {
        super(entitytypes, world);
    }

    public void spawn(Player player, Location location){
        ((CraftWorld)location.getWorld()).addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        this.setPosition(location.getX(), location.getY(), location.getZ());
    }


    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
        this.goalSelector.a(2, new PathfinderGoalStrollVillage(this, 0.6D, false));
        this.goalSelector.a(4, new PathfinderGoalStrollVillageGolem(this, 0.6D));
        this.goalSelector.a(5, new PathfinderGoalOfferFlower(this));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));

    }

    public void setCustomName(String name) {
        IChatBaseComponent comp = IChatBaseComponent.ChatSerializer.a(name);
        this.setCustomName(comp);
    }
}
