package btree;

public class Cell {
    
    /******** OFFSETS IN CELL HEADER BEGIN **********/
    
    /** LEFT Child Page Number Offset **/
    public static final int LEFT_CH_PG_NO_OFFSET = 0x00;
    /** 2 Bytes of Cell Payload Offset **/
    public static final int PAYLOAD_SIZE_OFFSET = 0x04;
    /** Hidden Row ID Offset **/
    public static final int ROWID_OFFSET = 0x06;
    
    /******** OFFSETS IN CELL HEADER END **********/
    
    /** payload remains null in case of interior nodes **/
    byte[] payload = null;
    /** Remains -1 in case of Leaf Nodes **/
    int leftChildPageNo = -1;
    byte[] mCell;
    int mCellSize = 0;
    int mOffsetInPage = 0;
    Page mPage;
    private int mRowID;
    
    int CELL_HEADER_SIZE =  0x0A;
    
    /**
     * Constructor with parameters set
     * @param leftChildPageNo : The page number of the left child if interior node. 
     * Ignored for leaf nodes
     * @param payload : byte array containing the row. Ignored for interior nodes.
     * @param rowId : The row ID 
     */
    public Cell(int leftChildPageNo, byte[] payload, int rowId){
        this.leftChildPageNo = leftChildPageNo;
        this.payload = payload;
        mRowID = rowId;   
    }
    
    public Cell(int offset, Page page, byte[] pageBytes) {
        mOffsetInPage = offset;
        mPage = page;
        unmarshallCell(pageBytes);
    }
    
    /** Extract All Cell Elements from the page 'mPage' at offset 'mOffsetInPage'**/
    private void unmarshallCell(byte[] pageBytes) {
        
        mCellSize = CELL_HEADER_SIZE;
        
        if(mPage.isLeaf()) {
            int payloadSz = Page.byteArrayToInt(pageBytes, mOffsetInPage + 
                    PAYLOAD_SIZE_OFFSET, 2);
            mCellSize = CELL_HEADER_SIZE + payloadSz;
        }
    }
    
    public byte[] marshalCell() {
        byte[] cellBytes = new byte[mCellSize];
        
        /** TODO: Implement the function **/
        
        return cellBytes;
    }
    
    public int getLeftChildPageNo(){
        return leftChildPageNo;
    }
    
    public int getCellSize(){
        return mCellSize;
    }
    
    public int getRowId() {
        return mRowID;
    }
}
