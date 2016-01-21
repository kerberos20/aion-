package io;

import gui.MainFrame;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
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

    static DefaultExecutor _exec = new DefaultExecutor();
    static String exe = "zip.dll";
    private static int _count;
    static String Temp = System.getProperty("java.io.tmpdir") + "Unpak";

    private static synchronized void checkCounter(int size) {
        if (_count == 0) {
            MainFrame.getInstance().updateBar1("Compressing game files ", 0);
        } else {
            int percentage = Math.round(100 * _count / size * 100) / 100;
            MainFrame.getInstance().updateBar1("Compressing game files ", percentage);
        }
    }

    public static synchronized void start(List<String> files) throws InterruptedException {
        if (files.isEmpty()) {
            MainFrame.getInstance().updateBar1("Compressing game files ", 100);
            return;
        }
        _count = 0;
        if (zipFiles(files,""))
            deleteFolder(new File(Temp));
        
    }

    public static synchronized boolean zipFiles(List<String> files, String folder) throws InterruptedException {

        List<String> list = new ArrayList<>();
        if (folder.isEmpty())
            checkCounter(UnZipFiles.getPakFiles().size());
        files.stream().forEach((file) -> {
            
            String folderpath = Temp + file.substring(file.lastIndexOf("Unpak") + 5, file.lastIndexOf(".pak") + 4) + "\\";
            if (!folder.isEmpty())
                folderpath=folderpath+folder+"\\";
            if (!list.contains(folderpath)) {
                _count++;
                String aionpakpath;
                String aionpath = Config.PATH;
                if (folder.isEmpty()){
                    String innerpath = folderpath.substring(folderpath.lastIndexOf("Unpak") + 5);
                    aionpakpath = aionpath + innerpath.substring(0, innerpath.length() - 1);
                }
                else
                {
                    aionpakpath = aionpath + "\\Data\\"+folder+"\\"+folder+".pak";
                }
                list.add(folderpath);

                File f = new File(exe);
                try {
                    Files.copy(f.toPath(), FileSystems.getDefault().getPath(folderpath + "zip.exe"), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    Logger.getLogger(ZipFiles.class.getName()).log(Level.SEVERE, null, ex);
                }

                ByteArrayOutputStream stdout = new ByteArrayOutputStream();
                PumpStreamHandler psh = new PumpStreamHandler(stdout);
                _exec.setStreamHandler(psh);
                try {
                    CommandLine cl = new CommandLine("start");
                    cl.addArgument("/D");
                    cl.addArgument(folderpath);
                    cl.addArgument("/B");
                    cl.addArgument("zip.exe");
                    cl.addArgument("-m");
                    cl.addArgument("-r");
                    cl.addArgument(aionpakpath);
                    cl.addArgument("*.*");
                    cl.addArgument("-x");
                    cl.addArgument("*.exe");
                    System.out.println(folderpath);
                    System.out.println(aionpakpath);
                    System.out.println(cl.toString());
                    int i = 0;
                    System.out.println(stdout.toString());
                    while (i == _exec.execute(cl)) {
                        System.out.println(stdout.toString());
                        if (folder.isEmpty())
                            checkCounter(UnZipFiles.getPakFiles().size());
                        File zip = new File(folderpath+"zip.exe");
                        zip.delete();
                        break;
                    }
                    
                } catch (IOException ex) {
                    System.out.println(stdout.toString());
                    Logger.getLogger(ZipFiles.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        
        _count = 0;
        return true;
    }
    
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    static void deleteFromDataPack(String path, String target) throws IOException {
        File f = new File(exe);
        try {
            Files.copy(f.toPath(), FileSystems.getDefault().getPath(path + "\\zip.exe"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(ZipFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
        _exec.setStreamHandler(psh);
        CommandLine cl = new CommandLine("start");
        cl.addArgument("/D");
        cl.addArgument(path);
        cl.addArgument("/B");
        cl.addArgument("zip.exe");
        cl.addArgument("-d");
        cl.addArgument("data.pak");
        cl.addArgument(target+"\\*.*");
        int i = 0;
        while (i == _exec.execute(cl)) {
            File zip = new File(path+"zip.exe");
            zip.delete();
            break;
        }
    }
}
