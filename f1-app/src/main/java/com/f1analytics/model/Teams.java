package com.f1analytics.model;
import java.time.LocalDate;

public class Teams {
    public String teamname;
    public String team_principal;
    public int wins;
    public int constructor_wins;
    public LocalDate founded;
    public String headquarters;
    public String engsupply;
    public int driverchamp;

    public Teams(String teamname,String team_principal,int wins,int constructor_wins,LocalDate founded,String headquarters,String engsupply,int driverchamp)
    {
        this.teamname=teamname;
        this.team_principal=team_principal;
        this.wins=wins;
        this.constructor_wins=constructor_wins;
        this.founded=founded;
        this.headquarters=headquarters;
        this.engsupply=engsupply;
        this.driverchamp=driverchamp;
    }
}


