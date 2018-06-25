package com.xyn.floatballsdk.utils;


import java.io.File;

public class FileUtil {

    public static boolean deleteFile(String path) {
        if (isBlank(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }
}
