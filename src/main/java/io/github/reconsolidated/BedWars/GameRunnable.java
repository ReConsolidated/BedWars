package io.github.reconsolidated.BedWars;

import io.github.reconsolidated.BedWars.ItemDrops.ItemSpawner;
import io.github.reconsolidated.BedWars.Party.PartyDataManager;
import io.github.reconsolidated.BedWars.Party.PartyDomain;
import io.github.reconsolidated.BedWars.Teams.Team;
import net.minecraft.server.v1_16_R2.Entity;
import net.minecraft.server.v1_16_R2.EntityEnderDragon;
import net.minecraft.server.v1_16_R2.EntityInsentient;
import net.minecraft.server.v1_16_R2.Navigation;
import org.bukkit.*;
import org.bukkit.block.data.type.Bed;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class GameRunnable extends BukkitRunnable {
    private final BedWars plugin;
    private int counter;
    private final ArrayList<Participant> participants;
    private final ArrayList<ItemSpawner> spawners;


    public GameRunnable(BedWars plugin) {
        this.plugin = plugin;
        this.counter = 0;
        this.participants = plugin.getParticipants();
        this.spawners = plugin.getSpawners();
    }

    public int getGameTime(){
        return counter;
    }

    @Override
    public void run() {

        int teamsPlaying = 0;
        for (Team t : plugin.getTeams()){
            boolean isPlaying = false;
            for (Participant m : t.members){
                if (!m.hasLost() && m.getPlayer().isOnline()){
                    teamsPlaying++;
                    isPlaying = true;
                    break;
                }
            }
            if (counter > 5 && !isPlaying){
                t.destroyBed();
            }
        }

        if (plugin.hasStarted && teamsPlaying <= 1){
            plugin.onGameEnd();
            this.cancel();
        }


        if (counter < 5){
            for (Participant p : participants){
                p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 10, counter+2);
                p.getPlayer().sendTitle(ChatColor.GOLD + "Start za " + (5-counter), "", 0, 21, 0);
            }
        }
        // POCZ??TEK GRY
        if (counter == 5){
            plugin.hasStarted = true;

            boolean[] teamHasParty = new boolean[plugin.getTeams().size()];
            for (int i = 0; i<plugin.getTeams().size(); i++){
                teamHasParty[i] = false;
            }

            SpawnDestroyer.destroy(plugin.getSpawnLocation());
            for (Participant p : participants) {
                if (p.getTeam() != null) {
                    continue;
                }
                PartyDomain party = PartyDataManager.getParty(p.getPlayer());
                if (party != null) {
                    for (int j = 0; j < plugin.getTeams().size(); j++) {
                        if (teamHasParty[j]) continue;
                        teamHasParty[j] = true;
                        Team team = plugin.getTeams().get(j);
                        p.setTeam(team);
                        team.addMember(p);
                        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("" + j).addEntry(p.getPlayer().getDisplayName());

                        for (String name : party.getMembers()) {
                            Player player = Bukkit.getPlayer(name);
                            if (player == null || !player.isOnline()) {
                                continue;
                            }
                            Participant m = plugin.getParticipant(player);
                            if (m == null) {
                                continue;
                            }
                            m.setTeam(team);
                            team.addMember(m);
                            Bukkit.getScoreboardManager().getMainScoreboard().getTeam("" + j).addEntry(m.getPlayer().getDisplayName());
                        }
                        break;
                    }
                }
            }

            for (Participant p : participants) {
                if (p.getTeam() == null) {
                    int minPlayers = plugin.getTeams().get(0).members.size();
                    int minPlayersTeamID = 0;
                    for (int j = 0; j < plugin.getTeams().size(); j++) {
                        if (minPlayers > plugin.getTeams().get(j).members.size()) {
                            minPlayers = plugin.getTeams().get(j).members.size();
                            minPlayersTeamID = j;
                        }
                    }
                    p.setTeam(plugin.getTeams().get(minPlayersTeamID));
                    plugin.getTeams().get(minPlayersTeamID).addMember(p);
                    Bukkit.getScoreboardManager().getMainScoreboard().getTeam("" + minPlayersTeamID).addEntry(p.getPlayer().getDisplayName());
                }

                p.onStart();
                p.setScoreboard(new ScoreScoreboard(plugin, plugin.getTeams(), p));
                p.getScoreboard().runTaskTimer(plugin, 0, 4);
            }
            for (ItemSpawner spawner : spawners) {
                spawner.start();
            }


        }

        if (counter == 365){
            for (ItemSpawner spawner : spawners) {
                if (spawner.getItem().getType().equals(Material.DIAMOND)) {
                    spawner.setPeriod(plugin.currentConfig.getInt("DIAMOND_II"));
                }
            }
        }
        if (counter == 725){
            for (ItemSpawner spawner : spawners) {
                if (spawner.getItem().getType().equals(Material.DIAMOND)) {
                    spawner.setPeriod(plugin.currentConfig.getInt("DIAMOND_III"));
                }
            }
        }
        if (counter == 1085){
            for (ItemSpawner spawner : spawners) {
                if (spawner.getTeamID() == -1 && spawner.getItem().getType().equals(Material.EMERALD)) {
                    spawner.setPeriod(plugin.currentConfig.getInt("EMERALD_II"));
                }
            }
        }
        if (counter == 1445){
            for (ItemSpawner spawner : spawners) {
                if (spawner.getItem().getType().equals(Material.DIAMOND)) {
                    spawner.setPeriod(plugin.currentConfig.getInt("DIAMOND_IV"));
                }
            }
        }
        if (counter == 1805){
            for (ItemSpawner spawner : spawners) {
                if (spawner.getTeamID() == -1 && spawner.getItem().getType().equals(Material.EMERALD)) {
                    spawner.setPeriod(plugin.currentConfig.getInt("EMERALD_III"));
                }
            }
        }
        if (counter == 2165){
            for (Team t : plugin.getTeams()){
                t.destroyBed();
            }
            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE +
                    "Wszystkie ??????ka " + ChatColor.BOLD + ""
                    + ChatColor.GOLD + "ZNISZCZONE!");

        }
        if (counter == 2525){
            for (Team t : plugin.getTeams()){
                boolean isAnyoneAlive = false;
                for (Participant m : t.members){
                    if (m.getPlayer().isOnline() && m.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
                        isAnyoneAlive = true;
                    }
                }
                if (isAnyoneAlive){
                    for (int i = 0; i<t.dragons; i++){
                        EnderDragon ed = (EnderDragon) t.getBedLocation().getWorld().spawnEntity(t.getBedLocation(), EntityType.ENDER_DRAGON);
                        ed.setInvulnerable(true);
                        new DragonRunnable(plugin, t, ed).runTaskTimer(plugin, 0L, 1L);
                    }
                }
            }
        }

        //
        counter++;
    }

    private void moveTo(LivingEntity l, Location loc, double velocity) {
        EntityInsentient e = (EntityInsentient) ((CraftLivingEntity)l).getHandle();
        e.getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), velocity);
    }

    public String getNextEventName(){
        if (counter < 5) return "Start gry";
        if (counter < 360+5) return ChatColor.AQUA + "Diamenty II" + ChatColor.WHITE;
        if (counter < 360*2+5) return ChatColor.AQUA + "Diamenty III" + ChatColor.WHITE;
        if (counter < 360*3+5) return ChatColor.GREEN + "Emeraldy II" + ChatColor.WHITE;
        if (counter < 360*4+5) return ChatColor.AQUA + "Diamenty IV" + ChatColor.WHITE;
        if (counter < 360*5+5) return ChatColor.GREEN + "Emeraldy III" + ChatColor.WHITE;
        if (counter < 360*6+5) return ChatColor.GOLD + "??????ka znikn??" + ChatColor.WHITE;
        if (counter < 360*7+5) return ChatColor.BLACK + "Apokalipsa" + ChatColor.WHITE;
        return ChatColor.GOLD + "Koniec" + ChatColor.WHITE;
    }

    public int getNextEventTime(){
        if (counter < 5) return 5;
        if (counter < 360+5) return 360+5;
        if (counter < 360*2+5) return 360*2+5;
        if (counter < 360*3+5) return 360*3+5;
        if (counter < 360*4+5) return 360*4+5;
        if (counter < 360*5+5) return 360*5+5;
        if (counter < 360*6+5) return 360*6+5;
        if (counter < 360*7+5) return 360*7+5;
        if (counter < 360*8+5) return 360*8+5;
        if (counter < 360*9+5) return 360*9+5;
        return 0;
    }
}