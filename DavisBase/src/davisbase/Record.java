/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package davisbase;
import static davisbase.filterandprint.numOfBytesByType;
import java.util.ArrayList;
/**
 *
 * @author madel
 */
public class Record {
    int rowid;
    int numOfColumns;
    ArrayList<Byte> columnTypes;
    ArrayList<String> rowContents;
 public Record(int rowid, int numOfColumns, ArrayList<Byte> columnTypes, ArrayList<String> rowContents){
     this.columnTypes=new ArrayList<Byte>();
     this.rowContents=new ArrayList<String>();
     this.rowid=rowid;
     //deep copy these ArrayLists
     for(byte x:columnTypes){
         this.columnTypes.add(x);
     }
     for(String x:rowContents){
         this.rowContents.add(x);
     }
     
} 
 public Record(byte[] rowData){
     this.columnTypes=new ArrayList<Byte>();
     //note that this assumes rowid is given in the rowData set
     this.rowid=(rowData[0]&0xFF)<<24;
     this.rowid+=(rowData[1]&0xFF)<<16;
     this.rowid+=(rowData[2]&0xFF)<<8;
     this.rowid+=rowData[3]&0xFF;
     
     this.numOfColumns=rowData[4]&0xFF;
     int pos=5;
     int end=5+numOfColumns;
     for(pos=5; pos<end; pos++){
         columnTypes.add(rowData[pos]);
     }
     ArrayList<Byte> payload=new ArrayList<Byte>();
     for(int i=4; i<rowData.length; i++){
         payload.add(rowData[i]);
     }
     this.rowContents=new ArrayList<>(DataConversion.convert_back_to_string_executor(payload));
}

 public int GetRowID(){
     return rowid;
 }
 public int numOfColumns(){
     return numOfColumns;
 }
 public ArrayList<Byte> GetColumnTypes(){
     return columnTypes;
 }
 public ArrayList<String> GetRowContents(){
     return rowContents;
 }
 //GetRowIDwRawData will return a byte array of row contents
 public byte[] GetRowIDwRawData(){
//convert rowid into a byte array
     byte[] rowIDHead=new byte[4];
     rowIDHead[0]=(byte)(this.rowid>>24);
     rowIDHead[1]=(byte)(this.rowid>>16);
     rowIDHead[2]=(byte)(this.rowid>>8);
     rowIDHead[3]=(byte)(this.rowid);
     ArrayList<Integer> columnTypesInt=new ArrayList<Integer>();
     for(byte x: columnTypes){
         columnTypesInt.add((int)x);
     }
     byte[]result=DataConversion.convert_to_storage_format_executor(columnTypesInt,rowContents);
     int newLength=4+result.length;
     byte[]rawData=new byte[newLength];
     for(int i=0; i<4; i++){
         rawData[i]=rowIDHead[i];
     }
     for(int j=4; j<newLength; j++){
         rawData[j]=result[j-4];
     }
     return rawData;
//convert_to_storage_format_executor
 }
 
 public void SetRowID(int newrowid){
     this.rowid=newrowid;
 }
 
public void setColumnType(int columnposition,byte newtype){//called for long strings
    /**@param columnposition=ordinal position 1st column is 1
     * 
     */
    this.columnTypes.set(columnposition-1, newtype);
}
public void setRowContent(int columnposition, String newvalue){
    /**@param columnposition=ordinal position 1st column is 1
     * 
     */
    this.rowContents.set(columnposition-1, new String(newvalue));
}
public void showfullRecord(boolean showid){//for debugging
    if(showid==true){
        System.out.printf("%30d",this.rowid);
    }
         ArrayList<Byte> rowDataByte = new ArrayList<Byte>();
         byte[] b=GetRowIDwRawData();
         for(int i=4; i<b.length; i++)
                rowDataByte.add(b[i]);
            ArrayList<String> result_bk = new ArrayList<String>(DataConversion.convert_back_to_string_executor(rowDataByte));
            for(int j=0; j<result_bk.size(); j++){
                System.out.printf("%30s", result_bk.get(j));
            }
            System.out.println();
     
}
}

