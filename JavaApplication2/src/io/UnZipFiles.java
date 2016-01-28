package io;

import gui.MainFrame;
import static io.RePacker.CORES;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.*;

public class UnZipFiles {

    static List<String> _zipfiles;
    static List<String> _newfiles;
    static List<String> _pakfiles;
    static List<String> _datapackfiles;
    static boolean _xml;
    static boolean _html;
    static final int BUFFER = 8196;
    static int _count = 0;
    static String Temp = System.getProperty("java.io.tmpdir") + "/Unpak/";

    private static synchronized void checkCounter() {
        int percentage = Math.round(100 * _count / getFiles().size() * 100) / 100;
        MainFrame.getInstance().updateBar1("Extracting XML files ", percentage);
    }

    private static class Runner implements Runnable {

        private final String file;
        private final Boolean xml;
        private final Boolean html;

        public Runner(String file, Boolean xml, Boolean html) {
            this.file = file;
            this.xml = xml;
            this.html = html;
        }

        @Override
        public void run() {
            _count++;
            unZipMe();
            checkCounter();
        }

        private synchronized void unZipMe() {
            try {
                BufferedOutputStream dest;
                BufferedInputStream is;
                File f = new File(file);
                if (f.exists()) {
                    try (ZipFile zipfile = new ZipFile(file, Charset.forName("Cp437"))) {
                        Enumeration<? extends ZipEntry> e = zipfile.entries();
                        ZipEntry entry;

                        while (e.hasMoreElements()) {
                            entry = e.nextElement();

                            int count;
                            byte data[] = new byte[BUFFER];
                            if (xml || html) {
                                if ((xml && entry.getName().contains(".xml")) || (html && entry.getName().contains(".html"))) {
                                    is = new BufferedInputStream(zipfile.getInputStream(entry));
                                    int[] XMLheader = new int[]{0x80};
                                    int[] HTMLheader = new int[]{0x81};
                                    try (InputStream stream = new BufferedInputStream(zipfile.getInputStream(entry))) {
                                        int read = stream.read();
                                        if (read == XMLheader[0] || read == HTMLheader[0]) {

                                            String path = zipfile.getName();
                                            String zipName = new File(zipfile.getName()).getName();

                                            int val = path.toLowerCase().lastIndexOf(Config.PATH) + Config.PATH.length() + 1;
                                            File mk;
                                            if (entry.getName().contains("/")) {
                                                new File(Temp + path.substring(val, path.lastIndexOf(zipName)) + "/" + zipName + "/" + entry.getName().substring(0, entry.getName().lastIndexOf("/"))).mkdirs();
                                            }
                                            mk = new File(Temp + path.substring(val, path.lastIndexOf(zipName)) + "/" + zipName);
                                            mk.mkdirs();
                                            File check = new File(mk.getPath() + "/" + entry.getName());
                                            // if file alreadz exists
                                            if (!check.exists()) {
                                                dest = new BufferedOutputStream(new FileOutputStream(mk.getPath() + "/" + entry.getName()), BUFFER);
                                                while ((count = is.read(data, 0, BUFFER)) != -1) {
                                                    dest.write(data, 0, count);
                                                }
                                                dest.flush();
                                                dest.close();
                                            }
                                            addFile(mk.getPath() + "/" + entry.getName());
                                            addPak(path);

                                        }
                                        stream.close();
                                    }
                                    is.close();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                Logger.getLogger(RePacker.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public static List<String> Start(List<String> files, boolean xml, boolean html) {
        _zipfiles = files;
        _newfiles = new ArrayList<>();
        _pakfiles = new ArrayList<>();
        checkCounter();
        ExecutorService executor = Executors.newFixedThreadPool(CORES);

        for (int i = 0; i < files.size(); i++) {
            executor.execute(new Runner(files.get(i), xml, html));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        MainFrame.getInstance().updateBar1("Extracting XML files ", 100);
        _count = 0;
        return _newfiles;
    }

    public static synchronized List<String> unZip(String file) {
        try {
            _datapackfiles = new ArrayList<>();
            BufferedOutputStream dest;
            BufferedInputStream is;
            int counter = 0;
            try (ZipFile zipfile = new ZipFile(file, Charset.forName("Cp437"))) {
                Enumeration<? extends ZipEntry> e = zipfile.entries();
                ZipEntry entry;
                String zipName = new File(zipfile.getName()).getName();
                MainFrame.getInstance().updateBar1("Decompresing " + zipName + " ", 0);

                while (e.hasMoreElements()) {
                    counter++;
                    entry = e.nextElement();

                    int count;
                    byte data[] = new byte[BUFFER];
                    is = new BufferedInputStream(zipfile.getInputStream(entry));

                    String path = zipfile.getName();

                    int val = path.toLowerCase().lastIndexOf(Config.PATH) + Config.PATH.length() + 1;
                    File mk;
                    if (entry.getName().contains("/")) {
                        new File(Temp + path.substring(val, path.lastIndexOf(zipName)) + "/" + zipName + "/" + entry.getName().substring(0, entry.getName().lastIndexOf("/"))).mkdirs();
                    }

                    mk = new File(Temp + path.substring(val, path.lastIndexOf(zipName)) + "/" + zipName);
                    mk.mkdirs();
                    File check = new File(mk.getPath() + "/" + entry.getName());
                    // if file alreadz exists
                    if (!check.exists()) {
                        dest = new BufferedOutputStream(new FileOutputStream(mk.getPath() + "/" + entry.getName()), BUFFER);
                        while ((count = is.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                        dest.close();
                    }
                    addDataPackFile(mk.getPath() + "/" + entry.getName());
                    int percentage = Math.round(100 * counter / zipfile.size() * 100) / 100;
                    MainFrame.getInstance().updateBar1("Decompresing " + zipName + " ", percentage);
                    is.close();
                }
                MainFrame.getInstance().updateBar1("Decompresing " + zipName + " ", 100);
            }

            return getDataPackFiles();
        } catch (Exception e) {
            Logger.getLogger(RePacker.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;

    }

    private static synchronized void addFile(String file) {
        if (!_newfiles.contains(file)) {
            _newfiles.add(file);
        }
    }

    private static synchronized void addPak(String file) {
        if (!_pakfiles.contains(file)) {
            _pakfiles.add(file);
        }
    }

    private synchronized static List<String> getFiles() {
        return _zipfiles;
    }

    private static synchronized void addDataPackFile(String file) {
        if (file != null && !_datapackfiles.contains(file)) {
            _datapackfiles.add(file);
        }
    }

    private synchronized static List<String> getDataPackFiles() {
        return _datapackfiles;
    }

    public synchronized static List<String> getPakFiles() {
        return _pakfiles;
    }
}
