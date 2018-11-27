package codes.biscuit.spawnerunlocker.utils;

import codes.biscuit.spawnerunlocker.SpawnerUnlocker;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CommandAliasManager {

    private Logger logger;
    private Server server;

    public CommandAliasManager(SpawnerUnlocker plugin) {
        logger = plugin.getLogger();
        server = plugin.getServer();
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, Command> getCommandMap() throws Exception {
        Method cmp = server.getClass().getMethod("getCommandMap");
        SimpleCommandMap scmp = (SimpleCommandMap)cmp.invoke(server);
        if (scmp == null) {
            return new HashMap<>();
        }
        Field field = scmp.getClass().getDeclaredField("knownCommands");
        field.setAccessible(true);
        HashMap<String, Command> map = (HashMap<String, Command>)field.get(scmp);
        field.setAccessible(false);
        return map;
    }

    public void setAdditionalAliases(Command command, List<String> aliases) {
        Map<String, Command> map = new HashMap<>();
        try {
            map = getCommandMap();
        } catch (Exception ex) {
            logger.severe(ChatColor.RED + "Error, unable to set command aliases!");
        }
        for (String alias : aliases) {
            map.put(alias.toLowerCase(), command);
        }
    }

}
