package top.cerbur.http.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClazzUtil {
    /**
     * 获取目录下的所有 class 文件
     * @param pack path
     * @return 目录下的所有 Class
     */
    public static Set<Class<?>> getClasses(String pack) {
        // 用来存所有的类
        Set<Class<?>> classes = new LinkedHashSet<>();
        String packageDirName = pack.replace('.','/');
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocal = url.getProtocol();
                if ("file".equals(protocal)) {
                    String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    getPathClassFiles(filePath,pack,classes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static void getPathClassFiles(String filePath,String packName,Set<Class<?>> classes) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirFiles = dir.listFiles(pathname -> {
            // 获取class文件和获取目录;
            return pathname.isDirectory() ||pathname.getName().endsWith(".class");
        });
        if (dirFiles == null) {
            return;
        }
        for (File dirFile : dirFiles) {
            if (dirFile.isDirectory()) {
                // 在进入此文件夹中获取
                getPathClassFiles(filePath + '/'  + dirFile.getName(),
                        packName + "." + dirFile.getName(),classes);
            } else {
                String className = dirFile.getName().substring(0, dirFile.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader()
                            .loadClass(packName + "." +className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
