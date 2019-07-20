/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sourav
 */
public class BPlusOneTest {
    
    public BPlusOneTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of insert method, of class BPlusOne.
     */
    @Test
    public void testInsert() {
        System.out.println("insert");
        int key = 0;
        byte[] rowData = null;
        BPlusOne instance = new BPlusOne();
        instance.insert( rowData);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
