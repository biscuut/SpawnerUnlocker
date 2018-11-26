package codes.biscuit.spawnerunlocker.timers;

import codes.biscuit.spawnerunlocker.SpawnerUnlocker;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class SaveTimer extends BukkitRunnable {

    private SpawnerUnlocker main;

    public SaveTimer(SpawnerUnlocker main) {
        this.main = main;
    }

    @Override
    public void run() {
        try {
            main.getConfigUtils().getSpawnerConfig().save(main.getConfigUtils().getSpawnerFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
