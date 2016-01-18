package io;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kerbe_000
 */
public class FolderUtils {

    private static Path _path = null;
    private static String _language = null;

    public static boolean getAionPathFromRegistry() {
        if (_path != null) {
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
            setAionLanguage(value2.substring(value2.length()-3));
            return setAionPath(FileSystems.getDefault().getPath(value1 + "/" + value2 + "/AION/Download/"));
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(FolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean setAionPath(Path path) {
        if (path == null) {
            return false;
        }
        _path = path;
        return true;
    }

    public static Path getAionPath() {
        return _path;
    }
    
    public static void setAionLanguage(String language) {
        _language = language;
    }
    
    public static String getAionLanguage() {
        return _language;
    }
}
