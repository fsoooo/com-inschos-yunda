package com.inschos.yunda.assist.kit;

/**
 * Created by IceAnt on 2018/5/24.
 */
public class SerialNumber {

    private String prefix = "1";

    private int sequenceLen = 11;

    private int sequence;

    private int workerId;

    private int dataCenterId;

    private final int DATA_CENTER_LEN =2;

    private final int WORKER_LEN =2;

    private final char DEFAULT_PADDING = '0';

    public SerialNumber(String prefix){
        this.prefix = prefix;
    }

    public synchronized String nextId(){
        StringBuilder builder = new StringBuilder();
        if(prefix!=null){
            builder.append(this.prefix);
        }
        builder.append(complement(dataCenterId,DATA_CENTER_LEN));
        builder.append(complement(workerId,WORKER_LEN));
        builder.append(complement(sequence,sequenceLen));
        return builder.toString();
    }

    private String complement(int old,int len){
        return complement(String.valueOf(old),len,DEFAULT_PADDING);
    }
    private String complement(String old,int len,char padding){
        if(old==null){
            old="";
        }
        StringBuilder  builder = new StringBuilder();
        if(old.length()<len){
            for (int i = 0 ,l=len-old.length(); i <l ; i++) {
                builder.append(padding);
            }
        }
        builder.append(old);
        return builder.toString();
    }


}
