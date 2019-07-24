/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        //instance.insert( rowData);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of closeFile method, of class BPlusOne.
     */
    @Test
    public void testCloseFile() {
        System.out.println("closeFile");
        BPlusOne instance = new BPlusOne();
        instance.closeFile();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRowData method, of class BPlusOne.
     */
    @Test
    public void testGetRowData() {
        System.out.println("getRowData");
        String tablename = "";
        BPlusOne instance = new BPlusOne();
        ArrayList expResult = null;
        ArrayList result = instance.getRowData(tablename);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dumpHashMapToFile method, of class BPlusOne.
     */
    @Test
    public void testDumpHashMapToFile() throws Exception {
        System.out.println("dumpHashMapToFile");
        var gfg = new ArrayList<Integer>(Arrays.asList(3,67)); 
        HashMap<String, ArrayList<Integer>> tableInfo = new HashMap<>(), ret;
        tableInfo.put("Table1", gfg);
        BPlusOne instance = new BPlusOne();
        instance.dumpHashMapToFile(tableInfo);
        ret = instance.loadHashMapFromFile();
        // TODO review the generated test code and remove the default call to fail.
        assertEquals(tableInfo, ret);
    }

    /**
     * Test of loadHashMapFromFile method, of class BPlusOne.
     */
    @Test
    public void testLoadHashMapFromFile() throws Exception {
        System.out.println("loadHashMapFromFile");
        BPlusOne instance = new BPlusOne();
        HashMap<String, ArrayList<Integer>> expResult = null;
        HashMap<String, ArrayList<Integer>> result = instance.loadHashMapFromFile();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
