package io.github.reconsolidated.BedWars.Chat;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatMessageListener implements Listener {
    private final BedWars plugin;
    public ChatMessageListener(BedWars plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event){
        Participant p = plugin.getParticipant(event.getPlayer());
        if (p == null || p.getTeam() == null){
            event.setFormat(BedWars.chat.getPlayerPrefix(event.getPlayer()) + ChatColor.GRAY + "%s" +
                    ChatColor.WHITE + ": " + "%s");
            return;
        }
        if (p.getTeamChat()){
            event.setCancelled(true);
            for (Participant t : p.getTeam().members){
                t.getPlayer().sendMessage(
                        p.getPlayer().displayName()
                        .append(
                        Component.text(ChatColor.GRAY + " >> DRUŻYNA: " + ChatColor.WHITE + event.getMessage())));
            }
        }
        else{
            event.setCancelled(true);
            Bukkit.broadcast(
                    Component.text("")
                    .append(p.getPlayer().displayName())
                    .append(Component.text(ChatColor.WHITE + ": " + event.getMessage())));
        }


    }
}
