/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package davisbase;

import btree.BPlusOne;

/**
 *
 * @author sourav
 */
public class DavisBase {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        BPlusOne tree = new BPlusOne();
        
        for(int i = 0; i < 3; i++) {
            var payload = ("Testing DavisBase. Inserting record number "+i).getBytes();
            System.out.println("Inserting "+ payload.length+" bytes");
            tree.insert(payload);
        }
        tree.closeFile();
    }
    
}
