package com.bingdeng.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /**
     * 替换指定字符串以外的字符为空
     * @param str
     * @return
     */
    public static String replaceOtherString (String str){
        if(str==null || "".equals(str)){
            return "";
        }
        String regEx="[^1-9a-zA-Z_&*@! #\\-/]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return  m.replaceAll("").trim();
    }

}
