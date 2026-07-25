package com.f1analytics.dao;
import java.util.*;
import java.sql.*;
import java.time.*;

public class DriverDAO {


    public static void insertDrivers(Scanner sc){
        
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
        
        boolean check1=true;
        while(check1){  
        try(Connection conn=DBConnection.connectit();
            PreparedStatement pst=conn.prepareStatement(insert))
            {
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
                    pst.clearParameters();
                check1=false;  
            }  
            catch(SQLException e){
                e.printStackTrace();
            }
        
        }
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

