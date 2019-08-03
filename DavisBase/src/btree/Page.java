package btree;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

public class Page {
    
    /******** OFFSETS IN PAGE HEADER BEGIN **********/    
    
    /** The one-byte flag indicating the type of the page **/
    public static final int PAGE_TYPE_OFFSET = 0x00;
    /** The two-byte integer designates the number of cells on the page**/
    public static final int NUM_CELLS_OFFSET = 0x02;
    /** The two-byte integer containing the page offset for start of cell content area **/
    public static final int START_CELL_OFFSET_OFFSET = 0x04; 
    /** The four-byte  page number of rightmost child or right sibling child (for leaf) **/
    public static final int PAGE_NUM_RIGHT_CHILD_OFFSET = 0x06;
    /** The four-byte integer page pointer references the page’s parent**/
    public static final int PARENT_PAGE_NO_OFFSET = 0x0A;
    /** An array of 2-byte integers that indicate the page offset location of each data cell **/
    public static final int CELL_PAGE_OFFSET_ARRAY_OFFSET = 0x10;
    
    /******** OFFSETS IN PAGE HEADER END **********/  
    
    public static final int HEADER_SIZE = 0x10;
    
    byte[] header = new byte[HEADER_SIZE];
    
    /** Newer cells added at the end, but marshaled in reverse order for Cells **/
    ArrayList<Cell> mCells;
    ArrayList<Integer> cellLocations;
    private short numCells;
    private byte[] mPage;
    private int mParent, mRightNode = -1;
    private boolean isLeaf = false;
    
    //NOTE: Big Endian : MSB at lower memory address
    public static int byteArrayToInt(byte[] arr, int start, int numBytes){
        ByteBuffer wrapped = ByteBuffer.wrap(arr, start, numBytes);
        if(numBytes == 2)
            return wrapped.getShort();
        return wrapped.getInt();
    }
    
    public static void intToByteArray(byte[] arr, int start, int val, int numBytes) {
        
        for(int i = numBytes-1; i >= 0; i--) {
            arr[start + i] = (byte)(val & 0xFF);
            val >>= 8;
        }
    }
    
    /** Called when new page is getting created
     * @param leaf : Boolean value indicating if Page is a leaf page
     * @param cells
     * @param locs
     * @param parent : Parent node number
     * @param right : Right child/sibling node number
     **/
    public Page(boolean leaf, ArrayList<Cell> cells, ArrayList<Integer> locs,
            int parent, int right) {
        
        numCells = (short) cells.size();
        mParent = parent;
        isLeaf = leaf;
        mRightNode = right;
        
        mCells = cells;
        cellLocations = locs;   
    }
    
    public Page(byte[] page){
        mPage = page;
    }
    
    /**
     * The following function takes all the page information and marshals them
     * into a byte array to be stored on the disk file.
     * @return The byte array to be stored on the disk
     */
    public byte[] marshalPage() {
        byte[] pageBytes = new byte[BPlusOne.PAGE_SIZE];
        
        pageBytes[PAGE_TYPE_OFFSET] = (byte) (isLeaf()? 0x0d:0x05);
        intToByteArray(pageBytes, NUM_CELLS_OFFSET, numCells, 2);
        intToByteArray(pageBytes, START_CELL_OFFSET_OFFSET, 
                (numCells > 0) ? cellLocations.get(numCells-1) : BPlusOne.PAGE_SIZE, 2);
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
        
        int offset = BPlusOne.PAGE_SIZE /*- 1*/;
        for(Cell cell: mCells) {
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
        System.arraycopy(mPage, 0, header, 0, HEADER_SIZE);
        isLeaf = (mPage[PAGE_TYPE_OFFSET] == 0x0d);
        numCells = (short)byteArrayToInt(mPage, NUM_CELLS_OFFSET, 2);
        int off = byteArrayToInt(mPage, START_CELL_OFFSET_OFFSET, 2);
        mCells = new ArrayList<>();
        cellLocations = new ArrayList<>();
        
        for(int i = 0; i < numCells; i++) {
            Cell cell = new Cell(off, this, mPage);
            
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
    
    public boolean isNodeFull(int cellSize) {
        return (HEADER_SIZE + (2 * numCells) 
                + (BPlusOne.PAGE_SIZE - cellLocations.get(0))) < cellSize;
    }
    
    public boolean isLeaf() {
        return isLeaf;
    }
    
    public boolean isNodeEmpty() {
        return (mCells.isEmpty());
    }
    
    /** Temporary Dummy Function **/
    public boolean isNodeFullDummy() {
        return numCells == 2;
    }
    
    /** All getter methods **/
    
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
    
    public Cell getCell(int index) {
        return mCells.get(index);
    }
    
    public int getCellLocation(int index){
        return cellLocations.get(index);
    }
    
    public ArrayList<Cell> getAllCells() {
        return mCells;
    }
    
    public ArrayList<Integer> getAllCellLocations() {
        return cellLocations;
    }
    
    public int getMaxRowidInPage() {
        return mCells.get(mCells.size() - 1).getRowId();
    }
    
    public int getMinRowidInPage() {
        return mCells.get(0).getRowId();
    }
    
    /** All setter methods **/
    
    /** Set Parent Page Number
     * @param parent 
     **/
    public void setParentPageNo(int parent) {
        mParent = parent;
    }
    
    /** Set Right Node page number
     * @param right
     **/
    public void setRightNodePageNo(int right) {
        mRightNode = right;
    }
    
    public void setCellArray(ArrayList<Cell> cells) {
        mCells = cells;
        numCells = (short)cells.size();
    }
    
    public void setCellLocationArray(ArrayList<Integer> cellLocations) {
        this.cellLocations = cellLocations;
    }
    
    public void addNewCell(Cell cell) {
        int lastCellLocation = (cellLocations.isEmpty())? 
                BPlusOne.PAGE_SIZE : cellLocations.get(cellLocations.size()-1);
        mCells.add(cell);
        cellLocations.add(lastCellLocation - cell.getCellSize());
        numCells++;
    }
}
