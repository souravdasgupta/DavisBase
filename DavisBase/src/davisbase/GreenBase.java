/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package davisbase;

import btree.BPlusOne;
import indexBtree.Btree_H;
import static java.lang.System.out;
import java.util.*;

/**
 *
 * @author sourav
 */
//public class DavisBase {
//
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//        // TODO code application logic here
//        BPlusOne tree = new BPlusOne();
//        
//        for(int i = 0; i < 7; i++) {
//            var payload = ("Testing DavisBase. Inserting record number "+i).getBytes();
//            System.out.println("Inserting "+ payload.length+" bytes");
//            tree.insert(payload);
//        }
//        
//        ArrayList<byte[]> rows = tree.getRowData();
//        rows.forEach((row) -> {
//            System.out.println(Arrays.toString(row));
//        });
//        tree.closeFile();
//    }
//    
//}

/**
 * @author Team Green
 * @version 2.0
 * Updated example prompt to add functionalilty to it.
 *
 *  @author Chris Irwin Davis
 *  @version 1.0
 *  <b>
 *  <p>This is an example of how to create an interactive prompt</p>
 *  <p>There is also some guidance to get started wiht read/write of
 *     binary data files using RandomAccessFile class</p>
 *  </b>
 *
 */
public class GreenBase {

	/* This can be changed to whatever you like */
	static String prompt = "greenql> ";
	static String version = "v2.0";
	static String copyright = "©2019 Team Green";
	static boolean isExit = false;
	/*
	 * Page size for alll files is 512 bytes by default.
	 * You may choose to make it user modifiable
	 */
	static long pageSize = 512; 
	
	//Global
	static String databaseTableName = "greenbase_tables";
	static String databaseColumnName = "greenbase_columns";

	/* 
	 *  The Scanner class is used to collect user commands from the prompt
	 *  There are many ways to do this. This is just one.
	 *
	 *  Each time the semicolon (;) delimiter is entered, the userCommand 
	 *  String is re-populated.
	 */
	static Scanner scanner = new Scanner(System.in).useDelimiter(";");
	
	static BPlusOne BPlustree = new BPlusOne();
	
	/** ***********************************************************************
	 *  Main method
	 */
    public static void main(String[] args) {
                InitStartUp();
        
		/* Display the welcome screen */
		splashScreen();

		/* Variable to collect user input from the prompt */
                String userCommand = ""; 

		while(!isExit) {
			System.out.print(prompt);
			/* toLowerCase() renders command case insensitive */
			userCommand = scanner.next().replace("\n", " ").replace("\r", "").trim().toLowerCase();
			// userCommand = userCommand.replace("\n", "").replace("\r", "");
			parseUserCommand(userCommand);
		}
		System.out.println("Exiting...");
	}

	/** ***********************************************************************
	 *  Static method definitions
	 */

	/**
	 *  Display the splash screen
	 */
    
    public static void InitStartUp() {
        ArrayList<ColumnInfo> columnInfo1 = ColumnInfo.GetColumnInfoFromTable(databaseColumnName, databaseColumnName);
        ArrayList<ColumnInfo> columnInfo2 = ColumnInfo.GetColumnInfoFromTable(databaseColumnName, databaseTableName);
        if(columnInfo1.size() == 0 && columnInfo2.size() == 0){
            parseUserCommand("create table " + databaseTableName + "( rowid Int, table_name TEXT primary key)");
            parseUserCommand("create table " + databaseColumnName + "( rowid Int, table_name TEXT primary key, column_name TEXT primary key, data_type TEXT, ordinal_position TINYINT, is_nullable TEXT, column_key TEXT, unique TEXT, hasIndex TEXT)");
        }
    }
	public static void splashScreen() {
		System.out.println(line("-",80));
        System.out.println("Welcome to GreenBaseLite"); // Display the string.
		System.out.println("GreenBaseLite Version " + getVersion());
		System.out.println(getCopyright());
		System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println(line("-",80));
	}
	
	/**
	 * @param s The String to be repeated
	 * @param num The number of time to repeat String s.
	 * @return String A String object, which is the String s appended to itself num times.
	 */
	public static String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
	
