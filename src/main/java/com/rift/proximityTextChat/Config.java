package com.rift.proximityTextChat;

import com.github.puregero.multilib.MultiLib;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static final String SETTINGS_FILE_VERSION = "1";

    public static File SETTINGS_FILE_PATH;
    public static final String SETTINGS_FILE_NAME = "com.rift-events.proximity-text-chat.config.json";
    public static final String OLD_SETTINGS_FILE_NAME = "com.rift-events.proximity-text-chat.old.json";

    @Getter @Setter
    public static class Schema {
        private String version = null;
        private double chatRange = 100.0;

        private LanguageDefinitions lang = new LanguageDefinitions();

        @Override
        public String toString() {
            return "Schema{" +
                    "version='" + version + '\'' +
                    ", chatRange=" + chatRange +
                    '}';
        }
    }

    @Getter
    private static Schema currentSettings;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load(File filepath) {
        if (!filepath.exists()) {
            try {
                filepath.mkdirs();
            } catch (SecurityException e) {
                logger.trace(e.getMessage(), e);
            }
        }

        SETTINGS_FILE_PATH = filepath;
        load();
    }

    public static String load() {
        logger.info("Loading settings...");

        final var file = new File(Path.of(SETTINGS_FILE_PATH.toString(), SETTINGS_FILE_NAME).toString());
        logger.info("Settings filepath: {}", file.getAbsolutePath());

        if (file.exists() && !file.isFile()) {
            logger.error("Settings filename is not a file!");
            return "Settings filename is not a file!";
        }

        if (!file.exists()) {
            currentSettings = new Schema();
            currentSettings.version = SETTINGS_FILE_VERSION;
            save();
            logger.info("Settings file did not exist, generated default and saved.");
            return "Settings file did not exist, generated default and saved.";
        }

        currentSettings = GSON.fromJson(FileHelper.readFileCompletely(file.getAbsolutePath()), Schema.class);
        logger.info("Config file read:");
        logger.info(currentSettings.toString());
        if (currentSettings.version == null || !currentSettings.version.equals(SETTINGS_FILE_VERSION)) {
            FileHelper.writeFileCompletely(new File(Path.of(SETTINGS_FILE_PATH.toString(), OLD_SETTINGS_FILE_NAME).toString()).getAbsolutePath(), GSON.toJson((currentSettings)));
            currentSettings = new Schema();
            currentSettings.version = SETTINGS_FILE_VERSION;
            save();

            logger.info("Settings file out of date. Cloned existing file to 'com.discord-auth.old.json' and created new default and saved.");
            return "Settings file out of date. Cloned existing file to 'com.discord-auth.old.json' and created new default and saved.";
        }

        logger.info("Settings file exists, successfully loaded.");
        return "Settings file exists, successfully loaded.";
    }

    public static void save() {
        final var file = new File(Path.of(SETTINGS_FILE_PATH.toString(), SETTINGS_FILE_NAME).toString());
        FileHelper.writeFileCompletely(file.getAbsolutePath(), GSON.toJson(currentSettings));
        MultiLib.notify("rift.ptc:reload-config", "");
    }
}
