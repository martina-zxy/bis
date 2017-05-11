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
			dbConn.setAutoCommit(false);
			
			Statement selectStatement = dbConn.createStatement();
//			Statement insertStatement = dbConn.createStatement();
			
			String selectQuery = "select top 5 * from [AmazonReviewData].[dbo].[ReviewDataFiltered]";
			String insertQuery = "";
			ResultSet rs = selectStatement.executeQuery(selectQuery);
			Review review = new Review();
			
			while(rs.next()){
				review.parseFromSQL(rs);
				review.calculateMetrics();
				System.out.println(review.toString());
				insertQuery = review.getInsertIntoReviewDataFiltered3050MetricsScore();
//				insertStatement.executeQuery(insertQuery);
			}
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.print("Connection not OK");
		}	
	}
}

