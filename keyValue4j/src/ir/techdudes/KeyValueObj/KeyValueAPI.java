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
        String olddir=Directory;
        String newdir=Directory.split("/")[0]+"/"+Arrays.toString(crypto.encrypt(newpass, crypto.decrypt(oldpass,StringToByte(Directory.split("/")[1]) )));
        File theDir2 = new File(newdir);
        if (!theDir2.exists()) {
            boolean result = false;

            try {
                theDir2.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
        }
        else{
            try {
                throw new Exception("problem with new Directory");
            } catch (Exception ex) {
                Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (File f : listOfFiles) {
            try {
                
                FileInputStream fin = null;
                File oldFile=new File(Directory + "/" + f.getName());
                fin = new FileInputStream(oldFile);
                String newName=Arrays.toString(crypto.encrypt(newpass, crypto.decrypt(oldpass,StringToByte(f.getName()) )));
                File file=new File(newdir + "/"+newName);
                byte[] readFully = readFully(fin, f);
                byte[] decrypted = crypto.decrypt(oldpass, readFully);
                fin.close();
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(crypto.encrypt(newpass, decrypted));
                
                
                outputStream.close();
                oldFile.delete();
                Password = newpass;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
        File myolddir=new File(olddir);
        myolddir.delete();
        Directory=newdir;
    }

    public KeyValueAPI(String Password, String directory) {
        if (cache == null) {
            cache = new LinkedHashMap<>();
        }
        this.Password = Password;
        CryptoUtils crypto = new CryptoUtils();
        Directory = "kv4j/"+crypto.encrypt(Password, directory);
        File theDir = new File("kv4j");
        if (!theDir.exists()) {
            boolean result = false;

            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
        }
        File theDir2 = new File(Directory);
        if (!theDir2.exists()) {
            boolean result = false;

            try {
                theDir2.mkdir();
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
            fin = new FileInputStream(Directory + "/" + crypto.encrypt(Password, key));
            byte[] readFully = readFully(fin, new File(Directory + "/" + crypto.encrypt(Password, key)));
            byte[] decrypted = crypto.decrypt(Password, readFully);
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
        CryptoUtils crypto = new CryptoUtils();
        cache.remove(key);
        File fileToDelete=new File(Directory + "/"+crypto.encrypt(Password, key));
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
                      
                    File encryptedFile = new File(Directory + "/" + crypto.encrypt(Password, key));
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
        CryptoUtils crypto = new CryptoUtils();
        File file=new File(Directory + "/"+crypto.encrypt(Password, key));
        return file.exists();
    }
    private byte[] StringToByte(String bytes){
        
       String s2=bytes.substring(1, bytes.length()-1);
        String[] arr=s2.split(", ");
        byte[] arrbyte = new byte[arr.length];
        int i=0;
        for(String ar:arr){
            arrbyte[i]=(byte) Integer.parseInt(ar);
            i++;
        }
        return arrbyte;
    }
}
