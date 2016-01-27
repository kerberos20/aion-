package io;

import static io.RePacker.CORES;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kerbe_000
 */
public class FolderUtils {
    static final ExecutorService executor = Executors.newFixedThreadPool(CORES);
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

    private static class Runner implements Runnable {

        private final File file;

        public Runner(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            deleteFiles();
        }

        private synchronized void deleteFiles() {
            if (file.isDirectory()) {
                deleteFolder(file);
            } else {
                file.delete();
            }
        }
    }

    public static synchronized void deleteFolder(File folder) {
        
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File file : files) {
                if (!executor.isShutdown()) {
                    executor.execute(new Runner(file));
                }
            }
        }
        folder.delete();
    }
}
