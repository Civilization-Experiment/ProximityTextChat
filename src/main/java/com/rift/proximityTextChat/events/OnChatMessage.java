package com.rift.proximityTextChat.events;

import com.github.puregero.multilib.MultiLib;
import com.rift.proximityTextChat.Config;
import com.rift.proximityTextChat.renderer.TextComponentNodeRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class OnChatMessage implements Listener {
    private static final Logger logger = LoggerFactory.getLogger(OnChatMessage.class);

    @EventHandler
    public void onChatMessage(AsyncChatEvent e) {
//        TextComponent formattedOriginalMessage = formatMessage((TextComponent) e.message());
        TextComponent formattedOriginalMessage = ((TextComponent) e.message());

        // User can type blank message due to formatter, cancel them.
//        if (formattedOriginalMessage.content().isBlank()) {
//            logger.info("was empty: {}", formattedOriginalMessage.content());
//            e.setCancelled(true);
//            return;
//        }

        // Operators get to chat to everyone in a funny format
        if (e.getPlayer().isOp()) {
            e.renderer((sender, displayName, message, receiver) ->
                    Component.text()
                            .append(Component.text("[OP] ", NamedTextColor.YELLOW, TextDecoration.BOLD))
                            .append(displayName.colorIfAbsent(NamedTextColor.YELLOW))
                            .append(Component.text(" » ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                            .append(formattedOriginalMessage.colorIfAbsent(NamedTextColor.WHITE))
                            .build()
            );
            return;
        }

        e.setCancelled(true);
        Player sender = e.getPlayer();
        TextComponent formattedMessageUsers = Component.text()
                .append(sender.displayName().colorIfAbsent(NamedTextColor.WHITE))
                .append(Component.text(" » ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .append(e.message().color(NamedTextColor.GRAY))
                .build();
        TextComponent formattedMessageOperators = Component.text()
                .append(sender.displayName().colorIfAbsent(TextColor.color(0x6d6d6d)))
                .append(Component.text(" » ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .append(e.message().color(TextColor.color(0x6d6d6d)))
                .decorate(TextDecoration.ITALIC)
                .build();

        double cutoffDistanceSquared = Config.getCurrentSettings().getChatRange() * Config.getCurrentSettings().getChatRange();
        AtomicInteger receivers = new AtomicInteger();

        MultiLib.getAllOnlinePlayers().forEach(receiver -> {
            boolean inRange = sender.getLocation().distanceSquared(receiver.getLocation()) < cutoffDistanceSquared;

            if (inRange) {
                // All players in range see the original message
                receiver.sendMessage(formattedMessageUsers);
                receivers.getAndIncrement();
            } else if (receiver.isOp()) {
                // All ops (even if not in range) see a special grayed out message
                receiver.sendMessage(formattedMessageOperators);
            }

            // Normal players not in range don't see anything
        });

        logger.info("{} (seen by {}) » {}", e.getPlayer().displayName(), receivers, ((TextComponent) e.message()).content());
    }

    // Parse Markdown features (bold, italics, underline, strikethrough)
    private TextComponent formatMessage(TextComponent original) {
        Parser parser = Parser.builder().build();
        Node message = parser.parse(original.content());

        var renderer = new TextComponentNodeRenderer();
        renderer.render(message);

        return renderer.toComponent();
    }
}
