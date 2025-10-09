package com.rift.proximityTextChat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileHelper {
    public static final Logger logger = LoggerFactory.getLogger(FileHelper.class);

    public static String readFileCompletely(String filepath) {
        try {
            File newFile = new File(filepath);

            BufferedReader reader = new BufferedReader(new FileReader(newFile));
            StringBuilder contents = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                contents.append(line);
            }

            return contents.toString();
        }
        catch (Exception e) {
            logger.trace(e.getMessage(), e);
        }

        return null;
    }

    public static void writeFileCompletely(String filepath, String data) {
        try {
            FileWriter writer = new FileWriter(filepath);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            logger.trace(e.getMessage(), e);
        }
    }
}
