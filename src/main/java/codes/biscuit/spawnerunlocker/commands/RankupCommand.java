package codes.biscuit.spawnerunlocker.commands;

import codes.biscuit.spawnerunlocker.SpawnerUnlocker;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class RankupCommand implements CommandExecutor {

    private SpawnerUnlocker main;

    public RankupCommand(SpawnerUnlocker main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player)sender;
            if (!main.getUtils().playerIsMax(p)) {
                int nextLevelXPRequirement = new ArrayList<>(main.getConfigUtils().getMobList().get(main.getUtils().getPlayerLevel(p)+1).values()).get(0);
                if (p.getLevel() >= nextLevelXPRequirement) {
                    p.setLevel(p.getLevel() - nextLevelXPRequirement);
                    main.getConfigUtils().addPlayerLevels(p,1);
                    if (!main.getConfigUtils().getNextLevelMessage(main.getUtils().getPlayerLevel(p)).equals("")) {
                        p.sendMessage(main.getConfigUtils().getNextLevelMessage(main.getUtils().getPlayerLevel(p)));
                    }
                } else {
                    if (!main.getConfigUtils().getNotEnoughLevelsMessage(main.getUtils().getPlayerLevel(p)+1, nextLevelXPRequirement, p.getLevel()).equals("")) {
                        p.sendMessage(main.getConfigUtils().getNotEnoughLevelsMessage(main.getUtils().getPlayerLevel(p)+1, nextLevelXPRequirement, p.getLevel()));
                    }
                }
            } else {
                if (!main.getConfigUtils().getMaxLevelMessage().equals("")) {
                    p.sendMessage(main.getConfigUtils().getMaxLevelMessage());
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You can only use this command as a player!");
        }
        return true;
    }
}
