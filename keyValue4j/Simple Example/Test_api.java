/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_api;


import ir.techdudes.KeyValueObj.*;



/**
 *
 * @author nima
 */
public class Test_api {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws CryptoException{
       testing t=null;
       
       KeyValueAPI conn=new KeyValueAPI("1234123412341234");
       
        testing myget = (testing) conn.GetObject("test2");
        System.out.println(myget.b);
        System.out.println(myget.getA());
    }
    
   
    
}
