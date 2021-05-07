package io.github.reconsolidated.BedWars.Teams.Traps;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.inventoryShop.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class AlarmTrap implements Trap {


    @Override
    public void onTriggered(BedWars plugin, ArrayList<Entity> entities, int teamID) {
        for (Entity e : entities) {
            if (e instanceof Player) {
                Participant p = plugin.getParticipant((Player) e);
                if (p == null) continue;
                if (p.team.ID != teamID) {
                    p.player.getActivePotionEffects().removeIf(effect -> effect.getType().equals(PotionEffectType.INVISIBILITY));
                }
            }
        }
    }

    @Override
    public ItemStack getItemStack() {
        return CustomItemStack.createCustomItemStack(Material.REDSTONE_TORCH, 1, Material.DIAMOND, -1, "not_clickable", new ArrayList<>(), null);
    }
}
