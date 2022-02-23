package io.github.reconsolidated.BedWars.Chat;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
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
            event.setFormat(ChatColor.GRAY + "%s" +
                    ChatColor.WHITE + ": " + "%s");
            return;
        }
        if (p.getTeamChat()){
            event.setCancelled(true);
            event.setFormat(p.getPlayer().displayName().toString() + p.getTeam().getChatColor() +
                    "%s" + ChatColor.GRAY + " >> DRUŻYNA: " + ChatColor.WHITE + "%s");
            for (Participant t : p.getTeam().members){
                t.getPlayer().sendMessage(p.getTeam().getChatColor() +
                        p.getPlayer().getName() + ChatColor.GRAY + " >> DRUŻYNA: " + ChatColor.WHITE + event.getMessage());
            }
        }
        else{
            event.setFormat(p.getTeam().getChatColor() + "[" + p.getTeam().getName() + "] " + ChatColor.GRAY +
                    "%s" + ChatColor.WHITE + ": " + "%s");
        }


    }
}
