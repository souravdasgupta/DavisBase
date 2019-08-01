import davisbase.Btree_H;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class testing {
    public static void main(String[] arg){

        ArrayList<Integer> rowID_set = new ArrayList<Integer>(Arrays.asList(1 , 2, 3));
        ArrayList<String> values = new ArrayList<String>(Arrays.asList("1.20", "140.0", "3.50"));
        String tablename = "GreenBase";
        String index_column_name = "testing";

        for(int i = 0 ; i<rowID_set.size() ; i++)
            Btree_H.insert( rowID_set.get(i), values.get(i), tablename, index_column_name);

        Btree_H.retrieve_whole_index_data_set(tablename,index_column_name);

        ArrayList<Integer> result1 = Btree_H.search("3.50", (byte)5,-1, tablename, index_column_name);
        System.out.println("Result for less then target value: " + Arrays.toString(result1.toArray()));

        ArrayList<Integer> result2 = Btree_H.search("3.50",(byte)5, 0, tablename, index_column_name);
        System.out.println("Result for equal to target value: " + Arrays.toString(result2.toArray()));

        ArrayList<Integer> result3 = Btree_H.search("3.50", (byte)5,1, tablename, index_column_name);
        System.out.println("Result for greater then target value: " + Arrays.toString(result3.toArray()));

        Btree_H.retrieve_whole_index_data_set(tablename,index_column_name);

        Btree_H.delete(2, tablename, index_column_name);
        Btree_H.retrieve_whole_index_data_set(tablename,index_column_name);
    }
}
