package io;

import gui.MainFrame;
import static io.RePacker.CORES;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kerbe_000
 */
public class Convert {

    static int _count = 0;
    static List<String> _files;

    private static synchronized void checkCounter() {
        int percentage = Math.round(100 * _count / _files.size() * 100) / 100;
        MainFrame.getInstance().updateBar1("Decoding XML files ", percentage);
    }

    private static class Runner implements Runnable {

        private final String file;

        public Runner(String file) {
            this.file = file;
        }

        @Override
        public void run() {
            convertMe();
        }

        private synchronized void convertMe() {
            Converter conv = null;
            InputStream input;
            OutputStream output;
            File f = new File(file);
            _count++;
            checkCounter();
            if (!f.exists()) {

            }
            if (file.endsWith(".xml")) {
                conv = new XmlConverter();
            } else if (file.endsWith(".html")) {
                conv = new HtmlConverter();
            }

            if (conv != null) {
                try {
                    input = new BufferedInputStream(new FileInputStream(file));
                    if (file.endsWith(".xml")) {
                        output = new BufferedOutputStream(new FileOutputStream(file.substring(0, file.lastIndexOf(".xml")) + ".new"));
                    } else {
                        output = new BufferedOutputStream(new FileOutputStream(file.substring(0, file.lastIndexOf(".html")) + ".new1"));
                    }

                    while (conv.Read(input, output)) {
                        f.delete();
                        if (file.endsWith(".xml")) {
                            new File(file.substring(0, file.lastIndexOf(".xml")) + ".new").renameTo(new File(file));
                        } else {
                            new File(file.substring(0, file.lastIndexOf(".html")) + ".new1").renameTo(new File(file));
                        }
                        break;

                    }
                    input.close();
                    output.close();
                    File oldfile = new File(file.substring(0, file.lastIndexOf(".xml")) + ".new");
                    if (oldfile.exists()) {
                        oldfile.getAbsoluteFile().delete();
                    } else {
                        input = new BufferedInputStream(new FileInputStream(file));
                        output = new BufferedOutputStream(new FileOutputStream(file.substring(0, file.lastIndexOf(".xml")) + ".tmp"));
                        int[] from = new int[]{0x00, 0x00};
                        int[] to = new int[]{0x30};
                        byte[] buffer = new byte[2];
                        while (input.read(buffer, 0, 2) == 2) {

                            if (buffer[0] == from[0]) {
                                if (buffer[1] == from[1]) {
                                    buffer[1] = (byte) to[0];
                                }
                            }
                            output.write(buffer);
                        }
                        input.close();
                        output.close();
                        // Once everything is complete, delete old file..
                        File oldFile = new File(file);
                        oldFile.delete();

                        // And rename tmp file's name to old file name
                        File newFile = new File(file.substring(0, file.lastIndexOf(".xml")) + ".tmp");
                        newFile.renameTo(oldFile);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Convert.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Convert.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void Start(List<String> files) throws InterruptedException {
        _files = files;
        if (files.size() > 0) {
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
            _count = 0;
            System.out.println("converted in " + (timeEnd - timeStart) + " ms");
        }
    }
}
