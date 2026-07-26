package com.f1analytics;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.io.IOException;
import com.f1analytics.dao.StagingDAO;
import com.f1analytics.util.Ac;
import com.f1analytics.util.CSVImporter;


public class RawDataUpdater {

    private static List<String> filenames=new ArrayList<>();;
    RawDataUpdater(){
        
        filenames.add("D:\\DBF\\data_files\\constructors.csv");
        filenames.add("D:\\DBF\\data_files\\drivers.csv");
        filenames.add("D:\\DBF\\data_files\\circuits.csv");
        filenames.add("D:\\DBF\\data_files\\races.csv");
        filenames.add("D:\\DBF\\data_files\\results.csv");
        filenames.add("D:\\DBF\\data_files\\lap_times.csv");
        filenames.add("D:\\DBF\\data_files\\pit_stops.csv");
        filenames.add("D:\\DBF\\data_files\\constructor_results.csv");
        filenames.add("D:\\DBF\\data_files\\driver_standings.csv");
        filenames.add("D:\\DBF\\data_files\\constructor_standings.csv");
        filenames.add("D:\\DBF\\data_files\\seasons.csv");
        filenames.add("D:\\DBF\\data_files\\status.csv");
        filenames.add("data_files/qualifying.csv");
        filenames.add("D:\\DBF\\data_files\\sprint_results.csv");
    }

    public static void main(String args[]){
        StagingDAO stagingDAO=new StagingDAO();
        RawDataUpdater rd=new RawDataUpdater();
        boolean check=true;
        Scanner sc=new Scanner(System.in);
        
        while(check){
        System.out.println("Enter your choice\n1.raw constructor data\n2.raw driver data.\n3.raw circuits\n4.raw races\n5.raw results\n6.raw laptimes\n7.raw pitstops\n8.raw constructor results\n9.raw driver standings\n10.raw constructor standings\n11.seasons\n12.status\n13.raw qualifyings\n14.raw sprint results\n15.exit...");
        int ch=Ac.acceptint(sc,"choice::");
        if(ch>0 && ch<=14){check=false; fileparser(stagingDAO, ch);}
        
        else if(ch==15){check=false;}
        
        else{System.out.println("enter among the choice");}}
        
        System.out.println("Execution completed...");
    }

    public static void fileparser(StagingDAO sgd, int ch){
        try{
            List<String[]> rawData=CSVImporter.readcsv(filenames.get(ch-1));
            sgd.switchinsert(rawData, ch);
        }
        catch(IOException e){
            System.out.println("An error occured, restart the program");    
        }
        catch(Exception e){
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }
}
    