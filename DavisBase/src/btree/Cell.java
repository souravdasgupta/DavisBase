package btree;

public class Cell {
    
    /******** OFFSETS IN CELL HEADER BEGIN **********/
    
    /** 4 bytes of LEFT Child Page Number Offset **/
    public static final int LEFT_CH_PG_NO_OFFSET = 0x00;
    /** 2 Bytes of Cell Payload Offset **/
    public static final int PAYLOAD_SIZE_OFFSET = 0x04;
    /** Hidden 4 byte Row ID Offset **/
    public static final int ROWID_OFFSET = 0x06;
    /** Payload Offset **/
    public static final int PAYLOAD_OFFSET = 0x0A;
    
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
    
    public static final int CELL_HEADER_SIZE =  0x0A;
    
    /**
     * Constructor with parameters set, used to add new row
     * @param leftChildPageNo : The page number of the left child if interior node. 
     * Ignored for leaf nodes
     * @param payload : byte array containing the row. Ignored for interior nodes.
     * @param rowId : The row ID 
     */
    public Cell(int leftChildPageNo, byte[] payload, int rowId){
        this.leftChildPageNo = leftChildPageNo;
        this.payload = payload;
        mRowID = rowId;   
        mCellSize = CELL_HEADER_SIZE + ((payload != null)? payload.length:0);
    }
    
    /** 
     * Cell created from existing formatted data stored in page
     * @param offset : Offset of the cell within the page payload
     * @param page : The Page object to which this cell belongs to
     * @param pageBytes : The payload of the page as byte array
     */
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
            payload = new byte[payloadSz];
            System.out.println("mOffsetInPage = "+mOffsetInPage);
            for(int i = 0; i < payloadSz; i++) {
                payload[i] = pageBytes[mOffsetInPage + PAYLOAD_OFFSET + i];
            }
            leftChildPageNo = -1;
        } else {
            payload = null;
            leftChildPageNo = Page.byteArrayToInt(pageBytes, mOffsetInPage + 
                    LEFT_CH_PG_NO_OFFSET, 4);
        }
        mRowID = Page.byteArrayToInt(pageBytes, 
                mOffsetInPage + ROWID_OFFSET, 4);
    }
    
    public byte[] marshalCell() {
        byte[] cellBytes = new byte[mCellSize];
        int payloadSz = mCellSize - CELL_HEADER_SIZE;
        
        Page.intToByteArray(cellBytes, LEFT_CH_PG_NO_OFFSET, 
                leftChildPageNo, 4);
        Page.intToByteArray(cellBytes, PAYLOAD_SIZE_OFFSET, payloadSz, 2);
        Page.intToByteArray(cellBytes, ROWID_OFFSET, mRowID, 4);
        
        if(payload != null && payloadSz != 0)
            System.arraycopy(payload, 0, cellBytes, PAYLOAD_OFFSET, payloadSz);
        
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
    
    public byte[] getPayLoadBytes() {
        if(!mPage.isLeaf())
            System.err.println("getPayLoadBytes() called for an internal node");
        return payload;
    }
    
    public void setOffsetInPage(int offset) {
        mOffsetInPage = offset;
    }
}
