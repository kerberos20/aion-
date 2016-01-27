package io;

import gui.MainFrame;
import static io.FolderUtils.deleteFolder;
import static io.RePacker.CORES;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
public class ZipFiles {

    private static final DefaultExecutor EXEC = new DefaultExecutor();
    private static final File ZIP = new File("zip.dll");
    private static int _count = 0;
    private static List<String> _files;
    private static List<String> list = new ArrayList<>();
    private static final String TEMP = System.getProperty("java.io.tmpdir") + "Unpak";

    private static synchronized void checkCounter() {
        int percentage = Math.round(100 * _count / _files.size() * 100) / 100;
        MainFrame.getInstance().updateBar1("Compressing game files ", percentage);
    }

    public static synchronized void start(List<String> files) throws InterruptedException {
        if (files.isEmpty()) {
            MainFrame.getInstance().updateBar1("Compressing game files ", 100);
            return;
        }
        _files = files;
        checkCounter();
        ExecutorService executor = Executors.newFixedThreadPool(CORES);
        for (int i = 0; i < files.size(); i++) {
            executor.execute(new Runner(files.get(i)));
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        MainFrame.getInstance().updateBar1("Compressing game files ", 100);
        _count = 0;

        deleteFolder(new File(TEMP));

    }

    public static synchronized boolean updateFile(String file, String pak) {
        if (file.contains("/")) {
            file = file.replace("/", "\\");
        }
        String path = file.substring(0, file.lastIndexOf("\\"));
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
        EXEC.setStreamHandler(psh);

        try {
            CommandLine cl = new CommandLine("start");
            cl.addArgument("/D");
            cl.addArgument(path);
            cl.addArgument("/B");
            cl.addArgument(ZIP.getAbsolutePath());
            cl.addArgument("-m");
            cl.addArgument(pak);
            cl.addArgument(file.substring(file.lastIndexOf("\\") + 1));
            while (0 == EXEC.execute(cl)) {
                break;
            }

        } catch (IOException ex) {
            System.out.println(stdout.toString());
            Logger.getLogger(ZipFiles.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    private static class Runner implements Runnable {

        private final String file;

        public Runner(String file) {
            this.file = file;
        }

        @Override
        public void run() {
            _count++;
            zipMe();
            int percentage = Math.round(100 * _count / _files.size() * 100) / 100;
            MainFrame.getInstance().updateBar1("Compressing game files ", percentage);
            //checkCounter();
        }

        private synchronized void zipMe() {
            String folderpath = TEMP + file.substring(file.lastIndexOf("Unpak") + 5, file.lastIndexOf(".pak") + 4) + "\\";

            if (!list.contains(folderpath)) {
                String aionpakpath;
                String aionpath = Config.PATH;
                String innerpath = folderpath.substring(folderpath.lastIndexOf("Unpak") + 5);
                aionpakpath = aionpath + innerpath.substring(0, innerpath.length() - 1);

                list.add(folderpath);

                ByteArrayOutputStream stdout = new ByteArrayOutputStream();
                PumpStreamHandler psh = new PumpStreamHandler(stdout);
                EXEC.setStreamHandler(psh);
                try {
                    CommandLine cl = new CommandLine("start");
                    cl.addArgument("/D");
                    cl.addArgument(folderpath);
                    cl.addArgument("/B");
                    cl.addArgument(ZIP.getAbsolutePath());
                    cl.addArgument("-m");
                    cl.addArgument("-r");
                    cl.addArgument(aionpakpath);
                    cl.addArgument("*.*");
                    cl.addArgument("-x");
                    cl.addArgument("*.exe");
                    while (EXEC.execute(cl) == 0) {
                        break;
                    }
                } catch (IOException ex) {
                    System.out.println(stdout.toString());
                    Logger.getLogger(ZipFiles.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static synchronized boolean zipFiles(String folder, String pak) {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
        EXEC.setStreamHandler(psh);
        String aionpakpath = Config.PATH + pak;
        try {
            CommandLine cl = new CommandLine("start");
            cl.addArgument("/D");
            cl.addArgument(folder);
            cl.addArgument("/B");
            cl.addArgument(ZIP.getAbsolutePath());
            cl.addArgument("-m");
            cl.addArgument("-r");
            cl.addArgument(aionpakpath);
            cl.addArgument("*.*");
            cl.addArgument("-x");
            cl.addArgument("*.exe");
            int i = 0;
            while (i == EXEC.execute(cl)) {
                if (folder.isEmpty()) {
                    checkCounter();
                }
                break;
            }

        } catch (IOException ex) {
            Logger.getLogger(ZipFiles.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public static synchronized void deleteFromDataPack(String path, String target) throws IOException {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
        EXEC.setStreamHandler(psh);
        CommandLine cl = new CommandLine("start");
        cl.addArgument("/D");
        cl.addArgument(path);
        cl.addArgument("/B");
        cl.addArgument(ZIP.getAbsolutePath());
        cl.addArgument("-d");
        cl.addArgument("data.pak");
        cl.addArgument(target + "\\*.*");
    }
}
