/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package davisbase;

import btree.BPlusOne;
import static davisbase.ColumnInfo.BPlustree;
import java.util.ArrayList;
import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;

/**
 *
 * @Madeline
 */
public class filterandprint {

    /**
     * @param args the command line arguments
     */
    public static ArrayList<byte[]> filterByColumn(ArrayList<byte[]> rowData, ArrayList<Integer> requestedColumns) {
        /**
         * @param rowData=byte array of each row from the table
         **@param requestedColumns=array of columns requested
         *
         */
        //System.out.println("Entered filterbyColumn");
        ArrayList<byte[]> filteredRows = new ArrayList<byte[]>();

        int reqColumnSize = requestedColumns.size();
        //System.out.println("Requested"+ reqColumnSize+" columns");
        //change the number of columns in each row and make new row header
        for (int i = 0; i < rowData.size(); i++) {
            int prevColumnSize = rowData.get(i)[0];
            //System.out.println("Previous column size="+prevColumnSize);
            ArrayList<Byte> recordhead = new ArrayList<Byte>();
            recordhead.add((byte) reqColumnSize);
            for (int j = 0; j < reqColumnSize; j++) {
                int k = requestedColumns.get(j);
                recordhead.add(rowData.get(i)[k]);
            }
            int rc_index = 0;
            int payload_position = 1 + prevColumnSize;
            //made the record header now get columns
            ArrayList<Byte> record = new ArrayList<Byte>();
            for (int l = 0; l < prevColumnSize; l++) {

                int bytesToRead = numOfBytesByType(rowData.get(i)[l + 1]);
                //System.out.printf("How many bytes will be read: %d\n",bytesToRead);
                //System.out.println("Current column read "+(l+1));//DEBUG
                //System.out.println("Is the column from the original table the same as reqCol "+requestedColumns.get(rc_index));
                //see if the column is requested. if not requested, inc l and payload size
                if ((l + 1) != requestedColumns.get(rc_index)) {
                    //do nothing
                } //else if requested, then 
                else {
                    //System.out.printf("Reading %d bytes\n",bytesToRead );//DEBUG
                    for (int m = 0; m < bytesToRead; m++) {
                        record.add(rowData.get(i)[payload_position + m]);
                    }
                    rc_index++;
                    if (rc_index >= requestedColumns.size()) {
                        break;
                    }
                }
                payload_position += bytesToRead;

            }
            ArrayList<Byte> mergedRow = new ArrayList<Byte>();
            mergedRow.addAll(recordhead);
            mergedRow.addAll(record);

            byte[] mergedRowArray = new byte[mergedRow.size()];
            for (int n = 0; n < mergedRow.size(); n++) {
                mergedRowArray[n] = mergedRow.get(n);
            }
            filteredRows.add(mergedRowArray);
        }
        return filteredRows;
    }

    public static int numOfBytesByType(byte type) {
        if (type < 0x00) {
            System.out.println("Negative type!\n");
        }
        switch (type) {
            case 0x00://NULL
                return 0;
            case 0x01://TINYINT
                return 1;
            case 0x02://SMALLINT
                return 2;
            case 0x03://INT
                return 4;
            case 0x04://BIGINT,LONG
                return 8;
            case 0x05://FLOAT
                return 4;
            case 0x06://DOUBLE
                return 8;
            case 0x08://YEAR
                return 1;
            case 0x09://TIME
                return 4;
            case 0x0A://DATETIME
                return 8;
            case 0x0B://DATE
                return 8;
            default://STRING
                return (type - 0x0C);
        }
    }
    //filter the columnTypes for only columns that we want
/*For Debugging only
     public static void printRowsBytes(ArrayList<byte[]> table){
     for(int i=0; i<table.size(); i++){
     System.out.println(Arrays.toString(table.get(i)));
     }
     }
     */

    public static void printRows(ArrayList<byte[]> rowData, ArrayList<ColumnInfo> requestedColumnInfo) {
        for (ColumnInfo x : requestedColumnInfo) {
            System.out.printf("%30s", x.GetName());
        }
        System.out.println();
        for (int i = 0; i < rowData.size(); i++) {

            ArrayList<Byte> rowDataByte = new ArrayList<Byte>();
            for (byte b : rowData.get(i)) {
                rowDataByte.add(b);
            }
            ArrayList<String> result_bk = new ArrayList<String>(DataConversion.convert_back_to_string_executor(rowDataByte));
            for (int j = 0; j < result_bk.size(); j++) {
                System.out.printf("%30s", result_bk.get(j));
            }
            System.out.println();
        }
    }

    public static ArrayList<ColumnInfo> columnTokensToReqColumnsList(ArrayList<String> columnTokens, String columnTable, String tableName) {
        ArrayList<ColumnInfo> columnList = new ArrayList<ColumnInfo>();
        ArrayList<ColumnInfo> result = new ArrayList<>();
        ArrayList<byte[]> rowResults = BPlustree.getRowData(columnTable);

        for (int x = 0; x < rowResults.size(); x++) {
            ArrayList<Byte> rowResultsByte = new ArrayList<>();
            for (byte b : rowResults.get(x)) {
                rowResultsByte.add(b);
            }
            ArrayList<String> result_bk = new ArrayList<>(DataConversion.convert_back_to_string_executor(rowResultsByte));
            if (result_bk.get(1).toLowerCase().equals(tableName.toLowerCase())) {
                result.add(new ColumnInfo(result_bk.get(2), result_bk.get(3), result_bk.get(4), result_bk.get(5), result_bk.get(6), result_bk.get(7)));
            }
        }
        for (int i = 0; i < columnTokens.size(); i++) {
            String cName = columnTokens.get(i);
            for (int j = 0; j < result.size(); j++) {
                ColumnInfo entry = result.get(j);
                if (cName.equals(entry.columnName)) {
                    columnList.add(entry);
                    break;//break out of searching for cName(i)
                } else if (j == result.size() - 1) {//the last columnName does not match the token
                    System.out.printf("I could not find column \n", cName);
                }
            }
        }
        return columnList;
    }


    /* 
     public static void main(String[] args) {
     ArrayList<byte[]> table= new ArrayList<byte[]>();
     int [] arr={0x04, 0x02, 0x11, 0x05,0x01, 0x03, 0xA5, 0x52, 0x6F, 0x76, 0x65, 0x72, 0x41, 0xA4, 0xCC, 0xCD, 0x04};
     ArrayList<String> teststring=new ArrayList<String>();
        
     byte [] barr= new byte[17];
     for(int i=0; i<arr.length; i++){
     barr[i]=(byte) arr[i];
     }
     table.add(barr);
     printRowsBytes(table);
     ArrayList<Integer> reqColumns= new ArrayList<Integer>();
     reqColumns.add(1);
     //reqColumns.add(2);
     //reqColumns.add(3);
     reqColumns.add(4);
     ArrayList<byte[]> filteredtable=filterByColumn(table,reqColumns);
     printRowsBytes(filteredtable);
     teststring.add("dog");
     teststring.add("10.4");
     teststring.add("1000");
     printRows(teststring);
     }*/
}
