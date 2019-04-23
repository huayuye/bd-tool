package com.bingdeng.tool;

import java.net.URL;

/**
 * @Author: Fran
 * @Date: 2019/3/8
 * @Desc:
 **/
public class ResourceLoaderUtil {
    public static String CLASS_PATH_PREFIX = "classpath:";

    /**
     * classpath中获取资源
     *
     * @param resource
     * @return
     * @Title: getResource
     * @Description: classpath中获取资源
     */
    public static URL getResource(String resource) {
        ClassLoader classLoader = null;
        classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(resource);
    }

    /**
     * classpath 中搜索路径
     *
     * @param resource
     * @return
     * @Title: getPath
     * @Description:
     */
    public static String getPath(String resource) {
        if (resource != null) {
            if (resource.startsWith(CLASS_PATH_PREFIX)) {
                resource = getPath("") + resource.replaceAll(CLASS_PATH_PREFIX, "");
            }
        }

        URL url = getResource(resource);
        if (url == null)
            return null;
        return url.getPath().replaceAll("%20", " ");
    }

    /**
     * @param resource
     * @param clazz
     * @return
     * @Title: getPath
     * @Description:
     */
    public static String getPath(String resource, Class clazz) {
        URL url = getResource(resource, clazz);
        if (url == null)
            return null;
        return url.getPath().replaceAll("%20", " ");
    }

    /**
     * 指定class中获取资源
     *
     * @param resource
     * @param clazz
     * @return
     * @Title: getResource
     * @Description: 指定class中获取资源
     */
    public static URL getResource(String resource, Class clazz) {
        return clazz.getResource(resource);
    }

    /**
     * If running under JDK 1.2 load the specified class using the
     * <code>Thread</code> <code>contextClassLoader</code> if that fails try
     * Class.forname. Under JDK 1.1 only Class.forName is used.
     */
    public static Class loadClass(String clazz) throws ClassNotFoundException {
        return Class.forName(clazz);
    }
}
