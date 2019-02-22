package com.bingdeng.tool;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Fran
 * @Date: 2019/1/9
 * @Desc:
 **/
@Slf4j
public class HttpClientUtil {
    private static int connectTimeOut = 30000;
    private static int socketTimeOut = 30000;
    private static int requestTimeOut = 10000;


    /**
     * send http post
     *
     * @param url
     * @param map param map
     * @param encoding
     * @return
     * @throws ParseException
     */
    public static String sendHttpPost(String url, Map<String,String> map, String encoding) throws ParseException {
        String body = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try{
//create httpclient
            CloseableHttpClient client = HttpClients.createDefault();
//create post way
            httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connectTimeOut).setConnectionRequestTimeout(requestTimeOut)
                    .setSocketTimeout(socketTimeOut).build();
            httpPost.setConfig(requestConfig);
//built param :发送Json格式的数据请求
//List<NameValuePair> nvps = getNameValuePairs(map);
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                jsonObject.put(entry.getKey(),entry.getValue());
            }
            //set param
            StringEntity stringEntity = new StringEntity(jsonObject.toString(), encoding);
            stringEntity.setContentEncoding(encoding);
            httpPost.setEntity(stringEntity);
//httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
            log.info("request url:{},params:{}",url,map==null?null:map.toString());
//set header
//【Content-type】、【User-Agent】
            httpPost.setHeader("Content-type", "application/json;charset=utf-8");
            response = client.execute(httpPost);
//get result content
            HttpEntity entity = response.getEntity();
            if (entity != null) {
//change result to String
                body = EntityUtils.toString(entity, encoding);
            }
        }catch (Exception e){
            log.error("send httpPost fail,errorMsg:{}",e.toString());
        }finally {
// releaseConnection
            if(httpPost != null){
                try {
                    httpPost.releaseConnection();
                } catch (Exception e) {
                    log.error("close httpPost fail,errorMsg:{}",e.toString());
                }
            }
        }
        return body;
    }

    /**
     * send https post ignoreCertificate
     *
     * @param url
     * @param map paramMap
     * @param encoding
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static String sendHttpsPostIgnoreCertificate(String url, Map<String,String> map,String encoding){
        String body = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try{
//create https by ignoreCertificate
            SSLContext sslcontext = createIgnoreVerifySSL();
// set http and https socket factory
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);
//create httpclient
            CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
//create post way
            httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connectTimeOut).setConnectionRequestTimeout(requestTimeOut)
                    .setSocketTimeout(socketTimeOut).build();
            httpPost.setConfig(requestConfig);
//built param
//List<NameValuePair> nvps = getNameValuePairs(map);
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                jsonObject.put(entry.getKey(),entry.getValue());
            }
            //set param:发送Json格式的数据请求
            StringEntity stringEntity = new StringEntity(jsonObject.toString(), encoding);
            stringEntity.setContentEncoding(encoding);
            httpPost.setEntity(stringEntity);
//httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
            log.info("request url:{},params:{}",url,map==null?null:map.toString());
//set header
//【Content-type】、【User-Agent】
            httpPost.setHeader("Content-type", "application/json;charset=utf-8");
//excute
            response = client.execute(httpPost);
//get result
            HttpEntity entity = response.getEntity();
            if (entity != null) {
//change to String
                body = EntityUtils.toString(entity, encoding);
            }
            EntityUtils.consume(entity);
        }catch (Exception e){
            log.error("send httpPost fail,errorMsg:{}",e.toString());
        }finally {
// releaseConnection
            if(httpPost != null){
                try {
                    httpPost.releaseConnection();
                } catch (Exception e) {
                    log.error("close httpPost fail,errorMsg:{}",e.toString());
                }
            }
        }
        return body;
    }

    /**
     * send https post,withCertificate
     *
     * @param url
     * @param map paramMap
     * @param encoding
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public static String sendHttpsPostWithCertificate(String url, Map<String,String> map,String encoding){
        String body = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try{
//first param:certificate file path
//second param:tomcat is password,you can replace yourself,if your password is empty，you can use "nopassword"
            SSLContext sslcontext = customCertificate("D:\\keys\\wsriakey", "tomcat");
// set http and https socket factory
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);

//create httpclient
            CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
//create post way
            httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connectTimeOut).setConnectionRequestTimeout(requestTimeOut)
                    .setSocketTimeout(socketTimeOut).build();
            httpPost.setConfig(requestConfig);
//built param
            List<NameValuePair> nvps = getNameValuePairs(map);
            log.info("request url:{},params:{}",url,nvps.toString());
//set param
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
//set header
//【Content-type】、【User-Agent】
            httpPost.setHeader("Content-type", "application/json;charset=utf-8");

//excute
            response = client.execute(httpPost);
//get result
            HttpEntity entity = response.getEntity();
            if (entity != null) {
//change to String
                body = EntityUtils.toString(entity, encoding);
            }
            EntityUtils.consume(entity);
        }catch (Exception e){
            log.error("send httpPost fail,errorMsg:{}",e.toString());
        }finally {
// releaseConnection
            if(httpPost != null){
                try {
                    httpPost.releaseConnection();
                } catch (Exception e) {
                    log.error("close httpPost fail,errorMsg:{}",e.toString());
                }
            }
        }
        return body;
    }

    /**
     * send http get
     */
    public static String sendHttpGet(String url, Map<String,String> map,String encoding) {
        HttpGet httpGet = null;
        String body = null;
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
//built param
            List<NameValuePair> nvps = getNameValuePairs(map);
            String paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(nvps, encoding));
            url = url + "?" + paramsStr;
