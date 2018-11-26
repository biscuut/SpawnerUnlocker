package codes.biscuit.spawnerunlocker;

import codes.biscuit.spawnerunlocker.commands.LevelCheckCommand;
import codes.biscuit.spawnerunlocker.commands.RankupCommand;
import codes.biscuit.spawnerunlocker.commands.SpawnerUnlockerCommand;
import codes.biscuit.spawnerunlocker.events.PlayerEvents;
import codes.biscuit.spawnerunlocker.utils.ConfigUtils;
import codes.biscuit.spawnerunlocker.utils.Utils;
import de.dustplanet.util.SilkUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnerUnlocker extends JavaPlugin {

    private ConfigUtils configUtils = new ConfigUtils(this);
    private Utils utils = new Utils(this);
    private SilkUtil silkUtil;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(this), this);
        getCommand("checklevel").setExecutor(new LevelCheckCommand(this));
        getCommand("rankup").setExecutor(new RankupCommand(this));
        getCommand("spawnerunlocker").setExecutor(new SpawnerUnlockerCommand(this));
        getCommand("spawnerunlocker").setTabCompleter(SpawnerUnlockerCommand.TAB_COMPLETER);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        configUtils.startSpawnerConfig();
        silkUtil = SilkUtil.hookIntoSilkSpanwers();
    }

    @Override
    public void onDisable() {
        if (configUtils.getSpawnerConfig() != null) {
            try {
                configUtils.getSpawnerConfig().save(configUtils.getSpawnerFile());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public ConfigUtils getConfigUtils() {
        return configUtils;
    }

    public Utils getUtils() {
        return utils;
    }

    public SilkUtil getSilkUtil() {
        return silkUtil;
    }
}
