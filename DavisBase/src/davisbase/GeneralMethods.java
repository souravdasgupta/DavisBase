/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package davisbase;

import java.util.*;

/**
 *
 * @author Vadim
 */
public class GeneralMethods {
    public static ArrayList<Record> bytesToRecord (ArrayList<byte[]> input){
        ArrayList<Record> result = new ArrayList<>();
        for(byte[] b : input){
            result.add(new Record(b));
        }
        return result;
    }
}
