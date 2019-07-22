/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;
 /**
 *
 * @
 */
public class filterandprint {

    /**
     * @param args the command line arguments
     */
    // this function retrieves page header information
    public static int getNumberOfColumns(byte[] row){
        //TO DO: use bitshift operators to make this cleaner
        return row[0];
}

    //first column=1
    public static ArrayList<byte[]> filterByColumn(ArrayList<byte[]> rowData, ArrayList<Integer> requestedColumns){
        /**@param rowData=byte array of each row from the table
         **@param requestedColumns=array of columns requested
         * 
         */
        ArrayList<byte[]> filteredRows=new ArrayList<byte[]>(); 
        
        int reqColumnSize=requestedColumns.size();
        //change the number of columns in each row and make new row header
        for(int i=0; i<rowData.size(); i++){
            byte[] row; 
            int prevColumnSize=rowData.get(i)[0];
            ArrayList<Byte> recordhead=new ArrayList<Byte>();
            recordhead.add((byte)reqColumnSize);
            for(int j=0; j<reqColumnSize; j++){
                int k=requestedColumns.get(j);
                recordhead.add(rowData.get(i)[k]);
            }
            int rc_index=0;
            int payload_position=1+prevColumnSize;
             //made the record header now get columns
            ArrayList<Byte> record=new ArrayList<Byte>();
            for(int l=0; l<prevColumnSize; l++){
                int bytesToRead=numOfBytesByType(rowData.get(i)[l+1]);
                System.out.println("Current column read "+(l+1));//DEBUG
                 System.out.println(requestedColumns.get(rc_index));
                //see if the column is requested. if not requested, inc l and payload size
                if((l+1)!=requestedColumns.get(rc_index)){
                    //do nothing
                }
                //else if requested, then 
                else{
                    System.out.printf("Reading %d bytes\n",bytesToRead );//DEBUG
                    for(int m=0; m<bytesToRead; m++){
                        record.add(rowData.get(i)[payload_position+m]);
                      }
                    rc_index++;
                }
               payload_position+=bytesToRead;

            }
            ArrayList<Byte> mergedRow=new ArrayList<Byte>();
            mergedRow.addAll(recordhead);
            mergedRow.addAll(record);
            
            byte[] mergedRowArray=new byte[mergedRow.size()];
            for(int n=0; n<mergedRow.size();n++){
                mergedRowArray[n]=mergedRow.get(n);
            }
            filteredRows.add(mergedRowArray);
        }
       return filteredRows;
    }
    public static int numOfBytesByType(byte type){
        switch(type){
            case 0x00://NULL
                return 0;
            case 0x01://TINYINT
                return 1;
            case 0x02://SMALLINT
                return 2;
            case 0x03://INT
                return 4;
            //TO DO finish this list
            case 0x04:
                return 8;
            case 0x05:
                return 4;
            case 0x06:
                return 8;
            case 0x08:
                return 1;
            case 0x09:
                return 4;
            case 0x0A:
                return 8;
            case 0x0B:
                return 8;
            default://STRING
                return type-0x0C;
        }
    }
    //filter the columnTypes for only columns that we want

    public static void printRows(ArrayList<byte[]> table){
        for(int i=0; i<table.size(); i++){
            System.out.println(Arrays.toString(table.get(i)));
        }
    }
    public static void main(String[] args) {
        ArrayList<byte[]> table= new ArrayList<byte[]>();
        int [] arr={0x04, 0x02, 0x11, 0x05,0x01, 0x03, 0xA5, 0x52, 0x6F, 0x76, 0x65, 0x72, 0x41, 0xA4, 0xCC, 0xCD, 0x04};
        byte [] barr= new byte[17];
        for(int i=0; i<arr.length; i++){
            barr[i]=(byte) arr[i];
        }
        table.add(barr);
        printRows(table);
        ArrayList<Integer> reqColumns= new ArrayList<Integer>();
        reqColumns.add(1);
        //reqColumns.add(2);
        //reqColumns.add(3);
        reqColumns.add(4);
         ArrayList<byte[]> filteredtable=filterByColumn(table,reqColumns);
         printRows(filteredtable);
    }
    
}
