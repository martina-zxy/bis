package edu.ufrt.bis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLToCSV {
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
			
			// write arff header
			File fout = new File("review_metrics_score.arff");
			FileOutputStream fos = new FileOutputStream(fout);
		 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			printARFFHeaderFull(bw);
			
			int counter = 1;

			String selectQuery = "select * from [AmazonReviewData].[dbo].[ReviewDataFiltered3050MetricsScore492996Test] "; 
			
			ResultSet rs = selectStatement.executeQuery(selectQuery);
			ReviewMetricsScore reviewMetricsScore = new ReviewMetricsScore();
	
			while(rs.next()){
				System.out.println(counter++);
				reviewMetricsScore.parseFromSQL(rs);
				bw.write(reviewMetricsScore.getCSVFull());
			}
				
			
			bw.close();
		} catch(Exception e){
			
		}
		
	}
	
	private static void printARFFHeader(BufferedWriter bw) throws IOException{
		bw.write("@RELATION review_metrics_score\n\n");
		bw.write("@ATTRIBUTE rating REAL\n");
		bw.write("@ATTRIBUTE rating_squared REAL\n");
		bw.write("@ATTRIBUTE nbHelpful REAL\n");
		bw.write("@ATTRIBUTE nbVotes REAL\n");
		bw.write("@ATTRIBUTE helpfulness REAL\n");
		bw.write("@ATTRIBUTE reviewTextLength REAL\n");
		bw.write("@ATTRIBUTE summaryLength REAL\n");
		bw.write("@ATTRIBUTE spellingErrRatio REAL\n");
		bw.write("@ATTRIBUTE reviewTextFOG REAL\n");
		bw.write("@ATTRIBUTE summaryFOG REAL\n");
		bw.write("@ATTRIBUTE reviewTextFK REAL\n");
		bw.write("@ATTRIBUTE summaryFK REAL\n");
		bw.write("@ATTRIBUTE reviewTextARI REAL\n");
		bw.write("@ATTRIBUTE summaryARI REAL\n");
		bw.write("@ATTRIBUTE reviewTextCLI REAL\n");
		bw.write("@ATTRIBUTE summaryCLI REAL\n");
		bw.write("@ATTRIBUTE polarity REAL\n");
		bw.write("@ATTRIBUTE deviation REAL\n\n");
		bw.write("@DATA\n");
	}
	
	private static void printARFFHeaderFull(BufferedWriter bw) throws IOException{
		bw.write("@RELATION review_metrics_score\n\n");
		bw.write("@ATTRIBUTE asin string\n");
		bw.write("@ATTRIBUTE reviewerID string\n");
		bw.write("@ATTRIBUTE reviewDate string\n");
		bw.write("@ATTRIBUTE rating REAL\n");
		bw.write("@ATTRIBUTE nbHelpful REAL\n");
		bw.write("@ATTRIBUTE nbVotes REAL\n");
		bw.write("@ATTRIBUTE helpfulness REAL\n");
		bw.write("@ATTRIBUTE reviewTextLength REAL\n");
		bw.write("@ATTRIBUTE summaryLength REAL\n");
		bw.write("@ATTRIBUTE spellingErrRatio REAL\n");
		bw.write("@ATTRIBUTE reviewTextFOG REAL\n");
		bw.write("@ATTRIBUTE summaryFOG REAL\n");
		bw.write("@ATTRIBUTE reviewTextFK REAL\n");
		bw.write("@ATTRIBUTE summaryFK REAL\n");
		bw.write("@ATTRIBUTE reviewTextARI REAL\n");
		bw.write("@ATTRIBUTE summaryARI REAL\n");
		bw.write("@ATTRIBUTE reviewTextCLI REAL\n");
		bw.write("@ATTRIBUTE summaryCLI REAL\n");
		bw.write("@ATTRIBUTE polarity REAL\n");
		bw.write("@ATTRIBUTE deviation REAL\n\n");
		bw.write("@DATA\n");
	}
}
		
