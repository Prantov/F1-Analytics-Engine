package com.f1analytics;

import java.util.*;
import com.f1analytics.util.Ac;
import com.f1analytics.dao.*;
import java.io.*;


public class App 
{  
    public static void main( String[] args ) throws Exception 
    { 
        BufferedReader br=null;
        Scanner sc=new Scanner(System.in);
        try{
        br=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("welcome to the console");
        System.out.println("Enter your choice\n 1.Teams Data\n 2. Drivers Data\n 3. Tracks Data\n 4. WDC Data\n 5. WCC Data\n 6. Miscellanious Data \n 7. Exit");
        int ch1=Ac.acceptint(sc, "choice"),ch2=0;
        switch(ch1){
            case 1: TeamDAO team=new TeamDAO();
                    team.passChoice(sc, br);
                    break;

            case 2: DriverDAO driver=new DriverDAO();
                    driver.passChoice(sc,br);
                    break;
            
            case 3: TracksDAO tracks=new TracksDAO();
                    tracks.passChoice(sc,br);
                    break;
            
                case 4: WdcDAO wdc=new WdcDAO();
                    wdc.passChoice(sc,br);
                    break;

                case 5: WccDAO wcc=new WccDAO();
                    wcc.passChoice(sc,br);
                    break;

            // case 6: Extras extra=new Extras();
            //         extra.passChoice(sc,br);
            //         break;
            
            case 7: break;

            default:System.out.println("Invalid choice");
                    
        }
        }
        
        catch(Exception e){
            System.out.println("an error occured");
            System.out.println(e.getMessage());
        }
        finally{
            if(br!=null){
                try{ br.close();}
                catch(IOException e){e.printStackTrace();}
            sc.close();
        }
        }
    }
}
