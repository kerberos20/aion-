package io;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kerbe_000
 */
public class FolderUtils {

    public static boolean getAionPathFromRegistry() {
        if (!Config.PATH.contains("none")) {
            return true;
        }
        try {

            // read install directory from registry
            String value1;
            String value2;
            value1 = WinRegistry.readString(
                    WinRegistry.HKEY_CURRENT_USER, //HKEY
                    "SOFTWARE\\Gameforge4d\\GameforgeLive\\MainApp", //Key
                    "PreferredInstallDir");                                     //Installed directory
            value2 = WinRegistry.readString(
                    WinRegistry.HKEY_CURRENT_USER, //HKEY
                    "SOFTWARE\\Gameforge4d\\GameforgeLive\\MainApp", //Key
                    "Locale");                                     //Installed directory
            Config.LANGUAGE = (value2.substring(value2.length() - 3));
            Config.PATH = (value1 + "/" + value2 + "/AION/Download/");
            return true;
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(FolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static synchronized void deleteFolder(File folder) {

        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                deleteFolder(file);
            }
        }
        folder.delete();
    }
}
