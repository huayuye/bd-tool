package com.bingdeng.tool;


import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import jcifs.smb.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.EnumSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Fran
 * @Date: 2019/1/9
 * @Desc:
 **/
@Slf4j
public class SMBFileUtil {
    @SuppressWarnings("static-access")
//    public static void main(String[] args) {
//
//        System.out.println(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+UUID.randomUUID().toString().replace("-","").substring(27));
//
////        readFile();
////        writeNetFile("xxxxxxxxxxx", "@xxxxxxxxxx", "https://avatar.csdn.net/9/B/1/3_u013092293.jpg", "smb://192.xxx.xxxx.xxx/share/test.jpg");
//    }

    /**
     * Read the SMB file to local，读取smb文件到本地
     *
     * @param username         username smbUsername
     * @param password         pwd smbPwd
     * @param smbSourceFileUrl smbFile path:smb服务器上文件，example: smb://127.0.0.1/share/xxxx.txt
     * @param destFileUrl      localFile path:本机文件路径
     * @return
     */
    public static boolean readFile(String username, String password, String smbSourceFileUrl, String destFileUrl) {
        try {
            return readFile(username, password, smbSourceFileUrl, new FileOutputStream(destFileUrl));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Read the SMB file to local，读取smb文件到目标文件
     *
     * @param username         smbUsername
     * @param password         smbPwd
     * @param smbSourceFileUrl mbFile path:smb服务器上文件，example: smb://127.0.0.1/share/xxxx.txt
     * @param outputStream     file OutPutStream:目标文件流
     * @return
     */
    public static boolean readFile(String username, String password, String smbSourceFileUrl, OutputStream outputStream) {
        BufferedOutputStream out = null;
        SmbFileInputStream in = null;
        try {
            //此方式不支持有特俗字符的密码
            //SmbFile smbFile = new SmbFile("smb://username:pwd@192.xxx.xxx.xxx/share/xxxx.txt");
            //由于密码中有特殊字符，因此需要使用NtlmPasswordAuthentication,先登录验证
            //局域网共享文件，读文件
            SmbFile smbFile = new SmbFile(smbSourceFileUrl, getNtlmPasswordAuthentication(null, username, password));
            // 通过 smbFile.isDirectory();isFile()可以判断smbFile是文件还是文件夹
            // 得到文件的大小
            int length = smbFile.getContentLength();
            int len = -1;
            byte buffer[] = new byte[2048];
            // 建立smb文件输入流
            in = new SmbFileInputStream(smbFile);
            //写出到本地
            out = new BufferedOutputStream(outputStream);
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return true;
        } catch (SmbAuthException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Upload local files to SMB server，上传本地文件到smb服务器
     *
     * @param username       smbUsername
     * @param password       smbPwd
     * @param sourceFileUrl  localFile path:需上传的本地文件路径
     * @param smbDestFileUrl smbFile path:需写入的smb 服务器文件路径 example: smb://127.0.0.1/share/xxxx.txt
     * @return
     */
    public static boolean writeFile(String username, String password, String sourceFileUrl, String smbDestFileUrl) {
        try {
            return writeFile(username, password, new FileInputStream(sourceFileUrl), smbDestFileUrl);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Upload net files to SMB server，上传net文件到smb服务器
     *
     * @param username       smbUsername
     * @param password       smbPwd
     * @param sourceFileUrl  netFile path:需上传的网络文件路径
     * @param smbDestFileUrl smbFile path:需写入的smb 服务器文件路径 example: smb://127.0.0.1/share/xxxx.txt
     * @return
     */
    public static boolean writeNetFile(String username, String password, String sourceFileUrl, String smbDestFileUrl) {
        try {
            URL url = new URL(sourceFileUrl);
            URLConnection urlConnection = url.openConnection();
            return writeFile(username, password, urlConnection.getInputStream(), smbDestFileUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Upload local files to SMB server，上传文件到smb服务器
     *
     * @param username       smbUsername
     * @param password       smbPwd
     * @param inputStream    uploadFile InputStream, 需上传的文件流
     * @param smbDestFileUrl smbFile path:需写入的smb 服务器文件路径 example: smb://127.0.0.1/share/xxxx.txt
     * @return
     */
    public static boolean writeFile(String username, String password, InputStream inputStream, String smbDestFileUrl) {
        SmbFileOutputStream out = null;
        BufferedInputStream in = null;
        try {

            //此方式不支持有特俗字符的密码
            //SmbFile smbFileOut = new SmbFile("smb://username:pwd@192.xxx.xxx.xxx/share/xxxx.txt");
            //局域网共享文件，写文件，写入远程服务器的文件，没有则新建
            SmbFile smbFileOut = new SmbFile(smbDestFileUrl, getNtlmPasswordAuthentication(null, username, password));
            // 通过 smbFile.isDirectory();isFile()可以判断smbFile是文件还是文件夹
            if (!smbFileOut.exists()) {
                smbFileOut.createNewFile();
            }
            //创建smb文件的输出流
            out = new SmbFileOutputStream(smbFileOut);
            //上传本地文件到smb服务器
            in = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[2048];
            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                //将内容写入到smb服务上的文件
                out.write(buffer, 0, len);
            }
            return true;
        } catch (SmbAuthException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    /**
     * 创建smb登录校验
     *
     * @param smbIP    example:127.0.0.1
     * @param username
     * @param password
     * @return
     */
    public static NtlmPasswordAuthentication getNtlmPasswordAuthentication(String smbIP, String username, String password) {
        return new NtlmPasswordAuthentication(smbIP, username, password);
    }

    /**
     * Read the SMB file to local，读取smb文件,返回字节数组
     *
     * @param username         username smbUsername
     * @param password         pwd smbPwd
     * @param smbSourceFileUrl smbFile path:smb服务器上文件，example: smb://127.0.0.1/share/xxxx.txt
     * @return
     */
    public static byte[] readFile(String username, String password, String smbSourceFileUrl) {
        BufferedOutputStream out = null;
        SmbFileInputStream in = null;
        try {
            //此方式不支持有特俗字符的密码
            //SmbFile smbFile = new SmbFile("smb://user:pwd@ip/path/file.txt");
            //由于密码中有特殊字符，因此需要使用NtlmPasswordAuthentication,先登录验证
            //局域网共享文件，读文件
            SmbFile smbFile = new SmbFile(smbSourceFileUrl, getNtlmPasswordAuthentication(null, username, password));
            // 通过 smbFile.isDirectory();isFile()可以判断smbFile是文件还是文件夹
            if (!smbFile.exists() || !smbFile.isFile()) {
                log.error("downloadFile don't exit or is not a file");
                return null;
            }
            // 得到文件的大小
            int length = smbFile.getContentLength();
            int len = -1;
            byte[] buffer = new byte[length];
            // 建立smb文件输入流
            in = new SmbFileInputStream(smbFile);
            in.read(buffer);
            return buffer;
        } catch (SmbAuthException e) {
            log.error("SmbAuth fail:{}", e.toString());
        } catch (Exception e) {
            log.error("read smb file to destFile fail:{}", e.toString());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("close BufferedOutputStream fail:{}", e.toString());
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("close SmbFileInputStream fail:{}", e.toString());
                }
            }
        }
        return null;
    }


    /**
     * SMBJ protocol
     * @param smbPropertis
     * @param sourceFolderUrl
     * @param mkdirFlag
     * @return
     */
    public static boolean isExistFolder(SMBPropertis smbPropertis, String sourceFolderUrl, boolean mkdirFlag){
        SmbConfig config = SmbConfig.builder().withTimeout(120, TimeUnit.SECONDS)
                .withTimeout(120, TimeUnit.SECONDS) // 超时设置读，写和Transact超时（默认为60秒）
                .withSoTimeout(180, TimeUnit.SECONDS) // Socket超时（默认为0秒）
                .withDfsEnabled(true)
                .build();

        // 如果不设置超时时间	SMBClient client = new SMBClient();
        SMBClient client = new SMBClient(config);
        try {
            Connection connection = client.connect(smbPropertis.getSmbDomain(),smbPropertis.getSmbPort());	// 如:123.123.123.123
            AuthenticationContext ac = new AuthenticationContext(smbPropertis.getSmbUsername(),smbPropertis.getSmbPassword().toCharArray(), smbPropertis.getSmbDomain());
            Session session = connection.authenticate(ac);
            // 连接共享文件夹
            DiskShare share = (DiskShare) session.connectShare(smbPropertis.getSmbShareFolder());
            if(share.folderExists(sourceFolderUrl)){
                return true;
            }
            //true,mkdir;
            if(mkdirFlag){
                share.mkdir(sourceFolderUrl);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("文件夹创建异常：{}",e.toString());
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return false;
    }


    /**
     * SMBJ protocol
     * @param smbPropertis
     * @param inputStream
     * @param smbDestFileUrl
     * @return
     */
    public static String writeFile(SMBPropertis smbPropertis, InputStream inputStream,String smbDestFileUrl) {
        String shareUrl = smbPropertis.getSmbProtocol()+"://"+smbPropertis.getSmbDomain()+((smbPropertis.getSmbPort()==null||smbPropertis.getSmbPort().equals(445))?"":(":"+String.valueOf(smbPropertis.getSmbPort())))+"/"+smbPropertis.getSmbShareFolder();
        // 设置超时时间(可选)
        SmbConfig config = SmbConfig.builder().withTimeout(120, TimeUnit.SECONDS)
                .withTimeout(120, TimeUnit.SECONDS) // 超时设置读，写和Transact超时（默认为60秒）
                .withSoTimeout(180, TimeUnit.SECONDS) // Socket超时（默认为0秒）
                .withDfsEnabled(true)
                .build();

        // 如果不设置超时时间	SMBClient client = new SMBClient();
        SMBClient client = new SMBClient(config);

        try {
            Connection connection = client.connect(smbPropertis.getSmbDomain(),smbPropertis.getSmbPort());	// 如:123.123.123.123
            AuthenticationContext ac = new AuthenticationContext(smbPropertis.getSmbUsername(),smbPropertis.getSmbPassword().toCharArray(), smbPropertis.getSmbDomain());
            Session session = connection.authenticate(ac);
            // 连接共享文件夹
            DiskShare share = (DiskShare) session.connectShare(smbPropertis.getSmbShareFolder());
//            smbDestFileUrl格式示例：folder/log/file.txt
            com.hierynomus.smbj.share.File smbFileWrite = share.openFile(smbDestFileUrl, EnumSet.of(AccessMask.GENERIC_WRITE), null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_CREATE, null);
//            InputStream in = new FileInputStream("E:/test/20181214172525942d0869f069e2c.png");
            OutputStream outputStream1 = smbFileWrite.getOutputStream();
            byte[] buffer = new byte[2048];
            int len = -1;
            while((len=inputStream.read(buffer))!=-1){
                //将内容写入到smb服务上的文件
                outputStream1.write(buffer,0,len);
            }
            outputStream1.flush();
            outputStream1.close();
            return shareUrl+"/"+smbDestFileUrl;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("文件存储异常：{}",e.toString());
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return "";
    }

    /**
     * SMBJ protocol
     * @param smbPropertis
     * @param smbSourceFileUrl
     * @param outputStream
     * @return
     */
    public static boolean readFile(SMBPropertis smbPropertis, String smbSourceFileUrl, OutputStream outputStream) {
        String shareUrl = smbPropertis.getSmbProtocol()+"://"+smbPropertis.getSmbDomain()+((smbPropertis.getSmbPort()==null||smbPropertis.getSmbPort().equals(445))?"":(":"+String.valueOf(smbPropertis.getSmbPort())))+"/"+smbPropertis.getSmbShareFolder();
        // 设置超时时间(可选)
        SmbConfig config = SmbConfig.builder().withTimeout(120, TimeUnit.SECONDS)
                .withTimeout(120, TimeUnit.SECONDS) // 超时设置读，写和Transact超时（默认为60秒）
                .withSoTimeout(180, TimeUnit.SECONDS) // Socket超时（默认为0秒）
                .withDfsEnabled(true)
                .build();
        // 如果不设置超时时间	SMBClient client = new SMBClient();
        SMBClient client = new SMBClient(config);

        try {
            Connection connection = client.connect(smbPropertis.getSmbDomain(),smbPropertis.getSmbPort());	// 如:123.123.123.123
            AuthenticationContext ac = new AuthenticationContext(smbPropertis.getSmbUsername(),smbPropertis.getSmbPassword().toCharArray(), smbPropertis.getSmbDomain());
            Session session = connection.authenticate(ac);
            // 连接共享文件夹
            DiskShare share = (DiskShare) session.connectShare(smbPropertis.getSmbShareFolder());
            String downFileName = smbSourceFileUrl.substring(smbSourceFileUrl.lastIndexOf("/")+1);
            String downFileFolder = smbSourceFileUrl.replace(shareUrl,"");
            downFileFolder = downFileFolder.replace(downFileName,"").substring(1);
//            downFileFolder格式示例:如（share/image/file.png,share是共享文件夹，则downFileFolder等于image/,downFileName等于file.png)
            for (FileIdBothDirectoryInformation f : share.list(downFileFolder, downFileName)) {
                String filePath =downFileFolder+ f.getFileName();
//                filePath格式示例：共享文件夹之后的文件路径，如（share/image/file.png,share是共享文件夹，则filePath等于image/file.png）
                if (share.fileExists(filePath)) {
                    log.info("正在下载文件:{}" , f.getFileName());

                    File smbFileRead = share.openFile(filePath, EnumSet.of(AccessMask.GENERIC_READ), null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN, null);
                    InputStream in = smbFileRead.getInputStream();
                    byte[] buffer = new byte[4096];
                    int len = 0;
                    while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                    outputStream.flush();
                    outputStream.close();
                    log.info("文件下载成功:{}",filePath);
                    return true;
                } else {
                    log.info("文件不存在:{}",filePath);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("文件读取异常：{}",e.toString());
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return false;
    }


}
