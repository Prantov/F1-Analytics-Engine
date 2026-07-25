package com.f1analytics;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.io.IOException;
import com.f1analytics.dao.StagingDAO;
import com.f1analytics.util.CSVImporter;


public class RawDataUpdater {
    public static void main(String args[]){
        BufferedReader consoleread=new BufferedReader(new InputStreamReader(System.in));
        StagingDAO stagingDAO=new StagingDAO();
        
        
        System.out.println("Enter your choice\n1.raw constructor data\n2.raw driver data.\n3.raw circuits\n4.raw races\n5.raw results\n6.raw laptimes");
        try{
            String input=consoleread.readLine();
            int ch=Integer.parseInt(input);

            switch(ch){
                case 1:loadRawConstructors(stagingDAO);
                    break;
                case 2:loadRawDrivers(stagingDAO);
                        break;
                case 3:loadRawCircuits(stagingDAO);
                    break;
                case 4:loadRawRaces(stagingDAO);
                    break;
                case 5:loadRawResults(stagingDAO);
                    break; 
                case 6:loadRawLapTimes(stagingDAO);
                    break;
                default:System.out.println("Invalid choice");
            }
        }
        catch(NumberFormatException e){
            System.out.println("enter integer");
        }
        catch(IOException e){
            System.out.println("An error occured, restart the program");
            
        }
        System.out.println("Execution completed...");
    }

    
    public static void loadRawConstructors(StagingDAO sgd){
        try{
            String filename="D:\\DBF\\data_files\\constructors.csv";
            List<String[]> rawdata=CSVImporter.readcsv(filename);
            sgd.insertRawConstructor(rawdata);
        }
        catch(Exception e){
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }

    public static void loadRawDrivers(StagingDAO sgd){
        try{
            String filename="D:\\DBF\\data_files\\drivers.csv";
            List<String[]> rawData=CSVImporter.readcsv(filename);
            sgd.insertRawDriver(rawData);
        }
        catch(Exception e){
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }
    
    public static void loadRawCircuits(StagingDAO sgd){
        try{
            String filename="D:\\DBF\\data_files\\circuits.csv";
            List<String[]> rawData=CSVImporter.readcsv(filename);
            sgd.insertRawCircuits(rawData);
        }
        catch(Exception e){
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }
    public static void loadRawRaces(StagingDAO sgd){
        try{
            String filename="D:\\DBF\\data_files\\races.csv";
            List<String[]> rawData=CSVImporter.readcsv(filename);
            sgd.insertRawRaces(rawData);
        }
        catch(Exception e){
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }
    public static void loadRawResults(StagingDAO sgd){
        try{
            String filename="D:\\DBF\\data_files\\results.csv";
            List<String[]> rawData=CSVImporter.readcsv(filename);
            sgd.insertRawResults(rawData);
        }
        catch(Exception e){
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }
    public static void loadRawLapTimes(StagingDAO sgd){
        try{
            String filename="D:\\DBF\\data_files\\lap_times.csv";
            List<String[]> rawData=CSVImporter.readcsv(filename);
            sgd.insertRawLapTimes(rawData);
        }
        catch(Exception e){
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }
}
