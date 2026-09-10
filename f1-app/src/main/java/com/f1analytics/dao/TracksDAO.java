package com.f1analytics.dao;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import com.f1analytics.model.Tracks;
import com.f1analytics.util.Ac;
import com.f1analytics.util.DBConnection;

public class TracksDAO {

    public void passChoice(Scanner sc,BufferedReader br){
        PrintWriter pw=new PrintWriter(System.out,true);
        pw.println("What action do you want to perform??");
        pw.println("1. Insert new track data\n2. Update Track Data\n3. Exit");

        int choice=Ac.acceptint("choice",br);
        if(choice==1){
            insertTracks(sc);
        }
        else if(choice==2){
            updateTracks(sc);
        }
    }

    private void insertTracks(Scanner sc){
        ArrayList<Tracks> arr=new ArrayList<>();
        HashMap<String,Integer> driverlist=getMap("drivername","id","drivers");
        String insert="INSERT INTO Tracks(trackname,country,record,recyear,driver_id) VALUES(?,?,?,?,?)";

        System.out.println("Enter how many tracks you want to insert");
        int n=Ac.acceptint(sc,"number of tracks");
        for(int i=0;i<n;i++){
            int driverId=-1;
            boolean driverFound=false;
            while(!driverFound){
                String driverName=Ac.acceptstr(sc,"drivername");
                driverId=driverlist.entrySet().stream()
                        .filter(entry->entry.getKey().equalsIgnoreCase(driverName.trim()))
                        .map(entry->entry.getValue())
                        .findFirst().orElse(-1);
                if(driverId>0){
                    driverFound=true;
                }
                else{
                    System.out.println("Driver doesnt exist,reenter drivername");
                }
            }
            arr.add(new Tracks(Ac.acceptstr(sc,"trackname"),Ac.acceptstr(sc,"country"),
                    Ac.acceptdbl(sc,"record"),driverId,LocalDate.parse(Ac.acceptdt(sc,"record year"))));
            System.out.println("next track");
        }

        Connection conn=null;
        try{
            conn=DBConnection.connectit();
            try(PreparedStatement pst=conn.prepareStatement(insert)){
                conn.setAutoCommit(false);
                System.out.println("Connecting to the database");
                System.out.println("Connection Successful");

                for(Tracks track:arr){
                    pst.setString(1,track.trackname);
                    pst.setString(2,track.country);
                    pst.setDouble(3,track.record);
                    pst.setObject(4,track.recyear);
                    pst.setInt(5,track.driver_id);
                    pst.addBatch();
                    pst.clearParameters();
                }
                int[] insertedRows=pst.executeBatch();
                int totalInserted=0;
                for(int rows:insertedRows){
                    if(rows>=0){
                        totalInserted+=rows;
                    }
                }
                System.out.println("Insert complete! Rows inserted: " + totalInserted);
                conn.commit();
            }
        }
        catch(SQLException e){
            System.out.println("An error occured...");
            try{
                if(conn!=null){
                    conn.rollback();
                }
            }
            catch(SQLException se){
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

    private void updateTracks(Scanner sc){
        HashMap<String,Integer> trackList=getMap("trackname","id","tracks");
        HashMap<String,Integer> driverList=getMap("drivername","id","drivers");
        HashMap<String,ArrayList<LinkedHashMap<String,Object>>> updates=new HashMap<>();

        System.out.println("Enter how many tracks you want to update");
        int numberOfTracks=Ac.acceptint(sc,"number of tracks");
        int trackNumber=0;
        while(trackNumber<numberOfTracks){
            Integer trackId=null;
            while(trackId==null){
                String trackName=Ac.acceptstr(sc,"trackname (or exit)");
                if(trackName.equalsIgnoreCase("exit")){
                    return;
                }
                trackId=findExactId(trackList,trackName);
                if(trackId==null){
                    System.out.println("Track doesnt exist, reenter trackname");
                }
            }

            int numberOfFields=Ac.acceptint(sc,"number of fields");
            StringBuilder update=new StringBuilder("UPDATE tracks SET ");
            LinkedHashMap<String,Object> values=new LinkedHashMap<>();
            values.put("track_id",trackId);
            int fieldNumber=0;
            while(fieldNumber<numberOfFields){
                System.out.println("Available fields:\n1. country | 2. record | 3. recyear | 4. drivername");
                int field=Ac.acceptint(sc,"field");
                switch(field){
                    case 1:
                        values.put("country",Ac.acceptstr(sc,"country"));
                        update.append("country = ?, ");
                        fieldNumber++;
                        break;
                    case 2:
                        values.put("record",roundRecord(Ac.acceptdbl(sc,"record")));
                        update.append("record = ?, ");
                        fieldNumber++;
                        break;
                    case 3:
                        values.put("recyear",java.sql.Date.valueOf(Ac.acceptdt(sc,"record year")));
                        update.append("recyear = ?, ");
                        fieldNumber++;
                        break;
                    case 4:
                        Integer driverId=null;
                        while(driverId==null){
                            String driverName=Ac.acceptstr(sc,"drivername");
                            if(driverName.equalsIgnoreCase("exit")){
                                return;
                            }
                            driverId=findExactId(driverList,driverName);
                            if(driverId==null){
                                System.out.println("Driver doesnt exist, reenter drivername");
                            }
                        }
                        values.put("driver_id",driverId);
                        update.append("driver_id = ?, ");
                        fieldNumber++;
                        break;
                    default:
                        System.out.println("Invalid field, try again");
                }
            }
            update.setLength(update.length()-2);
            update.append(" WHERE id = ?");
            updates.computeIfAbsent(update.toString(),key->new ArrayList<>()).add(values);
            trackNumber++;
        }
        executeTrackUpdates(updates);
    }

    private void executeTrackUpdates(HashMap<String,ArrayList<LinkedHashMap<String,Object>>> updates){
        Connection conn=null;
        int totalRows=0;
        try{
            conn=DBConnection.connectit();
            conn.setAutoCommit(false);
            for(String updateSql:updates.keySet()){
                try(PreparedStatement pst=conn.prepareStatement(updateSql)){
                    for(LinkedHashMap<String,Object> values:updates.get(updateSql)){
                        int index=1;
                        for(String column:values.keySet()){
                            if(!column.equals("track_id")){
                                Object value=values.get(column);
                                if(String.valueOf(value).equals("--")){
                                    pst.setNull(index,java.sql.Types.VARCHAR);
                                }
                                else{
                                    pst.setObject(index,value);
                                }
                                index++;
                            }
                        }
                        pst.setInt(index,(Integer)values.get("track_id"));
                        totalRows+=pst.executeUpdate();
                    }
                }
            }
            conn.commit();
            System.out.println("Update complete! Rows affected: " + totalRows);
        }
        catch(SQLException e){
            try{
                if(conn!=null){
                    conn.rollback();
                }
            }
            catch(SQLException rollbackError){
                rollbackError.printStackTrace();
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
            catch(SQLException cleanupError){
                cleanupError.printStackTrace();
            }
        }
    }

    private static Integer findExactId(HashMap<String,Integer> values,String input){
        for(Map.Entry<String,Integer> entry:values.entrySet()){
            if(entry.getKey().equalsIgnoreCase(input.trim())){
                return entry.getValue();
            }
        }
        return null;
    }

    private static double roundRecord(double record){
        return Math.round(record*1000.0)/1000.0;
    }

    private static HashMap<String,Integer> getMap(String txt,String id,String tablename){
        String query="SELECT " + txt + "," + id + " FROM " + tablename;
        HashMap<String,Integer> values=new HashMap<>();
        try(Connection conn=DBConnection.connectit();
            PreparedStatement pst=conn.prepareStatement(query);
            ResultSet rs=pst.executeQuery()){
            while(rs.next()){
                values.put(rs.getString(txt),rs.getInt(id));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return values;
    }
}
