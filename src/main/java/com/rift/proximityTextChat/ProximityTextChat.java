package com.rift.proximityTextChat;

import com.github.puregero.multilib.MultiLib;
import com.rift.proximityTextChat.commands.*;
import com.rift.proximityTextChat.events.OnChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class ProximityTextChat extends JavaPlugin {
    private static final Logger logger = LoggerFactory.getLogger(ProximityTextChat.class);

    @Override
    public void onEnable() {
        Config.load(getDataFolder());

        Bukkit.getPluginManager().registerEvents(new OnChatMessage(), this);

        CommandRegistrar.register(
                this,
                "reloadPTC",
                "Reloads PTC plugin config file.",
                "/reloadPTC",
                List.of(),
                "ptc.use",
                "§cYou do not have permission to use this command."
        ).setExecutor(new ReloadPTCConfig());

        CommandRegistrar.register(
                this,
                "msg",
                "Whisper to another player",
                "/msg <player> <message>",
                List.of("w", "whisper", "message", "tell", "t"),
                "ptc.msg",
                "§cYou do not have permission to use this command."
        ).setExecutor(new MsgCommand());

        CommandRegistrar.register(
                this,
                "localchat",
                "Toggle local chat",
                "/localchat",
                List.of("lc"),
                "ptc.use",
                "§cYou do not have permission to use this command."
        ).setExecutor(new LocalChatCommand());

        CommandRegistrar.register(
                this,
                "reply",
                "Reply to whisper",
                "/reply <message>",
                List.of("reply", "r"),
                "ptc.msg",
                "§cYou do not have permission to use this command."
        ).setExecutor(new ReplyCommand());

        ConversationHandler.registerIPCHandlers(this);
        MultiLib.onString(this, "rift.ptc:reload-config", data -> {
            logger.info("Received message to reload config for PTC plugin.");
            Config.load();
        });
    }
}
