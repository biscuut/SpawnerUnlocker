package codes.biscuit.spawnerunlocker.utils;

import codes.biscuit.spawnerunlocker.SpawnerUnlocker;
import codes.biscuit.spawnerunlocker.timers.SaveTimer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigUtils {

    private SpawnerUnlocker main;
    private File spawnerFile;
    private YamlConfiguration spawnerConfig;
    private HashMap<UUID, Integer> playerLevels = new HashMap<>();
    private BukkitTask saveTask;

    public ConfigUtils(SpawnerUnlocker main) {
        spawnerFile = new File("plugins/SpawnerUnlocker/levels.yml");
        this.main = main;
    }

    public void startSpawnerConfig() {
        if (!this.spawnerFile.exists()) {
            try {
                this.spawnerFile.createNewFile();
                spawnerConfig = YamlConfiguration.loadConfiguration(this.spawnerFile);
                spawnerConfig.createSection("levels");
                spawnerConfig.save(this.spawnerFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            spawnerConfig = YamlConfiguration.loadConfiguration(this.spawnerFile);
            Set<String> uuidStrings = spawnerConfig.getConfigurationSection("levels").getKeys(false);
            for (String uuidString : uuidStrings) {
                UUID uuid = UUID.fromString(uuidString);
                int level = spawnerConfig.getInt("levels." + uuidString);
                playerLevels.put(uuid, level);
            }
        }
        saveTask = new SaveTimer(main).runTaskTimer(main, getSaveInterval(), getSaveInterval());
    }

    public File getSpawnerFile() {
        return spawnerFile;
    }

    public YamlConfiguration getSpawnerConfig() {
        return spawnerConfig;
    }

    public HashMap<UUID, Integer> getPlayerLevels() {
        return playerLevels;
    }

    public void addPlayerLevels(Player p, int levels) {
        if (playerLevels.containsKey(p.getUniqueId())) {
            playerLevels.put(p.getUniqueId(), playerLevels.get(p.getUniqueId())+levels);
            spawnerConfig.set("levels."+p.getUniqueId(), playerLevels.get(p.getUniqueId()));
        } else {
            playerLevels.put(p.getUniqueId(), getDefaultLevel()+levels);
            spawnerConfig.set("levels."+p.getUniqueId(), getDefaultLevel()+levels);
        }
    }

    public void setPlayerLevel(Player p, int level) {
        playerLevels.put(p.getUniqueId(), level);
        spawnerConfig.set("levels."+p.getUniqueId(), level);
    }

    public void restartSaveTask() {
        saveTask.cancel();
        saveTask = new SaveTimer(main).runTaskTimer(main, getSaveInterval(), getSaveInterval());
    }

    public HashMap<Integer, HashMap<ArrayList<EntityType>, Integer>> getMobList() {
        HashMap<Integer, HashMap<ArrayList<EntityType>, Integer>> mobList = new HashMap<>();
        int spawnerLevel;
        for(String index : main.getConfig().getConfigurationSection("spawners").getKeys(false)) {
            ArrayList<EntityType> spawners = new ArrayList<>();
            for (String currentSpawner : main.getConfig().getStringList("spawners."+index+".mobs")) {
                try {
                    spawners.add(EntityType.valueOf(currentSpawner));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            spawnerLevel = main.getConfig().getInt("spawners." + index + ".levels");
            if (spawnerLevel == 0) {
                continue;
            }
            HashMap<ArrayList<EntityType>, Integer> spawnerProperties = new HashMap<>();
            spawnerProperties.put(spawners, spawnerLevel);
            try {
                mobList.put(Integer.valueOf(index), spawnerProperties);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        ArrayList<EntityType> spawners = new ArrayList<>();
        for (String currentSpawner : main.getConfig().getStringList("default-spawners")) {
            try {
                spawners.add(EntityType.valueOf(currentSpawner));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        HashMap<ArrayList<EntityType>, Integer> spawnerProperties = new HashMap<>();
        spawnerProperties.put(spawners, 0);
        try {
            mobList.put(0, spawnerProperties);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mobList;
    }

    private long getSaveInterval() {
        return 20L*main.getConfig().getInt("save-interval");
    }

    public String getNoPermissionMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.no-permission-command"));
    }

    public String getNoPermissionPlace() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.no-permission-place"));
    }

    public String getNoPermissionPlaceLevel(int level, EntityType spawnerType) {
        String spawnerText = spawnerType.toString().toLowerCase().replace("_", " ");
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.no-permission-place-level").replace("{level}", String.valueOf(level))
                .replace("{spawner}", String.valueOf(spawnerText)));
    }

    public String getLevelCheckMessage(int level) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.level").replace("{level}", String.valueOf(level)));
    }

    public String getNotEnoughLevelsMessage(int nextLevel, int nextLevelXPRequirement, int playerXPLevels) {
        int difference = nextLevelXPRequirement - playerXPLevels;
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.not-enough-levels").replace("{nextlevelxp}", String.valueOf(nextLevelXPRequirement))
                .replace("{xpremaining}", String.valueOf(difference)).replace("{nextlevel}", String.valueOf(nextLevel)));
    }

    public String getMaxLevelMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.max-level"));
    }

    public String getNextLevelMessage(int level) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.next-level").replace("{level}", String.valueOf(level))
                .replace("{previouslevel}", String.valueOf(level-1)));
    }

    public int getDefaultLevel() {
        return main.getConfig().getInt("default-level");
    }
}
