package com.f1analytics.model;
import java.time.LocalDate;

public class Tracks {
    public String trackname;
    public String country;
    public double record;
    public LocalDate recyear;
    public int driver_id;

    public Tracks(String trackname,String country,double record,int driver_id,LocalDate recyear){
        this.trackname=trackname;
        this.country=country;
        this.record=record;
        this.driver_id=driver_id;
        this.recyear=recyear;
    }
}
