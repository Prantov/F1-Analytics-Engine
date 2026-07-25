package com.f1analytics.util;

import java.nio.file.*;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AppLog {
    private static final String filename="D:\\DBF\\f1-app\\src\\main\\java\\com\\f1analytics\\activitylog.txt"; 
    public static void log(String msg){
        
        String stamp=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Path logpath=Path.of(filename);
        String logEntry = "["+stamp+"]"+ msg+System.lineSeparator();
        try{
            Files.write(logpath,logEntry.getBytes(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
        }
        catch(IOException e){
            System.out.println("Unable to log into the file, try again pls");
            e.printStackTrace();
        }

    }
}
