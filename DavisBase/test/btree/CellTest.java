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
public class CellTest {
    
    public CellTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of marshalCell method, of class Cell.
     */
    @Test
    public void testMarshalCell() {
        System.out.println("marshalCell");
        Cell instance = null;
        byte[] expResult = null;
        byte[] result = instance.marshalCell();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLeftChildPageNo method, of class Cell.
     */
    @Test
    public void testGetLeftChildPageNo() {
        System.out.println("getLeftChildPageNo");
        Cell instance = null;
        int expResult = 0;
        int result = instance.getLeftChildPageNo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCellSize method, of class Cell.
     */
    @Test
    public void testGetCellSize() {
        System.out.println("getCellSize");
        Cell instance = null;
        int expResult = 0;
        int result = instance.getCellSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRowId method, of class Cell.
     */
    @Test
    public void testGetRowId() {
        System.out.println("getRowId");
        Cell instance = null;
        int expResult = 0;
        int result = instance.getRowId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
