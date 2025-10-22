package com.rift.proximityTextChat.commands;

import com.github.puregero.multilib.MultiLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MsgCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender _sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(_sender instanceof Player sender)) {
            _sender.sendMessage(Component.text("This command cannot be run from the console."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Component.text("Invalid command. Usage: /msg <player> <message>", NamedTextColor.RED));
            return true;
        }

        String rawMessage = args.length == 1
                ? ""
                : String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (rawMessage.isBlank()) {
            sender.sendMessage(Component.text("Message cannot be blank. Usage: /msg <player> <message>", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Player does not exist. Usage: /msg <player> <message>", NamedTextColor.RED));
            return true;
        }

        ConversationHandler.finallyWhisper(sender, target, rawMessage);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            String querySelector = args[0];
            Stream<String> onlinePlayers = MultiLib.getAllOnlinePlayers().stream().map(Player::getName);

            return onlinePlayers
                    .filter(playerName -> playerName != null && playerName.toLowerCase().contains(querySelector))
                    .toList();
        }

        return List.of();
    }
}
