import java.util.ArrayList;
import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;
 /**
 *
 * @
 */
public class Database {

    /**
     * @param args the command line arguments
     */
    // this function retrieves page header information
    public static int getNumberOfColumns(byte[] row){
        //TO DO: use bitshift operators to make this cleaner
        return row[0]*16+row[1];
}
    public static ArrayList<Integer[]> getColumnTypes(int numOfColumns, ArrayList<byte[]> rowData){
        ArrayList<Integer[]> columnTypes=new ArrayList<Integer[]>();
        //for all rows get column types
        for(int i=0; i<rowData.size(); i++){
            Integer[] temp=new Integer[numOfColumns];
            for(int j=0; j<numOfColumns; j++){
            //number of columns is a 2-byte int
                temp[j]=16*rowData.get(0)[2+j*2]+rowData.get(0)[2+j*2+1];
            }
            columnTypes.add(temp);
        }
        return columnTypes;
}
    public static ArrayList<byte[][]> filterRowBytes(ArrayList<byte[]> rowData, ArrayList<Integer> requestedColumns){
        /**@param rowData=byte array of each row from the table
         **@param requestedColumns=array of columns requested
         * 
         */
        ArrayList<byte[][]> filteredRows=new ArrayList<byte[][]>();
        int numOfColumns=getNumberOfColumns(rowData.get(0));//first two bytes of recordPayload
        ArrayList<Integer[]> columnTypes=getColumnTypes(numOfColumns, rowData);
       
        for(int i=0; i<rowData.size(); i++){ //fo
