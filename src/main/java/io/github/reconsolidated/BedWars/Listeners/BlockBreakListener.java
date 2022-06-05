package io.github.reconsolidated.BedWars.Listeners;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.Teams.Team;
import io.github.reconsolidated.visibleeffects.VisibleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

import static io.github.reconsolidated.BedWars.BedWars.vEffects;

public class BlockBreakListener implements Listener {
    private BedWars plugin;
    public BlockBreakListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Participant p = plugin.getParticipant(player);
        if (p == null)
            return;
        if (event.getBlock().getBlockData() instanceof Bed){
            event.setDropItems(false);
            ArrayList<Team> teams = plugin.getTeams();
            for (int i = 0; i<teams.size(); i++){
                if (teams.get(i).getBedLocation().distance(event.getBlock().getLocation()) < 3){
                    if (p.getTeam() == teams.get(i)){
                        event.setCancelled(true);
                        p.getPlayer().sendMessage(ChatColor.RED + "Nie możesz zniszczyć swojego łóżka.");
                    }
                    else{
                        p.setBedsDestroyed(p.getBedsDestroyed() + 1);
                        teams.get(i).onBedDestroy();
                        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Zniszczono łóżko drużyny: " + teams.get(i).getChatColor() + teams.get(i).getName());
                        vEffects.playEffect(player, VisibleEffects.EFFECT_EVENT.BED_DESTROYED, event.getBlock().getLocation().clone().add(0, 3, 0));

                        for (Participant o : plugin.getParticipants()) {
                            if (o.getTeam().ID != i) {
                                o.getPlayer().playSound(o.getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 3, 1);
                            }
                        }
                        for (Participant m : teams.get(i).members) {
                            m.getPlayer().playSound(m.getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 7, 1);
                            m.getPlayer().sendMessage(ChatColor.RED + "Twoje łóżko zostało zniszczone i już się nie odrodzisz.");
                            m.getPlayer().showTitle(Title.title(
                                    Component.text("Łóżko zniszczone!").color(TextColor.color(231, 207, 97)),
                                    Component.text("Jeżeli zginiesz, nie odrodzisz się").color(TextColor.color(252, 144, 148))
                                    )
                            );
                        }
                    }

                }
            }
        }
    }
}
