package com.bingdeng.tool;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: Fran
 * @Date: 2019/4/29
 * @Desc:
 **/
public class URLUtil {

    public static final int ConnectTimeout=3000;
    public static final int ReadTimeout=3000;
    public static String ContentType_JSON="application/json";
    public static String ContentType_Application_Form="application/x-www-form-urlencoded";
    public static String ContentType_Multipart="multipart/form-data";
    public static String ContentType_Text_Plain="text/plain";


    public static String get(String url,LinkedHashMap<String,String> headers, LinkedHashMap<String,String> params){
      return   get(url,headers, params,null,null);
    }
    public static String get(String url, LinkedHashMap<String,String> params){
      return   get(url,null, params,null,null);
    }
    public static String get(String url,LinkedHashMap<String,String> headers, LinkedHashMap<String,String> params,Integer connectTimeout,Integer readTimeout){
        BufferedReader reader = null;
        try {
            URL httpUrl = null;
            Map.Entry<String,String> entry = null;
            //set url param
            if(params!=null && !params.isEmpty()){
                String param = "";
                for(Iterator<Map.Entry<String,String>> it = params.entrySet().iterator();it.hasNext();){
                    entry=it.next();
                    param=param+(entry.getKey()+"="+entry.getValue()+"&");
                }
                param=param.substring(0,param.length()-1);
                url=url+"?"+URLEncoder.encode(param);
            }
            httpUrl = new URL(url);
            //create connection
            HttpURLConnection connection = (HttpURLConnection)httpUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setAllowUserInteraction(true);
            if(connectTimeout!=null){
                connection.setConnectTimeout(connectTimeout);
            }
            if (readTimeout!=null){
                connection.setReadTimeout(readTimeout);
            }
            //set header
            if(headers!=null && !headers.isEmpty()){
                for(Iterator<Map.Entry<String,String>> it = headers.entrySet().iterator();it.hasNext();){
                    entry=it.next();
                    connection.setRequestProperty(entry.getKey(),entry.getValue());
                }
            }
            connection.connect();
            if(HttpURLConnection.HTTP_OK==connection.getResponseCode()){
               reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
               String temp = "";
               StringBuffer responseResult = new StringBuffer();
               while ((temp=reader.readLine())!=null){
                   responseResult.append(temp);
               }
//                System.out.println(responseResult.toString());
               return responseResult.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String post(String url,LinkedHashMap<String,String> headers, LinkedHashMap<String,String> params,String contentType){
       return post(url,headers,params,contentType,null,null);
    }
    public static String post(String url, LinkedHashMap<String,String> params,String contentType){
       return post(url,null,params,contentType,null,null);
    }
    public static String post(String url,LinkedHashMap<String,String> headers, LinkedHashMap<String,String> params,String contentType,Integer connectTimeout,Integer readTimeout){
        PrintWriter printWriter = null;
        BufferedReader reader = null;
        try {
            URL httpUrl = null;
            Map.Entry<String,String> entry = null;
            String param = "";
            httpUrl = new URL(url);
            //create connection
            HttpURLConnection connection = (HttpURLConnection)httpUrl.openConnection();
            connection.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setAllowUserInteraction(true);
            if(connectTimeout!=null){
                connection.setConnectTimeout(connectTimeout);
            }
            if (readTimeout!=null){
                connection.setReadTimeout(readTimeout);
            }
            //set header
            if(headers!=null && !headers.isEmpty()){
                for(Iterator<Map.Entry<String,String>> it = headers.entrySet().iterator();it.hasNext();){
                    entry=it.next();
                    connection.setRequestProperty(entry.getKey(),entry.getValue());
                }
            }
            //set param
            if(params!=null && !params.isEmpty()){
                if(ContentType_JSON.equals(contentType)){
                    JSONObject jsonObject = new JSONObject();
                    for(Iterator<Map.Entry<String,String>> it = params.entrySet().iterator();it.hasNext();){
                        entry=it.next();
                        jsonObject.put(entry.getKey(),entry.getValue());
                    }
                    param=jsonObject.toString();
                }else if(ContentType_Application_Form.equals(contentType)){
                    for(Iterator<Map.Entry<String,String>> it = params.entrySet().iterator();it.hasNext();){
                        entry=it.next();
                        param=param+(entry.getKey()+"="+entry.getValue()+"&");
                    }
                    param=param.substring(0,param.length()-1);
                }
                //post请求 需要将参数放到connection的OutputStream
                printWriter = new PrintWriter(connection.getOutputStream());
                printWriter.append(param);
                printWriter.flush();
            }
            connection.connect();
            if(HttpURLConnection.HTTP_OK==connection.getResponseCode()){
                reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
                String temp = "";
                StringBuffer responseResult = new StringBuffer();
                while ((temp=reader.readLine())!=null){
                    responseResult.append(temp);
                }
//                System.out.println(responseResult.toString());
                return responseResult.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(printWriter!=null){
                printWriter.close();
            }
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }





    public static void main(String[] args) {
//        http://us.lifefun.in/us/proposal-wish?page=1&limit=20
//        LinkedHashMap map = new LinkedHashMap();
//        map.put("name","1");
//        map.put("wish","20");
//        get("http://us.lifefun.in/us/proposal-wish",map);

//        post("http://us.lifefun.in/us/proposal-wish",null,map,ContentType_JSON,null,null);

    }

}
