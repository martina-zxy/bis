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

/**
 * This class is used to extract the review metrics score data from SQL to CSV.
 * Includes the iteration of filtering method based on the number of votes and number of reviews.
 */
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
			Connection dbConn = DriverManager.getConnection(url,username,password);
			System.out.println("Connection OK\n");
//			dbConn.setAutoCommit(false);
			
			Statement selectStatement = dbConn.createStatement();
			int index[] = {522801,29808,10971,3414,918};
			
			for(int ii = 0; ii < index.length; ii++)
			{
//				for (int jj = 10; jj <= 15; jj += 5){
					//int minVotes = index[ii]; // 522,801 to 918
//					int minNbReviews = jj;
					
					String minVotes = "";
					
					if(ii == 0)
						minVotes = "";
					else if(ii == 1)
						minVotes = "29808";
					else if(ii == 2)
						minVotes = "10971";
					else if(ii == 3)
						minVotes = "3414";
					else if(ii == 4)
						minVotes = "918";
					
					System.out.println("printing minVotes: " + minVotes);
					
					// write arff header
					String fileName = "review_metrics_score_new - minVotes " + minVotes  + ".arff";
//							+ " minNbReviews " + minNbReviews + ".arff";
					
					File fout = new File(fileName);
					FileOutputStream fos = new FileOutputStream(fout);
				 
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
					
					printARFFHeader(bw);
					
					int counter = 1;
					
					// form the SQL query
					String selectQuery = "select * from [AmazonReviewData].[dbo].[ReviewDataFiltered3050MetricsScoreNew" + minVotes + "]"; 
					
//					String whereQuery = "where rr.nbVotes> " + minVotes + " and reviewerID in (";
//					String subquerySelect = "select reviewerID from [AmazonReviewData].[dbo].[ReviewDataFiltered3050MetricsScore] as r ";
//					String subqueryWhere = "where r.nbvotes> " + minVotes + " group by reviewerid having count(*)>" + minNbReviews + ")";
//					
//					String fullQuery = selectQuery + whereQuery + subquerySelect + subqueryWhere;
					
					// obtain the data from SQL using the query
//					ResultSet rs = selectStatement.executeQuery(fullQuery);
					ResultSet rs = selectStatement.executeQuery(selectQuery);
					ReviewMetricsScore reviewMetricsScore = new ReviewMetricsScore();
			
					// iterate through the instances and write into CSV files.
					while(rs.next()){
						System.out.println(counter++);
						reviewMetricsScore.parseFromSQL(rs);
						bw.write(reviewMetricsScore.getCSV());
					}	
					
					bw.close();
					
					System.out.println("printing minVotes: " + minVotes + " success");
//				}
			}
		} catch(Exception e){
			
		}
		
	}
	
	/**
	 * Method to print the ARFF Header (weka files).
	 * @param bw
	 * @throws IOException
	 */
	private static void printARFFHeader(BufferedWriter bw) throws IOException{
		bw.write("@RELATION review_metrics_score\n\n");
		bw.write("@ATTRIBUTE rating REAL\n");
//		bw.write("@ATTRIBUTE rating_squared REAL\n");
//		bw.write("@ATTRIBUTE nbHelpful REAL\n");
//		bw.write("@ATTRIBUTE nbVotes REAL\n");
		bw.write("@ATTRIBUTE helpfulness REAL\n");
		bw.write("@ATTRIBUTE reviewTextLength REAL\n");
		bw.write("@ATTRIBUTE summaryLength REAL\n");
		bw.write("@ATTRIBUTE reviewTextSpellingErrRatio REAL\n");
		bw.write("@ATTRIBUTE summarySpellingErrRatio REAL\n");
//		bw.write("@ATTRIBUTE spellingErrRatio REAL\n");
		bw.write("@ATTRIBUTE reviewTextFOG REAL\n");
		bw.write("@ATTRIBUTE summaryFOG REAL\n");
		bw.write("@ATTRIBUTE reviewTextFK REAL\n");
		bw.write("@ATTRIBUTE summaryFK REAL\n");
		bw.write("@ATTRIBUTE reviewTextARI REAL\n");
		bw.write("@ATTRIBUTE summaryARI REAL\n");
		bw.write("@ATTRIBUTE reviewTextCLI REAL\n");
		bw.write("@ATTRIBUTE summaryCLI REAL\n");
		bw.write("@ATTRIBUTE polarityReviewText REAL\n");
		bw.write("@ATTRIBUTE polaritySummary REAL\n");
//		bw.write("@ATTRIBUTE polarity REAL\n");
		bw.write("@ATTRIBUTE deviation REAL\n\n");
		bw.write("@DATA\n");
	}
	
	/**
	 * Method to print the ARFF Header (weka files).
	 * Include the asin, reviewerID and reviewDate.
	 * This variables cannot be used in weka, hence need to be removed before building the model.
	 * @param bw
	 * @throws IOException
	 */
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
		bw.write("@ATTRIBUTE reviewTextSpellingErrRatio REAL\n");
		bw.write("@ATTRIBUTE summarySpellingErrRatio REAL\n");
		bw.write("@ATTRIBUTE spellingErrRatio REAL\n");
		bw.write("@ATTRIBUTE reviewTextFOG REAL\n");
		bw.write("@ATTRIBUTE summaryFOG REAL\n");
		bw.write("@ATTRIBUTE reviewTextFK REAL\n");
		bw.write("@ATTRIBUTE summaryFK REAL\n");
		bw.write("@ATTRIBUTE reviewTextARI REAL\n");
		bw.write("@ATTRIBUTE summaryARI REAL\n");
		bw.write("@ATTRIBUTE reviewTextCLI REAL\n");
		bw.write("@ATTRIBUTE summaryCLI REAL\n");
		bw.write("@ATTRIBUTE polarityReviewText REAL\n");
		bw.write("@ATTRIBUTE polaritySummary REAL\n");
		bw.write("@ATTRIBUTE polarity REAL\n");
		bw.write("@ATTRIBUTE deviation REAL\n\n");
		bw.write("@DATA\n");
	}
}
		
