package io.github.reconsolidated.BedWars.AfterGameEnd;

import io.github.reconsolidated.BedWars.BedWars;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Items implements Listener {
    private static final String ACTION_LEAVE = "leave";
    private static final String ACTION_QUEUE_AGAIN = "queue_again";

    public Items() {
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getItemMeta() == null) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;

        ItemMeta meta = item.getItemMeta();

        String action = meta.getPersistentDataContainer().get(getActionKey(), PersistentDataType.STRING);
        if (action == null) return;

        switch (action) {
            case ACTION_LEAVE -> {
                BedWars.getInstance().getBungeeChannelApi().connect(event.getPlayer(), "bedwars_l");
            }
            case ACTION_QUEUE_AGAIN -> {
                String command = "bdproxycd %s 1 join %s %s".formatted(
                        event.getPlayer().getName(),
                        event.getPlayer().getName(),
                        getQueueName()
                );
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                BedWars.getInstance().getBungeeChannelApi().connect(event.getPlayer(), "bedwars_l");
            }
        }
    }

    private static String getQueueName() {
        if (BedWars.getInstance().isRanked()) {
            return "bedwars" + BedWars.getInstance().getTEAM_SIZE();
        } else {
            return "unranked" + BedWars.getInstance().getTEAM_SIZE();
        }
    }

    public static void setItems(Player player) {
        player.getInventory().setItem(8, getLeaveItem());
        player.getInventory().setItem(0, getQueueAgainItem());
    }

    public static ItemStack getLeaveItem() {
        ItemStack item = new ItemStack(Material.RED_BED);
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(getActionKey(), PersistentDataType.STRING, ACTION_LEAVE);

        meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&cPowrót do lobby")));
        meta.lore(List.of(
                Component.text(""),
                Component.text("&eKliknij &lPPM, &eby wrócić do &7Lobby Bedwars."),
                Component.text("")
        ));

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack getQueueAgainItem() {
        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(getActionKey(), PersistentDataType.STRING, ACTION_QUEUE_AGAIN);

        meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&6&lGRAM NEXTA!")));
        meta.lore(List.of(
                Component.text(""),
                Component.text("&eKliknij &lPPM, &eaby wejść do kolejki"),
                Component.text("&ei wrócić do &7Lobby Bedwars."),
                Component.text("")
        ));

        item.setItemMeta(meta);
        return item;
    }



    private static NamespacedKey getActionKey() {
        return new NamespacedKey(BedWars.getInstance(), "bedwars_action");
    }
}
