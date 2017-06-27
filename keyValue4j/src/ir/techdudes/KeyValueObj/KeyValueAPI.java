package ir.techdudes.KeyValueObj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
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

    private HashMap<String, Object> cache;
    private String Password;
    private String Directory;

    public void changePassword(String oldpass, String newpass) {
        File folder = new File(Directory + "/");
        File[] listOfFiles = folder.listFiles();
        CryptoUtils crypto = new CryptoUtils();
        for (File f : listOfFiles) {
            try {
                
                FileInputStream fin = null;
                
                fin = new FileInputStream(Directory + "/" + f.getName());
                byte[] readFully = readFully(fin, f);
                byte[] decrypted = crypto.decrypy(oldpass, readFully);
                fin.close();
                FileOutputStream outputStream = new FileOutputStream(Directory + "/" + f.getName());
                outputStream.write(crypto.encrypt(newpass, decrypted));
                
                
                outputStream.close();
                Password = newpass;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public KeyValueAPI(String Password, String directory) {
        if (cache == null) {
            cache = new LinkedHashMap<>();
        }
        Directory = directory;
        this.Password = Password;
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
            if (cache.containsKey(key)) {
                return ((Entity) cache.get(key)).getData();
            }

            //System.out.println("ir.techdudes.KeyValueObj.KeyValueAPI.GetObject()");
            FileInputStream fin = null;
            ObjectInput in = null;
            //decrypt
            CryptoUtils crypto = new CryptoUtils();

            //end
            fin = new FileInputStream(Directory + "/" + key);
            byte[] readFully = readFully(fin, new File(Directory + "/" + key));
            byte[] decrypted = crypto.decrypy(Password, readFully);
            ByteArrayInputStream bis = new ByteArrayInputStream(decrypted);
            in = new ObjectInputStream(bis);
            Object readObject = in.readObject();
            Entity en = (Entity) readObject;
            caching(key, en);

            return en.getData();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public void remove(String key){
        cache.remove(key);
        File fileToDelete=new File(Directory + "/"+key);
        fileToDelete.delete();
    }
    public void SetObject(String key, Object object) throws CryptoException {
        CryptoUtils crypto = new CryptoUtils();
        Entity entity = new Entity(key);
        entity.setData(object);
        caching(key, entity);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;

        try {

            oos = new ObjectOutputStream(bos);
            oos.writeObject(entity);
            oos.flush();
            //System.out.println("Done");

        } catch (Exception ex) {

            ex.printStackTrace();

        } finally {

            if (bos != null) {
                try {
                    //encrypt
                      
                    File encryptedFile = new File(Directory + "/" + key);
                    FileOutputStream outputStream = new FileOutputStream(encryptedFile);
                    
                    outputStream.write(crypto.encrypt(Password, bos.toByteArray()));
                    
                    //end
                    bos.close();
                    outputStream.close();
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

    private void caching(String key, Entity entity) {
        if (cache == null) {
            cache = new LinkedHashMap<>();
        }
        if (cache.size() < 4000) {
            cache.put(key, entity);
        } else {
            for (int i = 0; i < 30; i++) {
                Iterator<Map.Entry<String, Object>> iterator = cache.entrySet().iterator();
                iterator.next();
                iterator.remove();
            }
        }
    }

    private byte[] readFully(FileInputStream fin, File file) {
        try {
            byte[] fileContent = new byte[(int) file.length()];
            fin.read(fileContent);
            return fileContent;
        } catch (IOException ex) {
            Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public List<String> getAllKeys(){
        File dir=new File(Directory);
        return Arrays.asList(dir.list());
    }
    public boolean containKey(String key){
        File file=new File(Directory + "/"+key);
        return file.exists();
    }

}
