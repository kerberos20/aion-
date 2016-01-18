package io;

import gui.MainFrame;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kerbe_000
 */
public class Convert {

    static int _count = 0;
    static List<ConvertMe> _list;

    private static synchronized void checkCounter() {
        int percentage = Math.round(100 * _count / _list.size() * 100) / 100;
        MainFrame.getInstance().updateBar1("Decoding XML files ", percentage);
    }

    public static List<String> Start(List<String> files) throws InterruptedException {

        _list = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {

            _list.add(new ConvertMe(files.get(i)));
        }

        Runnable runner1 = () -> {
            for (int i = 0; i < getList().size(); i += 2) {
                try {
                    getList().get(i).call();

                } catch (IOException ex) {
                    Logger.getLogger(Convert.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Runnable runner2 = () -> {
            for (int i = 1; i < getList().size(); i += 2) {
                try {
                    getList().get(i).call();

                } catch (IOException ex) {
                    Logger.getLogger(Convert.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread t1 = new Thread(runner1, "Unzip1");
        Thread t2 = new Thread(runner2, "Unzip2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        _count = 0;
        return files;
    }

    public static class ConvertMe implements Callable<String> {

        final private String _file;

        public ConvertMe(String file) {
            this._file = file;
        }

        @Override
        public synchronized String call() throws FileNotFoundException, IOException {
            //try {
            Converter conv = null;
            InputStream input;
            OutputStream output;
            File f = new File(_file);
            _count++;
            checkCounter();
            if (!f.exists()) {
                return "not a file : " + _file;
            }
            if (_file.endsWith(".xml")) {
                conv = new XmlConverter();
            } else if (_file.endsWith(".html")) {
                conv = new HtmlConverter();
            }

            if (conv == null) {
                return "nothing to do";
            }

            input = new BufferedInputStream(new FileInputStream(_file));
            if (_file.endsWith(".xml")) {
                output = new BufferedOutputStream(new FileOutputStream(_file.substring(0, _file.lastIndexOf(".xml")) + ".new"));
            } else {
                output = new BufferedOutputStream(new FileOutputStream(_file.substring(0, _file.lastIndexOf(".html")) + ".new1"));
            }

            while (conv.Read(input, output)) {
                f.delete();
                if (_file.endsWith(".xml")) {
                    new File(_file.substring(0, _file.lastIndexOf(".xml")) + ".new").renameTo(new File(_file));
                } else {
                    new File(_file.substring(0, _file.lastIndexOf(".html")) + ".new1").renameTo(new File(_file));
                }
                break;

            }
            input.close();
            output.close();
            File oldfile = new File(_file.substring(0, _file.lastIndexOf(".xml")) + ".new");
            if (oldfile.exists()) {
                oldfile.getAbsoluteFile().delete();
                return "empty";
            }
            input = new BufferedInputStream(new FileInputStream(_file));
            output = new BufferedOutputStream(new FileOutputStream(_file.substring(0, _file.lastIndexOf(".xml")) + ".tmp"));
            int[] from = new int[]{0x00, 0x00};
            int[] to = new int[]{0x30};
            byte[] buffer = new byte[2];
            while (input.read(buffer, 0, 2) ==2)
            {
                
                if (buffer[0] == from[0]){
                    if (buffer[1] == from[1]) {
                         buffer[1] = (byte) to[0];
                    }
                }
                output.write(buffer);
            }
            input.close();
            output.close();
            // Once everything is complete, delete old file..
            File oldFile = new File(_file);
            oldFile.delete();

            // And rename tmp file's name to old file name
            File newFile = new File(_file.substring(0, _file.lastIndexOf(".xml")) + ".tmp");
            newFile.renameTo(oldFile);

            return _file;
        }
    }

    private synchronized static List<ConvertMe> getList() {
        return _list;
    }
}
