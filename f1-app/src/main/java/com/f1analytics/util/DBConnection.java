package com.f1analytics.util;
import java.sql.*;
import com.f1analytics.*;

public class DBConnection {
    private static final String user="postgres";
    private static final String password="Pravendu@170906";
    private static final String url="jdbc:postgresql://localhost:5432/f1_analytics";
    public static Connection connectit(){
        Connection conn=null;
        try{
            conn=DriverManager.getConnection(url, user, password);
            return conn;
        }
        catch(SQLException e){
            e.printStackTrace();
            System.exit(0);
        }
    return conn;
    }
}
