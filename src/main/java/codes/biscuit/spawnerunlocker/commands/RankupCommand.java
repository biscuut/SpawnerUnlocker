package codes.biscuit.spawnerunlocker.commands;

import codes.biscuit.spawnerunlocker.SpawnerUnlocker;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class RankupCommand implements CommandExecutor {

    private SpawnerUnlocker main;

    public RankupCommand(SpawnerUnlocker main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("spawnerunlocker.rankup")) {
                Player p = (Player)sender;
                if (main.getConfigUtils().isGUIEnabled()) {
                    if (!p.getOpenInventory().getTitle().contains(main.getConfigUtils().getGUITitle())) {
                        Inventory confirmInv = Bukkit.createInventory(null, 9 * main.getConfigUtils().getGUIRows(), main.getConfigUtils().getGUITitle());
                        ItemStack acceptItem = main.getConfigUtils().getConfirmBlockItemStack();
                        if (!acceptItem.getType().equals(Material.AIR)) {
                            ItemMeta acceptItemMeta = acceptItem.getItemMeta();
                            acceptItemMeta.setDisplayName(main.getConfigUtils().getConfirmName());
                            if (main.getUtils().playerIsMax(p)) {
                                acceptItemMeta.setLore(main.getConfigUtils().getConfirmLoreMax(main.getUtils().getPlayerLevel(p), main.getUtils().getPlayerLevel(p)+1));
                            } else {
                                double nextLevelCost = new ArrayList<>(main.getConfigUtils().getMobList().get(main.getUtils().getPlayerLevel(p) + 1).values()).get(0);
                                acceptItemMeta.setLore(main.getConfigUtils().getConfirmLoreRegular(main.getUtils().getPlayerLevel(p), main.getUtils().getPlayerLevel(p)+1, nextLevelCost));
                            }
                            acceptItem.setItemMeta(acceptItemMeta);
                        }
                        ItemStack cancelItem = main.getConfigUtils().getCancelBlockItemStack();
                        if (!cancelItem.getType().equals(Material.AIR)) {
                            ItemMeta cancelItemMeta = cancelItem.getItemMeta();
                            cancelItemMeta.setDisplayName(main.getConfigUtils().getCancelName());
                            cancelItemMeta.setLore(main.getConfigUtils().getCancelLore());
                            cancelItem.setItemMeta(cancelItemMeta);
                        }
                        ItemStack fillItem = main.getConfigUtils().getFillItemStack();
                        if (!fillItem.getType().equals(Material.AIR)) {
                            ItemMeta fillItemMeta = fillItem.getItemMeta();
                            fillItemMeta.setDisplayName(main.getConfigUtils().getFillName());
                            fillItemMeta.setLore(main.getConfigUtils().getFillLore());
                            fillItem.setItemMeta(fillItemMeta);
                        }
                        for (int i = 0; i < 9 * main.getConfigUtils().getGUIRows(); i++) {
                            if (main.getConfigUtils().getConfirmSlots().contains(i)) {
                                confirmInv.setItem(i, acceptItem);
                            } else if (main.getConfigUtils().getCancelSlots().contains(i)) {
                                confirmInv.setItem(i, cancelItem);
                            } else {
                                confirmInv.setItem(i, fillItem);
                            }
                        }
                        p.openInventory(confirmInv);
                    }
                } else {
                    if (!main.getUtils().playerIsMax(p)) {
                        double nextLevelCost = new ArrayList<>(main.getConfigUtils().getMobList().get(main.getUtils().getPlayerLevel(p) + 1).values()).get(0);
                        if (main.getUtils().getCurrencyAmount(p) >= nextLevelCost) {
                            main.getUtils().removeCurrency(p, nextLevelCost);
                            main.getConfigUtils().addPlayerLevels(p, 1);
                            if (!main.getConfigUtils().getRankupMessage(main.getUtils().getPlayerLevel(p)).equals("")) {
                                p.sendMessage(main.getConfigUtils().getRankupMessage(main.getUtils().getPlayerLevel(p)));
                            }
                        } else {
                            if (!main.getConfigUtils().getInsufficientCurrencyMessage(main.getUtils().getPlayerLevel(p) + 1, nextLevelCost, main.getUtils().getCurrencyAmount(p)).equals("")) {
                                p.sendMessage(main.getConfigUtils().getInsufficientCurrencyMessage(main.getUtils().getPlayerLevel(p) + 1, nextLevelCost, main.getUtils().getCurrencyAmount(p)));
                            }
                        }
                    } else {
                        if (!main.getConfigUtils().getMaxLevelMessage().equals("")) {
                            p.sendMessage(main.getConfigUtils().getMaxLevelMessage());
                        }
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You can only use this command as a player!");
        }
        return true;
    }
}
