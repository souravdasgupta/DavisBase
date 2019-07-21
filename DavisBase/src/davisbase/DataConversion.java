import sun.plugin2.message.Conversation;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;

public class DataConversion {

    // input data
    private byte[] data_code;
    private String[] data_value;

    // output results
    private ArrayList<Byte> output_data_types;
    private ArrayList<Byte> output_data_values;
    private ArrayList<Byte> output;
    private byte column_number;

    private ArrayList<Byte> converted_data;
    private ArrayList<String> reverse_bk_data_value_in_string;

    public enum options{
        convert_to_assigned_data_format,
        convert_back_to_string
    }

    private options action;


    public DataConversion( byte[] in_data_type, String[] in_data_value){

        this.data_code = in_data_type.clone();
        this.data_value = Arrays.copyOf(in_data_value, in_data_value.length);
        this.output_data_types = new ArrayList<>();
        this.output_data_values = new ArrayList<>();
        this.output = new ArrayList<>();
        this.column_number = 0;
        this.reverse_bk_data_value_in_string = new ArrayList<>();
        this.action = options.convert_to_assigned_data_format;
    }

    public DataConversion( ArrayList<Byte> converted_data_type, ArrayList<Byte> converted_data_value){

        this.data_code = null;
        this.data_value = null;
        this.output_data_types = new ArrayList<>(converted_data_type);
        this.output_data_values = new ArrayList<>(converted_data_value);
        this.output = new ArrayList<>();
        this.column_number = 0;
        this.converted_data = new ArrayList<>();
        this.reverse_bk_data_value_in_string = new ArrayList<>();
        this.action = options.convert_back_to_string;
    }

    public DataConversion( DataConversion in_DC){

        this.data_code = in_DC.getData_code().clone();
        this.data_value = Arrays.copyOf(in_DC.getData_value(), in_DC.getData_value().length);
        this.output_data_types = new ArrayList<>(in_DC.getOutput_data_types());
        this.output_data_values = new ArrayList<>(in_DC.output_data_values);
        this.output = new ArrayList<>(in_DC.getOutput());
        this.column_number = in_DC.getColumn_number();
        this.reverse_bk_data_value_in_string = new ArrayList<>(in_DC.getReverse_bk_data_value_in_string());
    }


    // get functions

    public byte[] getData_code() { return data_code; }
    public String[] getData_value() { return data_value;}
    public ArrayList<Byte> getOutput_data_types() { return output_data_types; }
    public ArrayList<Byte> getOutput_data_values() {return output_data_values;}
    public ArrayList<Byte> getOutput() {return output;}
    public byte getColumn_number() {return column_number;}
    public ArrayList<Byte> getConverted_data() {return converted_data;}
    public ArrayList<String> getReverse_bk_data_value_in_string() {return reverse_bk_data_value_in_string;}


// set functions

    public void setData_code(byte[] dv) {this.data_code = dv.clone();}
    public void setData_value(String[] dv) {this.data_value = Arrays.copyOf(dv, dv.length);}
    public void setOutput_data_types_add_one(byte dv) {this.output_data_types.add(dv);}
    public void setOutput_data_values_add_one(byte[] dv) {
        for(byte pt: dv )this.output_data_values.add(pt);
    }
    public void setOutput_merge(){
        this.column_number = (byte) this.getOutput_data_types().size();
        this.output.add(column_number);
        for(byte dt : this.getOutput_data_types()) this.output.add(dt);
        for(byte dv : this.getOutput_data_values()) this.output.add(dv);
    }

    public void setReverse_bk_data_value_in_string(String dv) {this.reverse_bk_data_value_in_string.add(dv);}

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static ArrayList<Byte> convert_to_storage_format_executor(byte[] target_data_type, String[] target_data_value){

        DataConversion object = new DataConversion(target_data_type, target_data_value);

        object.convert_data_set(object);

/*
        System.out.println("Data Types:");
        Show_in_Hex(object.getOutput_data_types());
        System.out.println("data value:");
        Show_in_Hex(object.getOutput_data_values());
        System.out.println("output:");
        Show_in_Hex(object.getOutput());

        object.reverse_data_values_to_string(object);
        System.out.println("Reversed values: ");
        System.out.println(object.getReverse_bk_data_value_in_string());
*/
        return object.getOutput();

    }


    public void convert_data_set(DataConversion processing_data){
        int j = 0;
        for(int i=0 ; i < processing_data.getData_code().length; i++){
            if(processing_data.getData_code()[i] == 0x00) {
                processing_data.setOutput_data_types_add_one(processing_data.getData_code()[i]);
                j = j== 0 ? 0 : i-1;
            }
            else {
                convert_to_assigned_length_in_byte(processing_data.getData_code()[i], processing_data.getData_value()[j]);
                j++;
            }
        }

        processing_data.setOutput_merge();

    }


