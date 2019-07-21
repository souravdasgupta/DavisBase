/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package davisbase;

import btree.BPlusOne;
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

	/* 
	 *  The Scanner class is used to collect user commands from the prompt
	 *  There are many ways to do this. This is just one.
	 *
	 *  Each time the semicolon (;) delimiter is entered, the userCommand 
	 *  String is re-populated.
	 */
	static Scanner scanner = new Scanner(System.in).useDelimiter(";");
	
	/** ***********************************************************************
	 *  Main method
	 */
    public static void main(String[] args) {

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
		if(whereTokens.size() == 2){
			System.out.println("Has a Where Clause");
		}else{
			System.out.println("Does not have a Where Clause");
		}
		
		String fromTokenNoCommas = fromTokens.get(0).replace(","," ");
		
		ArrayList<String> columnTokens = new ArrayList<String>(Arrays.asList(fromTokenNoCommas.split("\\s+")));
		columnTokens.remove(0);
		
		System.out.println("Selecting " + columnTokens + " From " +  whereTokens.get(0));
	
	}
	
	/**
	 *  Stub method for updating records
	 *  @param updateString is a String of the user input
	 */
	public static void parseUpdate(String updateString) {
		//System.out.println("STUB: This is the parseUpdate method");
		System.out.println("Parsing the string:\"" + updateString + "\"");
	}

	public static void parseShow(String showString) {
		//System.out.println("STUB: This is the parseShow method");
		ArrayList<String> showTokens = new ArrayList<String>(Arrays.asList(showString.split("\\s+")));
		if(showTokens.size() != 2 || !showTokens.get(1).equals("tables")){
			System.out.println("I didn't understand the command: \"" + showString + "\"");
			return;
		}
		String showTablesQuery = "SELECT table_name FROM davisbase_tables";
		parseQuery(showTablesQuery);
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
	 *  Stub method for creating new indexs
	 *  @param createIndexString is a String of the user input
	 */
	public static void parseCreateIndex(String createIndexString) {
		//System.out.println("STUB: This is the parseCreateIndex method");
		ArrayList<String> createIndexTokens = new ArrayList<String>(Arrays.asList(createIndexString.split("\\s+")));
		if(createIndexTokens.size() != 3){
			System.out.println("I didn't understand the command: \"" + createIndexTokens + "\"");
			return;
		}
		System.out.println("Creating the Index:\"" + createIndexTokens.get(2) + "\"");
	}
	
	/**
	 *  Stub method for creating new tables
	 *  @param queryString is a String of the user input
	 */
	public static void parseCreateTable(String createTableString) {	
		System.out.println("STUB: Calling your method to create a table");
		System.out.println("Parsing the string:\"" + createTableString + "\"");
		ArrayList<String> createTableParameterTokens = new ArrayList<String>(Arrays.asList(createTableString.split("(\\(|\\))")));
		ArrayList<String> createTableTokens = new ArrayList<String>(Arrays.asList(createTableParameterTokens.get(0).split("\\s+")));
		if(createTableTokens.size() != 3 || createTableParameterTokens.size() != 2){
			System.out.println("I didn't understand the command: \"" + createTableString + "\"");
			return;
		}
		
		String tableName = createTableTokens.get(2);
		String tableFileName = tableName + ".tbl";
		
		ArrayList<String> TableColumns = new ArrayList<String>(Arrays.asList(createTableParameterTokens.get(1).split(",")));

		System.out.println("The table has " + TableColumns.size() + " columns");
		String tinyintDataType = "tinyint";
		for (int x = 0; x < TableColumns.size(); x++){
				String tableColumn = TableColumns.get(x);
				ArrayList<String> columnInfoTokens = new ArrayList<String>(Arrays.asList(tableColumn.trim().split("\\s+")));
				String columnName = columnInfoTokens.get(0);
				String columnType = columnInfoTokens.get(1);
				System.out.println("Column name " + columnName + " Column Type " + columnType);
				Boolean isNull = true;
				Boolean isPrimay = false;
				ArrayList<Integer> valueTypes = new ArrayList<Integer>();
				ArrayList<String> valueData = new ArrayList<String>();
				valueTypes.add(GreenBaseDataTypes.GetTextId(tableName));
				valueData.add(tableName);
				valueTypes.add(GreenBaseDataTypes.GetTextId(columnName));
				valueData.add(columnName);
				valueTypes.add(GreenBaseDataTypes.GetTextId(columnType));
				valueData.add(columnType);
				valueTypes.add(GreenBaseDataTypes.GetDataTypeByString(tinyintDataType));
				valueData.add(""+(x+1));
				valueTypes.add(GreenBaseDataTypes.GetTextId("NO"));
				valueData.add("NO");
				System.out.println(valueTypes + " " + valueData);
		}
	}
}
