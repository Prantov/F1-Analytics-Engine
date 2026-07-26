package com.f1analytics.util;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeParseException;
import com.f1analytics.dao.*;

public class Ac {
    private static String toupper(String s){
        int l=s.length();
        String w="";
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<l;i++){
            char c=s.charAt(i);
            if(c!=32){
                w=w+c;
            }
            else{
                char d=w.charAt(0);
                d=Character.toTitleCase(d);
                sb.append(d);
                sb.append(w.substring(1,w.length()));
                sb.append(" ");
                w="";
            }
        }
        return sb.toString().trim();
    }
    
    public static String acceptstr(Scanner sc,String val){
        System.out.println(String.format("Enter %s value",val));
        String s=sc.nextLine();
        s=s.trim()+" ";
        s=toupper(s);
        return s;
    }

    public static int acceptint(Scanner sc,String val){
        System.out.println(String.format("Enter %s value",val));
        int i=0;boolean check=true;
        while(check){
            try{
                i=sc.nextInt();
                sc.nextLine();
                if(i>=0){ 
                    return i; 
                }
                else{
                    System.out.println("Enter positive value");
                }
            }
            catch(InputMismatchException e){
                System.out.println("Pls enter an integer value>0");
                sc.nextLine();
            }    
        }
        return i;
    } 

    public static double acceptdbl(Scanner sc,String val){
        System.out.println(String.format("Enter %s value",val));
        boolean check=true;
        double i=0;
        while(check){
            try{
                i=sc.nextDouble();
                sc.nextLine();
                if(i>=0.0){
                    return i;
                }
                else{
                    System.out.println("Enter any positive value or keep it zero");
                }
            }
            catch(InputMismatchException e){
                System.out.println("Enter an integer or double value");
                sc.nextLine();
            }
        }
        return i;
    }

    public static String acceptdt(Scanner sc,String val){
        String regex="^\\d{4}[-./]\\d{2}[-./]\\d{2}$";
        boolean check=true;
        System.out.println(String.format("Enter %s value in format of YYYY-MM-DD.",val));
        String s="";
        while(check){
            s=sc.nextLine();
            s.trim();
            if(s.matches(regex)){
                try{
                    LocalDate verified=LocalDate.parse(s);
                    if(LocalDate.now().until(verified).isNegative()){
                        check=false;
                    }
                    else{
                        System.out.println("Entered time is in future");
                    }
                }
                catch(DateTimeParseException E)
                {
                    System.out.println("the enter time is out of limit");
                }
            }
            else{
                System.out.println("Enter the exact string in the format YYYY-MM-DD");
            }
        }
        return s;
    }
}
