package com.rift.proximityTextChat;

import com.github.puregero.multilib.MultiLib;
import com.rift.proximityTextChat.commands.CommandRegistrar;
import com.rift.proximityTextChat.commands.MsgCommand;
import com.rift.proximityTextChat.commands.ReloadPTCConfig;
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
                "Message Command",
                "/msg <player> <message>",
                List.of("w", "whisper", "message", "tell"),
                "ptc.msg",
                "§cYou do not have permission to use this command."
        ).setExecutor(new MsgCommand());

        MultiLib.onString(this, "rift.ptc:reload-config", data -> {
            logger.info("Received message to reload config for PTC plugin.");
            Config.load();
        });
    }
}
