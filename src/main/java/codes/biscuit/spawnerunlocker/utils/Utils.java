package codes.biscuit.spawnerunlocker.utils;

import codes.biscuit.spawnerunlocker.SpawnerUnlocker;
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
        for (Map.Entry<Integer, HashMap<ArrayList<EntityType>, Integer>> spawnerProperties : main.getConfigUtils().getMobList().entrySet()) {
            for (ArrayList<EntityType> spawnerLists : spawnerProperties.getValue().keySet()) {
                Set<Map.Entry<Integer, HashMap<ArrayList<EntityType>, Integer>>> test =  main.getConfigUtils().getMobList().entrySet();
                if (spawnerLists.contains(searchType)) {
                    return spawnerProperties.getKey();
                }
            }
        }
        return 0;
    }

    public boolean canPlaceMob(Player p, EntityType type) {
        for (HashMap<ArrayList<EntityType>, Integer> values : main.getConfigUtils().getMobList().values()) {
            for (ArrayList<EntityType> currentList : values.keySet()) {
                if (currentList.contains(type)) {
                    if (getPlayerLevel(p) >= getUnlockIndex(type)) {
                        return true;
                    } else {
                        if (!main.getConfigUtils().getNoPermissionPlaceLevel(getUnlockIndex(type), type).equals("")) {
                            p.sendMessage(main.getConfigUtils().getNoPermissionPlaceLevel(getUnlockIndex(type), type));
                        }
                        return false;
                    }
                }
            }
        }

        if (!main.getConfigUtils().getNoPermissionPlace().equals("")) {
            p.sendMessage(main.getConfigUtils().getNoPermissionPlace());
        }
        return false;
    }
}
