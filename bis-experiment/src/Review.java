import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Review {
	
	// database variables
	String reviewerID;
	String asin;
	String reviewerName;
	int[] helpful = {0,0};
	String reviewText;
	double overall;
	String summary;
	int unixReviewTime;
	String reviewTime;
	Date reviewDate;
	
	// derived variables
	int reviewTextLength;
	int summaryLength;
	
	public Review(){
		
	}
	
	public String toString() {
		return  "Database Variables" + "\n" +
				"    reviewerID: " + reviewerID + "\n" +
				"    asin: " + asin + "\n" +
				"    reviewerName: " + reviewerName + "\n" +
				"    helpful: " + "[" + helpful[0] + "," + helpful[1] + "]" + "\n" +
				"    reviewText: " + reviewText + "\n" +
				"    overall: " + overall + "\n" +
				"    summary: " + summary + "\n" +
				"    unixReviewTime: " + unixReviewTime + "\n" +
				"    reviewTime: " + reviewTime + "\n" +
				"    reviewDate: " + reviewDate + "\n" +
				"Derived Variables" + "\n" +
				"    reviewTextLength: " + reviewTextLength + "\n" +
				"    summaryLength: " + summaryLength + "\n"
				;
	}
	
	public void preprocessed(){
		this.reviewerName = reviewerName.replace("'", "''");
		this.reviewText = reviewText.replace("'", "''");
		this.summary = summary.replace("'", "''");
		this.reviewTime = reviewTime.replace("'", "''");
	}
	
	public String getInsertInto(){
		String insertIntoText = "INSERT INTO [AmazonReviewData].[dbo].[ReviewDataTest] ([asin], [reviewerID], [unixReviewTime], [reviewerName],[nbHelpful],[nbVotes],[reviewText],[overall],[summary],[reviewTime],[reviewDate])";
		// example
		//VALUES ('0000000116', 'AH2L9G3DQHHAJ', 1019865600, 'chris', 5, 5, 'XXX.', 
		//4.0, 'Show me the money!','04 27, 2002');
		
		preprocessed();
		
		String reviewDate;
		
		if(reviewTime.length() == 11) {
			reviewDate = reviewTime.substring(7, 11) + reviewTime.substring(0, 2) + reviewTime.substring(3, 5);
		} else {
			reviewDate = reviewTime.substring(6, 10) + reviewTime.substring(0, 2) + "0"+reviewTime.substring(3, 4);
		}
//		System.out.println(reviewDate);
		
		String valueText = " VALUES ('" + asin + "','" + reviewerID + "'," + unixReviewTime + ",'" + reviewerName + "'," + 
				helpful[0] + "," + helpful[1] + ",'" + reviewText + "'," + overall + ",'" + summary + "','" + 
				reviewTime + "','" + reviewDate + "');";
		return insertIntoText + valueText;
	}
	
	public void parseFromSQL(ResultSet rs){
		try {
			this.reviewerID = rs.getString("reviewerID");
			this.asin = rs.getString("asin");
			this.reviewerName = rs.getString("reviewerName");
			this.helpful[0] = rs.getInt("nbHelpful");
			this.helpful[1] = rs.getInt("nbVotes");
			this.reviewText = rs.getString("reviewText");
			this.overall = rs.getDouble("overall");
			this.summary = rs.getString("summary");
			this.unixReviewTime = rs.getInt("unixReviewTime");
			this.reviewTime = rs.getString("reviewTime");
			this.reviewDate = rs.getDate("reviewDate");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error when parsing from SQL");
		}
	}
	
	
	public void calculateMetrics(){
		calculateLength();
	}
	
	private void calculateLength(){
		this.reviewTextLength = reviewText.length();
		this.summaryLength = summary.length();
	}
	
}
