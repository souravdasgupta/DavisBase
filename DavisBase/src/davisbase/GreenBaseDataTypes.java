/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package davisbase;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sourav
 */
public class GreenBaseDataTypes {
    public static final Map<String, Integer> dataTypeString = new HashMap<String, Integer>();
	public static final Map<Integer, Integer> dataTypeSize = new HashMap<Integer, Integer>();

	static {
		dataTypeString.put("null", 0);
		dataTypeString.put("tinyint",1);
		dataTypeString.put("smallint",2);
		dataTypeString.put("int",3);
		dataTypeString.put("bigint",4);
		dataTypeString.put("long",4);
		dataTypeString.put("float",5);
		dataTypeString.put("double",6);
		dataTypeString.put("year",8);
		dataTypeString.put("time",9);
		dataTypeString.put("datetime",10);
		dataTypeString.put("date",11);
		dataTypeString.put("text",12);
		
		dataTypeSize.put(0, 0);
		dataTypeSize.put(1, 1);
		dataTypeSize.put(2, 2);
		dataTypeSize.put(3, 4);
		dataTypeSize.put(4, 8);
		dataTypeSize.put(5, 4);
		dataTypeSize.put(6, 8);
		dataTypeSize.put(8, 1);
		dataTypeSize.put(9, 4);
		dataTypeSize.put(10,8);
		dataTypeSize.put(11, 8);
		dataTypeSize.put(12, 0);
	}
	public GreenBaseDataTypes(){}
	
	public static int GetDataTypeByString(String dataType){
		int dataTypeInt = -1;
		String input = dataType.toLowerCase();
		if(dataTypeString.containsKey(input)){
			dataTypeInt = dataTypeString.get(input.toLowerCase());		
		}
		return dataTypeInt;
	}
	
	public static int GetTextId(String data){
		return 12 + data.length();
	}
	
	public static int GetDataSizeByKey(int key){
		int dataSize = -1;
		if(key >= 12){
			return key-12;
		}
		if(dataTypeSize.containsKey(key)){
			dataSize = dataTypeSize.get(key);		
		}
		return dataSize;
	}
}
