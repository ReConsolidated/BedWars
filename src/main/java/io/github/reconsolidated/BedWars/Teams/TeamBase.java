package io.github.reconsolidated.BedWars.Teams;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.Teams.Traps.Trap;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class TeamBase extends BukkitRunnable {
    private final int teamID;
    private final Location location;
    private final BedWars plugin;
    private int trapCooldown = 0;

    public TeamBase(BedWars plugin, int teamID, Location location){
        this.teamID = teamID;
        this.location = location;
        this.plugin = plugin;
    }

    private void onTrapTriggered(Team team){
        trapCooldown = 10;
        for (Participant defender : team.members){
            defender.getPlayer().sendTitle(ChatColor.RED + "Pułapka aktywowana!", ChatColor.RED + "Broń swojej bazy przed wrogiem", 10, 60, 10);
        }
    }

    @Override
    public void run() {
        trapCooldown--;
        ArrayList<Entity> entities = new ArrayList<>(location.getWorld().getNearbyEntities(location, 16, 16, 16));
        for (Entity e : entities){
            if (e instanceof Player){
                Participant p = plugin.getParticipant((Player) e);
                if (p == null) continue;
                if (p.getPlayer().getGameMode() != GameMode.SURVIVAL) continue;
                if (p.getTeam().ID == teamID){
                    if (p.getTeam().hasHealPool()){
                        p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
                    }
                }
                else if (!p.isTrapInvincible() && !p.hasLost() && p.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
                    if (trapCooldown > 0){
                        continue;
                    }
                    Team team = plugin.getTeams().get(teamID);
                    if (team.hasTrap()){
                        Trap trap = team.traps.pop();
                        trap.onTriggered(plugin, entities, teamID);
                        onTrapTriggered(team);
                    }
                }
            }
        }
    }
}
