/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package davisbase;

import btree.BPlusOne;
import java.util.*;

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
        
        for(int i = 0; i < 7; i++) {
            var payload = ("Testing DavisBase. Inserting record number "+i).getBytes();
            System.out.println("Inserting "+ payload.length+" bytes");
            tree.insert(payload);
        }
        
        ArrayList<byte[]> rows = tree.getRowData();
        rows.forEach((row) -> {
            System.out.println(Arrays.toString(row));
        });
        tree.closeFile();
    }
    
}
