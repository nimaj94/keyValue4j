/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ir.techdudes.KeyValueObj;
import java.io.Serializable;

/**
 *
 * @author nima
 */
public class Entity implements Serializable{
    private String ID;
    private Object data;
    private String key;
    public Entity(String key){
        ID=java.util.UUID.randomUUID().toString();
        this.key=key;
    }
    public String getKey(){
        return key;
    }
    public String GetID(){
        return ID;
    }
    public void setData(Object t){
        data = t;
    }
    public Object getData(){
        return data;
    }
}
