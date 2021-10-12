package io.github.reconsolidated.BedWars;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    private BedWars plugin;

    public Commands(BedWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players may execute this command");
            return true;
        }

        String commandName = command.getName().toLowerCase();

        if (commandName.equals("helpbedwars")){
            sender.sendMessage(ChatColor.YELLOW + "Komendy dostępne na bedwars: ");
            sender.sendMessage(ChatColor.AQUA + "/bdstart  " + ChatColor.YELLOW + "Uruchamia grę.");
            sender.sendMessage(ChatColor.AQUA + "/newshop1 " + ChatColor.YELLOW + "Tworzy zwykły sklep tam gdzie stoisz.");
            sender.sendMessage(ChatColor.AQUA + "/newshop2  " + ChatColor.YELLOW + "Tworzy sklep diamentowy tam gdzie stoisz.");
            sender.sendMessage(ChatColor.AQUA + "/itemspawner  NAZWA CO_ILE_SEKUND MAX_DROPPED [TEAM](opcjonalne)  " + ChatColor.YELLOW + "Tworzy spawner itemków.");
            sender.sendMessage(ChatColor.AQUA + "/setspawn TEAM_ID  " + ChatColor.YELLOW + "Tworzy spawn teamu nr TEAM_ID (od 0 do 3)");
            sender.sendMessage(ChatColor.AQUA + "/setbed TEAM_ID  " + ChatColor.YELLOW + "Ustawia miejsce łóżka teamu nr TEAM_ID (od 0 do 3)");
        }
        if (commandName.equals("bdstart")){
            plugin.onStart();
            sender.sendMessage("Arena started.");
        }
        if (commandName.equals("bdstop")) {
            sender.sendMessage("Arena stopped.");
        }
        if (commandName.equals("newshop1")){
            plugin.createShop1((Player) sender);
            sender.sendMessage("Utworzono normalny sklep.");
        }
        if (commandName.equals("newshop2")){
            plugin.createShop2((Player) sender);
            sender.sendMessage("Utworzono diax sklep.");
        }
        if (commandName.equals("release")){
            plugin.releasePlayer((Player) sender);
            sender.sendMessage("Wycofano cię z rozgrywki.");
        }
        if (commandName.equals("itemspawner")){
            if (args.length >= 3){
                try{
                    int frequency = Integer.parseInt(args[1]);
                    int maxAmount = Integer.parseInt(args[2]);
                    if (args.length == 3)
                        plugin.onItemSpawner((Player) sender, args[0], frequency, maxAmount, -1);
                    else {
                        int team = Integer.parseInt(args[3]);
                        plugin.onItemSpawner((Player) sender, args[0], frequency, maxAmount, team);
                    }
                } catch (NumberFormatException e){
                    sender.sendMessage("Podaj poprawną liczbę jako częstotliwość dropu, maksymalną liczbę zrespionych itemów i numer drużyny.");
                }

            }
            else{
                sender.sendMessage("Poprawne użycie: /itemspawner NAZWA CO_ILE_SEKUND MAX_DROPPED [TEAM](opcjonalne) ");
            }
        }

        if (commandName.equals("setspawn")){
            if (args.length >= 1){
                plugin.onSetSpawn((Player) sender, args[0]);
            }
            else{
                sender.sendMessage("Podaj numer drużyny");
            }
        }

        if (commandName.equals("setbed")){
            if (args.length >= 1){
                plugin.onSetBed((Player) sender, args[0]);
            }
            else{
                sender.sendMessage("Podaj numer drużyny");
            }
        }

        if (commandName.equals("team") || commandName.equals("d")){
            plugin.setTeamChat((Player)sender, true);
        }

        if (commandName.equals("all") || commandName.equals("w")){
            plugin.setTeamChat((Player)sender, false);
        }


        return false;
    }
}
