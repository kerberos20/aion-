package io;

import gui.MainFrame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.exec.DefaultExecutor;

/**
 *
 * @author kerbe_000
 */
public class Datapack {
    static DefaultExecutor _exec = new DefaultExecutor();
    static String Temp = System.getProperty("java.io.tmpdir") + "Unpak";
    static String exe = "zip.dll";
    static List<String> _files;
    static int _count = 0;
    static boolean _fonts = false;
    
    private static synchronized void checkCounter() {
        int percentage = Math.round(100 * _count / _files.size() * 100) / 100;
        MainFrame.getInstance().updateBar1("Altering Data.pak files ", percentage);
    }
    
    public static synchronized void start() throws InterruptedException, IOException{
        String aionpath = FolderUtils.getAionPath().toString();
        String language = FolderUtils.getAionLanguage();
        String datapack = aionpath+"\\l10n\\"+language+"\\Data\\Data.pak";
        
        // unzip datapack
        _files = UnZipFiles.unZip(datapack);
        
        // refactor it a bit
        String oldfonts = aionpath+"\\data\\fonts";            
        File oldfont = new File(oldfonts+"\\fonts.pak");
        if (_files == null){
            System.out.println("no datapak?");
            return;
        }
        _files.stream().forEach((f) -> {
            //if (f == null){}
            //fonts
            if (f.contains("fonts")){
                _fonts = true;
            }
        });
        String path = "\\l10n\\"+language+"\\Data\\Data.pak\\";
        String datapath = "\\l10n\\"+language+"\\Data\\";
        if (_fonts)
        {
            // delete old korean fonts from aion folder
            if (oldfont.exists())
                oldfont.delete();
            // compress fonts from temp folder into aion font folder
            List<String> list = new ArrayList<>();
            list.add(Temp+path);
            if (ZipFiles.zipFiles(list, "fonts")){
                // delete fonts from data.pak
                ZipFiles.deleteFromDataPack(aionpath+datapath, "fonts");
            }

            
        }
        File zip = new File(aionpath+datapath+"zip.exe");
        ZipFiles.deleteFolder(new File(Temp));
        zip.delete();
        _files = null;
    }
}
