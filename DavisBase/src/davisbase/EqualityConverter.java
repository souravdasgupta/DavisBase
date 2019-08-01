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
public class EqualityConverter {
    static int Greater = 1;
    static int Less = -1;
    static int Equal = 0;
    
    
    public static ArrayList<Integer> GetOperations(String operation, boolean isNot){
        ArrayList<Integer> result = new ArrayList<>();
        
        switch(operation) {
            case "=": {
                if(isNot){
                    result.add(Less);
                    result.add(Greater);
                }else{
                    result.add(Equal);
                }
                break;
            }
            case ">": {
                if(isNot){
                    result.add(Equal);
                    result.add(Less);
                }else{
                    result.add(Greater);
                }
                break;
            }
            case ">=": {
                if(isNot){
                    result.add(Less);
                }else{
                    result.add(Equal);
                    result.add(Greater);
                }
                break;
            }
            case "<": {
                if(isNot){
                    result.add(Equal);
                    result.add(Greater);
                }else{
                    result.add(Less);
                }
                break;
            }
            case "<=": {
                if(isNot){
                    result.add(Greater);
                }else{
                    result.add(Equal);
                    result.add(Less);
                }
                break;
            }
            case "!=": 
            {
                if(isNot){
                    result.add(Equal);
                }else{
                    result.add(Less);
                    result.add(Greater);
                }
                break;
            }
            case "<>":
            {
                if(isNot){
                    result.add(Equal);
                }else{
                    result.add(Less);
                    result.add(Greater);
                }
                break;
            }
        }
        return result;
    }
}
