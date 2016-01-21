/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kerbe_000
 */
public final class Config {

    public static final String EOL = System.lineSeparator();
    public static final String CONFIGURATION_FILE = "./app.cfg";

    public static String LANGUAGE;
    public static String PATH;
    public static String BACKUPPATH;
    public static Boolean REFACTORED;

    public static void load() {
        final ConfigParser Settings = new ConfigParser(CONFIGURATION_FILE);
        PATH = Settings.getString("AionInstallDir", "none");
        BACKUPPATH = Settings.getString("AionBackupDir", "none");
        LANGUAGE = Settings.getString("Language", "none");
    }

    public static void saveConfig() {
        try {
            
            Properties Setting = new Properties();
            File check = new File(CONFIGURATION_FILE);
            File file = check;
            // Create a new empty file only if it doesn't exist
            if (!check.createNewFile());
            check.delete();
            file.createNewFile();
            try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
                Setting.setProperty("AionInstallDir", PATH);
                Setting.setProperty("AionBackupDir", BACKUPPATH);
                Setting.setProperty("Language", LANGUAGE);
                Setting.store(out, "configuration file");
            }
        } catch (Exception e) {
            Logger.getLogger(Config.class.getName()).log(Level.WARNING, "Config: {0}", e.getMessage());
        }
    }

    public final static class ConfigParser {

        private final Properties _properties = new Properties();
        private final File _file;

        public ConfigParser(String name) {
            this(new File(name));
        }

        public ConfigParser(File file) {
            _file = file;
            if (!_file.exists()) {
                try {
                    _file.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(ConfigParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try (InputStream fileInputStream = new BufferedInputStream(new FileInputStream(file))) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.defaultCharset())) {
                    _properties.load(inputStreamReader);
                }
            } catch (Exception e) {
                Logger.getLogger(ConfigParser.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        public boolean containskey(String key) {
            return _properties.containsKey(key);
        }

        private String getValue(String key) {
            String value = _properties.getProperty(key);
            return value != null ? value.trim() : null;
        }

        public boolean getBoolean(String key, boolean defaultValue) {
            String value = getValue(key);
            if (value == null) {
                Logger.getLogger(ConfigParser.class.getName()).log(Level.WARNING, "[{0}] missing property for key: {1} using default value: {2}", new Object[]{_file.getName(), key, defaultValue});
                return defaultValue;
            }

            if (value.equalsIgnoreCase("true")) {
                return true;
            } else if (value.equalsIgnoreCase("false")) {
                return false;
            } else {
                Logger.getLogger(ConfigParser.class.getName()).log(Level.WARNING, "[{0}] Invalid value specified for key: {1} specified value: {2} should be \"boolean\" using default value: {3}", new Object[]{_file.getName(), key, value, defaultValue});
                return defaultValue;
            }
        }

        public int getInt(String key, int defaultValue) {
            String value = getValue(key);
            if (value == null) {
                Logger.getLogger(ConfigParser.class.getName()).log(Level.WARNING, "[{0}] missing property for key: {1} using default value: {2}", new Object[]{_file.getName(), key, defaultValue});
                return defaultValue;
            }

            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                Logger.getLogger(ConfigParser.class.getName()).log(Level.WARNING, "[{0}] Invalid value specified for key: {1} specified value: {2} should be \"int\" using default value: {3}", new Object[]{_file.getName(), key, value, defaultValue});
                return defaultValue;
            }
        }

        public String getString(String key, String defaultValue) {
            String value = getValue(key);
            if (value == null) {
                Logger.getLogger(ConfigParser.class.getName()).log(Level.WARNING, "[{0}] missing property for key: {1} using default value: {2}", new Object[]{_file.getName(), key, defaultValue});
                return defaultValue;
            }
            return value;
        }
    }
}
