package io.github.reconsolidated.BedWars;

import org.bukkit.Bukkit;
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

        if (commandName.equals("help")){
            sender.sendMessage("This is help command. It will be implemented later");
        }
        if (commandName.equals("start")){
            plugin.onStart();
            sender.sendMessage("Arena started.");
        }
        if (commandName.equals("stop")){
            plugin.onStop();
            sender.sendMessage("Arena stopped.");
        }
        if (commandName.equals("itemspawner")){
            if (args.length >= 2){
                try{
                    int frequency = Integer.parseInt(args[1]);
                    if (args.length == 2)
                        plugin.onItemSpawner((Player) sender, args[0], frequency, -1);
                    else{
                        int team = Integer.parseInt(args[2]);
                        plugin.onItemSpawner((Player) sender, args[0], frequency, team);
                    }
                } catch (NumberFormatException e){
                    sender.sendMessage("Podaj poprawną liczbę jako częstotliwość dropu i numer drużyny.");
                }

            }
            else{
                sender.sendMessage("Podaj angielską nazwę itemu i co ile sekund ma wypadać item.");
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


        return false;
    }
}