// create httpGet
            httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connectTimeOut).setConnectionRequestTimeout(requestTimeOut)
                    .setSocketTimeout(socketTimeOut).build();
            httpGet.setConfig(requestConfig);
            log.info("request uri is:{}",httpGet.getURI());
// excute
            CloseableHttpResponse response = httpclient.execute(httpGet);
// get result
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity,encoding);
            }
        } catch (Exception e) {
            log.error("send httpGet fail,errorMsg:{}",e.toString());
        } finally {
// releaseConnection
            if(httpGet != null){
                try {
                    httpGet.releaseConnection();
                } catch (Exception e) {
                    log.error("close httpGet fail,errorMsg:{}",e.toString());
                }
            }
        }
        return body;
    }


    /**
     * send https get,ignoreCertificate
     *
     * @param url
     * @param map paramMap
     * @param encoding
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static String sendHttpsGetIgnoreCertificate(String url, Map<String,String> map,String encoding){
        String body = null;
        HttpGet httpGet = null;
        CloseableHttpResponse response = null;
        try{
//create https by ignoreCertificate
            SSLContext sslcontext = createIgnoreVerifySSL();
//set http and https socket factory
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);
//create httpClient
            CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
//create post way
            httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connectTimeOut).setConnectionRequestTimeout(requestTimeOut)
                    .setSocketTimeout(socketTimeOut).build();
            httpGet.setConfig(requestConfig);
//built param
            List<NameValuePair> nvps = getNameValuePairs(map);
            String paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(nvps, encoding));
            url = url + "?" + paramsStr;
            log.info("request uri is:{}",httpGet.getURI());
//excute
            response = client.execute(httpGet);
//get result
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, encoding);
            }
        }catch (Exception e){
            log.error("send httpGet fail,errorMsg:{}",e.toString());
        }finally {
// releaseConnection
            if(httpGet != null){
                try {
                    httpGet.releaseConnection();
                } catch (Exception e) {
                    log.error("close httpGet fail,errorMsg:{}",e.toString());
                }
            }
        }
        return body;
    }

    /**
     * send https get,withCertificate
     *
     * @param url
     * @param map paramMap
     * @param encoding
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public static String sendHttpsGetWithCertificate(String url, Map<String,String> map,String encoding){
        String body = null;
        HttpGet httpGet = null;
        CloseableHttpResponse response = null;
        try{
//first param:certificate file path
//second param:password,tomcat is password,you can replace it with yourself,if your password is empty，you can use "nopassword"
            SSLContext sslcontext = customCertificate("D:\\keys\\wsriakey", "tomcat");
//set http and https socket factory
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);

//create httpclient
            CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
//create get way
            httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connectTimeOut).setConnectionRequestTimeout(requestTimeOut)
                    .setSocketTimeout(socketTimeOut).build();
            httpGet.setConfig(requestConfig);
//built param
            List<NameValuePair> nvps = getNameValuePairs(map);
            String paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(nvps, encoding));
            url = url + "?" + paramsStr;
            log.info("request uri is:{}",httpGet.getURI());
//excute
            response = client.execute(httpGet);
//get result
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, encoding);
            }
            EntityUtils.consume(entity);
        }catch (Exception e){
            log.error("send httpGet fail,errorMsg:{}",e.toString());
        }finally {
// releaseConnection
            if(httpGet != null){
                try {
                    httpGet.releaseConnection();
                } catch (Exception e) {
                    log.error("close httpGet fail,errorMsg:{}",e.toString());
                }
            }
        }
        return body;
    }

    /**
     * built param
     * @param map
     * @return
     */
    private static List<NameValuePair> getNameValuePairs(Map<String, String> map) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if(map!=null){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        return nvps;
    }

    /**
     * get response HTTP entity content
     * @param response
     * @param encoding
     * @return
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    private static String getHttpEntityContent(HttpResponse response, String encoding) throws IOException, UnsupportedEncodingException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream is = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line + "\n");
                line = br.readLine();
            }
            br.close();
            is.close();
            return sb.toString();
        }
        return null;
    }

    /**
     * ignoreVerifySSL
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");
// implement X509TrustManager interface to bypass validation，not reWrite any method
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }
            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }

    /**
     * create Certificate
     *
     * @param keyStorePath
     * @param keyStorepass
     * @return
     */
    private static SSLContext customCertificate(String keyStorePath, String keyStorepass){
        SSLContext sc = null;
        FileInputStream instream = null;
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            instream = new FileInputStream(new File(keyStorePath));
            trustStore.load(instream, keyStorepass.toCharArray());
            sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
        } catch (KeyStoreException | NoSuchAlgorithmException| CertificateException | IOException | KeyManagementException e) {
            e.printStackTrace();
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
                log.error("close streamfail,errorMsg:{}",e.toString());
            }
        }
        return sc;
    }

}
