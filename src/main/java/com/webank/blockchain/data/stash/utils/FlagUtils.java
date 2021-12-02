package com.webank.blockchain.data.stash.utils;

import java.util.ArrayList;

/**
 * @author aaronchu
 * @Description
 * @data 2021/01/07
 */
public class FlagUtils {

    private FlagUtils(){}

    public static String[] onlySettedFields(String[] fields, byte[] usedFlags){
        ArrayList<String> settedFields = new ArrayList<>();
        //Ignoring the last three fields( always status, num ,id)
        //key,value,origin,block_limit,_status_,_num_,_id_
        //1    1      1         1        0000
        int len = fields.length  -3;
        for(int i=0;i<len;i++){
            byte b = usedFlags[i >> 3];
            int bitVal = (b >> (7-i)) & 1;
            if(bitVal == 1){
                settedFields.add(fields[i]);
            }
        }
        return settedFields.toArray(new String[settedFields.size()]);


    }

    public static String toBinaryString(byte[] input)
    {
        String result = "";
        int i;
        for (i = 0; i < input.length; i++)
        {
            int e = input[i];
            for (int ii = 7; ii >=0; ii--)
            {
                int b = (e >>> ii) & 1;
                result += b;
            }
            if (i != input.length - 1)
            {
                result += " ";
            }
        }
        return result;
    }

}
