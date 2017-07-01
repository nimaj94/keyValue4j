/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testkeyvalue4j;

import com.keyvalue4j.Value;

/**
 *
 * @author nima
 */
public class Student extends Value {
    private String name;
    private int NationalId;
    private int StuId;
    public void setName(String name){
        this.name=name;
    }
    public void setNationalId(int NationalId){
        this.NationalId=NationalId;
    }
    public void setStuId(int StuId){
        this.StuId=StuId;
    }
    public String getName(){
        return name;
    }
    public int getNationalId(){
        return NationalId;
    }
    public int getStuId(){
        return StuId;
    }
}
