package codes.biscuit.spawnerunlocker.utils;

import codes.biscuit.spawnerunlocker.SpawnerUnlocker;
import codes.biscuit.spawnerunlocker.timers.SaveTimer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

    public HashMap<Integer, HashMap<ArrayList<EntityType>, Double>> getMobList() {
        HashMap<Integer, HashMap<ArrayList<EntityType>, Double>> mobList = new HashMap<>();
        double spawnerLevel;
        for(String index : main.getConfig().getConfigurationSection("spawners").getKeys(false)) {
            ArrayList<EntityType> spawners = new ArrayList<>();
            for (String currentSpawner : main.getConfig().getStringList("spawners."+index+".mobs")) {
                try {
                    spawners.add(EntityType.valueOf(currentSpawner));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            spawnerLevel = main.getConfig().getDouble("spawners." + index + ".cost");
            if (spawnerLevel == 0) {
                continue;
            }
            HashMap<ArrayList<EntityType>, Double> spawnerProperties = new HashMap<>();
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
        HashMap<ArrayList<EntityType>, Double> spawnerProperties = new HashMap<>();
        spawnerProperties.put(spawners, 0D);
        try {
            mobList.put(0, spawnerProperties);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mobList;
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

    private long getSaveInterval() {
        return 20L*main.getConfig().getInt("save-interval");
    }

    public int getDefaultLevel() {
        return main.getConfig().getInt("default-level");
    }

    public String getNoPermissionCommandMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.no-permission-command"));
    }

    public String getCannotPlaceMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.cannot-place"));
    }

    public String getInsufficientLevelMessage(int level, EntityType spawnerType) {
        String spawnerText = spawnerType.toString().toLowerCase().replace("_", " ");
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.insufficient-level").replace("{level}", String.valueOf(level))
                .replace("{spawner}", String.valueOf(spawnerText)));
    }

    public String getLevelCheckMessage(int level) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.level").replace("{level}", String.valueOf(level)));
    }

    public String getInsufficientCurrencyMessage(int nextLevel, double nextLevelCost, double playerCurrency) {
        double difference = nextLevelCost - playerCurrency;
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.insufficient-currency").replace("{nextlevelcost}", String.valueOf(nextLevelCost))
                .replace("{costremaining}", String.valueOf(difference)).replace("{nextlevel}", String.valueOf(nextLevel)));
    }

    public String getMaxLevelMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.max-level"));
    }

    public String getRankupMessage(int level) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.rankup").replace("{level}", String.valueOf(level))
                .replace("{previouslevel}", String.valueOf(level-1)));
    }

    public String getGUICancelMessage() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.gui-cancel"));
    }

    public boolean isGUIEnabled() { return main.getConfig().getBoolean("rankup-gui.enabled"); }

    public int getGUIRows() { return main.getConfig().getInt("rankup-gui.rows"); }

    public ItemStack getConfirmBlockItemStack() {
        String rawMaterial = main.getConfig().getString("rankup-gui.confirm-item");
        Material mat;
        if (rawMaterial.contains(":")) {
            String[] materialSplit = rawMaterial.split(":");
            try {
                mat = Material.valueOf(materialSplit[0]);
            } catch (IllegalArgumentException ex) {
                mat = main.getUtils().getDefaultConfirmMaterial();
                Bukkit.getLogger().severe("Your accept-block material is invalid!");
            }
            short damage;
            try {
                damage = Short.valueOf(materialSplit[1]);
            } catch (IllegalArgumentException ex) {
                damage = 0;
                Bukkit.getLogger().severe("Your accept-block damage is invalid!");
            }
            return new ItemStack(mat, 1, damage);
        } else {
            try {
                mat = Material.valueOf(rawMaterial);
            } catch (IllegalArgumentException ex) {
                mat = main.getUtils().getDefaultConfirmMaterial();
                Bukkit.getLogger().severe("Your accept-block material is invalid!");
            }
            return new ItemStack(mat, 1);
        }
    }

    public ItemStack getCancelBlockItemStack() {
        String rawMaterial = main.getConfig().getString("rankup-gui.cancel-item");
        Material mat;
        if (rawMaterial.contains(":")) {
            String[] materialSplit = rawMaterial.split(":");
            try {
                mat = Material.valueOf(materialSplit[0]);
            } catch (IllegalArgumentException ex) {
                mat = main.getUtils().getDefaultCancelMaterial();
                Bukkit.getLogger().severe("Your cancel-block material is invalid!");
            }
            short damage;
            try {
                damage = Short.valueOf(materialSplit[1]);
            } catch (IllegalArgumentException ex) {
                damage = 0;
                Bukkit.getLogger().severe("Your cancel-block damage is invalid!");
            }
            return new ItemStack(mat, 1, damage);
        } else {
            try {
                mat = Material.valueOf(rawMaterial);
            } catch (IllegalArgumentException ex) {
                mat = main.getUtils().getDefaultCancelMaterial();
                Bukkit.getLogger().severe("Your cancel-block material is invalid!");
            }
            return new ItemStack(mat, 1);
        }
    }

    public ItemStack getFillItemStack() {
        String rawMaterial = main.getConfig().getString("rankup-gui.fill-item");
        Material mat;
        if (rawMaterial.contains(":")) {
            String[] materialSplit = rawMaterial.split(":");
            try {
                mat = Material.valueOf(materialSplit[0]);
            } catch (IllegalArgumentException ex) {
                mat = Material.AIR;
                Bukkit.getLogger().severe("Your fill-block material is invalid!");
            }
            short damage;
            try {
                damage = Short.valueOf(materialSplit[1]);
            } catch (IllegalArgumentException ex) {
                damage = 0;
                Bukkit.getLogger().severe("Your fill-block damage is invalid!");
            }
            return new ItemStack(mat, 1, damage);
        } else {
            try {
                mat = Material.valueOf(rawMaterial);
            } catch (IllegalArgumentException ex) {
                mat = Material.AIR;
                Bukkit.getLogger().severe("Your fill-block material is invalid!");
            }
            return new ItemStack(mat, 1);
        }
    }

    public String getGUITitle() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("rankup-gui.title"));
    }

    public String getConfirmName() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("rankup-gui.confirm-name"));
    }

    public String getCancelName() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("rankup-gui.cancel-name"));
    }

    public String getFillName() {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("rankup-gui.fill-name"));
    }

    public List<String> getConfirmLoreRegular(int level, int nextLevel, double nextLevelCost) {
        List<String> uncolouredList = main.getConfig().getStringList("rankup-gui.confirm-lore-regular");
        List<String> colouredList = new ArrayList<>();
        for (String s : uncolouredList) {
            colouredList.add(ChatColor.translateAlternateColorCodes('&', s).replace("{level}", String.valueOf(level)).replace("{nextlevel}", String.valueOf(nextLevel)
            .replace("{nextlevelcost}", String.valueOf(nextLevelCost))));
        }
        return colouredList;
    }

    public List<String> getConfirmLoreMax(int level, int nextLevel) {
        List<String> uncolouredList = main.getConfig().getStringList("rankup-gui.confirm-lore-max");
        List<String> colouredList = new ArrayList<>();
        for (String s : uncolouredList) {
            colouredList.add(ChatColor.translateAlternateColorCodes('&', s).replace("{level}", String.valueOf(level)).replace("{nextlevel}", String.valueOf(nextLevel)));
        }
        return colouredList;
    }

    public List<String> getCancelLore() {
        List<String> uncolouredList = main.getConfig().getStringList("rankup-gui.cancel-lore");
        List<String> colouredList = new ArrayList<>();
        for (String s : uncolouredList) {
            colouredList.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return colouredList;
    }

    public List<String> getFillLore() {
        List<String> uncolouredList = main.getConfig().getStringList("rankup-gui.fill-lore");
        List<String> colouredList = new ArrayList<>();
        for (String s : uncolouredList) {
            colouredList.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return colouredList;
    }

    public List<String> getAliases() {
        return main.getConfig().getStringList("spawnerrankup-aliases");
    }

    public List<Integer> getConfirmSlots() {
        return main.getConfig().getIntegerList("rankup-gui.confirm-slots");
    }

    public List<Integer> getCancelSlots() {
        return main.getConfig().getIntegerList("rankup-gui.cancel-slots");
    }

    public boolean confirmSoundEnabled() {
        return main.getConfig().getBoolean("rankup-gui.confirm-sound-enabled");
    }

    public String getConfirmSound() {
        return main.getConfig().getString("rankup-gui.confirm-sound");
    }

    public float getConfirmSoundVolume() {
        return (float)main.getConfig().getDouble("rankup-gui.confirm-volume");
    }

    public float getConfirmSoundPitch() {
        return (float)main.getConfig().getDouble("rankup-gui.confirm-pitch");
    }

    public boolean cancelSoundEnabled() {
        return main.getConfig().getBoolean("rankup-gui.cancel-sound-enabled");
    }

    public String getCancelSound() {
        return main.getConfig().getString("rankup-gui.cancel-sound");
    }

    public float getCancelSoundVolume() {
        return (float)main.getConfig().getDouble("rankup-gui.cancel-volume");
    }

    public float getCancelSoundPitch() {
        return (float)main.getConfig().getDouble("rankup-gui.cancel-pitch");
    }

    public String getCurrency() {
        String currency = main.getConfig().getString("currency").toUpperCase();
        if (currency.equals("XPLEVELS") || currency.equals("MONEY") || currency.equals("XP")) {
            if (currency.equals("MONEY") && main.getVaultHook() == null) {
                return "XPLEVELS";
            }
            return currency;
        } else {
            return "XPLEVELS";
        }
    }
}
