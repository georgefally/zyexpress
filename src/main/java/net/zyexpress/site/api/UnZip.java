package net.zyexpress.site.api;

import org.apache.commons.lang3.CharSet;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by lumengyu on 2016/3/4.
 */
public class UnZip {

    static final int BUFFER = 1024;

    public void unzip(File zip,File unZipDir) throws IOException {
        if (!zip.exists()) {
            return;
        }
        if (!unZipDir.exists()) {
            unZipDir.mkdirs();
        }
        if (!unZipDir.isDirectory()) {
            return;
        }
        int fileCount = 0;
        Charset charset = Charset.forName("GBK");
        ZipFile zipfile = new ZipFile(zip,charset);
        BufferedOutputStream ds = null;
        BufferedInputStream is = null;
        ZipEntry entry = null;
        Enumeration entries = zipfile.entries();
        System.out.println("Start...");
        while (entries.hasMoreElements()) {
            try {
                entry = (ZipEntry) entries.nextElement();
                System.out.println((++fileCount)+") Extracting: " + entry);
                File tempFile = new File(unZipDir.getAbsolutePath() + "/"
                        + entry.getName());
                //File tempFile = new File(unZipDir.getAbsolutePath());
                if(entry.isDirectory()){
                    tempFile.mkdirs();
                    continue;
                } else if (!tempFile.getParentFile().exists()) {
                    tempFile.getParentFile().mkdirs();
                }

                is = new BufferedInputStream(zipfile.getInputStream(entry));
                int count;
                byte data[] = new byte[BUFFER];
                FileOutputStream fos = new FileOutputStream(unZipDir
                        .getAbsolutePath()
                        + "/" + entry.getName());
                ds = new BufferedOutputStream(fos, BUFFER);
                while ((count = is.read(data, 0, BUFFER)) != -1) {
                    ds.write(data, 0, count);
                }
                ds.flush();

            } catch (IOException ex) {
                throw ex;

            } finally {
                try {
                    if (ds != null)
                        ds.close();

                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    // ignore

                }
            }
        }
        System.out.println("Finished !");
    }
}
