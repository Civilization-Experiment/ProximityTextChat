package com.rift.proximityTextChat.events;

import com.rift.proximityTextChat.commands.ConversationHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerLeave implements Listener {
    @EventHandler
    public void onChatMessage(PlayerQuitEvent e) {
        ConversationHandler.clearConversationsOf(e.getPlayer());
    }
}
