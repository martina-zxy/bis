package edu.ufrt.bis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * The Main class of this program.
 * This class contains the function to calculate the metrics and put them into database.
 * For the function to obtain the review metrics score instance and put into CSV, refer to SQLToCSV class.
 *
 */
public class Main {
	// static variables due to this variables only need to be initialized once every execution.
	// the product information
	public static Product product;
	// the polarity calculator
	public static PolarityCalculator polarityCalculator;
	
	/**
	 * Main method
	 * @param args
	 */
	public static void main(String args[]){
		
		// initialize the asin score and polarity calculator
		String asinFile = "asin_info.csv";
		Main.product = new Product(asinFile);
		Main.polarityCalculator = new PolarityCalculator();
		
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
			
			// due to the high number of data, pagination of processing the data is needed.
			// to paginate, the database should be ordered
			// if the execution fails in the middle, we can continue the execution based on the last instances.
			
			int offset = 0;
			int fetch = 1000;
			int maxRow = 7899;
			
			int counter = 1;
			
			// remember when resetting, offset = counter - 1
			while(offset < maxRow){
				if((offset + fetch) > maxRow) {
					fetch = maxRow - offset;
				}
				// get the Review from the database
				String selectQuery = "select * from [AmazonReviewData].[dbo].[ReviewData2410] " + 
						"order by asin asc, reviewerID asc, unixReviewTime asc " + 
						"offset " + offset + " rows fetch next " + fetch + " rows only";
				String insertQuery = "";
				ResultSet rs = selectStatement.executeQuery(selectQuery);
				Review review = new Review();
				
				// iterate every instances, calculate the metrics, and input into review metrics score table
				while(rs.next()){
					System.out.println(counter++);
					review.parseFromSQL(rs);
//					System.out.println("Before: ");
//					System.out.println(review.toString());
					review.calculateMetrics();
//					System.out.println("After: ");
//					System.out.println(review.toString());
					insertQuery = review.getInsertIntoReviewData2410MetricsScore();
	//				System.out.println("insertQuery: " + insertQuery);
					insertStatement.execute(insertQuery);
				}
				offset += fetch;
			}
			
		} catch(Exception e)
		{
			e.printStackTrace();
			System.out.print("Connection not OK");
		}	
	}
}

