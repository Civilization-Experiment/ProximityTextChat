package com.rift.proximityTextChat.commands;

import com.github.puregero.multilib.MultiLib;
import com.rift.proximityTextChat.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadPTCConfig implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String successMessage = Config.load();
        Bukkit.broadcast(Component.text(successMessage), "ptc.use");
        Bukkit.broadcast(Component.text("New content: " + Config.getCurrentSettings().toString()), "ptc.use");
        MultiLib.notify("pvp-toggle:reload-config", "");
        return true;
    }
}
