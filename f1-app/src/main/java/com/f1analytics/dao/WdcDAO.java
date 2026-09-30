package com.f1analytics.dao;
import java.util.*;
import java.sql.*;
import java.time.*;
import java.io.*;
import com.f1analytics.model.*;
import com.f1analytics.util.*;

public class WdcDAO {

	public void passChoice(Scanner sc,BufferedReader br){
		PrintWriter pw=new PrintWriter(System.out,true);
		pw.println("What action do you want to perform??");
		pw.println("1. Insert new WDC data\n2. Update WDC Data\n3. Exit");

		int choice=Ac.acceptint("choice", br);
		if(choice==1){
			insertWdc(sc);
		}
		else if(choice==2){
			boolean selecting=true;
			while(selecting){
				pw.println("1. update by importing from file\n2. update by individual input\n3. exit");
				int updateChoice=Ac.acceptint(sc,"choice");
				switch(updateChoice){
					case 1:updateWdc(Ac.filepath(br));
						selecting=false;
						break;
					case 2:updateWdc(sc);
						selecting=false;
						break;
					case 3:selecting=false;
						break;
					default:pw.println("invalid choice try again");
				}
			}
		}
	}

	public void insertWdc(Scanner sc){
		ArrayList<Wdc> arr=new ArrayList<>();
		String insert="INSERT INTO Wdc(cyear,winnerdriver_id,pointsw,runnnerup_id,pointsr) VALUES(?,?,?,?,?)";

		System.out.println("Enter how many WDC records you want to insert");
		int n=Ac.acceptint(sc,"number of WDC records");
		for(int i=0;i<n;i++){
			arr.add(new Wdc(Ac.acceptint(sc,"winner driver id"),Ac.acceptdbl(sc,"winner points"),Ac.acceptint(sc,"runner-up driver id"),Ac.acceptdbl(sc,"runner-up points"),LocalDate.parse(Ac.acceptdt(sc,"championship year"))));
			System.out.println("next WDC record");
		}

		Connection conn=null;
		try{
			conn=DBConnection.connectit();
			PreparedStatement pst=conn.prepareStatement(insert);
			conn.setAutoCommit(false);
			System.out.println("Connecting to the database");
			System.out.println("Connection Successful");

			for(Wdc wdc:arr){
				pst.setObject(1,wdc.cyear);
				pst.setInt(2,wdc.winnerdriver_id);
				pst.setDouble(3,wdc.pointsw);
				pst.setInt(4,wdc.runnerup_id);
				pst.setDouble(5,wdc.pointsr);
				pst.addBatch();
				pst.clearParameters();
			}
			pst.executeBatch();
			conn.commit();
			pst.clearParameters();
		}
		catch(SQLException e){
			System.out.println("An error occured...");
			try{
				if(conn!=null){
					conn.rollback();
				}
			}
			catch(SQLException se){
				se.printStackTrace();
			}
			e.printStackTrace();
		}
		finally{
			try{
				if(conn!=null){
					conn.setAutoCommit(true);
					conn.close();
				}
			}
			catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	public void updateWdc(Scanner sc){
		System.out.println("Enter how many WDC records you want to update");
		int nc=Ac.acceptint(sc,"no of WDC records");
		HashMap<String,ArrayList<LinkedHashMap<String,Object>>> list=new HashMap<>();
		int temp=0;
		while(temp<nc){
			System.out.println("Enter the no of fields you want to update?\nAvailable fields:\n' winnerdriver_id | pointsw | runnnerup_id | pointsr'");
			int n=Ac.acceptint(sc,"number of fields");
			StringBuilder update=new StringBuilder();
			LinkedHashMap<String,Object> map=updaMap(sc,n,update);
			if(!list.containsKey(update.toString())){
				ArrayList<LinkedHashMap<String,Object>> al=new ArrayList<>();
				al.add(map);
				list.put(update.toString(),al);
			}
			else{
				list.get(update.toString()).add(map);
			}
			temp++;
		}
		executeWdcUpdates(list);
	}

	private void updateWdc(String csvFilePath){
		HashMap<String,ArrayList<LinkedHashMap<String,Object>>> list=new HashMap<>();
		String[] columns={"cyear","winnerdriver_id","pointsw","runnnerup_id","pointsr"};

		try{
			List<String[]> rows=CSVImporter.readcsv(csvFilePath);
			for(String[] row:rows){
				if(row.length<columns.length+1){
					throw new IllegalArgumentException("Invalid WDC update row: expected 6 columns");
				}
				LinkedHashMap<String,Object> map=new LinkedHashMap<>();
				map.put("cyear",java.sql.Date.valueOf(row[1].trim()));
				StringBuilder update=new StringBuilder("UPDATE wdc SET ");
				for(int i=2;i<columns.length+1;i++){
					String value=row[i].trim();
					if(!value.isEmpty()&&!value.equalsIgnoreCase("null")){
						Object parsedValue=switch(columns[i-1]){
							case "winnerdriver_id","runnnerup_id" -> Integer.valueOf(value);
							case "pointsw","pointsr" -> Double.valueOf(value);
							default -> java.sql.Date.valueOf(value);
						};
						map.put(columns[i-1],parsedValue);
						update.append(columns[i-1]).append(" = ?, ");
					}
				}
				if(map.size()==1){
					continue;
				}
				update.setLength(update.length()-2);
				update.append(" WHERE cyear = ?");
				list.computeIfAbsent(update.toString(),key -> new ArrayList<>()).add(map);
			}
		}
		catch(Exception e){
			System.out.println("Unable to read WDC update CSV: " + e.getMessage());
			return;
		}
		executeWdcUpdates(list);
	}

	private void executeWdcUpdates(HashMap<String,ArrayList<LinkedHashMap<String,Object>>> updates){
		Connection conn=null;
		try{
			conn=DBConnection.connectit();
			conn.setAutoCommit(false);
			for(String updateSql:updates.keySet()){
				try(PreparedStatement pst=conn.prepareStatement(updateSql)){
					int totalRows=0;
					for(LinkedHashMap<String,Object> paramMap:updates.get(updateSql)){
						int index=1;
						for(String key:paramMap.keySet()){
							if(!key.equalsIgnoreCase("cyear")&&paramMap.get(key)!=null){
								Object userValue=paramMap.get(key);
								if(String.valueOf(userValue).equals("--")){
									pst.setNull(index,java.sql.Types.VARCHAR);
								}
								else{
									pst.setObject(index,userValue);
								}
								index++;
							}
						}
						pst.setObject(index,paramMap.get("cyear"));
						totalRows+=pst.executeUpdate();
					}
					System.out.println("Update complete! Rows affected: " + totalRows);
				}
			}
			conn.commit();
		}
		catch(SQLException e){
			System.out.println("An error occured...");
			try{
				if(conn!=null){
					conn.rollback();
				}
			}
			catch(SQLException se){
				se.printStackTrace();
			}
			e.printStackTrace();
		}
		finally{
			try{
				if(conn!=null){
					conn.setAutoCommit(true);
					conn.close();
				}
			}
			catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	private static LinkedHashMap<String,Object> updaMap(Scanner sc,int count,StringBuilder update){
		LinkedHashMap<String,Object> map=new LinkedHashMap<>();
		System.out.println("Enter the championship year you want to update");
		map.put("cyear",java.sql.Date.valueOf(Ac.acceptdt(sc,"championship year")));
		System.out.println("you can use '--' mark to erase a field !!");
		System.out.println("if you want to leave a field as it is, type 'NULL'");
		update.append("UPDATE wdc SET ");
		int temp=0;
		while(temp<count){
			System.out.println("Enter the field you want to update!\n Available fields:\n' 1.winnerdriver_id | 2.pointsw | 3.runnnerup_id | 4.pointsr'");
			int ch=Ac.acceptint(sc,"int val");
			switch(ch){
				case 1:map.put("winnerdriver_id",Ac.acceptint(sc,"winner driver id"));
					break;
				case 2:map.put("pointsw",Ac.acceptdbl(sc,"winner points"));
					break;
				case 3:map.put("runnnerup_id",Ac.acceptint(sc,"runner-up driver id"));
					break;
				case 4:map.put("pointsr",Ac.acceptdbl(sc,"runner-up points"));
					break;
				default:System.out.println("reenter your choice");
			}
			temp++;
		}
		for(String col:map.keySet()){
			if(!col.equalsIgnoreCase("cyear")&&map.get(col)!=null){
				update.append(String.format("%s = ?, ",col));
			}
		}
		update.setLength(update.length()-2);
		update.append(" WHERE cyear = ?");
		return map;
	}
}
