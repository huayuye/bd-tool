package com.bingdeng.tool;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Fran
 * @Date: 2019/3/28
 * @Desc:
 **/
public class DeEncryptUtil {


    private static final String BD_KEY_SHA = "SHA";
    private static final String BD_KEY_MD5 = "MD5";

    /**
     * MD5加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encryptMD5(byte[] data) throws Exception {

        MessageDigest md5 = MessageDigest.getInstance(BD_KEY_MD5);
        md5.update(data);
        return md5.digest();

    }

    /**
     * SHA加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encryptSHA(byte[] data) throws Exception {

        MessageDigest sha = MessageDigest.getInstance(BD_KEY_SHA);
        sha.update(data);
        return sha.digest();
    }

    public static class BASE64{
        /**
         * BASE64解密
         *
         * @param key
         * @return
         * @throws Exception
         */
        public static byte[] decode(String key) throws Exception {
            return (new BASE64Decoder()).decodeBuffer(key);
        }

        /**
         * BASE64加密
         *
         * @param key
         * @return
         * @throws Exception
         */
        public static String encode(byte[] key) throws Exception {
            return (new BASE64Encoder()).encodeBuffer(key);
        }
    }

    public static class MAC{
        /**
         * MAC算法可选以下多种算法
         *
         * <pre>
         * HmacMD5
         * HmacSHA1
         * HmacSHA256
         * HmacSHA384
         * HmacSHA512
         * </pre>
         */
        private static final String BD_KEY_MAC = "HmacMD5";
        /**
         * 初始化HMAC密钥
         *
         * @return
         * @throws Exception
         */
        public static String initHMacKey() throws Exception {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(BD_KEY_MAC);

            SecretKey secretKey = keyGenerator.generateKey();
            return BASE64.encode(secretKey.getEncoded());
        }

        /**
         * HMAC加密
         *
         * @param data
         * @param key
         * @return
         * @throws Exception
         */
        public static byte[] encryptHMAC(byte[] data, String key) throws Exception {

            SecretKey secretKey = new SecretKeySpec(BASE64.decode(key), BD_KEY_MAC);
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);

            return mac.doFinal(data);

        }
    }

    public static class AES{
        /**
         * 加密
         * 1.构造密钥生成器
         * 2.根据ecnodeRules规则初始化密钥生成器
         * 3.产生密钥
         * 4.创建和初始化密码器
         * 5.内容加密
         * 6.返回字符串
         * @param encodeRules
         * @param content
         * @return
         * @throws NoSuchAlgorithmException
         * @throws NoSuchPaddingException
         * @throws InvalidKeyException
         * @throws UnsupportedEncodingException
         * @throws BadPaddingException
         * @throws IllegalBlockSizeException
         */
        public static String encode(String encodeRules,String content) throws Exception {
                //1.构造密钥生成器，指定为AES算法,不区分大小写
                KeyGenerator keygen=KeyGenerator.getInstance("AES");
                //2.根据ecnodeRules规则初始化密钥生成器
                //生成一个128位的随机源,根据传入的字节数组
                keygen.init(128, new SecureRandom(encodeRules.getBytes()));
                //3.产生原始对称密钥
                SecretKey original_key=keygen.generateKey();
                //4.获得原始对称密钥的字节数组
                byte [] raw=original_key.getEncoded();
                //5.根据字节数组生成AES密钥
                SecretKey key=new SecretKeySpec(raw, "AES");
                //6.根据指定算法AES自成密码器
                Cipher cipher=Cipher.getInstance("AES");
                //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
                cipher.init(Cipher.ENCRYPT_MODE, key);
                //8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
                byte [] byte_encode=content.getBytes("utf-8");
                //9.根据密码器的初始化方式--加密：将数据加密
                byte [] byte_AES=cipher.doFinal(byte_encode);
                //10.将加密后的数据转换为字符串
                //这里用Base64Encoder中会找不到包
                //解决办法：
                //在项目的Build path中先移除JRE System Library，再添加库JRE System Library，重新编译后就一切正常了。
                String AES_encode=new String(BASE64.encode(byte_AES));
                //11.将字符串返回
                return AES_encode;
        }

        /**
         * 解密
         * 解密过程：
         * 1.同加密1-4步
         * 2.将加密后的字符串反纺成byte[]数组
         * 3.将加密内容解密
         * @param encodeRules
         * @param content
         * @return
         * @throws NoSuchAlgorithmException
         * @throws NoSuchPaddingException
         * @throws InvalidKeyException
         * @throws IOException
         * @throws BadPaddingException
         * @throws IllegalBlockSizeException
         */
        public static String decode(String encodeRules,String content) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
                //1.构造密钥生成器，指定为AES算法,不区分大小写
                KeyGenerator keygen=KeyGenerator.getInstance("AES");
                //2.根据ecnodeRules规则初始化密钥生成器
                //生成一个128位的随机源,根据传入的字节数组
                keygen.init(128, new SecureRandom(encodeRules.getBytes()));
                //3.产生原始对称密钥
                SecretKey original_key=keygen.generateKey();
                //4.获得原始对称密钥的字节数组
                byte [] raw=original_key.getEncoded();
                //5.根据字节数组生成AES密钥
                SecretKey key=new SecretKeySpec(raw, "AES");
                //6.根据指定算法AES自成密码器
                Cipher cipher=Cipher.getInstance("AES");
                //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
                cipher.init(Cipher.DECRYPT_MODE, key);
                //8.将加密并编码后的内容解码成字节数组
                byte [] byte_content= new BASE64Decoder().decodeBuffer(content);
                /*
                 * 解密
                 */
                byte [] byte_decode=cipher.doFinal(byte_content);
                String AES_decode=new String(byte_decode,"utf-8");
                return AES_decode;
        }
    }


    public static class RSA{
        private static final String BD_KEY_ALGORITHM = "RSA";
        private static final String BD_SIGNATURE_ALGORITHM = "MD5withRSA";

        private static final String RSA_PUBLIC_KEY = "BDRSAPublicKey";
        private static final String RSA_PRIVATE_KEY = "BDRSAPrivateKey";

        /**
         * 用私钥对信息生成数字签名
         *
         * @param data
         *            加密数据
         * @param privateKey
         *            私钥
         *
         * @return
         * @throws Exception
         */
        public static String sign(byte[] data, String privateKey) throws Exception {
            // 解密由base64编码的私钥
            byte[] keyBytes = BASE64.decode(privateKey);
            // 构造PKCS8EncodedKeySpec对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            // KEY_ALGORITHM 指定的加密算法
            KeyFactory keyFactory = KeyFactory.getInstance(BD_KEY_ALGORITHM);
            // 取私钥匙对象
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // 用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(BD_SIGNATURE_ALGORITHM);
            signature.initSign(priKey);
            signature.update(data);
            return BASE64.encode(signature.sign());
        }

        /**
         * 校验数字签名
         *
         * @param data
         *            加密数据
         * @param publicKey
         *            公钥
         * @param sign
         *            数字签名
         *
         * @return 校验成功返回true 失败返回false
         * @throws Exception
         *
         */
        public static boolean verify(byte[] data, String publicKey, String sign)
                throws Exception {
            // 解密由base64编码的公钥
            byte[] keyBytes = BASE64.decode(publicKey);
            // 构造X509EncodedKeySpec对象
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            // KEY_ALGORITHM 指定的加密算法
            KeyFactory keyFactory = KeyFactory.getInstance(BD_KEY_ALGORITHM);
            // 取公钥匙对象
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance(BD_SIGNATURE_ALGORITHM);
            signature.initVerify(pubKey);
            signature.update(data);
            // 验证签名是否正常
            return signature.verify(BASE64.decode(sign));
        }

        /**
         * 解密<br>
         * 用私钥解密
         *
         * @param data
         * @param key
         * @return
         * @throws Exception
         */
        public static byte[] decryptByPrivateKey(byte[] data, String key)
                throws Exception {
            // 对密钥解密
            byte[] keyBytes = BASE64.decode(key);
            // 取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(BD_KEY_ALGORITHM);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        }

        /**
         * 解密<br>
         * 用公钥解密
         *
         * @param data
         * @param key
         * @return
         * @throws Exception
         */
        public static byte[] decryptByPublicKey(byte[] data, String key)
                throws Exception {
            // 对密钥解密
            byte[] keyBytes = BASE64.decode(key);
            // 取得公钥
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(BD_KEY_ALGORITHM);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        }

        /**
         * 加密<br>
         * 用公钥加密
         *
         * @param data
         * @param key
         * @return
         * @throws Exception
         */
        public static byte[] encryptByPublicKey(byte[] data, String key)
                throws Exception {
            // 对公钥解密
            byte[] keyBytes = BASE64.decode(key);
            // 取得公钥
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(BD_KEY_ALGORITHM);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        }

        /**
         * 加密<br>
         * 用私钥加密
         *
         * @param data
         * @param key
         * @return
         * @throws Exception
         */
        public static byte[] encryptByPrivateKey(byte[] data, String key)
                throws Exception {
            // 对密钥解密
            byte[] keyBytes = BASE64.decode(key);
            // 取得私钥
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(BD_KEY_ALGORITHM);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        }

        /**
         * 取得私钥
         *
         * @param keyMap
         * @return
         * @throws Exception
         */
        public static String getPrivateKey(Map<String, Object> keyMap)
                throws Exception {
            Key key = (Key) keyMap.get(RSA_PRIVATE_KEY);
            return BASE64.encode(key.getEncoded());
        }

        /**
         * 取得公钥
         *
         * @param keyMap
         * @return
         * @throws Exception
         */
        public static String getPublicKey(Map<String, Object> keyMap)
                throws Exception {
            Key key = (Key) keyMap.get(RSA_PUBLIC_KEY);
            return BASE64.encode(key.getEncoded());
        }

        /**
         * 初始化密钥
         *
         * @return
         * @throws Exception
         */
        public static Map<String, Object> initKey() throws Exception {
            KeyPairGenerator keyPairGen = KeyPairGenerator
                    .getInstance(BD_KEY_ALGORITHM);
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            // 公钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            // 私钥
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            Map<String, Object> keyMap = new HashMap<String, Object>(2);
            keyMap.put(RSA_PUBLIC_KEY, publicKey);
            keyMap.put(RSA_PRIVATE_KEY, privateKey);
            return keyMap;
        }

    }

}
