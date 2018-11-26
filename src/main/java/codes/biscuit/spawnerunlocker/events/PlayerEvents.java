package codes.biscuit.spawnerunlocker.events;

import codes.biscuit.spawnerunlocker.SpawnerUnlocker;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerPlaceEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
public class PlayerEvents implements Listener {

    private SpawnerUnlocker main;

    public PlayerEvents(SpawnerUnlocker main) {
        this.main = main;
    }

    @EventHandler
    public void onSpawnerPlace(SilkSpawnersSpawnerPlaceEvent e) {
        if (!main.getUtils().bypassList.contains(e.getPlayer().getUniqueId())) {
            if (!main.getUtils().canPlaceMob(e.getPlayer(), EntityType.fromId(e.getEntityID()))) {
                e.setCancelled(true);
            }
        }
    }
}
