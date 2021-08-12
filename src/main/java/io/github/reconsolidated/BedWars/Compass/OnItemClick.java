package io.github.reconsolidated.BedWars.Compass;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import io.github.reconsolidated.BedWars.inventoryShop.NbtWrapper;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class OnItemClick {
    public static void item(BedWars plugin, Player player, ItemStack item, CompassMenu menu) {
        ItemStack cost = getCost(item);
        if (cost == null){
            showComms(plugin, player, item.getType());
            return;
        }
        if (canBuy(plugin, player, item)) {
            charge(player, cost);
            startTracking(plugin, player, item.getType());
            success(plugin, player, menu);
        }
        else {

            fail(plugin, player, menu);
        }
    }

    private static void showComms(BedWars plugin, Player player, Material type){
        Participant p = plugin.getParticipant(player);
        if (p == null) return;
        String wiadomosc = "";
        switch (type){
            case SHIELD ->{
                wiadomosc = "POMOCY W BAZIE";
            }
            case STONE_SWORD -> {
                wiadomosc = "ATAKUJMY!";
            }
            case DIAMOND -> wiadomosc = "Potrzebujemy diamentów";
            case EMERALD -> wiadomosc = "Potrzebujemy emeraldów";
        }
        for (Participant m : p.getTeam().members){
            m.getPlayer().sendMessage(p.getTeam().getChatColor() + player.getName()
                    + " >> " + ChatColor.GREEN + wiadomosc);
        }
    }

    private static void startTracking(BedWars plugin, Player player, Material type){
        new BukkitRunnable() {
            @Override
            public void run() {
                Participant tp = plugin.getParticipant(player);
                if (tp == null) return;
                if (tp.isDead()){
                    this.cancel();
                }
                int t = 0;
                switch (type){
                    case BLUE_WOOL -> {
                        t = 0;
                    }
                    case RED_WOOL -> {
                        t = 1;
                    }
                    case YELLOW_WOOL -> {
                        t = 2;
                    }
                    case GREEN_WOOL -> {
                        t = 3;
                    }
                    case ORANGE_WOOL -> {
                        t = 4;
                    }
                    case GRAY_WOOL -> {
                        t = 5;
                    }
                    case PURPLE_WOOL -> {
                        t = 6;
                    }
                    case BROWN_WOOL -> {
                        t = 7;
                    }
                }
                Location location = tp.getTeam().getBedLocation();
                int currentSmallestDistance = 0;
                for (Participant p : plugin.getTeams().get(t).members){
                    if (location == null
                            || player.getLocation().distanceSquared(p.getPlayer().getLocation()) > currentSmallestDistance){
                        location = p.getPlayer().getLocation();
                    }
                }
                player.setCompassTarget(location);

            }
        }.runTaskTimer(plugin, 0, 4L);

    }

    private static boolean canBuy(BedWars plugin, Player player, ItemStack item) {
        ItemStack cost = getCost(item);
        int amount = cost.getAmount();
        boolean canBuy = false;
        for (ItemStack items : player.getInventory().getContents()){
            if (items == null)
                continue;
            if (items.getType().equals(cost.getType())){
                amount -= items.getAmount();
                if (amount <= 0)
                    canBuy = true;
            }
        }
        if (!canBuy){
            player.sendMessage(ChatColor.RED + "Brakuje ci " + amount + "x EMERALD żeby to kupić");
        }

        return canBuy;//Main.game.getParticipant(player).getMoney() >= cost;
    }

    private static void success(BedWars plugin, Player player, CompassMenu menu) {
        Bukkit.getServer().getScheduler().cancelTask(menu.getTimerID());
        menu.setGreenTiles();
        menu.setTimerID(Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, menu::setNormalTiles, 20L));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 3, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6F, 5);
    }

    private static void fail(BedWars plugin, Player player, CompassMenu menu) {
        Bukkit.getServer().getScheduler().cancelTask(menu.getTimerID());
        menu.setRedTiles();
        menu.setTimerID(Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, menu::setNormalTiles, 20L));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 3, 1);
    }


    /*
        Gets cost from the last lore of item
    */
    private static ItemStack getCost(ItemStack item) {
        int amount = Integer.MAX_VALUE;
        String nbtTag = NbtWrapper.getNBTTag("cost_amount", item);
        if (nbtTag == null) return null;
        try {
            amount = Integer.parseInt(nbtTag);
        }
        catch (NumberFormatException e){
            return null;
        }

        String matName = NbtWrapper.getNBTTag("cost_material", item);
        if (matName == null) return null;

        if (Material.getMaterial(matName) == null)
            return null;

        return new ItemStack(Material.getMaterial(matName), amount);
    }

    public static void charge(Player player, ItemStack cost) {
        int amount = cost.getAmount();
        for (int i = 0; i<player.getInventory().getContents().length; i++){
            ItemStack item = player.getInventory().getContents()[i];
            if (item == null)
                continue;
            if (item.getType().equals(cost.getType())){
                while (item.getAmount() > 0 && amount > 0){
                    item.setAmount(item.getAmount()-1);
                    amount--;
                }
                if (item.getAmount() == 0){
                    player.getInventory().remove(item);
                    i--;
                }
            }
            if (amount == 0)
                break;
        }
    }

    public static boolean canAfford(Player player, ItemStack cost) {
        int amount = cost.getAmount();
        for (ItemStack items : player.getInventory().getContents()){
            if (items == null)
                continue;
            if (items.getType().equals(cost.getType())){
                amount -= items.getAmount();
                if (amount <= 0)
                    return true;
            }
        }
        return false;//Main.game.getParticipant(player).getMoney() >= cost;
    }

    public static int getNeededAmount(Player player, ItemStack cost) {
        int amount = cost.getAmount();
        for (ItemStack items : player.getInventory().getContents()){
            if (items == null)
                continue;
            if (items.getType().equals(cost.getType())){
                amount -= items.getAmount();
                if (amount <= 0)
                    return 0;
            }
        }
        return amount;
    }
}
