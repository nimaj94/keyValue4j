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
    public KeyValueAPI(String Password) {
        this.Password=Password;
        File theDir = new File("objects");
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
            System.out.println("ir.techdudes.KeyValueObj.KeyValueAPI.GetObject()");
            FileInputStream fin = null;
            ObjectInputStream ois = null;
            //decrypt
            CryptoUtils crypto = new CryptoUtils();
            File decryptedFile= new File("objects/"+key + ".ser");
            File encryptedFile = new File("objects/"+key + "encrypted");
            crypto.decrypt(Password, encryptedFile, decryptedFile);
            //end
            fin = new FileInputStream("objects/"+key + ".ser");
            ois = new ObjectInputStream(fin);
            Object readObject = ois.readObject();
            Entity en = (Entity) readObject;
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
        
        Entity entity = new Entity();
        entity.setData(object);
        caching(key, entity);
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;

        try {
            
            fout = new FileOutputStream("objects/"+key + ".ser");
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
                    File inputFile= new File("objects/"+key + ".ser");
                    File encryptedFile = new File("objects/"+key + "encrypted");
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
