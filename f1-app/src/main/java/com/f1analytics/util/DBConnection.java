package com.f1analytics.util;
import java.sql.*;
import java.util.Properties;
import java.nio.file.*;
import com.f1analytics.*;
import java.io.*;

public class DBConnection {
   
    public static Connection connectit(){
        Connection conn=null;
        try{
            Properties prop=new Properties();
            Path path=Paths.get("D:\\DBF\\f1-app\\database.properties");
            try(Reader reader=Files.newBufferedReader(path)){
                prop.load(reader);
            }
            String url=prop.getProperty("db.url");
            String user=prop.getProperty("db.user");
            String password=prop.getProperty("db.password");
            conn=DriverManager.getConnection(url, user, password);
            return conn;
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    return conn;
    }
}
