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
//			dbConn.setAutoCommit(false);
			
			Statement selectStatement = dbConn.createStatement();
			Statement insertStatement = dbConn.createStatement();
			
			int offset = 0;
			int fetch = 1000;
//			int maxRow = 34;
			int maxRow = 1160195;
			
			int counter = 1;
			
			// remember when resetting, offset = counter - 1
			
			while(offset < maxRow){
				if((offset + fetch) > maxRow) {
					fetch = maxRow - offset;
				}
				String selectQuery = "select * from [AmazonReviewData].[dbo].[ReviewDataFiltered3050] " + 
						"order by asin asc, reviewerID asc, unixReviewTime asc " + 
						"offset " + offset + " rows fetch next " + fetch + " rows only";
				String insertQuery = "";
				ResultSet rs = selectStatement.executeQuery(selectQuery);
				Review review = new Review();
				
				while(rs.next()){
					System.out.println(counter++);
					review.parseFromSQL(rs);
	//				System.out.println("Before: ");
	//				System.out.println(review.toString());
					review.calculateMetrics();
//					System.out.println("After: ");
//					System.out.println(review.toString());
					insertQuery = review.getInsertIntoReviewDataFiltered3050MetricsScore();
	//				System.out.println("insertQuery: " + insertQuery);
					insertStatement.execute(insertQuery);
				}
				offset += fetch;
			}
			
//			String selectQuery = "select top 100000 * from [AmazonReviewData].[dbo].[ReviewDataFiltered3050]";
//			String insertQuery = "";
//			ResultSet rs = selectStatement.executeQuery(selectQuery);
//			Review review = new Review();
//			
//			while(rs.next()){
//				review.parseFromSQL(rs);
//				review.calculateMetrics();
//				System.out.println(review.toString());
//				insertQuery = review.getInsertIntoReviewDataFiltered3050MetricsScore();
////				System.out.println("insertQuery: " + insertQuery);
//				insertStatement.execute(insertQuery);
//			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.print("Connection not OK");
		}	
	}
}

