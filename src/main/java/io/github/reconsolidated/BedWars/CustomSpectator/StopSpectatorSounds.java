package io.github.reconsolidated.BedWars.CustomSpectator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;


public class StopSpectatorSounds {
    private BedWars pl;
    public StopSpectatorSounds(BedWars plugin){
        this.pl = plugin;
    }


    public void run(){
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(pl, ListenerPriority.NORMAL,
                        PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        Participant p = pl.getParticipant(event.getPlayer());
                        if (p == null) return;
                        if (p.isSpectating()){
                            event.setCancelled(true);
                        }
                    }
                });
    }
}
