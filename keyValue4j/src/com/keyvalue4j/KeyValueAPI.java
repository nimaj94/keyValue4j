package com.keyvalue4j;

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
    int testb;
    private final HashMap<String, Object> cache;
    private final String Password;
    private final String Directory;
    private final String IV;
    public void changePassword(String oldPass, String newPass) {
        String oldpass=padding(oldPass);
        String newpass=padding(newPass);
        File folder = new File(Directory + "/");
        File[] listOfFiles = folder.listFiles();
        CryptoUtils crypto = new CryptoUtils();
        String olddir=Directory;
        String[] dirs=Directory.split("/");
        String mydir="";
        if(dirs.length>2)
            for(int ii=0;ii<dirs.length-1;ii++)
                mydir=mydir+dirs[ii]+"/";
        else
            mydir=dirs[0]+"/";
        String newdir=mydir+Arrays.toString(crypto.encrypt(newpass,IV, crypto.decrypt(oldpass,IV,StringToByte(dirs[dirs.length-1]) )));
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
                String newName=Arrays.toString(crypto.encrypt(newpass,IV, crypto.decrypt(oldpass,IV,StringToByte(f.getName()) )));
                File file=new File(newdir + "/"+newName);
                byte[] readFully = readFully(fin, f);
                byte[] decrypted = crypto.decrypt(oldpass,IV, readFully);
                fin.close();
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(crypto.encrypt(newpass,IV, decrypted));
                
                
                outputStream.close();
                oldFile.delete();
                //Password = newpass;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
        File myolddir=new File(olddir);
        myolddir.delete();
        
    }
    public void changePassword(String oldPass, String newPass,String oldIV,String newIV) {
        String oldpass=padding(oldPass);
        String newpass=padding(newPass);
        String oldiv=padding(oldIV);
        String newiv=padding(newIV);
        File folder = new File(Directory + "/");
        File[] listOfFiles = folder.listFiles();
        CryptoUtils crypto = new CryptoUtils();
        String olddir=Directory;
        String[] dirs=Directory.split("/");
        String mydir="";
        if(dirs.length>2)
            for(int ii=0;ii<dirs.length-1;ii++)
                mydir=mydir+dirs[ii]+"/";
        else
            mydir=dirs[0]+"/";
        String newdir=mydir+Arrays.toString(crypto.encrypt(newpass,newiv, crypto.decrypt(oldpass,oldiv,StringToByte(dirs[dirs.length-1]) )));
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
                String newName=Arrays.toString(crypto.encrypt(newpass,newiv, crypto.decrypt(oldpass,oldiv,StringToByte(f.getName()) )));
                File file=new File(newdir + "/"+newName);
                byte[] readFully = readFully(fin, f);
                byte[] decrypted = crypto.decrypt(oldpass,oldiv, readFully);
                fin.close();
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(crypto.encrypt(newpass,newiv, decrypted));
                
                
                outputStream.close();
                oldFile.delete();
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(KeyValueAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
        File myolddir=new File(olddir);
        myolddir.delete();
        
    }
    private String padding(String toPad){
        String result=toPad;
        
        for(int i=0;i<result.length()%16;i++)
            result=result+" ";
        
        return result;
    }
    public KeyValueAPI(String password, String repository) {
        
            cache = new LinkedHashMap<>();
        
        this.IV="                ";
        this.Password = padding(password);
        CryptoUtils crypto = new CryptoUtils();
        Directory = "kv4j/"+crypto.encrypt(Password,IV, repository);
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
    public KeyValueAPI(String password,String secondPassword, String repository, String directory) {
        
            cache = new LinkedHashMap<>();
        
        this.IV=padding(secondPassword);
        this.Password = padding(password);
        CryptoUtils crypto = new CryptoUtils();
        String mydirectory = directory.replace('\\', '/');
        if(!mydirectory.substring(directory.length()-1).equals("/") )
            mydirectory=mydirectory+"/";
        Directory = mydirectory+"kv4j/"+crypto.encrypt(Password,IV, repository);
        File theDir = new File(mydirectory+"kv4j");
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
    public void removeRepository(){
        File folder = new File(Directory );
        File[] listOfFiles = folder.listFiles();
        for(File f:listOfFiles)
            f.delete();
        folder.delete();
        cache.clear();
    }
    /**
     *
     * @param key
     * @return
     */
    public Object GetObject(String key) {
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
            fin = new FileInputStream(Directory + "/" + crypto.encrypt(Password,IV, key));
            byte[] readFully = readFully(fin, new File(Directory + "/" + crypto.encrypt(Password,IV, key)));
            byte[] decrypted = crypto.decrypt(Password,IV, readFully);
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
        File fileToDelete=new File(Directory + "/"+crypto.encrypt(Password,IV, key));
        fileToDelete.delete();
    }
    public void SetObject(String key, Object object) {
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
                      
                    File encryptedFile = new File(Directory + "/" + crypto.encrypt(Password,IV, key));
                    FileOutputStream outputStream = new FileOutputStream(encryptedFile);
                    
                    outputStream.write(crypto.encrypt(Password,IV, bos.toByteArray()));
                    
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
        
        if (cache.size() < 4000) {
            cache.put(key, entity);
        } else {
            Iterator<Map.Entry<String, Object>> iterator = cache.entrySet().iterator();
            for (int i = 0; i < 30; i++){
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
        CryptoUtils crypto = new CryptoUtils();
        File dir=new File(Directory);
        String[] mylist = new String[dir.list().length];
        String[] temp=dir.list();
        int i=0;
        for(String str : temp){
            mylist[i]=new String(crypto.decrypt(Password,IV,StringToByte(str)));
            i++;
        }
        return Arrays.asList(mylist);
    }
    public boolean containKey(String key){
        CryptoUtils crypto = new CryptoUtils();
        File file=new File(Directory + "/"+crypto.encrypt(Password,IV, key));
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
