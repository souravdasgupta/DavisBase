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
    int columnPosition;
    Boolean isNullable;
    
    /*public static void main (String[] args){
        ArrayList<ColumnInfo> result = GetColumnInfoFromTable("greenbase_columns", "test123");
        for(int x = 0; x < result.size(); x ++){
            System.out.println(result.get(x).columnPosition);
        }
    }*/
    
    public ColumnInfo(String name, String type, String position, String isNullable){
        this.columnName = name;
        this.columnType = type;
        this.columnPosition = Integer.parseInt(position);
        Boolean isN = true;
        if(isNullable.toLowerCase().equals("no")){
            isN = false;
        }
        this.isNullable = isN;
    }
    
    public String GetName(){
        return columnName;
    }
    
    public String GetType(){
        return columnType;
    }
        
    public int GetPosition(){
        return columnPosition;
    }
            
    public Boolean GetIsNullable(){
        return isNullable;
    }
    
    public static ArrayList<ColumnInfo> GetColumnInfoFromTable(String columnTable, String table){
        ArrayList<ColumnInfo> result = new ArrayList<>();
        
        ArrayList<byte[]> rowResults = BPlustree.getRowData(columnTable);
        
        for(int x = 0; x < rowResults.size(); x++){
             ArrayList<Byte> rowResultsByte = new ArrayList<>();
            for(byte b: rowResults.get(x))
                rowResultsByte.add(b);
            ArrayList<String> result_bk = new ArrayList<>(DataConversion.convert_back_to_string_executor(rowResultsByte));
            if(result_bk.get(0).toLowerCase().equals(table.toLowerCase())){
                result.add(new ColumnInfo(result_bk.get(1),result_bk.get(2),result_bk.get(3),result_bk.get(4)));
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