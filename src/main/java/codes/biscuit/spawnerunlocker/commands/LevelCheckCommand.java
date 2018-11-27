package codes.biscuit.spawnerunlocker.commands;

import codes.biscuit.spawnerunlocker.SpawnerUnlocker;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelCheckCommand implements CommandExecutor {

    private SpawnerUnlocker main;

    public LevelCheckCommand(SpawnerUnlocker main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("spawnerunlocker.checklevel")) {
                if (!main.getConfigUtils().getLevelCheckMessage(main.getUtils().getPlayerLevel((Player)sender)).equals("")) {
                    sender.sendMessage(main.getConfigUtils().getLevelCheckMessage(main.getUtils().getPlayerLevel((Player)sender)));
                }
            } else {
                sender.sendMessage(ChatColor.RED + "No permission!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You can only use this command ingame!");
        }
        return true;
    }
}
