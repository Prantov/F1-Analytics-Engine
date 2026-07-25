package com.f1analytics.model;

import java.time.LocalDate;

public class Wcc {
    public LocalDate cyear;
    public int winnerteam_id;
    public double pointsw;
    public int runnerup_id;
    public double pointsr;
    
    public Wcc(int winnerteam_id,double pointsw,int runnerup_id,double pointsr,LocalDate cyear){
        this.cyear=cyear;
        this.winnerteam_id=winnerteam_id;
        this.pointsw=pointsw;
        this.runnerup_id=runnerup_id;
        this.pointsr=pointsr;
    }
}
