package com.f1analytics;

import java.util.*;
import com.f1analytics.util.CSVImporter;

public class Checktable{
    private static final String filepath="D:\\DBF\\tableoverview.csv";
    private void showtables(List<String[]> rawData){
        List<String> tablenames=new ArrayList<>();
        Iterator<String[]> iter=rawData.iterator();
        while(iter.hasNext()){
            String k[]=iter.next();
            if(!tablenames.contains(k[0])){
                tablenames.add(k[0]);
            }
        }
        for(String ele:tablenames){
            System.out.println(ele);
        }
    }
    public static void main(String args[]){
        Checktable ct=new Checktable();
        try{
            List<String[]> rawData=CSVImporter.readcsv(filepath);
            ct.showtables(rawData);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}