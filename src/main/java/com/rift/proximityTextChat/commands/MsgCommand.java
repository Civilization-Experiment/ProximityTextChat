package com.rift.proximityTextChat.commands;

import com.github.puregero.multilib.MultiLib;
import com.rift.proximityTextChat.renderer.TextComponentNodeRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class MsgCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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

        TextComponent formattedMessage = formatMsg(rawMessage);

        TextComponent messageToRecipients = Component.text()
                .append(sender.name().color(NamedTextColor.AQUA))
                .append(Component.text(" → ", NamedTextColor.AQUA))
                .append(target.displayName().color(NamedTextColor.AQUA))
                .append(Component.text(" » ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .append(formattedMessage.color(NamedTextColor.GRAY))
                .build();

        TextComponent messageToOperators = Component.text()
                .append(sender.name().colorIfAbsent(NamedTextColor.DARK_AQUA))
                .append(Component.text(" → ", NamedTextColor.DARK_AQUA))
                .append(target.displayName().colorIfAbsent(NamedTextColor.DARK_AQUA))
                .append(Component.text(" » ",  NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .append(formattedMessage.color(TextColor.color(0x6d6d6d)).decorate(TextDecoration.ITALIC))
                .build();

        sender.sendMessage(messageToRecipients);
        target.sendMessage(messageToRecipients);

        Bukkit.getOperators().forEach(op -> {
            Player opPlayer = op.getPlayer();
            if (opPlayer != null && opPlayer != sender && opPlayer != target)
                opPlayer.sendMessage(messageToOperators);
        });

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

    public TextComponent formatMsg(String original) {
        Parser parser = Parser.builder()
                .enabledBlockTypes(Set.of())
                .build();
        Node message = parser.parse(original);

        var renderer = new TextComponentNodeRenderer();
        renderer.render(message);

        return renderer.toComponent();
    }
}
