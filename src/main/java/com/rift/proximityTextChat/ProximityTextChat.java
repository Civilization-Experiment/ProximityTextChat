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
                List.of(new String[]{}),
                "ptc.use",
                "§cYou do not have permission to use this command."
        ).setExecutor(new ReloadPTCConfig());

        MsgCommand msgcmd = new MsgCommand();
        CommandRegistrar.register(
                this,
                "msg",
                "Message Command",
                "/msg <player> [message]",
                java.util.Arrays.asList("w", "whisper", "message"),
                "ptc.msg",
                "§cYou do not have permission to use this command."
        ).setExecutor(msgcmd);

        MultiLib.onString(this, "rift.ptc:reload-config", data -> {
            logger.info("Received message to reload config for PTC plugin.");
            Config.load();
        });
    }
}
