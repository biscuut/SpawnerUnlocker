package codes.biscuit.spawnerunlocker.events;

import codes.biscuit.spawnerunlocker.SpawnerUnlocker;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerPlaceEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

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

    @EventHandler
    public void onGUIClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null &&
                e.getClickedInventory().getName().equals(main.getConfigUtils().getGUITitle())) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().contains(main.getConfigUtils().getConfirmName())) {
                e.getWhoClicked().closeInventory();
                if (!main.getUtils().playerIsMax(p)) {
                    double nextLevelCost = new ArrayList<>(main.getConfigUtils().getMobList().get(main.getUtils().getPlayerLevel(p) + 1).values()).get(0);
                    if (main.getUtils().getCurrencyAmount(p) >= nextLevelCost) {
                        main.getUtils().removeCurrency(p, nextLevelCost);
                        main.getConfigUtils().addPlayerLevels(p, 1);
                        if (!main.getConfigUtils().getRankupMessage(main.getUtils().getPlayerLevel(p)).equals("")) {
                            p.sendMessage(main.getConfigUtils().getRankupMessage(main.getUtils().getPlayerLevel(p)));
                        }
                        if (main.getConfigUtils().confirmSoundEnabled()) {
                            p.playSound(p.getLocation(), main.getConfigUtils().getConfirmSound(), main.getConfigUtils().getConfirmSoundVolume(), main.getConfigUtils().getConfirmSoundPitch());
                        }
                    } else {
                        if (!main.getConfigUtils().getInsufficientCurrencyMessage(main.getUtils().getPlayerLevel(p) + 1, nextLevelCost, main.getUtils().getCurrencyAmount(p)).equals("")) {
                            p.sendMessage(main.getConfigUtils().getInsufficientCurrencyMessage(main.getUtils().getPlayerLevel(p) + 1, nextLevelCost, main.getUtils().getCurrencyAmount(p)));
                        }
                        if (main.getConfigUtils().cancelSoundEnabled()) {
                            p.playSound(p.getLocation(), main.getConfigUtils().getCancelSound(), main.getConfigUtils().getCancelSoundVolume(), main.getConfigUtils().getCancelSoundPitch());
                        }
                    }
                } else {
                    if (!main.getConfigUtils().getMaxLevelMessage().equals("")) {
                        p.sendMessage(main.getConfigUtils().getMaxLevelMessage());
                    }
                    if (main.getConfigUtils().cancelSoundEnabled()) {
                        p.playSound(p.getLocation(), main.getConfigUtils().getCancelSound(), main.getConfigUtils().getCancelSoundVolume(), main.getConfigUtils().getCancelSoundPitch());
                    }
                }
            } else if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().contains(main.getConfigUtils().getCancelName())) {
                e.getWhoClicked().closeInventory();
                if (!main.getConfigUtils().getGUICancelMessage().equals("")) {
                    p.sendMessage(main.getConfigUtils().getGUICancelMessage());
                }
                if (main.getConfigUtils().cancelSoundEnabled()) {
                    p.playSound(p.getLocation(), main.getConfigUtils().getCancelSound(), main.getConfigUtils().getCancelSoundVolume(), main.getConfigUtils().getCancelSoundPitch());
                }
            }
        }
    }
}
