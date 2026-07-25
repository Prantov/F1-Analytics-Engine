package com.f1analytics.model;
import java.time.LocalDate;

public class Wdc {
    public LocalDate cyear;
    public int winnerdriver_id;
    public double pointsw;
    public int runnerup_id;
    public double pointsr;
    
    public Wdc(int winnerdriver_id,double pointsw,int runnerup_id,double pointsr,LocalDate cyear){
        this.cyear=cyear;
        this.winnerdriver_id=winnerdriver_id;
        this.pointsw=pointsw;
        this.runnerup_id=runnerup_id;
        this.pointsr=pointsr;
    }
}
