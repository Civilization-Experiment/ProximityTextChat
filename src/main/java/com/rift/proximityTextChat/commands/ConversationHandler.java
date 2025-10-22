package com.rift.proximityTextChat.commands;

import com.github.puregero.multilib.MultiLib;
import com.rift.proximityTextChat.renderer.TextComponentNodeRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class ConversationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConversationHandler.class);

    public record ConversationReceiver(boolean isOffline, Player sender) {}

    private static final HashMap<Player, Player> conversations = new HashMap<>();

    public static void finallyWhisper(Player sender, Player target, String rawMessage) {
        TextComponent formattedMessage = formatMsg(rawMessage);

        TextComponent messageToRecipients = Component.text()
                .append(sender.name().color(NamedTextColor.AQUA))
                .append(Component.text(" → ", NamedTextColor.GRAY))
                .append(target.displayName().color(NamedTextColor.AQUA))
                .append(Component.text(" » ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .append(formattedMessage.color(NamedTextColor.GRAY))
                .build();

        TextComponent messageToOperators = Component.text()
                .append(sender.name().color(NamedTextColor.DARK_AQUA))
                .append(Component.text(" → ", TextColor.color(0x6d6d6d)))
                .append(target.displayName().color(NamedTextColor.DARK_AQUA))
                .append(Component.text(" » ",  NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .append(formattedMessage.color(TextColor.color(0x6d6d6d)).decorate(TextDecoration.ITALIC))
                .build();

        setPlayerConversation(sender, target);
        sender.sendMessage(messageToRecipients);
        target.sendMessage(messageToRecipients);
        logger.info("{} whispers to {} » {}", sender.getName(), target.getName(), rawMessage);

        MultiLib.getAllOnlinePlayers().stream()
                .filter(player -> player.isOp() && player != sender && player != target)
                .forEach(player -> player.sendMessage(messageToOperators));
    }

    public static TextComponent formatMsg(String original) {
        Parser parser = Parser.builder()
                .enabledBlockTypes(Set.of())
                .build();
        Node message = parser.parse(original);

        var renderer = new TextComponentNodeRenderer();
        renderer.render(message);

        return renderer.toComponent();
    }

    public static void registerIPCHandlers(Plugin plugin) {
        MultiLib.onString(plugin, "rift.ptc:setPlayerConversation", data -> {
            String[] dataArray = data.split("\\|");
            Player sender = getPlayerFromUUID(dataArray[0]);
            Player target = getPlayerFromUUID(dataArray[1]);
            conversations.put(target, sender);
        });

        MultiLib.onString(plugin, "rift.ptc:clearConversationsOf", data -> conversations.remove(getPlayerFromUUID(data)));
    }

    private static Player getPlayerFromUUID(String uuid) {
        return getPlayerFromUUID(UUID.fromString(uuid));
    }

    private static Player getPlayerFromUUID(UUID uuid) {
        return MultiLib.getAllOnlinePlayers().stream().filter(player -> player.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public static void setPlayerConversation(Player sender, Player target) {
        MultiLib.notify("rift.ptc:setPlayerConversation", sender.getUniqueId() + "|" + target.getUniqueId());
        conversations.put(target, sender);
    }

    public static ConversationReceiver getConversationReceiver(Player player) {
        Player potentialReceiver = conversations.get(player);
        if (potentialReceiver == null) {
            return new ConversationReceiver(false, null);
        }

        if (!MultiLib.getAllOnlinePlayers().contains(potentialReceiver)) {
            return new ConversationReceiver(true, null);
        }

        return new ConversationReceiver(false, potentialReceiver);
    }

    public static void clearConversationsOf(Player sender) {
        MultiLib.notify("rift.ptc:clearConversationsOf", sender.getUniqueId().toString());
        conversations.remove(sender);
    }
}
