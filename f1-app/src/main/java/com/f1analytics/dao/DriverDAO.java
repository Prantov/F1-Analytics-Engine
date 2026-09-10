package com.f1analytics.dao;
import java.util.*;
import java.sql.*;
import java.time.*;
import java.io.*;
import com.f1analytics.model.*;
import com.f1analytics.util.*;

public class DriverDAO {

    public void passChoice(Scanner sc,BufferedReader br){
        PrintWriter pw=new PrintWriter(System.out,true);
        pw.println("What action do you want to perform??");
        pw.println("1. Insert new driver data\n2. Update Driver Data\n3. Exit");

        int choice=Ac.acceptint("choice", br);
        if(choice==1){
            insertDrivers(sc);
        }
        else if(choice==2){
            boolean selecting=true;
            while(selecting){
                pw.println("1. update by importing from file\n2. update by individual input\n3. exit");
                int updateChoice=Ac.acceptint(sc,"choice");
                switch(updateChoice){
                    case 1:updateDrivers(Ac.filepath(br));
                        selecting=false;
                        break;
                    case 2:updateDrivers(sc);
                        selecting=false;
                        break;
                    case 3:selecting=false;
                        break;
                    default:pw.println("invalid choice try again");
                }
            }
        }
    }


    public void insertDrivers(Scanner sc){
        
        ArrayList<Drivers> arr=new ArrayList<>();
        HashMap<String,Integer> tmlist=getMap();
        String insert="INSERT INTO Drivers(drivername,wins,podiums,country,birthdate,current_team_id) VALUES(?,?,?,?,?,?)";
        
        System.out.println("Enter how many drivers you want to insert");
        int n=Ac.acceptint(sc, "number of drivers");
        for(int i=0;i<n;i++){
            System.out.println("Enter teamname");
            int extId=-1;
            boolean check2=true;
            while(check2){
                String tm=Ac.acceptstr(sc, "teamname");
                extId=tmlist.entrySet().stream().filter(entry->entry.getKey().toLowerCase().contains(tm.toLowerCase())).map(entry->entry.getValue()).findFirst().orElse(-1);
                if(extId>0){
                    check2=false;
                }
                else{
                    System.out.println("Team doesnt exist,reenter teamname");
                }
            }
            arr.add(new Drivers(Ac.acceptstr(sc,"drivername"),Ac.acceptstr(sc,"country"),Ac.acceptint(sc,"race wins"),Ac.acceptint(sc,"podiums"),extId,LocalDate.parse(Ac.acceptdt(sc,"birthdate"))));
            System.out.println("next driver");
        } 
        
        Connection conn=null;  
        try{
            conn=DBConnection.connectit();
            PreparedStatement pst=conn.prepareStatement(insert);
            conn.setAutoCommit(false);
            System.out.println("Connecting to the database");
            System.out.println("Connection Successful");              

            for(Drivers dr:arr){
                pst.setString(1,dr.drivername);
                pst.setInt(2, dr.wins);
                pst.setInt(3,dr.podiums);
                pst.setString(4,dr.country);
                pst.setObject(5,dr.birthdate);
                pst.setInt(6,dr.current_team_id);

                pst.addBatch();
                pst.clearParameters();
            }
                    
            int a[]=pst.executeBatch();
            conn.commit();
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

    public void updateDrivers(Scanner sc){
        System.out.println("Enter how many drivers you want to update");
        int nc=Ac.acceptint(sc, "no of drivers");
        HashMap<String,ArrayList<LinkedHashMap<String,Object>>> list=new HashMap<>();
        int temp=0;
        HashMap<String,Integer> tmlist=getMap();
        while(temp<nc){
            System.out.println("Enter the no of fields you want to update?\nAvailable fields:\n' drivername | wins | podiums | country | birthdate | current_team_name'");
            int n=Ac.acceptint(sc, "number of fields");
            StringBuilder update=new StringBuilder();
            LinkedHashMap<String,Object> map=updaMap(sc,n,update,tmlist);
            if(!list.containsKey(update.toString())){
                ArrayList<LinkedHashMap<String,Object>> al=new ArrayList<>();
                al.add(map);
                list.put(update.toString(),al);
            }
            else{
                list.get(update.toString()).add(map);
            }
            temp++;
        }
        executeDriverUpdates(list);
    }

    private void updateDrivers(String csvFilePath){
        HashMap<String,ArrayList<LinkedHashMap<String,Object>>> list=new HashMap<>();
        String[] columns={"drivername","wins","podiums","country","birthdate","current_team_id"};

        try{
            List<String[]> rows=CSVImporter.readcsv(csvFilePath);
            for(String[] row:rows){
                if(row.length < columns.length + 1){
                    throw new IllegalArgumentException("Invalid driver update row: expected 7 columns");
                }

                LinkedHashMap<String,Object> map=new LinkedHashMap<>();
                map.put("drivername",row[1].trim());
                StringBuilder update=new StringBuilder("UPDATE drivers SET ");
                for(int i=2;i<columns.length + 1;i++){
                    String value=row[i].trim();
                    if(!value.isEmpty() && !value.equalsIgnoreCase("null")){
                        Object parsedValue=switch(columns[i-1]){
                            case "wins", "podiums", "current_team_id" -> Integer.valueOf(value);
                            case "birthdate" -> java.sql.Date.valueOf(value);
                            default -> value;
                        };
                        map.put(columns[i-1],parsedValue);
                        update.append(columns[i-1]).append(" = ?, ");
                    }
                }
                if(map.size()==1){
                    continue;
                }
                update.setLength(update.length()-2);
                update.append(" WHERE drivername = ?");
                list.computeIfAbsent(update.toString(),key -> new ArrayList<>()).add(map);
            }
        }
        catch(Exception e){
            System.out.println("Unable to read driver update CSV: " + e.getMessage());
            return;
        }

        executeDriverUpdates(list);
    }

    private void executeDriverUpdates(HashMap<String,ArrayList<LinkedHashMap<String,Object>>> updates){
        Connection conn=null;
        try{
            conn=DBConnection.connectit();
            conn.setAutoCommit(false);
            for(String updateSql:updates.keySet()){
                try(PreparedStatement pst=conn.prepareStatement(updateSql)){
                System.out.println("Connecting to the Database");
                System.out.println("Connection succcessful");
                int totalRows=0;
                for(LinkedHashMap<String,Object> paramMap:updates.get(updateSql)){
                    int index = 1;
                    for (String key : paramMap.keySet()) {
                        if (!key.equalsIgnoreCase("drivername") && paramMap.get(key) != null) {
                            Object userValue = paramMap.get(key);
                        if (String.valueOf(userValue).equals("--")) {
                            pst.setNull(index, java.sql.Types.VARCHAR);
                        } else {
                            pst.setObject(index, userValue); 
                        }
                        index++;
                        }
                    }
                    pst.setString(index, String.valueOf(paramMap.get("drivername")));
                    int rowsAffected = pst.executeUpdate();
                    totalRows+=rowsAffected;
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


    private static LinkedHashMap<String,Object> updaMap(Scanner sc,int count,StringBuilder update,HashMap<String,Integer> tmlist){
        LinkedHashMap<String,Object> map=new LinkedHashMap<>();
        System.out.println("Enter the drivername you want to update");
        map.put("drivername",Ac.acceptstr(sc, "drivername"));
        System.out.println("you can use '--' mark to erase a field !!");
        System.out.println("if you want to leave a field as it is, type 'NULL'");
        update.append("UPDATE drivers SET ");
        int temp=0;
        while(temp<count){
            System.out.println("Enter the field you want to update!\n Available fields:\\n' 1.wins | 2.podiums | 3.country | 4.birthdate | 5.current_team_name'");
            int ch=Ac.acceptint(sc,"int val");
            switch(ch){
                case 1:map.put("wins",Ac.acceptint(sc, "wincount"));
                break;
                case 2:map.put("podiums",Ac.acceptint(sc, "podiums"));
                break;
                case 3:map.put("country",Ac.acceptstr(sc, "country"));
                break;
                case 4:map.put("birthdate",java.sql.Date.valueOf(Ac.acceptdt(sc, "birthdate")));
                break;
                case 5:System.out.println("Enter teamname");
                        int extId=-1;
                        boolean check2=true;
                        while(check2){
                            String tm=Ac.acceptstr(sc, "teamname");
                            extId=tmlist.entrySet().stream().filter(entry->entry.getKey().toLowerCase().contains(tm.toLowerCase())).map(entry->entry.getValue()).findFirst().orElse(-1);
                            if(extId>0){
                                check2=false;
                            }
                            else{
                                System.out.println("Team doesnt exist,reenter teamname");
                            }
                            map.put("current_team_id",extId);}
                break;
                case 7:System.out.println("reenter your choice");
            }
            temp++;
        }
        for(String col:map.keySet()){
            if(!col.equalsIgnoreCase("drivername") && map.get(col)!=null)update.append(String.format("%s = ?, ",col));
        }
        update.setLength(update.length() - 2);
        update.append("WHERE drivername = ?");
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

