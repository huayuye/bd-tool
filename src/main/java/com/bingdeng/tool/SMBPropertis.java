package com.bingdeng.tool;

import lombok.Data;

/**
 * @Author: Fran
 * @Date: 2019/2/22
 * @Desc:
 **/
@Data
public class SMBPropertis {
    private String smbUsername="comma";
    private String smbPassword="comma";

    //主要用于jcifs协议
    private String smbLogFilepath="smb://10.60.178.251/share/log/";
    private String smbImagePath="smb://10.60.178.251/share/image/";


    //    private String smbUrl;主要用于smbj协议
    private String smbShareFolder="share";
    private Integer smbPort=445;
    private String smbDomain="10.60.178.251";
    private String smbProtocol="smb";
    private String smbLogFolder="log/";
    private String smbImageFolder="image/";
    private String smbPdfFolder="pdf/";
}
