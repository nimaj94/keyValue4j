/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.techdudes.KeyValueObj;

/**
 *
 * @author nima
 */
public class CryptoException extends Exception {
    public CryptoException() {
    }
 
    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
        
    }
}
