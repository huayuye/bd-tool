package com.bingdeng.tool;

/**
 * @Author: Fran
 * @Date: 2019/1/18
 * @Desc:
 **/
public class StringUtil {

    public static Boolean hasBlank(String ...strAttr){
        for(String str:strAttr){
            if(str==null || str=="" || str.trim()==""){
                return true;
            }
        }
        return false;
    }


}
