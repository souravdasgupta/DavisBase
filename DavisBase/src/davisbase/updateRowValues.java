/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package davisbase;

import btree.BPlusOne;
import static davisbase.ColumnInfo.BPlustree;
import static davisbase.filterandprint.numOfBytesByType;
import java.util.ArrayList;
import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;

/**
 *
 * @author madel
 */
public class updateRowValues {
//     public static ArrayList<byte[]> updatedRows(String tablename, ArrayList<byte[]> rowData, ArrayList<Integer> rowIDs,ColumnInfo targetColumn,String newValue){
//        /**@param rowData=byte array of each row from the table
//         **@param targetColumn=column requested for changing
//         **@param newValue=value to set in row based on column number
//         */
//        //System.out.println("Entered filterbyColumn");
//        ArrayList<byte[]> filteredRows=new ArrayList<byte[]>(); 
//        if(targetColumn.GetType().toLowerCase().equals("string")){
//            //check size
//            //if size is less than or equal to original string then change value
//            //else 
//                //call insert
//                //call delete from index-how will we know the row id?
//            //update header
//        }
//        //System.out.println("Requested"+ reqColumnSize+" columns");
//        //change the number of columns in each row and make new row header
//        for(int i=0; i<rowData.size(); i++){
//            int prevColumnSize=rowData.get(i)[0];
//            //System.out.println("Previous column size="+prevColumnSize);
//            //copy old recordheader
//            ArrayList<Byte> recordhead=new ArrayList<Byte>();
//            recordhead.add((byte)prevColumnSize);
//            for(int j=0; j<prevColumnSize; j++){
//                recordhead.add(rowData.get(i)[j]);
//            }
//            
//            int payload_position=1+prevColumnSize;
//             //made the record header now get columns
//            ArrayList<Byte> record=new ArrayList<Byte>();
//            for(int l=1; l<=prevColumnSize; l++){
//                int bytesToRead=numOfBytesByType(rowData.get(i)[l+1]);
//                //System.out.printf("How many bytes will be read: %d\n",bytesToRead);
//                //System.out.println("Current column read "+(l+1));//DEBUG
//                //System.out.println("Is the column from the original table the same as reqCol "+requestedColumns.get(rc_index));
//                //see if the column is requested. if not requested, inc l and payload size
//                if(l==targetColumn.GetPosition()){//column to change
//                    ArrayList<Integer> tempType=new ArrayList<Integer>();
//                    tempType.add(Integer.parseInt(targetColumn.GetType()));
//                    ArrayList<String> tempValue=new ArrayList<String>();
//                    tempValue.add(newValue);
//                    byte[] arr=((DataConversion.convert_to_storage_format_executor(tempType,tempValue)));
//                    for(byte x: arr){
//                        record.add(x);
//                    }
//                    //don't read the previous value in the row
//                }
//                //else if requested, then 
//                else{
//                    //System.out.printf("Reading %d bytes\n",bytesToRead );//DEBUG
//                    for(int m=0; m<bytesToRead; m++){
//                        record.add(rowData.get(i)[payload_position+m]);
//                      }
//                }
//               payload_position+=bytesToRead;
//
//            }
//            ArrayList<Byte> mergedRow=new ArrayList<Byte>();
//            mergedRow.addAll(recordhead);
//            mergedRow.addAll(record);
//            
//            byte[] mergedRowArray=new byte[mergedRow.size()];
//            for(int n=0; n<mergedRow.size();n++){
//                mergedRowArray[n]=mergedRow.get(n);
//            }
//            filteredRows.add(mergedRowArray);
//        }
//       return filteredRows;
//    }

    public static byte[] updatedRowValue(byte[] rowData, ColumnInfo targetColumn, String newValue) {
        Record r = new Record(rowData);
        int colposition = targetColumn.GetPosition();
        r.setRowContent(colposition, newValue);
        byte type = r.GetColumnTypes().get(colposition);
        if (type >= 0x0C) {//changing a string
            //check to see if the newValue length is greater than the old length
            //change type if it is a string
            //change rowid if string is longer
            if (numOfBytesByType(type) < newValue.length()) {
                //change columnsize
                r.setColumnType(colposition, (byte) newValue.length());
                //set RowID to some dummy rowid
                r.SetRowID(0);
            }
        }
        return r.GetRowIDwRawData();
    }
}
