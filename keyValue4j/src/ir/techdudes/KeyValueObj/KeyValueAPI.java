package ir.techdudes.KeyValueObj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author nima
 */
public class KeyValueAPI {
    private HashMap<String,Object> cache;
    private String Password;
    private String Directory;
    public void changePassword(String oldpass,String newpass){
        File folder = new File(Directory+"/");
        File[] listOfFiles = folder.listFiles();

        for (File f:listOfFiles) {
            try {
                
                CryptoUtils crypto = new CryptoUtils();
                File decryptedFile= new File(Directory+"/"+"temp" + ".ser");
                crypto.decrypt(oldpass, f, decryptedFile);
                File encryptedFile = new File(Directory+"/"+f.getName() );
                crypto.encrypt(newpass, decryptedFile, encryptedFile);
                decryptedFile.delete();
                Password=newpass;
            } catch (CryptoException ex) {
                Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public KeyValueAPI(String Password,String directory) {
        if(cache==null){
            cache =new LinkedHashMap<>();
        }
        Directory=directory;
        this.Password=Password;
        File theDir = new File(Directory);
        if (!theDir.exists()) {
            boolean result = false;

            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public Object GetObject(String key) throws CryptoException {
        try {
            if(cache.containsKey(key))
                return ((Entity)cache.get(key)).getData();
            
            //System.out.println("ir.techdudes.KeyValueObj.KeyValueAPI.GetObject()");
            FileInputStream fin = null;
            ObjectInputStream ois = null;
            //decrypt
            CryptoUtils crypto = new CryptoUtils();
            File decryptedFile= new File(Directory+"/"+key + ".ser");
            File encryptedFile = new File(Directory+"/"+key + "encrypted");
            crypto.decrypt(Password, encryptedFile, decryptedFile);
            //end
            fin = new FileInputStream(Directory+"/"+key + ".ser");
            ois = new ObjectInputStream(fin);
            Object readObject = ois.readObject();
            Entity en = (Entity) readObject;
            caching(key,en);
            decryptedFile.delete();
            return en.getData();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void SetObject(String key, Object object) throws CryptoException {
        
        Entity entity = new Entity(key);
        entity.setData(object);
        caching(key, entity);
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;

        try {
            
            fout = new FileOutputStream(Directory+"/"+key + ".ser");
            oos = new ObjectOutputStream(fout);
            oos.writeObject(entity);
            oos.flush();
            //System.out.println("Done");
            
        } catch (Exception ex) {

            ex.printStackTrace();

        } finally {

            if (fout != null) {
                try {
                    //encrypt
                    CryptoUtils crypto = new CryptoUtils();
                    File inputFile= new File(Directory+"/"+key + ".ser");
                    File encryptedFile = new File(Directory+"/"+key + "encrypted");
                    crypto.encrypt(Password, inputFile, encryptedFile);
                    inputFile.delete();
                    //end
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
    private void caching(String key,Entity entity){
        if(cache==null){
            cache =new LinkedHashMap<>();
        }
        if(cache.size()<4000){
            cache.put(key,entity);
        } else{
            for(int i=0;i<30;i++)
                cache.entrySet().iterator().remove();
        }
    }
}
