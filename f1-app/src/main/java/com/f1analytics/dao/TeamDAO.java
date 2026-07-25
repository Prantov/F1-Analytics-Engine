package com.f1analytics.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TeamDAO {
    public static void insertTeams(Scanner sc) {
        ArrayList<Teams> arr=new ArrayList<>();
        String insert="INSERT INTO Teams(teamname,team_principal,founded,wins,constructor_wins,headquarters,engine_supplier,driver_championship) VALUES(?,?,?,?,?,?,?,?)";  
        System.out.println("Enter how many teams you want to insert");
        int n=Ac.acceptint(sc, "number of team");
        for(int i=0;i<n;i++){
            arr.add(new Teams(Ac.acceptstr(sc,"teamname"),Ac.acceptstr(sc,"team principal"),Ac.acceptint(sc,"race wins"),Ac.acceptint(sc,"constructor wins"),LocalDate.parse(Ac.acceptdt(sc,"founding date")),Ac.acceptstr(sc, "Headquarters"),Ac.acceptstr(sc,"engine supplier"),Ac.acceptint(sc, "driver championship")));
        }
        try(Connection conn=DBConnection.connectit();
            PreparedStatement pst=conn.prepareStatement(insert))
            {
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
                e.printStackTrace();
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
