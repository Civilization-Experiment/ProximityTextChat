package com.rift.proximityTextChat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public class CommandRegistrar {
    public static PluginCommand register(
            JavaPlugin plugin,
            String name,
            String description,
            String usage,
            List<String> aliases,
            String permission,
            String permissionMessage) {
        PluginCommand existing = plugin.getCommand(name);
        if (existing != null)
            return existing;

        try {
            Constructor<PluginCommand> ctor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            ctor.setAccessible(true);
            PluginCommand cmd = ctor.newInstance(name, plugin);
            if (description != null)
                cmd.setDescription(description);
            if (usage != null)
                cmd.setUsage(usage);
            if (aliases != null)
                cmd.setAliases(aliases);
            if (permission != null)
                cmd.setPermission(permission);
            if (permissionMessage != null)
                cmd.permissionMessage(Component.text(permissionMessage).color(NamedTextColor.RED));

            CommandMap commandMap = getCommandMap();
            // Use plugin name as fallback label/namespace for registration collisions
            commandMap.register(plugin.getName().toLowerCase(), cmd);
            return cmd;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to dynamically register command '" + name + "'", e);
        }
    }

    private static CommandMap getCommandMap() throws Exception {
        Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        f.setAccessible(true);
        return (CommandMap) f.get(Bukkit.getServer());
    }
}
