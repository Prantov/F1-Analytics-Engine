package com.f1analytics.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import com.f1analytics.model.*;
import com.f1analytics.util.*;
import java.io.*;

public class TeamDAO {

    public void passChoice(Scanner sc,BufferedReader br){
        PrintWriter pw=new PrintWriter(System.out,true);
        pw.println("What action do you want to perform??");
        pw.println("1. Insert new team data\n 2. Update Team Data\n3.Read Team Data \n4. exit");

        boolean check2=true;
        int ch=Ac.acceptint("choice", br);
        if(ch==2){
            while(check2){
            pw.println("1. update by importing from file \n 2.update by individual input \n3. exit");
            int ch2=Ac.acceptint(sc,"choice");
            
            switch(ch2){
                case 1:updateTeams(Ac.filepath(br));
                        check2=false;
                        break;

                case 2:updateTeams(sc);
                        check2=false;
                        break;
                
                case 3:check2=false; 
                    break;

                default: pw.println("invalid choice try again");
            }
        }
        }
        if(ch==1){
            insertTeams(sc);
        }
    }

    private void insertTeams(Scanner sc) {
        ArrayList<Teams> arr=new ArrayList<>();
        String insert="INSERT INTO Teams(teamname,team_principal,founded,wins,constructor_wins,headquarters,engine_supplier,driver_championship) VALUES(?,?,?,?,?,?,?,?)";  
        System.out.println("Enter how many teams you want to insert");
        int n=Ac.acceptint(sc, "number of team");
        for(int i=0;i<n;i++){
            arr.add(new Teams(Ac.acceptstr(sc,"teamname"),Ac.acceptstr(sc,"team principal"),Ac.acceptint(sc,"race wins"),Ac.acceptint(sc,"constructor wins"),LocalDate.parse(Ac.acceptdt(sc,"founding date")),Ac.acceptstr(sc, "Headquarters"),Ac.acceptstr(sc,"engine supplier"),Ac.acceptint(sc, "driver championship")));
        }
        Connection conn=null;
        try{
            conn=DBConnection.connectit();
            PreparedStatement pst=conn.prepareStatement(insert);
            
                conn.setAutoCommit(false);
                System.out.println("Connecting to the database");
            
                System.out.println("Connection Successful");
                
                for(Teams tm:arr){
                    pst.setString(1,tm.teamname);
                    pst.setString(2,tm.team_principal);
                    pst.setObject(3,tm.founded);
                    pst.setInt(4,tm.wins);
                    pst.setInt(5,tm.constructor_wins);
                    pst.setString(6,tm.headquarters);
                    pst.setString(7,tm.engsupply);
                    pst.setInt(8,tm.driverchamp);

                    pst.addBatch();
                    pst.clearParameters();
                }
                System.out.println("added to list");
                
                int a[]=pst.executeBatch();
                    pst.clearParameters();
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

    private void updateTeams(Scanner sc){
        //  this method takes no of fields user want to update, then passes the count to updaMap(), there the whole input works happen and it return the map with the user input, then in this method the user does the updation work
        System.out.println("Enter how many teams you want to update");
        int nc=Ac.acceptint(sc, "no of teams");
        HashMap<String,ArrayList<LinkedHashMap<String,Object>>> list=new HashMap<>();
        int temp=0;
        
        while(temp<nc){
            System.out.println("Enter the no of fields you want to update?\nAvailable fields:\n' team_principal | founded | wins | constructor_wins | headquarters | engine_supplier | driver_championship'");
            int n=Ac.acceptint(sc, "number of fields");
            StringBuilder update=new StringBuilder();
            LinkedHashMap<String,Object> map=updaMap(sc,n,update);
            if(!list.containsKey(update.toString())){
                ArrayList<LinkedHashMap<String,Object>> al=new ArrayList<>();
                al.add(map);
                list.put(update.toString(),al);
            }
            else{
                list.get(update.toString()).add(map);
            }
        }
        executeTeamUpdates(list);
    }

    private void updateTeams(String csvFilePath) {
        HashMap<String,ArrayList<LinkedHashMap<String,Object>>> list=new HashMap<>();
        String[] columns={"teamname","team_principal","founded","wins","constructor_wins","headquarters","engine_supplier","driver_championship"};

        try {
            List<String[]> rows=CSVImporter.readcsv(csvFilePath);
            for(String[] row:rows){
                if(row.length < columns.length + 1){
                    throw new IllegalArgumentException("Invalid team update row: expected 9 columns");
                }

                LinkedHashMap<String,Object> map=new LinkedHashMap<>();
                map.put("teamname", row[1].trim());
                StringBuilder update=new StringBuilder("UPDATE teams SET ");
                for(int i=2;i<columns.length + 1;i++){
                    String value=row[i].trim();
                    if(!value.isEmpty() && !value.equalsIgnoreCase("null")){
                        Object parsedValue=switch(columns[i-1]){
                            case "founded" -> value.matches("\\d{4}")
                                    ? java.sql.Date.valueOf(value + "-01-01")
                                    : java.sql.Date.valueOf(value);
                            case "wins", "constructor_wins", "driver_championship" -> Integer.valueOf(value);
                            default -> value;
                        };
                        map.put(columns[i-1], parsedValue);
                        update.append(columns[i-1]).append(" = ?, ");
                    }
                }
                if(map.size() == 1){
                    continue;
                }
                update.setLength(update.length() - 2);
                update.append(" WHERE teamname = ?");
                list.computeIfAbsent(update.toString(), key -> new ArrayList<>()).add(map);
            }
        }
        catch(Exception e){
            System.out.println("Unable to read team update CSV: " + e.getMessage());
            return;
        }

        executeTeamUpdates(list);
    }

    private void executeTeamUpdates(HashMap<String,ArrayList<LinkedHashMap<String,Object>>> updates){
        Connection conn=null;
        try{
            conn=DBConnection.connectit();
            conn.setAutoCommit(false);
            for(String updateSql:updates.keySet()){
                try(PreparedStatement pst=conn.prepareStatement(updateSql)){
                    int totalRows=0;
                    for(LinkedHashMap<String,Object> paramMap:updates.get(updateSql)){
                        int index=1;
                        for(String key:paramMap.keySet()){
                            if(!key.equalsIgnoreCase("teamname") && paramMap.get(key)!=null){
                                Object userValue=paramMap.get(key);
                                if(String.valueOf(userValue).equals("--")){
                                    pst.setNull(index,java.sql.Types.VARCHAR);
                                }
                                else{
                                    pst.setObject(index,userValue);
                                }
                                index++;
                            }
                        }
                        pst.setString(index,String.valueOf(paramMap.get("teamname")));
                        totalRows+=pst.executeUpdate();
                    }
                    System.out.println("Update complete! Rows affected: " + totalRows);
                }
            }
            conn.commit();
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


    private static LinkedHashMap<String,Object> updaMap(Scanner sc,int count,StringBuilder update){
        LinkedHashMap<String,Object> map=new LinkedHashMap<>();
        System.out.println("Enter the teamname you want to update");
        map.put("teamname",Ac.acceptstr(sc, "teamname"));
        System.out.println("you can use '--' mark to erase a field !!");
        System.out.println("if you want to leave a field as it is, type 'NULL'");
        update.append("UPDATE teams SET ");
        int temp=0;
        while(temp<count){
            System.out.println("Enter the field you want to update!\n Available fields:\\n'1. team_principal | 2.founded | 3.wins | 4.constructor_wins | 5.headquarters | 6.engine_supplier | 7.driver_championship'");
            int ch=Ac.acceptint(sc,"int val");
            switch(ch){
                case 1:map.put("team_principal",Ac.acceptstr(sc, "prinicipal name"));count++;
                break;
                case 2:map.put("founded",java.sql.Date.valueOf(Ac.acceptdt(sc, "foundation date")));count++;
                break;
                case 3:map.put("wins",Ac.acceptdbl(sc, "wins"));count++;
                break;
                case 4:map.put("constructor_wins",Ac.acceptint(sc, "const wins"));count++;
                break;
                case 5:map.put("headquarters",Ac.acceptstr(sc, "headquarters"));count++;
                break;
                case 6:map.put("engine_supplier",Ac.acceptstr(sc, "engine supplier"));count++;
                break;
                case 7:map.put("driver_championship",Ac.acceptint(sc, "driver champs"));count++;
                break;
                case 8:System.out.println("reenter your choice");
            }
        }
        for(String col:map.keySet()){
            if(!col.equalsIgnoreCase("teamname") && map.get(col)!=null)update.append(String.format("%s = ?, ",col));
        }
        update.setLength(update.length() - 2);
        update.append(" WHERE teamname = ?");
        return map;
    }
    
    private static HashMap<String,Integer> getMap(){
        String teamlist="SELECT teamname,id FROM Teams";
        HashMap<String,Integer> tml=new HashMap<>();
            try(Connection conn=DBConnection.connectit();
                PreparedStatement pst=conn.prepareStatement(teamlist))
                {    
                    ResultSet rs=pst.executeQuery();
                    while(rs.next()){
                        tml.put(rs.getString("teamname"),rs.getInt("id"));
                    }
                    return tml;
                }
            catch(SQLException e){
                e.printStackTrace();
            }
        return tml;
    }

    
}
