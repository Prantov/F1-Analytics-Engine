package com.f1analytics.model;
import java.time.LocalDate;

public class Drivers {
    public String drivername;
    public String country;
    public int wins;
    public int podiums;
    public int current_team_id;
    public LocalDate birthdate;
    
    public Drivers(String drivername,String country,int wins,int podiums,int current_team_id,LocalDate birthdate)
    {
        this.drivername=drivername;
        this.country=country;
        this.wins=wins;
        this.podiums=podiums;
        this.current_team_id=current_team_id;
        this.birthdate=birthdate;
    }
}