	public static void printCmd(String s) {
		System.out.println("\n\t" + s + "\n");
	}
	public static void printDef(String s) {
		System.out.println("\t\t" + s);
	}
	
        /**
         *  Help: Display supported commands
         */
        public static void help() {
                out.println(line("*",80));
                out.println("SUPPORTED COMMANDS\n");
                out.println("All commands below are case insensitive\n");
                out.println("SHOW TABLES;");
                out.println("\tDisplay the names of all tables.\n");
                //printCmd("SELECT * FROM <table_name>;");
                //printDef("Display all records in the table <table_name>.");
                out.println("SELECT <column_list> FROM <table_name> [WHERE <condition>];");
                out.println("\tDisplay table records whose optional <condition>");
                out.println("\tis <column_name> = <value>.\n");
                out.println("DROP TABLE <table_name>;");
                out.println("\tRemove table data (i.e. all records) and its schema.\n");
                out.println("UPDATE TABLE <table_name> SET <column_name> = <value> [WHERE <condition>];");
                out.println("\tModify records data whose optional <condition> is\n");
                out.println("VERSION;");
                out.println("\tDisplay the program version.\n");
                out.println("HELP;");
                out.println("\tDisplay this help information.\n");
                out.println("EXIT;");
                out.println("\tExit the program.\n");
                out.println(line("*",80));
        }

	/** return the DavisBase version */
	public static String getVersion() {
		return version;
	}
	
	public static String getCopyright() {
		return copyright;
	}
	
	public static void displayVersion() {
		System.out.println("DavisBaseLite Version " + getVersion());
		System.out.println(getCopyright());
	}
		
