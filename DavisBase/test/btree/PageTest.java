/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;

import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sourav
 */
public class PageTest {
    
    public PageTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of byteArrayToInt method, of class Page.
     */
    @Test
    public void testByteArrayToInt() {
        System.out.println("byteArrayToInt");
        byte[] arr = {(byte)0xAA, (byte)0xBB, (byte)0xCC, (byte)0xDD};
        int start = 0;
        int numBytes = 4;
        int expResult = 0xAABBCCDD;
        int result = Page.byteArrayToInt(arr, start, 4);
        assertEquals(expResult, result);
    }

    /**
     * Test of intToByteArray method, of class Page.
     */
    @Test
    public void testIntToByteArray() {
        System.out.println("intToByteArray");
        byte[] arr = new byte[4], expected = {(byte)0xAA, (byte)0xBB, (byte)0xCC, (byte)0xDD};
        int start = 0;
        int val = 0xAABBCCDD;
        int numBytes = 4;
        Page.intToByteArray(arr, start, val, numBytes);
        // TODO review the generated test code and remove the default call to fail.
        assertArrayEquals(arr, expected);
        
        arr = new byte[2];
        byte[] expected2 = {(byte)0xAA, (byte)0xBB};
        start = 0;
        val = 0xAABB;
        numBytes = 2;
        Page.intToByteArray(arr, start, val, numBytes);
        // TODO review the generated test code and remove the default call to fail.
        assertArrayEquals(arr, expected2);
    }

    /**
     * Test of marshalPage method, of class Page.
     */
    @Test
    public void testMarshalPage() {
        System.out.println("marshalPage");
        Page instance = null;
        byte[] expResult = null;
        byte[] result = instance.marshalPage();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of unmarshalPage method, of class Page.
     */
    @Test
    public void testUnmarshalPage() {
        System.out.println("unmarshalPage");
        Page instance = null;
        instance.unmarshalPage();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isNodeFull method, of class Page.
     */
    @Test
    public void testIsNodeFull() {
        System.out.println("isNodeFull");
        int cellSize = 0;
        Page instance = null;
        boolean expResult = false;
        boolean result = instance.isNodeFull(cellSize);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isLeaf method, of class Page.
     */
    @Test
    public void testIsLeaf() {
        System.out.println("isLeaf");
        Page instance = null;
        boolean expResult = false;
        boolean result = instance.isLeaf();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isNodeFullDummy method, of class Page.
     */
    @Test
    public void testIsNodeFullDummy() {
        System.out.println("isNodeFullDummy");
        Page instance = null;
        boolean expResult = false;
        boolean result = instance.isNodeFullDummy();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParentPageNo method, of class Page.
     */
    @Test
    public void testGetParentPageNo() {
        System.out.println("getParentPageNo");
        Page instance = null;
        int expResult = 0;
        int result = instance.getParentPageNo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRightNodePageNo method, of class Page.
     */
    @Test
    public void testGetRightNodePageNo() {
        System.out.println("getRightNodePageNo");
        Page instance = null;
        int expResult = 0;
        int result = instance.getRightNodePageNo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCell method, of class Page.
     */
    @Test
    public void testGetCell() {
        System.out.println("getCell");
        int index = 0;
        Page instance = null;
        Cell expResult = null;
        Cell result = instance.getCell(index);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCellLocation method, of class Page.
     */
    @Test
    public void testGetCellLocation() {
        System.out.println("getCellLocation");
        int index = 0;
        Page instance = null;
        int expResult = 0;
        int result = instance.getCellLocation(index);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllCells method, of class Page.
     */
    @Test
    public void testGetAllCells() {
        System.out.println("getAllCells");
        Page instance = null;
        ArrayList<Cell> expResult = null;
        ArrayList<Cell> result = instance.getAllCells();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllCellLocations method, of class Page.
     */
    @Test
    public void testGetAllCellLocations() {
        System.out.println("getAllCellLocations");
        Page instance = null;
        ArrayList<Integer> expResult = null;
        ArrayList<Integer> result = instance.getAllCellLocations();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setParentPageNo method, of class Page.
     */
    @Test
    public void testSetParentPageNo() {
        System.out.println("setParentPageNo");
        int parent = 0;
        Page instance = null;
        instance.setParentPageNo(parent);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setRightNodePageNo method, of class Page.
     */
    @Test
    public void testSetRightNodePageNo() {
        System.out.println("setRightNodePageNo");
        int right = 0;
        Page instance = null;
        instance.setRightNodePageNo(right);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCellArray method, of class Page.
     */
    @Test
    public void testSetCellArray() {
        System.out.println("setCellArray");
        ArrayList<Cell> cells = null;
        Page instance = null;
        instance.setCellArray(cells);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCellLocationArray method, of class Page.
     */
    @Test
    public void testSetCellLocationArray() {
        System.out.println("setCellLocationArray");
        ArrayList<Integer> cellLocations = null;
        Page instance = null;
        instance.setCellLocationArray(cellLocations);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addNewCell method, of class Page.
     */
    @Test
    public void testAddNewCell() {
        System.out.println("addNewCell");
        Cell cell = null;
        Page instance = null;
        instance.addNewCell(cell);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
