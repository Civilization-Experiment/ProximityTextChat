package com.rift.proximityTextChat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReplyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender _sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(_sender instanceof Player sender)) {
            _sender.sendMessage(Component.text("This command cannot be run from the console."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Component.text("Invalid command. Usage: /reply <message>", NamedTextColor.RED));
            return true;
        }

        String rawMessage = String.join(" ", args);
        if (rawMessage.isBlank()) {
            sender.sendMessage(Component.text("Message cannot be blank. Usage: /reply <message>", NamedTextColor.RED));
            return true;
        }

        ConversationHandler.ConversationReceiver potentialReceiver = ConversationHandler.getConversationReceiver(sender);
        if (potentialReceiver.isOffline()) {
            sender.sendMessage(Component.text("Cannot reply: player in your conversation left", NamedTextColor.YELLOW));
            return true;
        }

        Player target = potentialReceiver.sender();
        if (target == null) {
            sender.sendMessage(Component.text("Cannot reply: you are not in a conversation with any players", NamedTextColor.YELLOW));
            return true;
        }

        ConversationHandler.finallyWhisper(sender, target, rawMessage);
        return true;
    }
}
