/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.techdudes.KeyValueObj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import javax.crypto.spec.SecretKeySpec;


public class CryptoUtils {
    private final String ALGORITHM = "AES";
    private final String TRANSFORMATION = "AES";
    
    public void encrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
    public String encrypt(String key,String text){
        return CryptoByte(Cipher.ENCRYPT_MODE, key, text.getBytes()).toString();
    }
    public String decrypy(String key,String text){
        return CryptoByte(Cipher.DECRYPT_MODE, key, text.getBytes()).toString();
    }
    public byte[] encrypt(String key,byte [] bytes){
        return CryptoByte(Cipher.ENCRYPT_MODE, key, bytes);
    }
    public byte[] decrypy(String key,byte [] bytes){
        return CryptoByte(Cipher.DECRYPT_MODE, key, bytes);
    }
    public void decrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }
 
    private void doCrypto(int cipherMode, String key, File inputFile,
            File outputFile) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
             
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
             
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
        
    }
    private byte[] CryptoByte(int cipherMode, String key,byte[] myobj){
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
            byte[] encryptedText=cipher.doFinal(myobj);
            return encryptedText;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(CryptoUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        }
}
