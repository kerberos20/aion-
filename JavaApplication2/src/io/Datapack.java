package io;

import gui.MainFrame;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
    static List<String> _textures;
    static int _count = 0;
    static boolean _fonts = false;
    static boolean _ui = false;
    static boolean _strings = false;
    static boolean _dialogs = false;

    private static synchronized void checkCounter() {
        int percentage = Math.round(100 * _count / _files.size() * 100) / 100;
        MainFrame.getInstance().updateBar1("Altering Data.pak files ", percentage);
    }

    public static synchronized void start() throws InterruptedException, IOException {
        String aionpath = Config.PATH;
        String language = Config.LANGUAGE;
        String l10n = "\\l10n\\" + language;
        String data = l10n + "\\Data\\";
        String datapak = data + "Data.pak\\";

        // unzip datapack
        _files = UnZipFiles.unZip(aionpath + datapak);

        // refactor it a bit
        String oldfonts = aionpath + "\\data\\fonts";
        File oldfont = new File(oldfonts + "\\fonts.pak");
        if (_files == null) {
            System.out.println("no datapak?");
            return;
        }
        _files.stream().forEach((f) -> {
            if (f.contains("fonts")) {
                _fonts = true;
            } else if (f.contains("ui")) {
                _ui = true;
            }
        });

        if (_fonts) {
            // delete old korean fonts from aion folder
            if (oldfont.exists()) {
                oldfont.delete();
            }
            // compress fonts from temp folder into aion font folder
            if (ZipFiles.zipFiles(Temp + datapak + "fonts", "\\Data\\fonts\\fonts.pak")) {
                // delete fonts from data.pak
                ZipFiles.deleteFromDataPack(aionpath + data, "fonts");
            }
        }
        if (_ui) {
            // todo
        }

        //textures
        String textures = aionpath + l10n + "\\Textures\\";
        File oldtextures = new File(textures + "Textures.pak");
        if (oldtextures.exists()) {
            String texturesfolder = aionpath + "\\Textures\\";
            String temptexturesfolder = Temp + l10n + "\\Textures\\Textures.pak\\";
            System.out.println(temptexturesfolder);
            _textures = UnZipFiles.unZip(oldtextures.getAbsolutePath());
            _textures.stream().forEach((f) -> {
                f = f.toLowerCase();
                File test = new File(f);
                if (test.exists() && test.isDirectory()) {

                } else if (f.contains("graphicchar.dds")
                        || f.contains("teleport_world_dark_dragon.dds")
                        || f.contains("teleport_world_dark_light.dds")
                        || f.contains("v5_common.dds")
                        || f.contains("v5_common_1.dds")
                        || f.contains("v5_common_2.dds")
                        || f.contains("v5_common_3.dds")
                        || f.contains("v5_housing.dds")
                        || f.contains("v5_hud_s1.dds")
                        || f.contains("v5_indun.dds")
                        || f.contains("v5_lobby_race_dark_01.dds")
                        || f.contains("v5_lobby_race_light_01.dds")
                        || f.contains("v5_loginstage.dds")
                        || f.contains("v5_map.dds")
                        || f.contains("worldmap_abyss.dds")
                        || f.contains("worldmap_dragon_1.dds")
                        || f.contains("worldmap_dragon_bb.dds")
                        || f.contains("worldmap_globe_bb.dds")
                        || !f.contains(".")) {
                    // ignore
                    System.out.println("ignored " + f);
                } else if (f.contains("loading")) {
                    // move files
                    String loading = "loading";
                    try {
                        File dir1 = new File(temptexturesfolder + loading);
                        File[] content = dir1.listFiles();
                        for (File content1 : content) {
                            String file = content1.toString().substring(content1.toString().lastIndexOf("loading\\"));
                            File oldFile = new File(texturesfolder + file);
                            if (oldFile.exists() && !oldFile.canWrite()) {
                                oldFile.setWritable(true);
                            }
                            if (content1.isDirectory()) {

                                File[] content2 = content1.listFiles();
                                for (File content21 : content2) {
                                    file = content21.toString().substring(content1.toString().lastIndexOf("loading\\"));
                                    Files.move(content21.toPath(), FileSystems.getDefault().getPath(texturesfolder + file), StandardCopyOption.REPLACE_EXISTING);
                                }
                            } else {
                                Files.move(content1.toPath(), FileSystems.getDefault().getPath(texturesfolder + file), StandardCopyOption.REPLACE_EXISTING);
                            }
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(Datapack.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if (f.contains("ui")) {
                    String ui = "ui\\";
                    if (f.contains("hit_number")) {
                        String hitnumber = ui + "hit_number\\";
                        ZipFiles.updateFile(f, texturesfolder + hitnumber + "hit_number.pak");
                    } else if (f.contains("zonemap")) {
                        final String zonemap = ui + "zonemap\\";
                        try {
                            final String file = f;
                            Files.walk(Paths.get(texturesfolder + zonemap)).forEach(filePath -> {

                                if (Files.isRegularFile(filePath)) {
                                    try {
                                        BufferedInputStream is;
                                        try (ZipFile zipfile = new ZipFile(filePath.toFile(), Charset.forName("Cp437"))) {
                                            Enumeration<? extends ZipEntry> e = zipfile.entries();
                                            ZipEntry entry;

                                            while (e.hasMoreElements()) {
                                                entry = e.nextElement();
                                                
                                                if (entry.getName().contains(file.substring(file.lastIndexOf("/"))))
                                                {
                                                    ZipFiles.updateFile(file, filePath.toString().contains("/") ? filePath.toString().replaceAll("/", "\\") : filePath.toString());
                                                    break;
                                                }
                                            }
                                        }
                                    } catch (IOException ex) {
                                        Logger.getLogger(Datapack.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }
                            });
                        } catch (IOException ex) {
                            Logger.getLogger(Datapack.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        ZipFiles.updateFile(f, texturesfolder + ui + "ui.pak");
                    }
                }
            });
            oldtextures.delete();
        }

        if (_strings) {
            // todo
        }
        if (_dialogs) {
            // todo
        }
        File zip = new File(aionpath + data + "zip.exe");

        ZipFiles.deleteFolder(
                new File(Temp));
        zip.delete();
        _files = null;
    }
}
