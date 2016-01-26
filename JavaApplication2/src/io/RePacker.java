package io;

import gui.MainFrame;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    static final int CORES = Runtime.getRuntime().availableProcessors();
    static String _path;
    private static List<String> _files;
    static DefaultExecutor _exec = new DefaultExecutor();
    static final String EXE = "pak2zip.dll ";
    private static int _count = 0;

    private static synchronized void checkCounter() {
        int percentage = Math.round(100 * _count / _files.size() * 100) / 100;
        MainFrame.getInstance().updateBar1("Repacking game files ", percentage);
    }

    private static class Runner implements Runnable {

        private final String file;

        public Runner(String file) {
            this.file = file;
        }

        @Override
        public void run() {
            _count++;
            pak2zip();
            checkCounter();
        }

        private synchronized void pak2zip() {
            try {
                // if not already ZIP file, decompress it
                if (!testZip(file)) {
                    String zip = file.replace(".pak", ".zip");
                    if (!testZip(zip)) {
                        // todo later check for file if exists
                        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
                        PumpStreamHandler psh = new PumpStreamHandler(stdout);
                        _exec.setStreamHandler(psh);
                        int exit = _exec.execute(CommandLine.parse(EXE + file + " " + zip));
                        while (exit == 0) {
                            zip2pak(zip);
                            break;
                        }
                    } else {
                        zip2pak(zip);
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(RePacker.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public static void start() throws IOException, InterruptedException {

        _files = readFile("/FileInfoMap_AION-LIVE.dat");
        MainFrame.setPakFiles(_files);
        long timeStart = Calendar.getInstance().getTimeInMillis();
        checkCounter();
        ExecutorService executor = Executors.newFixedThreadPool(CORES);
        for (int i = 0; i < _files.size(); i++) {
            executor.execute(new Runner(_files.get(i)));
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        long timeEnd = Calendar.getInstance().getTimeInMillis();
        MainFrame.getInstance().updateBar1("Repacking game files ", 100);
        _count = 0;
    }

    private static List<String> readFile(String fileName) throws IOException {

        List<String> files = new ArrayList<>();

        //read lines in your file all at once
        List<String> allLines = Files.readAllLines(FileSystems.getDefault().getPath(Config.PATH + fileName), Charset.forName("UTF-16"));

        for (int i = 0; i < allLines.size(); i++) {

            int size = allLines.size();

            int percentage = Math.round(100 * (i + 1) / size * 100) / 100;
            MainFrame.getInstance().updateBar1("Loading game files ", percentage);

            if (allLines.get(i).contains(".pak")
                    && !allLines.get(i).contains("RelicCalc.pak")
                    && !allLines.get(i).contains("func_pet.pak")
                    && !allLines.get(i).contains("bin32.pak")) {
                String str = allLines.get(i);
                String file = Config.PATH + "/" + str.substring(0, str.lastIndexOf(".pak") + 4);
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
