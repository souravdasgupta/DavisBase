package indexBtree;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

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

    public static ArrayList<Integer> search( String target, byte data_type, int filter_action,
                                             String tablename, String index_column_name){

        target_file_name = tablename+"_"+index_column_name+".ndx";

        HashMap<String, ArrayList<Integer>> retrieved_data = new HashMap<>();

        ArrayList<Integer> result = new ArrayList<>();

        try {
            File file = new File(target_file_name);
            if(file.exists()){
                retrieved_data = loadHashMapFromFile();

                System.out.println("Retrieved data: " + retrieved_data.values());

                for(String key : retrieved_data.keySet()) {
                    /*
                    int temp_result = compareByteArrays(convert_to_assigned_length_in_byte(data_type, key),
                                                            convert_to_assigned_length_in_byte(data_type, target),
                                                            data_type);
                    */

                    switch (filter_action) {
                        case -1:
                            if (key.compareTo(target) < 0) result.addAll(retrieved_data.get(key));
                            //if (temp_result == -1) result.addAll(retrieved_data.get(key));
                            break;
                        case 0:
                            //for (String key : retrieved_data.keySet())
                                if (key.compareTo(target) == 0) result.addAll(retrieved_data.get(key));
                            //if (temp_result == 0) result.addAll(retrieved_data.get(key));
                            break;
                        case 1:
                            //for (String key : retrieved_data.keySet())
                                if (key.compareTo(target) > 0) result.addAll(retrieved_data.get(key));
                            //if (temp_result == 1) result.addAll(retrieved_data.get(key));
                            break;
                        default:
                            System.out.println("Wrong index file command!");
                            break;
                    }
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

                HashMap<String, ArrayList<Integer>> data_set_temp = new HashMap<>(retrieved_data);
                for(Map.Entry<String, ArrayList<Integer>> rowID_set : data_set_temp.entrySet()) {
                    rowID_set.getValue().removeAll(Collections.singleton(rowID));
                    if(rowID_set.getValue().isEmpty()) retrieved_data.remove(rowID_set.getKey());
                }
                dumpHashMapToFile(retrieved_data);
            }
            else{
                System.out.println("index file for " + index_column_name + " does not exist!");

            }
        }catch (Exception e) {e.printStackTrace();}

    }

    public static void deleteAllindex(String tablename){
        String cwd = System.getProperty("user.dir");
        File folder = new File(cwd);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> candidate = new ArrayList<>();
        ArrayList<String> target = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                //System.out.println("File " + listOfFiles[i].getName());
                candidate.add(listOfFiles[i].getName());
            }
        }

        for(String c_value: candidate){
            if(Pattern.compile(tablename+"_.*").matcher(c_value).matches() &&
                    Pattern.compile(".*ndx").matcher(c_value).matches()) {
                target.add(c_value);
            }
        }

        for(String tg_value : target){
            File file = new File(tg_value);
            if(file.exists()) file.delete();
        }
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

    public static byte[] convert_to_assigned_length_in_byte(byte in_data_type, String in_data_value){

        ArrayList<byte[]> temp_result = new ArrayList<>();


        if(in_data_type < 0x0c) {
            switch (in_data_type) {
                case 0x01:
                    temp_result.add(ByteBuffer.allocate(1).put(Byte.parseByte(in_data_value)).array());
                    break;
                case 0x02:
                    if (Short.parseShort(in_data_value) > 32767 || Short.parseShort(in_data_value) < -32768)
                        System.out.println("Out of range for 'SMALLINT'");
                    else {
                        temp_result.add(ByteBuffer.allocate(2).putShort(Short.parseShort(in_data_value)).array());
                    }
                    break;
                case 0x03:
                    if (Integer.parseInt(in_data_value) > 2147483647 || Integer.parseInt(in_data_value) < -2147483648)
                        System.out.println("Out of range for 'INT'");
                    else {
                        temp_result.add(ByteBuffer.allocate(4).putInt(Integer.parseInt(in_data_value)).array());
                    }
                    break;

                case 0x04:
                    if (Long.parseLong(in_data_value) > Long.parseLong("9223372036854775807") ||
                            Long.parseLong(in_data_value) < Long.parseLong("-9223372036854775808"))
                        System.out.println("Out of range for 'LONG'");
                    else {
                        temp_result.add(ByteBuffer.allocate(8).putLong(Long.parseLong(in_data_value)).array());
                    }
                    break;

                case 0x05:
                    if (Float.parseFloat(in_data_value) > 3.40282347E+38F ||
                            Float.parseFloat(in_data_value) < -3.40282347E+38F)
                        System.out.println("Out of range for 'FLOAT'");
                    else {
                        temp_result.add(ByteBuffer.allocate(4).putFloat(Float.parseFloat(in_data_value)).array());
                    }
                    break;

                case 0x06:
                    if (Double.parseDouble(in_data_value) > 1.79769313486231570E+308 ||
                            Double.parseDouble(in_data_value) < -1.79769313486231570E+308)
                        System.out.println("Out of range for 'Double'");
                    else {
                        temp_result.add(ByteBuffer.allocate(8).putDouble(Double.parseDouble(in_data_value)).array());
                    }
                    break;

                case 0x08:
                    String year_temp = String.valueOf(Integer.parseInt(in_data_value) - 2000);
                    temp_result.add(ByteBuffer.allocate(1).put(Byte.parseByte(year_temp)).array());
                    break;

                case 0x09:
                    if (Integer.parseInt(in_data_value) > 86400000 || Integer.parseInt(in_data_value) < 0)
                        System.out.println("Out of range for 'TIME'");
                    else {
                        temp_result.add(ByteBuffer.allocate(4).putInt(Integer.parseInt(in_data_value)).array());
                    }
                    break;

                case 0x0A:
                    if (Long.parseLong(in_data_value) > Long.parseLong("9223372036854775807") ||
                            Long.parseLong(in_data_value) < 0)
                        System.out.println("Out of range for 'DATETIME'");
                    else {
                        temp_result.add(ByteBuffer.allocate(8).putLong(Long.parseLong(in_data_value)).array());
                    }
                    break;

                case 0x0B:
                    if (Long.parseLong(in_data_value) > Long.parseLong("9223372036854775807") ||
                            Long.parseLong(in_data_value) < 0)
                        System.out.println("Out of range for 'DATE'");
                    else {
                        temp_result.add(ByteBuffer.allocate(8).putLong(Long.parseLong(in_data_value)).array());
                    }
                    break;
            }
        } else if (in_data_type < (0x0c+0x80)){
            temp_result.add(in_data_value.getBytes(StandardCharsets.US_ASCII).clone());
        }

        else System.out.println("Wrong Data Type!!");

        return temp_result.get(0);

    }

    public static int compareByteArrays(byte[] a, byte[] b, byte type) {
        int numBytes = getSizeOfIndexVal(type);

        for(int i = 0; i < numBytes; i++) {
            if(a[i] > b[i])
                return 1;
            if(a[i] < b[i])
                return -1;
        }
        return 0;
    }

    public static int getSizeOfIndexVal(byte indexDataType){
        switch(indexDataType) {
            case 0x01:
            case 0x08:
                return 1;
            case 0x02:
                return 2;
            case 0x03:
            case 0x05:
            case 0x09:
                return 4;
            case 0x04:
            case 0x06:
            case 0x0A:
            case 0x0B:
                return 8;
        }
        return indexDataType - 0x0C;
    }

}
