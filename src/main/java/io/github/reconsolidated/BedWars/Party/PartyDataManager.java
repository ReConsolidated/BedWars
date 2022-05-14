package io.github.reconsolidated.BedWars.Party;


import io.github.reconsolidated.jediscommunicator.JedisCommunicator;
import io.github.reconsolidated.jediscommunicator.Party;
import org.bukkit.entity.Player;

public class PartyDataManager {

    public static Party getParty(Player player) {
        JedisCommunicator jedis = new JedisCommunicator();
        return jedis.getParty(player.getName());
    }


}
