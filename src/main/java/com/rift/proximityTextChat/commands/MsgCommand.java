package com.rift.proximityTextChat.commands;

import com.github.puregero.multilib.MultiLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class MsgCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 0) {
            sender.sendMessage(Component.text("/msg <player> [message]", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(sender instanceof Player msger && target != null) {
            TextComponent formattedOriginalMessage = formateMsg(args);

            TextComponent MsgforSender  = Component.text()
                    .append(Component.text("You", NamedTextColor.WHITE))
                    .append(Component.text(" » ", NamedTextColor.DARK_BLUE, TextDecoration.BOLD))
                    .append(target.displayName().colorIfAbsent(NamedTextColor.WHITE))
                    .append(Component.text(": ", NamedTextColor.DARK_BLUE, TextDecoration.BOLD))
                    .append(formattedOriginalMessage.color(NamedTextColor.BLUE))
                    .build();

            TextComponent MsgforTarget  = Component.text()
                    .append(msger.displayName().colorIfAbsent(NamedTextColor.WHITE))
                    .append(Component.text(" » ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                    .append(Component.text("You", NamedTextColor.WHITE))
                    .append(Component.text(": ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                    .append(formattedOriginalMessage.color(NamedTextColor.BLUE))
                    .build();

            TextComponent MsgforOp  = Component.text()
                    .append(msger.displayName().colorIfAbsent(NamedTextColor.WHITE))
                    .append(Component.text(" » ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                    .append(target.displayName().colorIfAbsent(NamedTextColor.WHITE))
                    .append(Component.text(": ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                    .append(formattedOriginalMessage.color(NamedTextColor.BLUE))
                    .build();

            msger.sendMessage(MsgforSender);
            target.sendMessage(MsgforTarget);

            Bukkit.getOperators().forEach(op -> {
                Player opPlayer = op.getPlayer();
                if (opPlayer != null)
                    opPlayer.sendMessage(MsgforOp);
            });
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return MultiLib.getAllOnlinePlayers().stream().map(Player::getName).toList();
        }

        return List.of();
    }

    public TextComponent formateMsg(String[] args) {
        if (args.length <= 1) return Component.text().build();

        TextComponent.Builder builder = Component.text(); // mutable builder

        // Start at index 1 to skip the first element
        for (int i = 1; i < args.length; i++) {
            builder.append(Component.text(args[i], NamedTextColor.WHITE));

            // Add a space after each word except the last
            if (i < args.length - 1) {
                builder.append(Component.text(" "));
            }
        }

        return builder.build();
    }


}
