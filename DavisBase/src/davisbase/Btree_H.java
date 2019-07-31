package davisbase;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Btree_H {

    int rowID;

    String value;

    byte[] data_type;

    static String target_file_name;

    public Btree_H( int rowID, String value, byte[] data_type){
        this.rowID = rowID;
        this.value = value;
        this.data_type = data_type.clone();
    }

    public static void insert( int rowID, String value, String tablename, String index_column_name){

        target_file_name = tablename+"_"+index_column_name+".ndx";

        HashMap<String, ArrayList<Integer>> retrieved_data = new HashMap<>();

        try {
            File file = new File(target_file_name);
            if(file.exists()){
                retrieved_data = loadHashMapFromFile();

                if(retrieved_data.containsKey(value)) retrieved_data.get(value).add(rowID);
                else {
                    ArrayList<Integer> temp = new ArrayList<>();
                    temp.add(rowID);
                    retrieved_data.put( value, temp);
                }
                dumpHashMapToFile(retrieved_data);
            }
            else{
                file.createNewFile();
                ArrayList<Integer> temp = new ArrayList<>();
                temp.add(rowID);
                retrieved_data.put(value, temp);
                dumpHashMapToFile(retrieved_data);

            }
        }catch (Exception e) {e.printStackTrace();}

    }

    public static ArrayList<Integer> search( String target, int filter_action,
                                             String tablename, String index_column_name){

        target_file_name = tablename+"_"+index_column_name+".ndx";

        HashMap<String, ArrayList<Integer>> retrieved_data = new HashMap<>();

        ArrayList<Integer> result = new ArrayList<>();

        try {
            File file = new File(target_file_name);
            if(file.exists()){
                retrieved_data = loadHashMapFromFile();

                System.out.println("Retrieved data: " + retrieved_data.values());

                switch (filter_action){
                    case -1:
                        for(String key : retrieved_data.keySet())
                            if(key.compareTo(target) < 0 ) result.addAll(retrieved_data.get(key));
                        break;
                    case 0:
                        for(String key : retrieved_data.keySet())
                            if(key.compareTo(target) == 0 ) result.addAll(retrieved_data.get(key));
                        break;
                    case 1:
                        for(String key : retrieved_data.keySet())
                            if(key.compareTo(target) > 0 ) result.addAll(retrieved_data.get(key));
                        break;
                    default:
                        System.out.println("Wrong index file command!");
                        break;
                }

            }
            else{
                System.out.println("Please create index file for " + index_column_name + " first!");

            }
        }catch (Exception e) {e.printStackTrace();}

        System.out.println("Result: " + result);
        return result;
    }

    public static void delete( int rowID, String tablename, String index_column_name){

        target_file_name = tablename+"_" +index_column_name+".ndx";

        HashMap<String, ArrayList<Integer>> retrieved_data = new HashMap<>();

        try {
            File file = new File(target_file_name);
            if(file.exists()){
                retrieved_data = loadHashMapFromFile();

                for(ArrayList<Integer> rowID_set : retrieved_data.values())
                    rowID_set.removeAll(Collections.singleton(rowID));

                dumpHashMapToFile(retrieved_data);
            }
            else{
                System.out.println("index file for " + index_column_name + " does not exist!");

            }
        }catch (Exception e) {e.printStackTrace();}

    }

    public static void retrieve_whole_index_data_set(String tablename, String index_column_name){

        String target = tablename+"_"+index_column_name+".ndx";
        HashMap<String, ArrayList<Integer>> data_set = new HashMap<>();
        try {
            data_set = loadHashMapFromFile();
        }catch (Exception e) {e.printStackTrace();}
        System.out.println("Data set for " + target);
        for(String key : data_set.keySet())
        System.out.println( key + " : " + data_set.get(key));
    }


    public static void dumpHashMapToFile(HashMap<String, ArrayList<Integer>> tableInfo)
            throws IOException {

        if (tableInfo == null || tableInfo.isEmpty()) {
            return;
        }

        File file = new File(target_file_name);
        FileOutputStream f = new FileOutputStream(file);
        try (ObjectOutputStream s = new ObjectOutputStream(f)) {
            s.writeObject(tableInfo);
        }
    }

    public static HashMap<String, ArrayList<Integer>> loadHashMapFromFile()
            throws IOException, ClassNotFoundException {
        HashMap<String, ArrayList<Integer>> ret;

        File file = new File(target_file_name);
        FileInputStream f = new FileInputStream(file);
        try (ObjectInputStream s = new ObjectInputStream(f)) {
            ret = (HashMap<String, ArrayList<Integer>>) s.readObject();
        }
        return ret;
    }
}