    public void convert_to_assigned_length_in_byte(byte in_data_type, String in_data_value){

        byte[] temp_result;
        setOutput_data_types_add_one(in_data_type);

        if(in_data_type < 0x0c) {
            switch (in_data_type) {
                case 0x01:
                    temp_result = ByteBuffer.allocate(1).put(Byte.parseByte(in_data_value)).array();
                    setOutput_data_values_add_one(temp_result);
                    break;
                case 0x02:
                    if (Short.parseShort(in_data_value) > 32767 || Short.parseShort(in_data_value) < -32768)
                        System.out.println("Out of range for 'SMALLINT'");
                    else {
                        temp_result = ByteBuffer.allocate(2).putShort(Short.parseShort(in_data_value)).array();
                        setOutput_data_values_add_one(temp_result);
                    }
                    break;
                case 0x03:
                    if (Integer.parseInt(in_data_value) > 2147483647 || Integer.parseInt(in_data_value) < -2147483648)
                        System.out.println("Out of range for 'INT'");
                    else {
                        temp_result = ByteBuffer.allocate(4).putInt(Integer.parseInt(in_data_value)).array();
                        setOutput_data_values_add_one(temp_result);
                    }
                    break;

                case 0x04:
                    if (Long.parseLong(in_data_value) > Long.parseLong("9223372036854775807") ||
                            Long.parseLong(in_data_value) < Long.parseLong("-9223372036854775808"))
                        System.out.println("Out of range for 'LONG'");
                    else {
                        temp_result = ByteBuffer.allocate(8).putLong(Long.parseLong(in_data_value)).array();
                        setOutput_data_values_add_one(temp_result);
                    }
                    break;

                case 0x05:
                    if (Float.parseFloat(in_data_value) > 3.40282347E+38F ||
                            Float.parseFloat(in_data_value) < -3.40282347E+38F)
                        System.out.println("Out of range for 'FLOAT'");
                    else {
                        temp_result = ByteBuffer.allocate(4).putFloat(Float.parseFloat(in_data_value)).array();
                        setOutput_data_values_add_one(temp_result);
                    }
                    break;

                case 0x06:
                    if (Double.parseDouble(in_data_value) > 1.79769313486231570E+308 ||
                            Double.parseDouble(in_data_value) < -1.79769313486231570E+308)
                        System.out.println("Out of range for 'Double'");
                    else {
                        temp_result = ByteBuffer.allocate(8).putDouble(Double.parseDouble(in_data_value)).array();
                        setOutput_data_values_add_one(temp_result);
                    }
                    break;

                case 0x08:
                    String year_temp = String.valueOf(Integer.parseInt(in_data_value) - 2000);
                    temp_result = ByteBuffer.allocate(1).put(Byte.parseByte(year_temp)).array();
                    setOutput_data_values_add_one(temp_result);
                    break;

                case 0x09:
                    if (Integer.parseInt(in_data_value) > 86400000 || Integer.parseInt(in_data_value) < 0)
                        System.out.println("Out of range for 'TIME'");
                    else {
                        temp_result = ByteBuffer.allocate(4).putInt(Integer.parseInt(in_data_value)).array();
                        setOutput_data_values_add_one(temp_result);
                    }
                    break;

                case 0x0A:
                    if (Long.parseLong(in_data_value) > Long.parseLong("9223372036854775807") ||
                            Long.parseLong(in_data_value) < 0)
                        System.out.println("Out of range for 'DATETIME'");
                    else {
                        temp_result = ByteBuffer.allocate(8).putLong(Long.parseLong(in_data_value)).array();
                        setOutput_data_values_add_one(temp_result);
                    }
                    break;

                case 0x0B:
                    if (Long.parseLong(in_data_value) > Long.parseLong("9223372036854775807") ||
                            Long.parseLong(in_data_value) < 0)
                        System.out.println("Out of range for 'DATE'");
                    else {
                        temp_result = ByteBuffer.allocate(8).putLong(Long.parseLong(in_data_value)).array();
                        setOutput_data_values_add_one(temp_result);
                    }
                    break;
            }
        } else if (in_data_type < (0x0c+0x80)){
            byte[] string_temp = in_data_value.getBytes(StandardCharsets.US_ASCII).clone();
            setOutput_data_values_add_one(string_temp);
        }

        else System.out.println("Wrong Data Type!!");

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static ArrayList<String> convert_back_to_string_executor(ArrayList<Byte> target_data){

        int target_column_number;
        ArrayList<Byte> target_data_type = new ArrayList<>();
        ArrayList<Byte> target_data_value;

        ArrayList<Byte> target_data_temp = new ArrayList<>(target_data);
        target_column_number = target_data_temp.get(0);
        target_data_temp.remove(0);

        for(int i=0; i<target_column_number; i++) {
            target_data_type.add(target_data_temp.get(0));
            target_data_temp.remove(0);

        }
        target_data_value = new ArrayList<>(target_data_temp);
        System.out.println(target_data_value.size());
        DataConversion object_bk = new DataConversion(target_data_type, target_data_value);
        object_bk.reverse_data_values_to_string(object_bk);
        return object_bk.getReverse_bk_data_value_in_string();
    }

    public void reverse_data_values_to_string(DataConversion target){
        ArrayList<Byte> data_temp = new ArrayList<>(target.getOutput_data_values());
        byte[] reverse_buffer;

        for(byte dt: target.getOutput_data_types()) {
            if (dt != 0x00) {
                if (dt < 0x0c) {
                    switch (dt) {
                        case 0x01:
                            reverse_buffer = new byte[1];
                            for (int j = 0; j < 1; j++) {
                                reverse_buffer[j] = data_temp.get(0);
                                data_temp.remove(0);
                            }

                            target.setReverse_bk_data_value_in_string(String.valueOf(ByteBuffer.wrap(reverse_buffer).get()));
                            break;
                        case 0x02:
                            reverse_buffer = new byte[2];
                            for (int j = 0; j < 2; j++) {
                                reverse_buffer[j] = data_temp.get(0);
                                data_temp.remove(0);
                            }
                            target.setReverse_bk_data_value_in_string(String.valueOf(ByteBuffer.wrap(reverse_buffer).getShort()));
                            break;
                        case 0x03:
                            reverse_buffer = new byte[4];
                            for (int j = 0; j < 4; j++) {
                                reverse_buffer[j] = data_temp.get(0);
                                data_temp.remove(0);
                            }
                            target.setReverse_bk_data_value_in_string(String.valueOf(ByteBuffer.wrap(reverse_buffer).getInt()));
                            break;

                        case 0x04:
                            reverse_buffer = new byte[8];
                            for (int j = 0; j < 8; j++) {
                                reverse_buffer[j] = data_temp.get(0);
                                data_temp.remove(0);
                            }
                            target.setReverse_bk_data_value_in_string(String.valueOf(ByteBuffer.wrap(reverse_buffer).getLong()));
                            break;

                        case 0x05:
                            reverse_buffer = new byte[4];
                            for (int j = 0; j < 4; j++) {
                                reverse_buffer[j] = data_temp.get(0);
                                data_temp.remove(0);
                            }
                            target.setReverse_bk_data_value_in_string(String.valueOf(ByteBuffer.wrap(reverse_buffer).getFloat()));
                            break;

                        case 0x06:
                            reverse_buffer = new byte[4];
                            for (int j = 0; j < 4; j++) {
                                reverse_buffer[j] = data_temp.get(0);
                                data_temp.remove(0);
                            }
                            target.setReverse_bk_data_value_in_string(String.valueOf(ByteBuffer.wrap(reverse_buffer).getDouble()));
                            break;

                        case 0x08:
                            reverse_buffer = new byte[1];
                            for (int j = 0; j < 1; j++) {
                                reverse_buffer[j] = data_temp.get(0);
                                data_temp.remove(0);
                            }
                            target.setReverse_bk_data_value_in_string(
                                    String.valueOf(
                                            Integer.parseInt(
                                                    String.valueOf(ByteBuffer.wrap(reverse_buffer).get()) + 2000)));
                            break;

                        case 0x09:
                            reverse_buffer = new byte[4];
                            for (int j = 0; j < 4; j++) {
                                reverse_buffer[j] = data_temp.get(0);
                                data_temp.remove(0);
                            }
                            target.setReverse_bk_data_value_in_string(String.valueOf(ByteBuffer.wrap(reverse_buffer).getInt()));

                            break;

                        case 0x0A:
                            reverse_buffer = new byte[8];
                            for (int j = 0; j < 8; j++) {
                                reverse_buffer[j] = data_temp.get(0);
                                data_temp.remove(0);
                            }

                            long datetime_temp = ByteBuffer.wrap(reverse_buffer).getLong();
                            SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                            Date date = new Date(datetime_temp);
                            target.setReverse_bk_data_value_in_string(jdf.format(date));
                            break;

                        case 0x0B:
                            reverse_buffer = new byte[8];
                            for (int j = 0; j < 8; j++) {
                                reverse_buffer[j] = data_temp.get(0);
                                data_temp.remove(0);
                            }

                            long time_temp = ByteBuffer.wrap(reverse_buffer).getLong();
                            SimpleDateFormat jdf_time = new SimpleDateFormat("HH:mm:ss");
                            Date time = new Date(time_temp);
                            target.setReverse_bk_data_value_in_string(jdf_time.format(time));

                            break;
                    }
                } else if (dt < (0x0c + 0x80)) {
                    int word_length = dt - 0x0c;
                    reverse_buffer = new byte[word_length];

                    for (int j = 0; j < word_length; j++) {
                        reverse_buffer[j] = data_temp.get(0);
                        data_temp.remove(0);
                    }

                    target.setReverse_bk_data_value_in_string(new String(reverse_buffer));
                } else System.out.println("Data type error in storage!!");
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void Show_in_Hex(ArrayList<Byte> byte_array){

        Vector<String> tb = new Vector<>();
        for(byte pt: byte_array)  tb.add(String.format("%02x", pt));
        System.out.println("Testing result: " + Arrays.toString(tb.toArray()) + " Length: " + byte_array.size());
    }



}
