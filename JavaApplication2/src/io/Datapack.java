package io;

import gui.MainFrame;
import static io.FolderUtils.deleteFolder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private static Map<String, List<String>> _zonemaps = new ConcurrentHashMap<>(1);

    private static synchronized void checkCounter() {
        int percentage = Math.round(100 * _count / _files.size() * 100) / 100;
        MainFrame.getInstance().updateBar1("Altering l10n folder ", percentage);
    }

    public static synchronized void start() throws InterruptedException, IOException {
        long timeStart = Calendar.getInstance().getTimeInMillis();

        String aionpath = Config.PATH;
        String language = Config.LANGUAGE;
        String l10n = "\\l10n\\" + language;
        String data = l10n + "\\Data\\";
        String datapak = data + "Data.pak\\";

        // unzip datapack
        _files = UnZipFiles.unZip(aionpath + datapak);
        checkCounter();

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
            MainFrame.getInstance().updateBar1("Moving textures from l10n folder ", 0);
            String texturesfolder = aionpath + "\\Textures\\";
            String temptexturesfolder = Temp + l10n + "\\Textures\\Textures.pak\\";
            _textures = UnZipFiles.unZip(oldtextures.getAbsolutePath());
            _textures.stream().forEach((f) -> {
                updateCounter();
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

                        File file = new File(texturesfolder + zonemap);
                        File[] files = file.listFiles();
                        for (File pak : files) {
                            if (pak.getName().endsWith(".pak")) {
                                Path filePath = pak.toPath();

                                if (Files.isRegularFile(filePath)) {
                                    try {
                                        ZipFile zipfile = new ZipFile(filePath.toFile(), Charset.forName("Cp437"));
                                        Enumeration<? extends ZipEntry> e = zipfile.entries();
                                        ZipEntry entry;

                                        while (e.hasMoreElements()) {
                                            entry = e.nextElement();

                                            if (entry.getName().contains(f.substring(f.lastIndexOf("/") + 1))) {
                                                String pakname = filePath.toString().contains("/") ? filePath.toString().replaceAll("/", "\\") : filePath.toString();
                                                List<String> zonetextures = _zonemaps.get(pakname);
                                                if (zonetextures != null)
                                                {
                                                    zonetextures.add(f);
                                                }
                                                else
                                                {
                                                    _zonemaps.put(pakname, new ArrayList<>());
                                                    zonetextures = _zonemaps.get(pakname);
                                                    zonetextures.add(f);
                                                }
                                                break;
                                            }
                                        }

                                    } catch (IOException ex) {
                                        Logger.getLogger(Datapack.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }
                            }
                        }

                    } else {
                        ZipFiles.updateFile(f, texturesfolder + ui + "ui.pak");
                    }
                    int percentage = Math.round(100 * _count / _textures.size() * 100) / 100;
                    MainFrame.getInstance().updateBar1("Moving textures from l10n folder ", percentage);
                }
            });
            for (String s : _zonemaps.keySet())
            {
                ZipFiles.updateFile(_zonemaps.get(s), s);
            }
            oldtextures.delete();
            MainFrame.getInstance().updateBar1("Moving textures from l10n folder ", 100);
        }

        if (_strings) {
            // todo
        }
        if (_dialogs) {
            // todo
        }

        deleteFolder(new File(Temp));
        _files = null;

        long timeEnd = Calendar.getInstance().getTimeInMillis();
        MainFrame.getInstance().updateBar1("Altering l10n folder ", 100);
        _count = 0;
        System.out.println("optimised l10n in " + (timeEnd - timeStart) + " ms");
    }

    private static void updateCounter() {
        _count++;
    }
}
