package codes.biscuit.spawnerunlocker.utils;

import codes.biscuit.spawnerunlocker.SpawnerUnlocker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class Utils {

    private SpawnerUnlocker main;
    private int maxIndex;
    public ArrayList<UUID> bypassList = new ArrayList<>();

    public Utils(SpawnerUnlocker main) {
        this.main = main;
    }

    public int getPlayerLevel(Player p) {
        return main.getConfigUtils().getPlayerLevels().getOrDefault(p.getUniqueId(), main.getConfigUtils().getDefaultLevel());
    }

    private int getMaxIndex() {
        if (maxIndex == 0 && main.getConfigUtils().getMobList().size() != 0) {
            for (Integer spawnerLevel : main.getConfigUtils().getMobList().keySet()) {
                if (spawnerLevel > maxIndex) {
                    maxIndex = spawnerLevel;
                }
            }
        }
        return maxIndex;
    }

    public boolean playerIsMax(Player p) {
        return getPlayerLevel(p) >= getMaxIndex();
    }

    private int getUnlockIndex(EntityType searchType) {
        for (Map.Entry<Integer, HashMap<ArrayList<EntityType>, Double>> spawnerProperties : main.getConfigUtils().getMobList().entrySet()) {
            for (ArrayList<EntityType> spawnerLists : spawnerProperties.getValue().keySet()) {
                Set<Map.Entry<Integer, HashMap<ArrayList<EntityType>, Double>>> test =  main.getConfigUtils().getMobList().entrySet();
                if (spawnerLists.contains(searchType)) {
                    return spawnerProperties.getKey();
                }
            }
        }
        return 0;
    }

    public boolean canPlaceMob(Player p, EntityType type) {
        for (HashMap<ArrayList<EntityType>, Double> values : main.getConfigUtils().getMobList().values()) {
            for (ArrayList<EntityType> currentList : values.keySet()) {
                if (currentList.contains(type)) {
                    if (getPlayerLevel(p) >= getUnlockIndex(type)) {
                        return true;
                    } else {
                        if (!main.getConfigUtils().getInsufficientLevelMessage(getUnlockIndex(type), type).equals("")) {
                            p.sendMessage(main.getConfigUtils().getInsufficientLevelMessage(getUnlockIndex(type), type));
                        }
                        return false;
                    }
                }
            }
        }

        if (!main.getConfigUtils().getCannotPlaceMessage().equals("")) {
            p.sendMessage(main.getConfigUtils().getCannotPlaceMessage());
        }
        return false;
    }

    public Material getDefaultConfirmMaterial() {
        Material mat;
        if (Bukkit.getVersion().contains("1.13")) {
            mat = Material.valueOf("GREEN_WOOL");
        } else {
            mat = Material.valueOf("WOOL");
        }
        return mat;
    }

    public Material getDefaultCancelMaterial() {
        Material mat;
        if (Bukkit.getVersion().contains("1.13")) {
            mat = Material.valueOf("RED_WOOL");
        } else {
            mat = Material.valueOf("WOOL");
        }
        return mat;
    }

    public double getCurrencyAmount(Player p) {
        switch (main.getConfigUtils().getCurrency()) {
            case "XP":
                return p.getTotalExperience();
            case "MONEY":
                return main.getVaultHook().getMoney(p);
            default:
                return p.getLevel();
        }
    }

    public void removeCurrency(Player p, double amount) {
        switch (main.getConfigUtils().getCurrency()) {
            case "XP":
                p.setTotalExperience((int) (p.getTotalExperience() - amount));
                break;
            case "MONEY":
                main.getVaultHook().removeMoney(p, amount);
                break;
            default:
                p.setLevel((int) (p.getLevel() - amount));
                break;
        }
    }
}
