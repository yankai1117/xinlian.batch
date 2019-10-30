package com.example.batch.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AES {
    /**
     * AES加密字符串
     *
     * @param content
     *            需要被加密的字符串
     * @param password
     *            加密需要的密码
     * @return 密文
     * @throws InvalidAlgorithmParameterException
     */
    public static String encrypt(String content, String password) throws InvalidAlgorithmParameterException {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者

            kgen.init(128, new SecureRandom(password.getBytes()));// 利用用户密码作为随机数初始化出
            // 128位的key生产者
            //加密没关系，SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以解密只要有password就行
//            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
//            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥，如果此密钥不支持编码，则返回// null。
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
//            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化为加密模式的密码器
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(password.getBytes()));// 初始化为加密模式的密码器
            byte[] result = cipher.doFinal(byteContent);// 加密
            return Base64.encodeBase64String(result);

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
