package com.crucian.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class IOUtils {

    public static InputStream tryGetResourceAsStream(String cfg) {
        // 顺序为: 文件, ClassPath, 当前类的 ClassLoader, 当前线程的 ClassLoader
        InputStream is = null;

        File file = new File(cfg);
        if (file.canRead() && file.isFile()) {
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException ignored) {
            }
        }

        if (is == null)
            is = ClassLoader.getSystemResourceAsStream(cfg);

        if (is == null)
            is = IOUtils.class.getResourceAsStream(cfg);

        if (is == null) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null)
                is = classLoader.getResourceAsStream(cfg);
        }
        return is;
    }
}
