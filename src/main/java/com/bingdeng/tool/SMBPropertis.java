package com.bingdeng.tool;

import lombok.Data;

/**
 * @Author: Fran
 * @Date: 2019/2/22
 * @Desc:
 **/
@Data
public class SMBPropertis {
    private String smbUsername="Administrator";
    private String smbPassword="password";

    //主要用于jcifs协议
    private String smbLogFilepath="smb://192.168.1.25/share/log/";
    private String smbImagePath="smb://192.168.1.25/share/image/";


    //    private String smbUrl;主要用于smbj协议
    private String smbShareFolder="share";
    private Integer smbPort=445;
    private String smbDomain="192.168.1.25";
    private String smbProtocol="smb";
    private String smbLogFolder="log/";
    private String smbImageFolder="image/";
}
