package io.github.reconsolidated.BedWars.Teams.Traps;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class CounterOffensiveTrap implements Trap {
    @Override
    public void onTriggered(BedWars plugin, ArrayList<Entity> entities, int teamID) {
        for (Entity e : entities) {
            if (e instanceof Player) {
                Participant p = plugin.getParticipant((Player) e);
                if (p == null) continue;
                if (p.team.ID == teamID) {
                    p.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 1));
                    p.player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 10, 2));
                }
            }
        }
    }
}
