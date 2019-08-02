/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package davisbase;

import btree.BPlusOne;
import java.util.*;

/**
 *
 * @author Vadim
 */
public class ColumnInfo {
    static BPlusOne BPlustree = new BPlusOne();
    String columnName;
    String columnType;
    int rowId; 
    int columnTypeInt;
    int columnPosition;
    Boolean isNullable;
    Boolean isPrimary;
    Boolean isUnique;
    Boolean hasIndex;
    
    /*public static void main (String[] args){
        ArrayList<ColumnInfo> result = GetColumnInfoFromTable("greenbase_columns", "test123");
        for(int x = 0; x < result.size(); x ++){
            System.out.println(result.get(x).columnPosition);
        }
    }*/
    
    public ColumnInfo(String rowId, String name, String type, String position, String isNullable, String isPrimary, String isUnique, String hasIndex){
        this.columnName = name;
        this.columnType = type;
        this.columnTypeInt = GreenBaseDataTypes.GetDataTypeByString(type.toLowerCase());
        this.columnPosition = Integer.parseInt(position);
        this.rowId = Integer.parseInt(rowId)-1;
        Boolean isN = true;
        if(isNullable.toLowerCase().equals("no")){
            isN = false;
        }
        this.isNullable = isN;
        Boolean isP = false;
        if(isPrimary.toLowerCase().equals("pri")){
            isP=true;
        }
        this.isPrimary=isP;
        Boolean isU=false;
        if(isUnique.toLowerCase().equals("yes")){
            isU=true;
        }
        this.isUnique=isU;
        Boolean hId=false;
        if(hasIndex.toLowerCase().equals("y")){
            hId=true;
        }
        this.hasIndex = hId;
    }
   
    public int GetRowId(){
        return rowId;
    }
    
    public String GetName(){
        return columnName;
    }
    
    public String GetType(){
        return columnType;
    }
    
    public int GetTypeInt(){
        return columnTypeInt;
    }
        
    public int GetPosition(){
        return columnPosition;
    }
            
    public Boolean GetIsNullable(){
        return isNullable;
    }
    public Boolean GetIsPrimary(){
        return isPrimary;
    }
    public Boolean GetIsUnique(){
        return isPrimary;
    }
    
    public Boolean GetHasIndex(){
        return hasIndex;
    }
    
    public static ColumnInfo GetColumnByName(String columnTable, String table, String columnName){
        ArrayList<ColumnInfo> info = GetColumnInfoFromTable(columnTable, table);
        for (ColumnInfo i : info){
            if(i.GetName().toLowerCase().equals(columnName.toLowerCase())){
                return i;
            }
        }
        
        return null;
    }
    
    public static int GetColumnRowId(String columnTable, String table, String columnName){
        ArrayList<ColumnInfo> info = GetColumnInfoFromTable(columnTable, table);
        for (ColumnInfo i : info){
            if(i.GetName().toLowerCase().equals(columnName.toLowerCase())){
                return i.GetRowId();
            }
        }
        
        return -1;
    }
    
    public static int GetColumnPos(String columnTable, String table, String columnName){
        ArrayList<ColumnInfo> info = GetColumnInfoFromTable(columnTable, table);
        for (ColumnInfo i : info){
            if(i.GetName().toLowerCase().equals(columnName.toLowerCase())){
                return i.GetPosition();
            }
        }
        
        return -1;
    }
    
    public static ArrayList<ColumnInfo> GetIndexedColumns(String columnTable, String table){
        ArrayList<ColumnInfo> info = GetColumnInfoFromTable(columnTable, table);
        ArrayList<ColumnInfo> result = new ArrayList<>();
        for (ColumnInfo i : info){
            if(i.GetHasIndex()){
                result.add(i);
            }
        }
        
        return result;
    }
    
    public static ArrayList<ColumnInfo> GetColumnInfoFromTable(String columnTable, String table){
        ArrayList<ColumnInfo> result = new ArrayList<>();
        
        ArrayList<byte[]> rowResults = BPlustree.getRowData(columnTable, null);
        
        for(int x = 0; x < rowResults.size(); x++){
             ArrayList<Byte> rowResultsByte = new ArrayList<>();
            for(byte b: rowResults.get(x))
                rowResultsByte.add(b);
            ArrayList<String> result_bk = new ArrayList<>(DataConversion.convert_back_to_string_executor(rowResultsByte));
            if(result_bk.get(1).toLowerCase().equals(table.toLowerCase())){
                result.add(new ColumnInfo(result_bk.get(0),result_bk.get(2),result_bk.get(3),result_bk.get(4),result_bk.get(5),result_bk.get(6), result_bk.get(7), result_bk.get(8)));
            }
        }
        Collections.sort(result, ColumnInfo.ColumnOrder);
        return result;
    }
    
    public static Comparator<ColumnInfo> ColumnOrder = new Comparator<ColumnInfo>() {

	public int compare(ColumnInfo c1, ColumnInfo c2) {

	   int columnNo1 = c1.GetPosition();
	   int columnNo2 = c2.GetPosition();

	   /*For ascending order*/
	   return columnNo1-columnNo2;

	   /*For descending order*/
	   //return columnNo2-columnNo1;
   }};
}
