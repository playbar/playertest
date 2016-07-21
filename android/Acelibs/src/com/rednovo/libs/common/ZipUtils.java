package com.rednovo.libs.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Dk on 16/5/30.
 */
public class ZipUtils {

    private static final String TAG = "ZipUtils";

    private static final int BUFFER_SIZE = 1024;

    /**
     * 压缩为ZIP
     *
     * @param content
     * @return
     * @throws IOException
     */
    public static ByteArrayOutputStream doZip(final String content) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(content.length());
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        gzipOutputStream.write(content.getBytes(), 0, content.getBytes().length);
        gzipOutputStream.close();
        return byteArrayOutputStream;
    }

    /**
     * 解压ZIP
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static InputStream doUnZip(InputStream inputStream) throws IOException {
        return new GZIPInputStream(inputStream);
    }

    /**
     * 解压缩流
     * @param inputZipStream 输入压缩数据流
     * @param outPathString 输出路径
     * @throws Exception
     */
    public static void doUnZipFolder(InputStream inputZipStream, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(inputZipStream);
        ZipEntry zipEntry;
        String name;

        while ((zipEntry = inZip.getNextEntry()) != null) {
            name = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                name = name.substring(0, name.length() - 1);
                File folder = new File(outPathString + File.separator + name);

                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + name);

                if (file.getParentFile() != null && !file.exists()) {
                    file.getParentFile().mkdirs();
                }

                file.createNewFile();

                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((len = inZip.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }

        inZip.close();
    }

    /**
     * 通过目录进行解压
     * @param savePath 要解压文件的路径
     * @param saveFolderPath 解压存放的文件夹
     * @return
     */
    public static boolean unZipFiles(String savePath, String saveFolderPath) {
        try {
            File srcZipFile = new File(savePath);
            doUnZipFolder(new FileInputStream(srcZipFile), saveFolderPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
