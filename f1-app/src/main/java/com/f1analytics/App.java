package com.f1analytics;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import com.f1analytics.util.DBConnection;
import com.f1analytics.dao.TeamDAO;
import com.f1analytics.dao.DriverDAO;

/**
 * Hello world!
 *
 */
public class App 
{  
    public static void main( String[] args )
    {   
        Scanner sc=new Scanner(System.in);
        System.out.println("welcome to the console");
        Connection conn=DBConnection.connectit();
        System.out.println("Enter your choice\n 1.Insert Data\n2.Update Data\n3.Delete Data\n4.Read Data");
        int ch1=Ac.acceptint(sc, "choice"),ch2=0;
        switch(ch1){
            case 1: System.out.println("Enter your choice\n1.Teams Data\n2.Driver Data\n3.Tracks Data\n4.WDC Data\n5.WCC Data");
                    ch2=Ac.acceptint(sc, "choice");
                    switch(ch2){
                        case 1: TeamDAO.insertTeams(sc);
                            break;
                        case 2: DriverDAO.insertDrivers(sc);
                            break;
                        // case 3:insertTracks(sc);
                        //     break;
                        // case 4:insertWDC(sc);
                        //     break;
                        // case 5:insertWCC(sc);c;1
                        //     break;
                        default:System.out.println("invalid choice");
                                System.exit(0);
                    }
                    break;
            default:System.out.println("Invalid choice");
                    
        }
        sc.close();
    }
}
