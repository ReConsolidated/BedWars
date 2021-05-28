package io.github.reconsolidated.BedWars.CustomSpectator;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CustomSpectator {
    public static void setSpectator(BedWars plugin, Player player){
        Participant p = plugin.getParticipant(player);
        if (p == null) return;
        p.setIsSpectating(true);
        p.saveInventoryForSpectating();
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setCollidable(false);
        player.setInvulnerable(true);
        player.setHealth(20);
        player.setSilent(true);
        for (Player otherPlayer : Bukkit.getOnlinePlayers()){
            Participant op = plugin.getParticipant(otherPlayer);
            if (op == null) continue;
            if (!op.isSpectating()){
                otherPlayer.hidePlayer(plugin, player);
                player.showPlayer(plugin, otherPlayer);
            }
            else{
                otherPlayer.showPlayer(plugin, player);
                player.showPlayer(plugin, otherPlayer);
            }

        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, 1));


    }

    public static void endSpectator(BedWars plugin, Player player){
        Participant p = plugin.getParticipant(player);
        if (p == null) return;
        p.setIsSpectating(false);
        p.restoreInventoryAfterSpectating();
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setCollidable(true);
        player.setInvulnerable(false);
        player.setHealth(20);
        player.setSilent(false);

        for (PotionEffect eff : player.getActivePotionEffects()){
            player.removePotionEffect(eff.getType());
        }


        for (Player otherPlayer : Bukkit.getOnlinePlayers()){
            Participant op = plugin.getParticipant(otherPlayer);
            if (op == null) continue;
            if (op.isSpectating()){
                player.hidePlayer(plugin, otherPlayer);
                otherPlayer.showPlayer(plugin, player);
            }
            else{
                otherPlayer.showPlayer(plugin, player);
                player.showPlayer(plugin, otherPlayer);
            }

        }
    }
}
