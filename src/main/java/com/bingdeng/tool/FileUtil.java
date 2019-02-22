package com.bingdeng.tool;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author: Fran
 * @Date: 2019/1/9
 * @Desc:
 **/
@Slf4j
public class FileUtil {

    //校验路径开头
    private static final String PATTERN_REGEX = "^(http|https)://.+";
    //文件上传服务器
    private static String fileHost;

    public static void setFileHost(String fileHost) {
        FileUtil.fileHost = fileHost;
    }


    /**
     * 存储文件
     *
     * @param file 文件
     * @param path 文件存储路径（文件夹）
     * @return
     */
    public static String uploadFile(MultipartFile file, String path) {
        if (path == null) {
            log.error("保存文件路径为空");
            return null;
        }
        String newFileUrl = null;
        if (file != null) {
            // //获取内容类型
            // String contentType = file.getContentType();
            //获取文件名及类型
            String fieldOriginalName = file.getOriginalFilename();
            //获取文件后缀
            String fieldType = fieldOriginalName.substring(fieldOriginalName.lastIndexOf(".") + 1);
            //重新定义文件名
            String tempNewName = getRandomFileName();
            //保存图片服务器的请求路径
            newFileUrl = path + tempNewName + "." + fieldType;
            // long size = file.getSize();
            try {
                //若是文件服务器，走这里
                if (verifyUrlByRegEx(path)) {
                    //实例化一个Jersey
                    Client client = new Client();
                    //设置请求路径
                    WebResource resource = client.resource(newFileUrl);
                    //发送post get put
                    resource.put(String.class, file.getBytes());
                } else {
                    File dirPath = new File(path);
                    if (!dirPath.exists()) {
                        dirPath.mkdirs();
                    }
                    dirPath.setWritable(true, false);
                    file.transferTo(new File(newFileUrl));
                    File fieldIsExit = new File(newFileUrl);
                    if (!fieldIsExit.exists()) {
                        log.error("文件转存失败");
                    } else {
                        log.info("文件上传成功：url={}", newFileUrl);
                    }
//                    newFileUrl = newFileUrl.replace(fileHost,"");
                }
            } catch (IOException e) {
                log.error("文件上传失败，msg:{}", e.getMessage());
            }
        }
        return newFileUrl;
    }

    /**
     * 存储文件
     *
     * @param fileBytes  文件
     * @param nameSuffix 后缀名
     * @param path       文件存储路径（文件夹）
     * @return
     */
    public static String uploadFile(byte[] fileBytes, String path, String nameSuffix) {
        if (path == null || path == "" || nameSuffix == null || nameSuffix == "") {
            log.error("文件路径或文件后缀名为空");
            return null;
        }
        String newFileUrl = null;
        if (fileBytes != null && fileBytes.length > 0) {
            //重新定义文件名-当前时间:如2018-6-9-10-30-57-104000000.jpg
            LocalDateTime today = LocalDateTime.now();
            String tempNewName = getRandomFileName();
            //保存图片服务器的请求路径
            newFileUrl = path + tempNewName + "." + nameSuffix;
            try {
                //若是文件服务器，走这里
                if (verifyUrlByRegEx(path)) {
                    //实例化一个Jersey
                    Client client = new Client();
                    //设置请求路径
                    WebResource resource = client.resource(newFileUrl);
                    //发送post get put
                    resource.put(String.class, fileBytes);
                } else {
                    File dirPath = new File(path);
                    if (!dirPath.exists()) {
                        dirPath.mkdirs();
                    }
                    dirPath.setWritable(true, false);
                    OutputStream outputStream = new FileOutputStream(newFileUrl);
                    outputStream.write(fileBytes);
                    outputStream.flush();
                    outputStream.close();
//                    newFileUrl = newFileUrl.replace(fileHost,"");
                }
            } catch (Exception e) {
                log.error("文件上传失败，msg:{}", e.getMessage());
                throw new RuntimeException();
            }
        }
        return newFileUrl;
    }

    /**
     * URL以http或者https开头返回true,否则返回false
     *
     * @param path
     * @return
     */
    public static boolean verifyUrlByRegEx(String path) {
        Pattern pattern = Pattern.compile(PATTERN_REGEX);
        Matcher matcher = pattern.matcher(path);
        return matcher.matches();
    }


    /**
     * 判断是否是支持的图片格式
     *
     * @param type
     * @return
     */
    public static boolean judgeImageType(String type) {
        String[] imgs = {"jpg", "jpeg"};
        for (String imageType : imgs) {
            if (imageType.equals(type.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static String getRandomFileName() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+UUID.randomUUID().toString().replace("-","").substring(27);
    }

    /**
     * 下载功能
     */
    public static long loadFile(String url, HttpServletResponse response) {
        try {
// 取得文件的后缀名。
            String ext = url.substring(url.lastIndexOf("."));
            String filename = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".")) + ext;
// 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
// response.addHeader("Content-Length", "" + file.length());
            //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("multipart/form-data");
            OutputStream outputStream = response.getOutputStream();
            if (verifyUrlByRegEx(url)) {
                URL url1 = new URL(url);
                URLConnection urlConnection = url1.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                byte[] buff = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(buff)) > 0) {
                    outputStream.write(buff, 0, len);
                }
            } else {
                InputStream inputStream = new FileInputStream(url);
                byte[] buff = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(buff)) > 0) {
                    outputStream.write(buff, 0, len);
                }
            }
            outputStream.flush();
            outputStream.close();
            return 1;
        } catch (IOException e) {
            log.error("下载文件失败：{}", e.toString());
        }
        return -1;
    }

    /**
     * @param imgs        要压缩的文件的路径列表
     * @param destZipPath 临时保存压缩的文件的路径
     */
    public void zipFile(List<String> imgs, String destZipPath) {
        File zipFile = new File(destZipPath);
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            byte[] tempBuff = null;
            FileInputStream fileInputStream = null;
            for (String img : imgs) {
                //获取要压缩的图片流:这里可以根据不同服务器的获取方式去获取图片，比如网络图片。可以使用URL.openConnection();我这里是直接读取本地的
                fileInputStream = new FileInputStream(img);
                int len = -1;
                tempBuff = new byte[2048];
                //分别设置压缩包中每一文件的文件名（必须含文件后缀）
                zipOutputStream.putNextEntry(new ZipEntry(img.substring(img.lastIndexOf("/") + 1)));
                //将图片添加到压缩包中
                while ((len = fileInputStream.read(tempBuff)) != -1) {
                    zipOutputStream.write(tempBuff);
                }
                zipOutputStream.closeEntry();
                fileInputStream.close();
            }
            zipOutputStream.close();
            //删除压缩包
            zipFile.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
