/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;
import java.util.ArrayList;

/**
 *
 * @author sourav
 */
public class IndexCellPayload{
        /** 1 byte INT **/
        public static final int NUM_ROWID_OFFSET = 0x00;
        /** 1 byte INT **/
        public static final int INDEX_TYPE_OFFSET = 0x01;
        /** N Bytes where N is the based on Index Type **/
        public static final int INDEX_VAL_OFFSET = 0x02;
    
        byte numRowIDs;
        byte indexDataType;
        byte[] indexValue;
        ArrayList<Integer> rowIds;
        
        public IndexCellPayload(byte numRowIDs, byte[] indexValue, byte indexDataType, 
                ArrayList<Integer> rowIds) {
            this.numRowIDs = numRowIDs;
            this.indexDataType = indexDataType;
            this.rowIds = rowIds;
            this.indexValue = indexValue;
        }
        
        boolean isIndexString(){
            return (indexDataType >= 0x0C);
        }
        
        public int getPayloadSz(){
            int indexSz = IndexCell.getSizeOfIndexVal(indexDataType);
            return 1 + 1 + indexSz + rowIds.size() * 4;
        }
        
        byte[] marshalPayload(){
            int payloadSz = getPayloadSz();
            byte[] ret = new byte[payloadSz];
            
            ret[NUM_ROWID_OFFSET] = numRowIDs;
            ret[INDEX_TYPE_OFFSET] = (byte)indexDataType;
//            if(isIndexString()) {
//                String index = (String)indexValue;
//                System.arraycopy(index.getBytes(), 0, ret, INDEX_VAL_OFFSET, index.length());
//            } else {
//                long index = (long)indexValue;
//                for (int i = 0; i < indexSz; i++) {
//                    ret[INDEX_VAL_OFFSET + indexSz - 1 - i] = (byte) (index & 0xFF);
//                    index >>= 8;
//                }
//            }
            
            /** TODO: Check if index to byte conversion methods already implemented **/
            
            return ret;
        }
        
        public void unmarshalPayload(byte[] inBytes) {
            /** TODO **/
        }
        
        public byte[] getKey() {
            return indexValue;
        }
        
        public byte getType(){
            return indexDataType;
        }
        
        public void addRowId(int rowID) {
            rowIds.add(rowID);
        }
    }