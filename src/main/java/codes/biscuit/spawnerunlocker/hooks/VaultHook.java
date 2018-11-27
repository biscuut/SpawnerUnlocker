package codes.biscuit.spawnerunlocker.hooks;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public class VaultHook {

    private Economy economy;

    public VaultHook setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }
        return this;
    }

    public double getMoney(Player p) {
        return economy.getBalance(p);
    }

    public boolean hasMoney(Player p, double money) {
        return economy.has(p, money);
    }

    public void removeMoney(Player p, double money) {
        economy.withdrawPlayer(p, money);
    }
}
