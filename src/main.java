import javafx.scene.effect.Light;

import java.util.ArrayList;
import java.util.Arrays;

public class main {
    public static void main(String[] args){

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Input: byte[] and String[]
        // Output: ArrayList<Byte>
        //byte[] data_type = new byte[] {0x02, 0x11, 0x05, 0x01};
        //String[] data_value = new String[] {"933","Rover", "20.6", "4"};

        ArrayList<Integer> data_type = new ArrayList<>(Arrays.asList(0x02, 0x11, 0x05, 0x01));
        ArrayList<String> data_value = new ArrayList<>(Arrays.asList("933","Rover", "20.6", "4"));

        // Example for converting to the format
        ArrayList<Byte> result = new ArrayList<>(DataConversion.convert_to_storage_format_executor(data_type,data_value));

        // test for showing in hex format
        System.out.println("output_assigned_format:");
        DataConversion.Show_in_Hex(result);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Inout: Data, ArrayList<Byte> as cell payload part in DavisBase 2019 document
        // Output: ArrayList<String>
        ArrayList<String> result_bk = new ArrayList<>(DataConversion.convert_back_to_string_executor(result));

        // test for showing in hex format
        System.out.println("output_reversed:");
        System.out.println(Arrays.toString(result_bk.toArray()));
    }

}
