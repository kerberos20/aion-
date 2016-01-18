package io;

import gui.MainFrame;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 *
 * @author kerbe_000
 */
public class RePacker {

    static String _path;
    private static List<String> _files;
    static DefaultExecutor _exec = new DefaultExecutor();
    static String exe = "pak2zip.dll ";
    private static int _count = 0;

    private static synchronized void checkCounter() {
        int percentage = Math.round(100 * _count / _files.size() * 100) / 100;
        MainFrame.getInstance().updateBar1("Repacking game files ", percentage);
    }

    public static void start() throws IOException, InterruptedException {

        _files = readFile("/FileInfoMap_AION-LIVE.dat");
        Runnable runner1 = () -> {
            for (int i = 0; i < _files.size(); i += 2) {
                pak2zip(_files.get(i));
                checkCounter();
            }
        };
        Runnable runner2 = () -> {
            for (int i = 1; i < _files.size(); i += 2) {
                pak2zip(_files.get(i));
                checkCounter();
            }
        };
        Thread t1 = new Thread(runner1, "Repack1");
        Thread t2 = new Thread(runner2, "Repack2");

        MainFrame.setPakFiles(_files);

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        _count = 0;

    }

    private static List<String> readFile(String fileName) throws IOException {

        List<String> files = new ArrayList<>();

        //read lines in your file all at once
        List<String> allLines = Files.readAllLines(FileSystems.getDefault().getPath(FolderUtils.getAionPath() + fileName), Charset.forName("UTF-16"));

        for (int i = 0; i < allLines.size(); i++) {

            int size = allLines.size();

            int percentage = Math.round(100 * (i + 1) / size * 100) / 100;
            MainFrame.getInstance().updateBar1("Loading game files ", percentage);

            if (allLines.get(i).contains(".pak")
                    && !allLines.get(i).contains("RelicCalc.pak")
                    && !allLines.get(i).contains("func_pet.pak")
                    && !allLines.get(i).contains("bin32.pak")) {
                String str = allLines.get(i);
                String file = FolderUtils.getAionPath() + "/" + str.substring(0, str.lastIndexOf(".pak") + 4);
                File check = new File(file);
                if (check.exists()) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    public static boolean testZip(String file) throws FileNotFoundException, IOException {
        if (!new File(file).exists()) {
            return false;
        }
        int[] header = new int[]{0x50, 0x4B, 0x03, 0x04}; // header of zip file
        try (InputStream ins = new BufferedInputStream(new FileInputStream(file), 4)) {
            byte[] buffer = new byte[4];
            ins.read(buffer, 0, 4);
            for (int i = 0; i < 4; ++i) {
                if (buffer[i] != header[i]) {
                    ins.close();
                    return false;
                }
            }
            ins.close();
            return true;
        }
    }

    public static synchronized void pak2zip(String file) {
        try {
            String pak = file;

            // if not already ZIP file, decompress it
            if (!testZip(pak)) {
                String zip = pak.replace(".pak", ".zip");
                if (!testZip(zip)) {
                    // todo later check for file if exists
                    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
                    PumpStreamHandler psh = new PumpStreamHandler(stdout);
                    _exec.setStreamHandler(psh);
                    int exit = _exec.execute(CommandLine.parse(exe + pak + " " + zip));
                    while (exit == 0) {
                        zip2pak(zip);
                        break;
                    }
                } else {
                    zip2pak(zip);
                }
            }
            _count++;
        } catch (Exception e) {
            Logger.getLogger(RePacker.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static synchronized void zip2pak(String file) {
        try {
            File newzip = new File(file);
            if (newzip.exists()) {
                // if it is valid ZIP file, rename it
                if (testZip(file)) {
                    String newpak = file.replace(".zip", ".pak");
                    File pak = new File(newpak);
                    if (pak.exists()) {
                        pak.delete();
                    }
                    newzip.renameTo(pak);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(RePacker.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static List<String> getFiles() {
        return _files;
    }

}
