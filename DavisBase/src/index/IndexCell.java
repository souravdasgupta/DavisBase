/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.util.ArrayList;

public class IndexCell {
    /** 4 bytes of LEFT Child Page Number Offset **/
    public static final int LEFT_CH_PG_NO_OFFSET = 0x00;
    /** 2 Bytes of Cell Payload Offset **/
    public static final int PAYLOAD_SIZE_OFFSET = 0x04;
    /** Payload Offset **/
    public static final int PAYLOAD_OFFSET = 0x06;
    
    public static int getSizeOfIndexVal(byte indexDataType){
        switch(indexDataType) {
            case 0x01:
            case 0x08:
                return 1;
            case 0x02:
                return 2;
            case 0x03:
            case 0x05:
            case 0x09:
                return 4;
            case 0x04:
            case 0x06:
            case 0x0A:
            case 0x0B:
                return 8; 
        }
        return indexDataType - 0x0C;
    }
    
    /**
     * compareByteArrays() : Method to compare two byte arrays represent objects 
     * of some data type
     * @param a
     * @param b
     * @param type
     * @return 1 if a > b, -1 if b > a , 0 if they are equal *
     */
    public static int compareByteArrays(byte[] a, byte[] b, byte type) {
        int numBytes = getSizeOfIndexVal(type);
        
        for(int i = 0; i < numBytes; i++) {
            if(a[i] > b[i])
                return 1;
            if(a[i] < b[i])
                return -1;
        }
        return 0;
    }
    
    int mCellSize = 0;
    int mOffsetInPage = 0;
    IndexPage mPage;
    IndexCellPayload payload;
    /** Remains -1 in case of Leaf Nodes **/
    int leftChildPageNo = -1;
    
     /** 
     * Cell created from existing formatted data stored in page
     * @param offset : Offset of the cell within the page payload
     * @param page : The Page object to which this cell belongs to
     * @param pageBytes : The payload of the page as byte array
     */
    public IndexCell(int offset, IndexPage page, byte[] pageBytes) {
        mOffsetInPage = offset;
        mPage = page;
        unmarshalCell(pageBytes);
    }
    
    public IndexCell(int leftChildNo, IndexCellPayload payload) {
        leftChildPageNo = leftChildNo;
        this.payload = payload;
    }
    
    IndexCellPayload getCellBody() {
        return payload;
    }
    
    public int getCellSize(){
        return mCellSize;
    }
    
    public int getLeftChildPageNo() {
        return leftChildPageNo;
    }
    
    public byte[] marshalCell() {
        byte[]ret = new byte[mCellSize];
        /** TODO **/
        return ret;
    }
    
    private void unmarshalCell(byte[] pageBytes) {
        /** TODO **/
    }
    
    public void setLeftChildPageNo(int leftChildPageNo) {
        this.leftChildPageNo = leftChildPageNo;
    }
}
