package com.rift.proximityTextChat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LocalChatCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender _sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(_sender instanceof Player sender)) {
            _sender.sendMessage("You cannot run this command as a non-player!");
            return true;
        }

        ConversationHandler.togglePlayerLocalChatting(sender);
        if (ConversationHandler.isPlayerLocalChatting(sender)) {
            sender.sendMessage(Component.text(
                    ">> You are now in local chat. You will only be able to see messages in range and only your private messages.",
                    NamedTextColor.YELLOW));
        } else {
            sender.sendMessage(Component.text(
                    ">> You are now in global (moderator) chat. You will now see all messages and all private messages.",
                    NamedTextColor.YELLOW));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
