/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keyvalue4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


class CryptoUtils {
    private final String ALGORITHM = "AES";
    private final String TRANSFORMATION = "AES/CBC/PKCS5PADDING";
    
    public String encrypt(String key,String IV,String text){
        return Arrays.toString(CryptoByte(Cipher.ENCRYPT_MODE, key, text.getBytes(),IV));
    }
    public String decrypt(String key,String IV,String text){
        return Arrays.toString(CryptoByte(Cipher.DECRYPT_MODE, key, text.getBytes(),IV));
    }
    public byte[] encrypt(String key,String IV,byte [] bytes){
        return CryptoByte(Cipher.ENCRYPT_MODE, key, bytes,IV);
    }
    public byte[] decrypt(String key,String IV,byte [] bytes){
        return CryptoByte(Cipher.DECRYPT_MODE, key, bytes,IV);
    }
    
    private byte[] CryptoByte(int cipherMode, String key,byte[] myobj, String iv){
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey,new IvParameterSpec(iv.getBytes()));
            byte[] encryptedText=cipher.doFinal(myobj);
            return encryptedText;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(CryptoUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        }
}