	public static void parseUserCommand (String userCommand) {
		
                userCommand = userCommand.toLowerCase();
            
		/* commandTokens is an array of Strings that contains one token per array element 
		 * The first token can be used to determine the type of command 
		 * The other tokens can be used to pass relevant parameters to each command-specific
		 * method inside each case statement */
		// String[] commandTokens = userCommand.split(" ");
		ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));
		

		/*
		*  This switch handles a very small list of hardcoded commands of known syntax.
		*  You will want to rewrite this method to interpret more complex commands. 
		*/
		switch (commandTokens.get(0)) {
			case "select":
				//System.out.println("CASE: SELECT");
				parseQuery(userCommand);
				break;
			case "drop":
				//System.out.println("CASE: DROP");
				dropTable(userCommand);
				break;
			case "create":
				//System.out.println("CASE: CREATE");
				parseCreate(userCommand);
				break;
			case "update":
				//System.out.println("CASE: UPDATE");
				parseUpdate(userCommand);
				break;
			case "show":
				//System.out.println("CASE: SHOW");
				parseShow(userCommand);
				break;
			case "insert":
				//System.out.println("CASE: INSERT");
				parseInsert(userCommand);
				break;
                        case "delete":
				parseDelete(userCommand);
				break;
			case "help":
				help();
				break;
			case "version":
				displayVersion();
				break;
			case "exit":
				isExit = true;
				break;
			case "quit":
				isExit = true;
				break;
			default:
				System.out.println("I didn't understand the command: \"" + userCommand + "\"");
				break;
		}
	}
	/**
	 *  Stub method for dropping tables
	 *  @param deleteRowString is a String of the user input
	 */
	public static void parseDelete(String deleteRowString) {
		//System.out.println("STUB: This is the dropTable method.");
		ArrayList<String> dropTokens = new ArrayList<>(Arrays.asList(deleteRowString.split("\\s+")));
		String tableName = dropTokens.get(3);
                ArrayList<String> whereTokens = new ArrayList<>(Arrays.asList(deleteRowString.split("where")));
		System.out.println("Deleting from the Table : \"" + tableName + "\"");
                ArrayList<Integer> dropValues = ParseWhereStatement(tableName, whereTokens.get(1));
                System.out.println("Number of rows deleted " + dropValues.size());
                ArrayList<ColumnInfo> info = ColumnInfo.GetIndexedColumns(databaseColumnName, tableName);
                BPlustree.delete(tableName, dropValues);
                for(int d : dropValues){
                    for(ColumnInfo i : info){
                        Btree_H.delete(d, tableName, i.GetName());
                    }
                }
	}
        
	/**
	 *  Stub method for dropping tables
	 *  @param dropTableString is a String of the user input
	 */
	public static void dropTable(String dropTableString) {
		//System.out.println("STUB: This is the dropTable method.");
		ArrayList<String> dropTokens = new ArrayList<String>(Arrays.asList(dropTableString.split("\\s+")));
		if(dropTokens.size() != 3 || !dropTokens.get(1).equals("table")){
			System.out.println("I didn't understand the command: \"" + dropTableString + "\"");
			return;
		}
		String tableName = dropTokens.get(2);
		System.out.println("Dropping the Table : \"" + tableName + "\"");
                
                //Delete Database BPlusOne Tree
                Btree_H.deleteAllindex(tableName);

                String command = "Delete from table " + databaseColumnName + " where table_name = " + tableName;
                String command1 = "Delete from table " + databaseTableName + " where table_name = " + tableName;
                parseUserCommand(command.toLowerCase());
                parseUserCommand(command1.toLowerCase());
	}
	
	/**
	 *  Stub method for executing queries
	 *  @param queryString is a String of the user input
	 */
	public static void parseQuery(String queryString) {
		//System.out.println("STUB: This is the parseQuery method");
		ArrayList<String> fromTokens = new ArrayList<String>(Arrays.asList(queryString.split("from",2)));
		if(fromTokens.size() != 2){
			System.out.println("I didn't understand the command: \"" + queryString + "\"");
			return;
		}
                                
		ArrayList<String> whereTokens = new ArrayList<String>(Arrays.asList(fromTokens.get(1).split("where",2)));
                
                String tableName=whereTokens.get(0).substring(1).trim();//for some reason this has an extra space
                if(!DoesTableExist(tableName)){
                   System.out.println("Table " + tableName + " does not exist");
                   return;
                }

                ArrayList<Integer> whereInt = null;
		if(whereTokens.size() == 2){
			whereInt = ParseWhereStatement(tableName, whereTokens.get(1));
		}
                
		String fromTokenNoCommas = fromTokens.get(0).replace(","," ");
		
		ArrayList<String> columnTokens = new ArrayList<String>(Arrays.asList(fromTokenNoCommas.split("\\s+")));
		columnTokens.remove(0);               
                
                ArrayList<ColumnInfo> requestedColumnInfo=new ArrayList<ColumnInfo>();
                ArrayList<Integer> requestedColumns=new ArrayList<Integer>();
                ArrayList<byte[]> rowBytes=new ArrayList<byte[]>();
                rowBytes=BPlustree.getRowData(tableName, whereInt);
                
                if(columnTokens.size()==1&&columnTokens.get(0).equals("*")){
                    //select all columns
                    requestedColumnInfo= ColumnInfo.GetColumnInfoFromTable("greenbase_columns", tableName);
                    //System.out.println("* detected");
                    for(int i=0; i<requestedColumnInfo.size(); i++)
                        requestedColumns.add(requestedColumnInfo.get(i).GetPosition());
                    //System.out.println(requestedColumns.toString());
                    //rowBytes=filterandprint.filterByColumn(BPlustree.getRowData(tableName), requestedColumns);
                    //System.out.println(rowBytes.toString());
                    for(int i=0; i<rowBytes.size(); i++){
                        ArrayList<Byte> toHex=new ArrayList<Byte>();
                        for(byte b: rowBytes.get(i))
                            toHex.add(b);
                        DataConversion.Show_in_Hex(toHex);
                    }
                    
                    rowBytes=filterandprint.filterByColumn(rowBytes, requestedColumns);
                }else {
                    //
                    //System.out.println("filtering columns");//debug
                    requestedColumnInfo= filterandprint.columnTokensToReqColumnsList(columnTokens, "greenbase_columns",tableName );

                    for(int i=0; i<requestedColumnInfo.size(); i++)
                        requestedColumns.add(requestedColumnInfo.get(i).GetPosition());

                    rowBytes=filterandprint.filterByColumn(rowBytes, requestedColumns);
                }
                filterandprint.printRows(rowBytes, requestedColumnInfo);
	}
	
	/**
	 *  Stub method for updating records
	 *  @param updateString is a String of the user input
	 */
	public static void parseUpdate(String updateString) {
		//System.out.println("STUB: This is the parseUpdate method");
		//System.out.println("Parsing the string:\"" + updateString + "\"");
                ArrayList<String> updateTokens = new ArrayList<String>(Arrays.asList(updateString.split("\\s+")));
                ArrayList<String> selectTokens = new ArrayList<String>(Arrays.asList(updateString.trim().split("set")));
                ArrayList<String> whereTokens = new ArrayList<>(Arrays.asList(selectTokens.get(1).trim().split("where")));
                String tableName = updateTokens.get(1);
                String setValue = whereTokens.get(0).trim();
                ArrayList<String> setTokens = new ArrayList<String>(Arrays.asList(setValue.split("\\s+")));
                String columnName = setTokens.get(0).trim();
                int columnPosition = ColumnInfo.GetColumnPos(databaseColumnName, tableName, columnName)-1;
                if(columnPosition == -1){
                    System.out.println("Error Column does not exist");
                }
                String UpdatedValue = setTokens.get(2);
                ArrayList<Integer> whereArr = ParseWhereStatement(tableName, whereTokens.get(1).trim());
                ArrayList<byte[]> val = BPlustree.getRowData(tableName, whereArr);
                ArrayList<ArrayList<String>> values = new ArrayList<>();
                for (int i = 0; i < val.size(); i++) {
                    ArrayList<Byte> rowDataByte = new ArrayList<Byte>();
                    for (byte b : val.get(i)) {
                        rowDataByte.add(b);
                    }
                    ArrayList<String> result_bk = new ArrayList<String>(DataConversion.convert_back_to_string_executor(rowDataByte));
                    values.add(result_bk);
                }
                parseUserCommand("Delete form table "+tableName+" WHERE " + whereTokens.get(1));
                for(int x = 0; x < values.size(); x++){
                    ArrayList<String> value = values.get(x);
                    String command = "Insert Into table " + tableName + " values (";
                    for(int y = 0; y < value.size(); y++){
                        if(y == columnPosition){
                            command+= UpdatedValue;
                        }else{
                            command+= value.get(y);
                        }
                        if(y != value.size()-1){
                            command+= ",";
                        }
                    }
                    command += ")";
                    parseUserCommand(command);
                }
	}

	public static void parseShow(String showString) {
		//System.out.println("STUB: This is the parseShow method");
		ArrayList<String> showTokens = new ArrayList<String>(Arrays.asList(showString.split("\\s+")));
		if(showTokens.size() != 2 || !showTokens.get(1).equals("tables")){
			System.out.println("I didn't understand the command: \"" + showString + "\"");
			return;
		}
		String showTablesQuery = "SELECT table_name FROM " +  databaseTableName;
		parseQuery(showTablesQuery.toLowerCase());
	}
	
	public static void parseCreate(String createString) {
		//System.out.println("Checking which create it is");
		ArrayList<String> createTokens = new ArrayList<String>(Arrays.asList(createString.split("\\s+")));
		switch (createTokens.get(1)) {
			case "table":
				//System.out.println("CASE: TABLE");
				parseCreateTable(createString);
				break;
			case "index":
				//System.out.println("CASE: INDEX");
				parseCreateIndex(createString);
				break;
			default:
				System.out.println("I didn't understand the command: \"" + createString + "\"");
				break;
		}	
	}
	
	/**
	 *  Stub method for creating new index
	 *  @param createIndexString is a String of the user input
	 */
	public static void parseCreateIndex(String createIndexString) {
		//System.out.println("STUB: This is the parseCreateIndex method");
		ArrayList<String> createIndexTokens = new ArrayList<String>(Arrays.asList(createIndexString.split("\\s+")));
		if(createIndexTokens.size() != 4){
			System.out.println("I didn't understand the command: \"" + createIndexTokens + "\"");
			return;
		}
		System.out.println("Creating the Index:\"" + createIndexTokens.get(2) + "\" on column " + createIndexTokens.get(3));
                String tableName = createIndexTokens.get(2);
                String columnName = createIndexTokens.get(3);
                ColumnInfo columnRow = ColumnInfo.GetColumnByName(databaseColumnName, tableName, columnName);
                String command = "update " + databaseColumnName + " set hasindex = y where table_name = " + tableName + " and column_name = " + columnName;
                parseUserCommand(command.toLowerCase());
        }
	
	/**
	 *  Stub method for creating new tables
	 *  @param createTableString is a String of the user input
	 */
	public static void parseCreateTable(String createTableString) {	
		//System.out.println("STUB: Calling your method to create a table");
		//System.out.println("Parsing the string:\"" + createTableString + "\"");
		ArrayList<String> createTableParameterTokens = new ArrayList<String>(Arrays.asList(createTableString.split("(\\(|\\))")));
		ArrayList<String> createTableTokens = new ArrayList<String>(Arrays.asList(createTableParameterTokens.get(0).split("\\s+")));
		if(createTableTokens.size() != 3 || createTableParameterTokens.size() != 2){
			System.out.println("I didn't understand the command: \"" + createTableString + "\"");
			return;
		}
		
		String tableName = createTableTokens.get(2);
		
                if(DoesTableExist(tableName)){
                   System.out.println("Table " + tableName + " Already exists");
                   return;
                }
		
		ArrayList<String> tableNameArray = new ArrayList<String>();
                ArrayList<Integer> tableValueArray = new ArrayList<Integer>();
                tableValueArray.add(3);
		tableNameArray.add(BPlustree.getMaxRowID(databaseTableName)+"");
		tableNameArray.add(tableName);
		tableValueArray.add(GreenBaseDataTypes.GetTextId(tableName));
		
		byte[] tableTableResult = DataConversion.convert_to_storage_format_executor(tableValueArray,tableNameArray);
		
		BPlustree.insert(databaseTableName, tableTableResult);
		Btree_H.insert(BPlustree.getMaxRowID(databaseTableName)-2,tableName, databaseTableName, "table_name");

		ArrayList<String> TableColumns = new ArrayList<String>(Arrays.asList(createTableParameterTokens.get(1).split(",")));

		String tinyintDataType = "tinyint";
		for (int x = 0; x < TableColumns.size(); x++){
				String tableColumn = TableColumns.get(x);
				ArrayList<String> columnInfoTokens = new ArrayList<String>(Arrays.asList(tableColumn.trim().split("\\s+")));
				String columnName = columnInfoTokens.get(0);
				String columnType = columnInfoTokens.get(1);
				Boolean isNull = true;
                                Boolean isUnique=false;
				Boolean isPrimary = false;
                                if(columnInfoTokens.size() == 3){
                                    if(columnInfoTokens.get(2).equals("unique")){
                                        isUnique = true;
                                        isNull=false;
                                    }
                                }else{
                                    if(columnInfoTokens.size() == 4){
                                        if(columnInfoTokens.get(2).equals("not") && columnInfoTokens.get(3).equals("null")){
                                            isNull = false;
                                        }
                                        if(columnInfoTokens.get(2).equals("primary") && columnInfoTokens.get(3).equals("key")){
                                            isPrimary = true;
                                        }
                                    }//columnname type UNIQUE NOT NULL Primary Key
                                    if(columnInfoTokens.size() == 5){
                                        if((columnInfoTokens.get(2).equals("not") && columnInfoTokens.get(3).equals("null"))||(columnInfoTokens.get(3).equals("not") && columnInfoTokens.get(4).equals("null"))){
                                            isNull = false;
                                        }
                                        if(columnInfoTokens.get(2).equals("unique") || columnInfoTokens.get(3).equals("unique") || columnInfoTokens.get(4).equals("unique")){
                                            isUnique = true;
                                            isNull=false;
                                        }
                                        if((columnInfoTokens.get(2).equals("primary") && columnInfoTokens.get(3).equals("key"))||(columnInfoTokens.get(3).equals("primary") && columnInfoTokens.get(4).equals("key"))){
                                            isPrimary = true;
                                        }
                                    }
                                    if(columnInfoTokens.size() == 6){
                                        if(columnInfoTokens.get(2).equals("unique") || columnInfoTokens.get(3).equals("unique") || columnInfoTokens.get(4).equals("unique") || columnInfoTokens.get(5).equals("unique")){
                                            isUnique = true;
                                        }
                                        if((columnInfoTokens.get(2).equals("not") && columnInfoTokens.get(3).equals("null"))||(columnInfoTokens.get(3).equals("not") && columnInfoTokens.get(4).equals("null")) || (columnInfoTokens.get(4).equals("not") && columnInfoTokens.get(5).equals("null"))){
                                            isNull = false;
                                        }
                                        if((columnInfoTokens.get(2).equals("primary") && columnInfoTokens.get(3).equals("key"))||(columnInfoTokens.get(3).equals("primary") && columnInfoTokens.get(4).equals("key")) ||(columnInfoTokens.get(4).equals("primary") && columnInfoTokens.get(5).equals("key"))){
                                            isPrimary = true;
                                            isUnique=true;
                                            isNull=false;
                                        }
                                    }
                                }
				ArrayList<Integer> valueTypes = new ArrayList<Integer>();
				ArrayList<String> valueData = new ArrayList<String>();
                                valueTypes.add(3);
				valueData.add(BPlustree.getMaxRowID(databaseColumnName)+"");
				valueTypes.add(GreenBaseDataTypes.GetTextId(tableName));
				valueData.add(tableName);
				valueTypes.add(GreenBaseDataTypes.GetTextId(columnName));
				valueData.add(columnName);
				valueTypes.add(GreenBaseDataTypes.GetTextId(columnType));
				valueData.add(columnType.toUpperCase());
				valueTypes.add(GreenBaseDataTypes.GetDataTypeByString(tinyintDataType));
				valueData.add(""+(x+1));
                                String isNuller = "YES";
                                String isPrimaryKey="PRI";
                                String isUnique_p="YES";
                                String hasIndex="N";
                                if(!isNull){
                                    isNuller = "NO";
                                }
                                if(!isPrimary){
                                    isPrimaryKey = "NULL";
                                }
                                if(!isUnique){
                                    isUnique_p = "NO";
                                }
				valueTypes.add(GreenBaseDataTypes.GetTextId(isNuller));
				valueData.add(isNuller);
                                valueTypes.add(GreenBaseDataTypes.GetTextId(isPrimaryKey));
				valueData.add(isPrimaryKey);
                                valueTypes.add(GreenBaseDataTypes.GetTextId(isUnique_p));
				valueData.add(isUnique_p);
                                if(isPrimary || isUnique){
                                    hasIndex = "Y";
                                }
                                valueTypes.add(GreenBaseDataTypes.GetTextId(hasIndex));
				valueData.add(hasIndex);
				//System.out.println(valueTypes + " " + valueData);
				byte[] result = DataConversion.convert_to_storage_format_executor(valueTypes,valueData);
				BPlustree.insert(databaseColumnName, result);
                                Btree_H.insert(BPlustree.getMaxRowID(databaseColumnName)-2,tableName, databaseColumnName, "table_name");
                                Btree_H.insert(BPlustree.getMaxRowID(databaseColumnName)-2,columnName, databaseColumnName, "column_name");
		}
	}
        
       /**
	 *  Stub method for inserting records
	 *  @param insertString is a String of the user input
	 */
	public static void parseInsert(String insertString) {
		ArrayList<String> insertvaluesTokens = new ArrayList<String>(Arrays.asList(insertString.trim().split("values")));
                if(insertvaluesTokens.size() != 2){
                    System.out.println("I didn't understand the command: \"" + insertString + "\"");
		    return;
                }
                ArrayList<String> insertParameterTokens = new ArrayList<String>(Arrays.asList(insertvaluesTokens.get(0).split("(\\(|\\))")));
                if(insertParameterTokens.size() != 1 && insertParameterTokens.size() != 3){
                    System.out.println("I didn't understand the command: \"" + insertString + "\"");
		    return;
                }
                String tableName = "";
                String tableParamString = "";
                ArrayList<String> insertValuesName = new ArrayList<>();
                boolean hasParam = false;
                if(insertParameterTokens.size() == 1){
                    ArrayList<String> insertTokens = new ArrayList<String>(Arrays.asList(insertParameterTokens.get(0).trim().split("\\s+")));
                    if(insertTokens.size() < 4){
                        System.out.println("I didn't understand the command: \"" + insertString + "\"");
                        return;
                    }
                    tableName = insertTokens.get(3).trim();
                }
                if(insertParameterTokens.size() == 3){
                    tableParamString = insertParameterTokens.get(1);
                    insertValuesName = new ArrayList<String>(Arrays.asList(tableParamString.trim().split(",")));
                    tableName = insertParameterTokens.get(2).trim();
                    hasParam = true;
                }
                
                ArrayList<ColumnInfo> columnInfo = ColumnInfo.GetColumnInfoFromTable(databaseColumnName, tableName);
                if(columnInfo.size() == 0){
                    System.out.println("No table found with the name \"" + tableName + "\"");
		    return;
                }
                
                ArrayList<String> insertValues = new ArrayList<String>(Arrays.asList(insertvaluesTokens.get(1).trim().split("(\\(|\\))")));
                insertValues = new ArrayList<String>(Arrays.asList(insertValues.get(1).trim().split(",")));
                
                if(hasParam){
                     ArrayList<String> orderedValues = new ArrayList<>();
                    for(int x = 0; x < columnInfo.size(); x++){
                        String columnName = columnInfo.get(x).GetName().trim();
                        Boolean added = false;
                        for(int y = 0; x < insertValuesName.size(); y++){
                            if(columnName.equals(insertValuesName.get(y).trim())){
                                orderedValues.add(insertValues.get(y).trim());
                                added = true;
                                break;
                            }
                        }
                        if(!added)
                        {
                            orderedValues.add("null");
                        }
                    }
                    insertValues = new ArrayList<String>(orderedValues);
                }
                
                insertValues = TrimStringsValues(insertValues);
                //System.out.println(insertValues);
                
                if(columnInfo.size() != insertValues.size()){
                    System.out.println("Invalid number of parameters");
                    return;
                }
                
                ArrayList<String> ParamValueArray = new ArrayList<String>();
		ArrayList<Integer> ParamTypeArray = new ArrayList<Integer>();
                
                ArrayList<ColumnInfo> indexColumn = new ArrayList<>();
		ArrayList<String> indexValue = new ArrayList<>();
                
                for(int x = 0; x < columnInfo.size(); x++){  
                    String insertVar = insertValues.get(x);
                    boolean hasIndex = columnInfo.get(x).GetHasIndex();
                    if(insertVar.equals("null")){
                        if(!columnInfo.get(x).isNullable){
                            System.out.println(columnInfo.get(x).GetName() + " is not Nullable");
                            return;
                        }
                        ParamTypeArray.add(GreenBaseDataTypes.GetDataTypeByString("null"));
                        ParamValueArray.add("");
                        continue;
                    }
                    ParamTypeArray.add(GreenBaseDataTypes.GetDataTypeByString(columnInfo.get(x).GetType(),insertVar));
                    ParamValueArray.add(insertVar);
                    if(hasIndex){
                        indexColumn.add(columnInfo.get(x));
                        indexValue.add(insertVar);
                    }
                }
                int rowId = BPlustree.getMaxRowID(tableName)-1;
                byte[] result = DataConversion.convert_to_storage_format_executor(ParamTypeArray,ParamValueArray);
		BPlustree.insert(tableName, result);  
                for(int x = 0; x < indexValue.size(); x++){
                    InsertIntoIndex(tableName, indexColumn.get(x), rowId, indexValue.get(x));
                }
	} 
        
        public static ArrayList<String> TrimStringsValues(ArrayList<String> values){
            ArrayList<String> trimmedStrings = new ArrayList<>();
            for(String s : values) {
              trimmedStrings.add(s.trim());
            }
            return trimmedStrings;
        }
        
        public static Boolean DoesTableExist(String tableName){
            ArrayList<byte[]> rowResults = BPlustree.getRowData(databaseTableName, null);
            for(int x = 0; x < rowResults.size(); x++){
                ArrayList<Byte> rowResultsByte = new ArrayList<>();
                   for(byte b: rowResults.get(x))
                       rowResultsByte.add(b);
                   ArrayList<String> result_bk = new ArrayList<>(DataConversion.convert_back_to_string_executor(rowResultsByte));
                   if(result_bk.get(1).toLowerCase().equals(tableName.toLowerCase())){
                      return true;
                   }
               }
            return false;
        }
        
        public static ArrayList<Integer> ParseWhereStatement(String tableName, String whereStatement){
            ArrayList<Integer> result = new ArrayList<>();
            
            ArrayList<ColumnInfo> columnInfo = ColumnInfo.GetColumnInfoFromTable(databaseColumnName, tableName);
            
            if(columnInfo.isEmpty()){
                System.out.println("Error table " + tableName + " has no columns");
                return null;
            }
            
            ArrayList<String> whereTokens = new ArrayList<>(Arrays.asList(whereStatement.trim().split("\\s+")));
            
            boolean hasAnd = false;
            boolean hasOr = false;
            boolean hasNot = false;           
            
            for(int x = 0; x < whereTokens.size(); x+=3){
                hasAnd = false;
                hasOr = false;
                hasNot = false;
                switch (whereTokens.get(x).toLowerCase().trim()){
                    case "and" : 
                        hasAnd = true;
                        x++;
                        break;
                    case "or": 
                        hasOr = true;
                        x++;
                        break;  
                }
                if(whereTokens.get(x).toLowerCase().trim().equals("not")){
                    hasNot = true;
                    x++;
                }
                
                if(x+2 >= whereTokens.size()){
                    System.out.println("Error Invalid Command.");
                    return null;
                }
                
                ColumnInfo column = GetColumnInfoFromArray(whereTokens.get(x).trim(),columnInfo);
                if(column == null){
                    System.out.println("Error column "+whereTokens.get(x).trim()+ " does not exist!");
                    return null;
                }
                
                ArrayList<Integer> equalInt = EqualityConverter.GetOperations(whereTokens.get(x+1).trim(), hasNot);
                if(equalInt.isEmpty()){
                    System.out.println("Error symbol "+whereTokens.get(x+1).trim()+ " does not exist!");
                    return null;
                }
                
                String compareValue = whereTokens.get(x+2).trim();
                
                ArrayList<Integer> xRes = new ArrayList<>();
                for(int z : equalInt){
                    xRes.addAll(Btree_H.search(compareValue, (byte)column.GetTypeInt(), z, tableName, column.GetName()));
                }
                 
                if(hasAnd){
                    ArrayList<Integer> temp = new ArrayList<>();
                    for (int t : result) {
                        if(xRes.contains(t)) {
                            temp.add(t);
                        }
                    }
                    result = temp;
                }else{
                    result.addAll(xRes);
                }
            } 
            //remove duplicates
            LinkedHashSet<Integer> hashSet = new LinkedHashSet<>(result);
            result = new ArrayList<>(hashSet);
            return result;
        }
        
        public static ColumnInfo GetColumnInfoFromArray(String columnName, ArrayList<ColumnInfo> columnInfo){
            for(ColumnInfo i : columnInfo){
                if(i.GetName().toLowerCase().trim().equals(columnName.toLowerCase().trim())){
                    return i;
                }
            }   
            return null;
        };
        
        public static void InsertIntoIndex(String tableName, ColumnInfo column, int rowId, String value){
            Btree_H.insert(rowId, value, tableName, column.GetName());
        }
}
