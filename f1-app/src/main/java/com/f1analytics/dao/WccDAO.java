package com.f1analytics.dao;
import java.util.*;
import java.sql.*;
import java.time.*;
import java.io.*;
import com.f1analytics.model.*;
import com.f1analytics.util.*;

public class WccDAO {

	public void passChoice(Scanner sc,BufferedReader br){
		PrintWriter pw=new PrintWriter(System.out,true);
		pw.println("What action do you want to perform??");
		pw.println("1. Insert new WCC data\n2. Update WCC Data\n3. Exit");
		int choice=Ac.acceptint("choice", br);
		if(choice==1){
			insertWcc(sc);
		}
		else if(choice==2){
			boolean selecting=true;
			while(selecting){
				pw.println("1. update by importing from file\n2. update by individual input\n3. exit");
				int updateChoice=Ac.acceptint(sc,"choice");
				switch(updateChoice){
					case 1:updateWcc(Ac.filepath(br));
						selecting=false;
						break;
					case 2:updateWcc(sc);
						selecting=false;
						break;
					case 3:selecting=false;
						break;
					default:pw.println("invalid choice try again");
				}
			}
		}
	}

	public void insertWcc(Scanner sc){
		ArrayList<Wcc> arr=new ArrayList<>();
		String insert="INSERT INTO Wcc(cyear,winnerteam_id,pointsw,runnerup_id,pointsr) VALUES(?,?,?,?,?)";
		System.out.println("Enter how many WCC records you want to insert");
		int n=Ac.acceptint(sc,"number of WCC records");
		for(int i=0;i<n;i++){
			arr.add(new Wcc(Ac.acceptint(sc,"winner team id"),Ac.acceptdbl(sc,"winner points"),Ac.acceptint(sc,"runner-up team id"),Ac.acceptdbl(sc,"runner-up points"),LocalDate.parse(Ac.acceptdt(sc,"championship year"))));
			System.out.println("next WCC record");
		}

		Connection conn=null;
		try{
			conn=DBConnection.connectit();
			PreparedStatement pst=conn.prepareStatement(insert);
			conn.setAutoCommit(false);
			System.out.println("Connecting to the database");
			System.out.println("Connection Successful");
			for(Wcc wcc:arr){
				pst.setObject(1,wcc.cyear);
				pst.setInt(2,wcc.winnerteam_id);
				pst.setDouble(3,wcc.pointsw);
				pst.setInt(4,wcc.runnerup_id);
				pst.setDouble(5,wcc.pointsr);
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

	public void updateWcc(Scanner sc){
		System.out.println("Enter how many WCC records you want to update");
		int nc=Ac.acceptint(sc,"no of WCC records");
		HashMap<String,ArrayList<LinkedHashMap<String,Object>>> list=new HashMap<>();
		int temp=0;
		while(temp<nc){
			System.out.println("Enter the no of fields you want to update?\nAvailable fields:\n' winnerteam_id | pointsw | runnerup_id | pointsr'");
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
		executeWccUpdates(list);
	}

	private void updateWcc(String csvFilePath){
		HashMap<String,ArrayList<LinkedHashMap<String,Object>>> list=new HashMap<>();
		String[] columns={"cyear","winnerteam_id","pointsw","runnerup_id","pointsr"};
		try{
			List<String[]> rows=CSVImporter.readcsv(csvFilePath);
			for(String[] row:rows){
				if(row.length<columns.length+1){
					throw new IllegalArgumentException("Invalid WCC update row: expected 6 columns");
				}
				LinkedHashMap<String,Object> map=new LinkedHashMap<>();
				map.put("cyear",java.sql.Date.valueOf(row[1].trim()));
				StringBuilder update=new StringBuilder("UPDATE wcc SET ");
				for(int i=2;i<columns.length+1;i++){
					String value=row[i].trim();
					if(!value.isEmpty()&&!value.equalsIgnoreCase("null")){
						Object parsedValue=switch(columns[i-1]){
							case "winnerteam_id","runnerup_id" -> Integer.valueOf(value);
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
			System.out.println("Unable to read WCC update CSV: " + e.getMessage());
			return;
		}
		executeWccUpdates(list);
	}

	private void executeWccUpdates(HashMap<String,ArrayList<LinkedHashMap<String,Object>>> updates){
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
		update.append("UPDATE wcc SET ");
		int temp=0;
		while(temp<count){
			System.out.println("Enter the field you want to update!\n Available fields:\n' 1.winnerteam_id | 2.pointsw | 3.runnerup_id | 4.pointsr'");
			int ch=Ac.acceptint(sc,"int val");
			switch(ch){
				case 1:map.put("winnerteam_id",Ac.acceptint(sc,"winner team id"));
					break;
				case 2:map.put("pointsw",Ac.acceptdbl(sc,"winner points"));
					break;
				case 3:map.put("runnerup_id",Ac.acceptint(sc,"runner-up team id"));
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
