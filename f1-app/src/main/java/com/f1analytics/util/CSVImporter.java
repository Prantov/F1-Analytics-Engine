package com.f1analytics.util;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;


public class CSVImporter {
    public static List<String[]> readcsv(String filename) throws Exception{
        List<String[]> rawData=new ArrayList<>();
        try(BufferedReader br=Files.newBufferedReader(Path.of(filename))){
            
            CSVParser parser=new CSVParserBuilder().withSeparator(',').withQuoteChar('"').build();
            
            CSVReader cr=new CSVReaderBuilder(br).withCSVParser(parser).withSkipLines(1).build();
            String[] nxtline;
            while((nxtline=cr.readNext())!=null){
                rawData.add(nxtline);
            } 
        }
        
        return rawData;
    }
}
