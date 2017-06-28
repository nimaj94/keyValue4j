/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testkeyvalue4j;
import ir.techdudes.KeyValueObj.KeyValueAPI;
/**
 *
 * @author nima
 */
public class TestKeyValue4j {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Student stu=new Student();
        stu.setName("nima");
        stu.setNationalId(1234);
        stu.setStuId(4321);
        KeyValueAPI api=new KeyValueAPI("1234123412341234", "Students");
        api.SetObject("stu1", stu);//storing Object
        Student getstu=(Student) api.GetObject("stu1");
        System.out.println(getstu.getNationalId());
    }
    
}
