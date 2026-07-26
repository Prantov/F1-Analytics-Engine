package com.f1analytics.dao;
import java.sql.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import com.f1analytics.util.DBConnection;
import com.f1analytics.util.AppLog;


public class StagingDAO {

    //parse the string data into int datatype
    private int parseNum(String data){
        try{
            return Integer.parseInt(data.trim());
        }
        catch(Exception e){
                return 0;
        }
    }
    //parse the string data into double datatype
    private double parseNumD(String data){
        try{
            return Double.parseDouble(data.trim());
        }
        catch(Exception e){
                return 0.0;
        }
    }

    // parse date into flexible yyyy-mm-dd format
    private java.sql.Date parseFlexDate(String date){
        if(date==null || date.trim().equals("\\N") || date.trim().isEmpty()){
            return null;
        }
        try{
            return java.sql.Date.valueOf(date.trim());
        }
        catch(Exception e){
            try{
                LocalDate britishform=LocalDate.parse(date.trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                return java.sql.Date.valueOf(britishform);
            }
            catch(Exception e2){
                System.out.println("couldnt parse date...");
                return null;
            }
        }
    }
    public void switchinsert(List<String[]> rawData,int ch){
        Connection conn=null;
        try{
            conn=DBConnection.connectit();
            conn.setAutoCommit(false);
            switch(ch){
                case 1:insertRawConstructor(rawData,conn);
                    break;
                case 2:insertRawDriver(rawData,conn);
                    break;
                case 3:insertRawCircuits(rawData,conn);
                    break;
                case 4:insertRawRaces(rawData,conn);
                    break;
                case 5:insertRawResults(rawData,conn);
                    break;
                case 6:insertRawLapTimes(rawData,conn);
                    break;
                case 7:insertRawPitstops(rawData,conn);
                    break;
                case 8:insertRawConstructorResults(rawData,conn);
                    break;
                case 9:insertRawDriverStandings(rawData,conn);
                    break;
                case 10:insertRawConstructorStandings(rawData,conn);
                    break;
                case 11:insertSeasons(rawData,conn);
                    break;
                case 12:insertStatus(rawData,conn);
                    break;
                case 13:insertRawQualifying(rawData,conn);
                    break;
                case 14:insertRawSprintResults(rawData,conn);
                    break;
            }   
        }
        catch(SQLException e){
            System.out.println("An error occured...");
            try{
                if(conn!=null){
                    conn.rollback();
                    AppLog.log("rollback succesfull");
                }
            }
            catch(SQLException se){
                    AppLog.log("Rollback failed: "+se.getMessage());
                    se.printStackTrace();
            }
            
            e.printStackTrace();
        }
        finally{
            try{
                if(conn!=null){
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
    }

    private void insertRawSprintResults(List<String[]> rawData,Connection conn) throws SQLException {
        String insert="INSERT INTO raw_sprint_results(result_id,race_id,driver_id,constructor_id,number,grid,position,position_order,points,laps,fastest_lap) VALUES(?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT DO NOTHING";
        int count=0;
        conn.setAutoCommit(false);
            try(PreparedStatement pt=conn.prepareStatement(insert)){
                for(String[] data:rawData){
                    pt.setInt(1,parseNum(data[0]));
                    pt.setInt(2,parseNum(data[1]));
                    pt.setInt(3,parseNum(data[2]));
                    pt.setInt(4,parseNum(data[3]));
                    pt.setInt(5,parseNum(data[4]));
                    pt.setInt(6,parseNum(data[5]));
                    pt.setInt(7,parseNum(data[6]));
                    pt.setInt(8,parseNum(data[8]));
                    pt.setDouble(9,parseNumD(data[9]));
                    pt.setInt(10,parseNum(data[10]));
                    pt.setInt(11,parseNum(data[13]));
                    
                    count++;
                    pt.addBatch();
                    pt.clearParameters();
                }
                pt.executeBatch();
                System.out.println(String.format("Succesfully added %d rows",count));
                AppLog.log(String.format("Succesfully added %d rows into raw_sprint_results table",count));

            }
        }

    //load data into raw_qualifying table
    private void insertRawQualifying(List<String[]> rawData,Connection conn) throws SQLException{
        String insert="INSERT INTO raw_qualifying(qualify_id,race_id,driver_id,constructor_id,number,position) VALUES(?,?,?,?,?,?) ON CONFLICT DO NOTHING";
        int count=0;
        
        conn.setAutoCommit(false);
            try(PreparedStatement pt=conn.prepareStatement(insert)){
                for(String[] data:rawData){
                    pt.setInt(1,parseNum(data[0]));
                    pt.setInt(2,parseNum(data[1]));
                    pt.setInt(3,parseNum(data[2]));
                    pt.setInt(4,parseNum(data[3]));
                    pt.setInt(5,parseNum(data[4]));
                    pt.setInt(6,parseNum(data[5]));
                    
                    count++;
                    pt.addBatch();
                    pt.clearParameters();
                }
                pt.executeBatch();
                System.out.println(String.format("Succesfully added %d rows",count));
                AppLog.log(String.format("Succesfully added %d rows into raw_qualifying table",count));

            }
    }

    //load data into status table
    private void insertStatus(List<String[]> rawData,Connection conn)throws SQLException{
        String insert="INSERT INTO status(status_id,status) VALUES(?,?) ON CONFLICT DO NOTHING";
        int count=0;
            try(PreparedStatement pt=conn.prepareStatement(insert)){
                for(String[] data:rawData){
                    pt.setInt(1,parseNum(data[0]));
                    pt.setString(2,data[1]);
                    
                    count++;
                    pt.addBatch();
                    pt.clearParameters();
                }
                pt.executeBatch();
                System.out.println(String.format("Succesfully added %d rows",count));
                AppLog.log(String.format("Succesfully added %d rows into status table",count));

            }
    }
        
    

    //load data into seasons table
    private void insertSeasons(List<String[]> rawData,Connection conn)throws SQLException{
        String insert="INSERT INTO seasons(year,url) VALUES(?,?) ON CONFLICT DO NOTHING";
        int count=0;
            try(PreparedStatement pt=conn.prepareStatement(insert)){
                for(String[] data:rawData){
                    pt.setInt(1,parseNum(data[0]));
                    pt.setString(2,data[1]);
                    
                    count++;
                    pt.addBatch();
                    pt.clearParameters();
                }
                pt.executeBatch();
                System.out.println(String.format("Succesfully added %d rows",count));
                AppLog.log(String.format("Succesfully added %d rows into seasons table",count));

            }
        }
        
    

    //load data into raw_constructor_standings
    private void insertRawConstructorStandings(List<String[]> rawData,Connection conn)throws SQLException{
        String insert="INSERT INTO raw_constructor_standings(constructor_standing_id,race_id,constructor_id,points,position,wins) VALUES(?,?,?,?,?,?) ON CONFLICT DO NOTHING";
        int count=0;
            try(PreparedStatement pt=conn.prepareStatement(insert)){
                for(String[] data:rawData){
                    pt.setInt(1,parseNum(data[0]));
                    pt.setInt(2,parseNum(data[1]));
                    pt.setInt(3,parseNum(data[2]));
                    pt.setDouble(4,parseNumD(data[3]));
                    pt.setInt(5,parseNum(data[4]));
                    pt.setInt(6,parseNum(data[6]));
                    
                    count++;
                    pt.addBatch();
                    pt.clearParameters();
                }
                pt.executeBatch();
                System.out.println(String.format("Succesfully added %d rows",count));
                AppLog.log(String.format("Succesfully added %d rows into raw_constructor_standings table",count));

            }
        }
        
    

    //load data into raw_driver_standings
    private void insertRawDriverStandings(List<String[]> rawData,Connection conn)throws SQLException{
        String insert="INSERT INTO raw_driver_standings(driver_standing_id,race_id,driver_id,points,position,wins) VALUES(?,?,?,?,?,?) ON CONFLICT DO NOTHING";
        int count=0;
            try(PreparedStatement pt=conn.prepareStatement(insert)){
                for(String[] data:rawData){
                    pt.setInt(1,parseNum(data[0]));
                    pt.setInt(2,parseNum(data[1]));
                    pt.setInt(3,parseNum(data[2]));
                    pt.setInt(4,parseNum(data[3]));
                    pt.setInt(5,parseNum(data[4]));
                    pt.setInt(6,parseNum(data[6]));
                    
                    count++;
                    pt.addBatch();
                    pt.clearParameters();
                }
                pt.executeBatch();
                System.out.println(String.format("Succesfully added %d rows",count));
                AppLog.log(String.format("Succesfully added %d rows into raw_driver_standings table",count));

            }
        }
        
    

    //load data into raw_constructorresults
    private void insertRawConstructorResults(List<String[]> rawData,Connection conn)throws SQLException{
        String insert="INSERT INTO raw_constructorresults(constructor_results_id,race_id,constructor_id,points) VALUES(?,?,?,?) ON CONFLICT DO NOTHING";
        int count=0;
            try(PreparedStatement pt=conn.prepareStatement(insert)){
                for(String[] data:rawData){
                    pt.setInt(1,parseNum(data[0]));
                    pt.setInt(2,parseNum(data[1]));
                    pt.setInt(3,parseNum(data[2]));
                    pt.setInt(4,parseNum(data[3]));
                    
                    count++;
                    pt.addBatch();
                    pt.clearParameters();
                }
                pt.executeBatch();
                System.out.println(String.format("Succesfully added %d rows",count));
                AppLog.log(String.format("Succesfully added %d rows into raw_constructorresults table",count));

            }
        }
        
    

    //insert data into raw_pitstops
    private void insertRawPitstops(List<String[]> rawData,Connection conn)throws SQLException{
        String insert="INSERT INTO raw_pitstops(race_id,driver_id,stop,lap,time,duration,millisecond) VALUES(?,?,?,?,?,?,?) ON CONFLICT DO NOTHING";
        int count=0;
            try(PreparedStatement pt=conn.prepareStatement(insert)){
                for(String[] data:rawData){
                    pt.setInt(1,parseNum(data[0]));
                    pt.setInt(2,parseNum(data[1]));
                    pt.setInt(3,parseNum(data[2]));
                    pt.setInt(4,parseNum(data[3]));
                    pt.setString(5,data[4]);
                    pt.setString(6,data[5]);
                    pt.setInt(7,parseNum(data[6]));

                    count++;
                    pt.addBatch();
                    pt.clearParameters();
                }
                pt.executeBatch();
                System.out.println(String.format("Succesfully added %d rows",count));
                AppLog.log(String.format("Succesfully added %d rows into raw_pitstops table",count));

            }
    }
    

    //insert raw laptimes into table
    private void insertRawLapTimes(List<String[]> rawData,Connection conn)throws SQLException{
        String insert="INSERT INTO raw_laptimes(race_id,driver_id,lap,position,time,millisecond) VALUES(?,?,?,?,?,?) ON CONFLICT DO NOTHING";
        int count=0;
            try(PreparedStatement pt=conn.prepareStatement(insert)){
                for(String[] data:rawData){
                    pt.setInt(1,parseNum(data[0]));
                    pt.setInt(2,parseNum(data[1]));
                    pt.setInt(3,parseNum(data[2]));
                    pt.setInt(4,parseNum(data[3]));
                    pt.setString(5,data[4]);
                    pt.setInt(6,parseNum(data[5]));

                    count++;
                    pt.addBatch();
                    pt.clearParameters();
                }
                pt.executeBatch();
                System.out.println(String.format("Succesfully added %d rows",count));
                AppLog.log(String.format("Succesfully added %d rows into raw_laptimes table",count));

            }
    }
        

    //insert data into raw_results table
    private void insertRawResults(List<String[]> rawData,Connection conn)throws SQLException{
        String insert="INSERT INTO raw_results(result_id,race_id,driver_id,constructor_id,number,grid,position,points) VALUES(?,?,?,?,?,?,?,?) ON CONFLICT DO NOTHING";
        int count=0;
            try(PreparedStatement pt=conn.prepareStatement(insert)){
                for(String[] data:rawData){
                    pt.setInt(1,parseNum(data[0]));
                    pt.setInt(2,parseNum(data[1]));
                    pt.setInt(3,parseNum(data[2]));
                    pt.setInt(4,parseNum(data[3]));
                    pt.setInt(5,parseNum(data[4]));
                    pt.setInt(6,parseNum(data[5]));
                    pt.setString(7,data[6]);
                    pt.setDouble(8,parseNumD(data[9]));

                    count++;
                    pt.addBatch();
                    pt.clearParameters();
                }
                pt.executeBatch();
                System.out.println(String.format("Succesfully added %d rows",count));
                AppLog.log(String.format("Succesfully added %d rows into raw_resutls table",count));

            }
        }
        
    

    //insert data into raw_races table
    private void insertRawRaces(List<String[]> rawData,Connection conn)throws SQLException{
        String insert="INSERT INTO raw_races(race_id,year,round,circuit_id,name,date,time,url) VALUES(?,?,?,?,?,?,?,?) ON CONFLICT DO NOTHING";
        int count=0;
            try(PreparedStatement pt=conn.prepareStatement(insert)){
                for(String[] data:rawData){
                    pt.setInt(1,Integer.parseInt(data[0]));
                    pt.setInt(2,Integer.parseInt(data[1]));
                    pt.setInt(3,Integer.parseInt(data[2]));
                    pt.setInt(4,Integer.parseInt(data[3]));
                    pt.setString(5,data[4]);
                    pt.setDate(6,parseFlexDate(data[5]));
                    pt.setString(7,data[6]);
                    pt.setString(8,data[7]);

                    count++;
                    pt.addBatch();
                    pt.clearParameters();
                }
                pt.executeBatch();
                System.out.println(String.format("Succesfully added %d rows",count));
                AppLog.log(String.format("Succesfully added %d rows into raw_races table",count));

            }
        }
        
    

    //insert data into raw_circuits table
    private void insertRawCircuits(List<String[]> rawData,Connection conn)throws SQLException{
        String insert="INSERT INTO raw_circuits(circuit_id,circuit_ref,name,location,country,lat,lng,alt,url) VALUES(?,?,?,?,?,?,?,?,?) ON CONFLICT DO NOTHING";
        int count=1;
            try(PreparedStatement pt=conn.prepareStatement(insert)){
                for(String[] data:rawData){
                    pt.setInt(1,Integer.parseInt(data[0]));
                    pt.setString(2,data[1].trim());
                    pt.setString(3,data[2].trim());
                    pt.setString(4,data[3].trim());
                    pt.setString(5,data[4].trim());
                    pt.setDouble(6,Double.parseDouble(data[5]));
                    pt.setDouble(7,Double.parseDouble(data[6]));
                    pt.setDouble(8,Double.parseDouble(data[7]));
                    pt.setString(9,data[8].trim());
                    count++;
                    pt.addBatch();
                    pt.clearParameters();

                }
                pt.executeBatch();
                System.out.println(String.format("Succesfully added %d rows",count));
                AppLog.log(String.format("Succesfully added %d rows into raw circuits table",count));

            }
        }
        
    
    
    //Used to input raw constructor f1 data into database
    private void insertRawConstructor(List<String[]> rawData,Connection conn)throws SQLException{
        String insert="INSERT INTO raw_constructors(constructor_id,constructor_ref,name,nationality,url) VALUES (?,?,?,?,?) ON CONFLICT DO NOTHING";
        int count=1;
            try (PreparedStatement pt = conn.prepareStatement(insert)) {
                    for(String[] st:rawData){
                        pt.setInt(1,Integer.parseInt(st[0]));
                        pt.setString(2,st[1].trim());
                        pt.setString(3,st[2].trim());
                        pt.setString(4,st[3].trim());
                        pt.setString(5,st[4].trim());
                        count++;
                        pt.addBatch();
                        pt.clearParameters();
                    }
                    count--;
                    pt.executeBatch();
                    System.out.println(String.format("succesfully added %d",count));

                    conn.commit();
                    AppLog.log(String.format("succesfully added %d rows into the raw constructor table",count));
                }
            }
    

    //Used to input raw drivers f1 data into database
    private void insertRawDriver(List<String[]> rawData,Connection conn)throws SQLException{
        String insert="INSERT INTO raw_drivers(driver_id,driver_ref,number,code,forename,surname,dob,nationality,url) VALUES(?,?,?,?,?,?,?,?,?) ON CONFLICT DO NOTHING";
        int count=1;
            try(PreparedStatement pt=conn.prepareStatement(insert)) {
                for(String[] data:rawData){
                    pt.setInt(1,Integer.parseInt(data[0]));
                    pt.setString(2,data[1].trim());
                    pt.setString(3,data[2].trim());
                    pt.setString(4,data[3].trim());
                    pt.setString(5,data[4].trim());
                    pt.setString(6,data[5].trim());
                    pt.setDate(7,parseFlexDate(data[6]));
                    pt.setString(8,data[7].trim());
                    pt.setString(9, data[8].trim());
                    count++;
                    pt.addBatch();
                    pt.clearParameters();

                }
                count--;
                pt.executeBatch();
                System.out.println(String.format("successfully added %d rows",count));
                AppLog.log(String.format("Successfully added %d rows into the raw driver table.",count));
            }
        }
}
