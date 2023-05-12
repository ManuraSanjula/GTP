package com.gtpuser.gtpuser.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Encryption {
    static public final SecretKey KEY = AESUtil.generateKey(128);
    static public final IvParameterSpec IVPARAMETERSPEC = AESUtil.generateIv();
    static public final String ALGORITHM = "AES/CBC/PKCS5Padding";

    public static String encrypt(String algorithm, String input, SecretKey key,
                                 IvParameterSpec iv) {

       try{
           Cipher cipher = Cipher.getInstance(algorithm);
           cipher.init(Cipher.ENCRYPT_MODE, key, iv);
           byte[] cipherText = cipher.doFinal(input.getBytes());
           return Base64.getEncoder()
                   .encodeToString(cipherText);
       }catch (Exception e){
           return null;
       }
    }
    public static String decrypt(String algorithm, String cipherText, SecretKey key,
                                 IvParameterSpec iv){

        try{
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] plainText = cipher.doFinal(Base64.getDecoder()
                    .decode(cipherText));
            return new String(plainText);
        }catch (Exception e){
            return null;
        }
    }
}
