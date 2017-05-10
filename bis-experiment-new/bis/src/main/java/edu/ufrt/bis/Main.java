package edu.ufrt.bis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

	public static void main(String args[]){
		
		// connection to the database
		String driverName="com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String username = "admin";
		String password = "admin";
		String servername = "ASUS\\PANDUWICAKSONO91";
		String database = "AmazonReviewData";
		
		String url = "jdbc:sqlserver://" + servername + ":1433;DatabaseName=" + database;
		try
		{
			Class.forName(driverName);
			System.out.println("Class for name OK");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Class for name not OK");
		}
		
		// obtain data from the database
		try{
			Connection dbConn=DriverManager.getConnection(url,username,password);
			System.out.println("Connection OK\n");
			Statement st = dbConn.createStatement(); 
			
			String query = "select top 2 * from [AmazonReviewData].[dbo].[ReviewDataFiltered]";
			ResultSet rs = st.executeQuery(query);
			Review review = new Review();
			
			while(rs.next()){
				review.parseFromSQL(rs);
				System.out.println(review.toString());
				review.calculateMetrics();
			}
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.print("Connection not OK");
		}	
	}
}

