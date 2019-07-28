/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import btree.BPlusOne;
import static btree.Page.*;
import java.util.ArrayList;
import java.util.Collections;

public class IndexPage {
    byte[] header = new byte[HEADER_SIZE];
    ArrayList<IndexCell> mCells;
    ArrayList<Integer> cellLocations;
    private short numCells;
    private int mParent, mRightNode = -1;
    private byte[] mPage;
    private boolean isLeaf = false;
    
    public IndexPage(byte[] page) {
        mPage = page;
    }
    
    public IndexPage(boolean leaf, ArrayList<IndexCell> cells, ArrayList<Integer> locs,
            int parent, int right) {
        
        numCells = (short) cells.size();
        mParent = parent;
        mRightNode = right;
        
        mCells = cells;
        cellLocations = locs;   
        
        isLeaf = leaf;
    }
    
    /**
     * The following function takes all the page information and marshals them
     * into a byte array to be stored on the disk file.
     * @return The byte array to be stored on the disk
     */
    public byte[] marshalPage() {
        byte[] pageBytes = new byte[BPlusOne.PAGE_SIZE];
        
        pageBytes[PAGE_TYPE_OFFSET] = (byte) (isLeaf()? 0x0a:0x02);
        intToByteArray(pageBytes, NUM_CELLS_OFFSET, numCells, 2);
        intToByteArray(pageBytes, START_CELL_OFFSET_OFFSET, cellLocations.get(numCells-1), 2);
        intToByteArray(pageBytes, PAGE_NUM_RIGHT_CHILD_OFFSET, mRightNode, 4);
        intToByteArray(pageBytes, PARENT_PAGE_NO_OFFSET, mParent, 4);
        
        for(int i = 0; i < cellLocations.size(); i++) {
            intToByteArray(
                pageBytes, 
                CELL_PAGE_OFFSET_ARRAY_OFFSET + i*2, 
                cellLocations.get(i), 
                2
            );
        }
        
        int offset = BPlusOne.PAGE_SIZE;
        for(IndexCell cell: mCells) {
            byte[] temp = cell.marshalCell();
            
            offset -= cell.getCellSize();
            System.arraycopy(temp, 0, pageBytes, offset, cell.getCellSize());
        }
        
        return pageBytes;
    }
     /**
     * The following function is un-marshals a page into header, cell location
     * array and actual cells for easy retrieval and storage in the future
     */
    public void unmarshalPage() {
        int off = byteArrayToInt(mPage, START_CELL_OFFSET_OFFSET, 2);
        System.arraycopy(mPage, 0, header, 0, HEADER_SIZE);
        isLeaf = (mPage[PAGE_TYPE_OFFSET] == 0x0a);
        numCells = (short)byteArrayToInt(mPage, NUM_CELLS_OFFSET, 2);
        
        for(int i = 0; i < numCells; i++) {
            IndexCell cell = new IndexCell(off, this, mPage);
            
            mCells.add(cell);
            off += cell.getCellSize();
        }
        Collections.reverse(mCells);
        
         for(
            int i = CELL_PAGE_OFFSET_ARRAY_OFFSET; 
            i < (CELL_PAGE_OFFSET_ARRAY_OFFSET + (2 * numCells)); 
            i += 2) {
            cellLocations.add(byteArrayToInt(mPage, i, 2));
        }
         
        mParent = byteArrayToInt(mPage, PARENT_PAGE_NO_OFFSET, 4);
        mRightNode = byteArrayToInt(mPage, PAGE_NUM_RIGHT_CHILD_OFFSET, 4);
    }
    
    public boolean isLeaf() {
        return isLeaf;
    }
    
    /** Temporary Dummy Function **/
    public boolean isNodeFullDummy() {
        return numCells == 2;
    }
    
    /** All Getter Functions **/
     /**
     * Get the parent page number
     * @return the parent page number
     */
    public int getParentPageNo() {
        return mParent;
    }
    
    public int getRightNodePageNo() {
        return mRightNode;
    }
    
    public IndexCell getCell(int index) {
        return mCells.get(index);
    }
    
    public int getCellLocation(int index){
        return cellLocations.get(index);
    }
    
    public ArrayList<IndexCell> getAllCells() {
        return mCells;
    }
    
    public ArrayList<Integer> getAllCellLocations() {
        return cellLocations;
    }
    /** All Setter Functions **/
    
    public void setParentPageNo(int parent) {
        mParent = parent;
    }
    
    /** Set Right Node page number
     * @param right
     **/
    public void setRightNodePageNo(int right) {
        mRightNode = right;
    }
    
    public void setCellArray(ArrayList<IndexCell> cells) {
        mCells = cells;
        numCells = (short)cells.size();
    }
    
    public void setCellLocationArray(ArrayList<Integer> cellLocations) {
        this.cellLocations = cellLocations;
    }
    
    public void addNewCell(IndexCell cell) {
        int lastCellLocation = (cellLocations.isEmpty())? 
                BPlusOne.PAGE_SIZE : cellLocations.get(cellLocations.size()-1);
        mCells.add(cell);
        cellLocations.add(lastCellLocation - cell.getCellSize());
        numCells++;
    }
}
