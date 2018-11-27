package codes.biscuit.spawnerunlocker.commands;

import codes.biscuit.spawnerunlocker.SpawnerUnlocker;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class SpawnerUnlockerCommand implements CommandExecutor {

    private SpawnerUnlocker main;

    public SpawnerUnlockerCommand(SpawnerUnlocker main) {
        this.main = main;
    }

    public static final TabCompleter TAB_COMPLETER = (sender, cmd, alias, args) -> {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>(Arrays.asList("reload", "setlevel"));
            for (String arg : Arrays.asList("reload", "setlevel")) {
                if (!arg.startsWith(args[0].toLowerCase())) {
                    arguments.remove(arg);
                }
            }
            return arguments;
        }
        return null;
    };

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("spawnerunlocker.admin") || sender.isOp()) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "reload":
                        main.reloadConfig();
                        main.getConfigUtils().restartSaveTask();
                        main.getAliasManager().setAdditionalAliases(main.getCommand("spawnerrankup"), main.getConfigUtils().getAliases());
                        sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the config. Most values have been instantly updated.");
                        break;
                    case "setlevel":
                        if (args.length > 2) {
                            Player p = Bukkit.getPlayerExact(args[1]);
                            if (p != null) {
                                int level;
                                try {
                                    level = Integer.parseInt(args[2]);
                                } catch (NumberFormatException ex) {
                                    sender.sendMessage(ChatColor.RED + "This isn't a valid level!");
                                    return false;
                                }
                                main.getConfigUtils().setPlayerLevel(p, level);
                                sender.sendMessage(ChatColor.GREEN + "Set "+args[1]+"'s level to "+level+".");
                            } else {
                                sender.sendMessage(ChatColor.RED + "This player is not online!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Please specify a player and a level! /su setlevel <player> <level>");
                        }
                        break;
                    case "bypass":
                        if (sender instanceof Player) {
                            if (main.getUtils().bypassList.contains(((Player)sender).getUniqueId())) {
                                main.getUtils().bypassList.remove(((Player)sender).getUniqueId());
                                sender.sendMessage(ChatColor.RED + "You can no longer bypass spawner placement.");
                            } else {
                                main.getUtils().bypassList.add(((Player)sender).getUniqueId());
                                sender.sendMessage(ChatColor.GREEN + "You can now place any spawner!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You can only use this command as a player!");
                        }
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Invalid argument! Do /su for usages.");
                }
            } else {
                sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------" + ChatColor.GRAY + "[" + ChatColor.YELLOW + ChatColor.BOLD + " SpawnerUnlocker " + ChatColor.GRAY + "]" + ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------");
                sender.sendMessage(ChatColor.YELLOW + "● /su reload " + ChatColor.GRAY + "- Reload the config");
                sender.sendMessage(ChatColor.YELLOW + "● /su setlevel <player> <level> " + ChatColor.GRAY + "- Set a player's spawner unlock level");
                sender.sendMessage(ChatColor.YELLOW + "● /su bypass " + ChatColor.GRAY + "- Toggle being able to place any spawner");
                sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "v" + main.getDescription().getVersion() + " by Biscut");
                sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------");
            }
        } else {
            if (!main.getConfigUtils().getNoPermissionCommandMessage().equals("")) {
                sender.sendMessage(main.getConfigUtils().getNoPermissionCommandMessage());
            }
        }
        return true;
    }
}
