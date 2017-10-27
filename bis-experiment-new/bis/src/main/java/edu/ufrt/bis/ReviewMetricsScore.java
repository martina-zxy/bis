package edu.ufrt.bis;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class to parse the review metrics score from the database.
 *
 */
public class ReviewMetricsScore {
	
	// variables
	String asin;
	String reviewerID;
	Date reviewDate;
	double rating;
	int nbHelpful;
	int nbVotes;
	double helpfulness = 0.0;
	int reviewTextLength;
	int summaryLength;
//	double reviewTextLength;
//	double summaryLength;
	double reviewTextSpellingErrorRatio = 0.0;
	double summarySpellingErrorRatio = 0.0;
	double spellingErrRatio = 0.0;
	double reviewTextFOG = 0.0;
	double summaryFOG = 0.0;
	double reviewTextFK = 0.0;
	double summaryFK = 0.0;
	double reviewTextARI = 0.0;
	double summaryARI = 0.0;
	double reviewTextCLI = 0.0;
	double summaryCLI = 0.0;
	double polarityReviewText = 0.0;
	double polaritySummary = 0.0;
	double polarity = 0.0;
	double deviation = 0.0;
	
	/**
	 * Default constructor.
	 */
	public ReviewMetricsScore(){
		
	}
	
	/**
	 * Constructor to parse review metrics score from csv file.
	 * Not used in the final version.
	 * @param csv
	 */
	@SuppressWarnings("deprecation")
	public ReviewMetricsScore(String csv){
		String[] data = csv.split(",");
		
		this.asin = data[0].replace("\"", "");
		this.reviewerID = data[1].replace("\"", "");

		String date[] = data[2].replace("\"","").split("-");
//		System.out.println(date[0] + "-" + date[1] + "-" + date[2]);
		this.reviewDate = new Date(Integer.parseInt(date[0])-1900,
				Integer.parseInt(date[1])-1,Integer.parseInt(date[2]));
		this.rating = Double.parseDouble(data[3]);
		this.nbHelpful = Integer.parseInt(data[4]);
		this.nbVotes = Integer.parseInt(data[5]);
		this.helpfulness = Double.parseDouble(data[6]);
		this.reviewTextLength = (int)Double.parseDouble(data[7]);
		this.summaryLength = (int)Double.parseDouble(data[8]);
		this.reviewTextSpellingErrorRatio = Double.parseDouble(data[9]);
		this.summarySpellingErrorRatio = Double.parseDouble(data[10]);
		this.spellingErrRatio = Double.parseDouble(data[11]);
		this.reviewTextFOG = Double.parseDouble(data[12]);
		this.summaryFOG = Double.parseDouble(data[13]);
		this.reviewTextFK = Double.parseDouble(data[14]);
		this.summaryFK = Double.parseDouble(data[15]);
		this.reviewTextARI = Double.parseDouble(data[16]);
		this.summaryARI = Double.parseDouble(data[17]);
		this.reviewTextCLI = Double.parseDouble(data[18]);
		this.summaryCLI = Double.parseDouble(data[19]);
		this.polaritySummary = Double.parseDouble(data[20]);
		this.polaritySummary = Double.parseDouble(data[21]);
		this.polarity = Double.parseDouble(data[22]);
		this.deviation = Double.parseDouble(data[23]);
	}
	
	/**
	 * Method to parse the review metrics score from SQL.
	 * @param rs
	 */
	public void parseFromSQL(ResultSet rs){
		try {
			this.asin = rs.getString("asin");
			this.reviewerID = rs.getString("reviewerID");
			this.reviewDate = rs.getDate("reviewDate");
			this.rating = rs.getDouble("rating");
			this.nbHelpful = rs.getInt("nbHelpful");
			this.nbVotes = rs.getInt("nbVotes");
			this.helpfulness = rs.getDouble("helpfulness");
			this.reviewTextLength = rs.getInt("reviewTextLength");
			this.summaryLength = rs.getInt("summaryLength");
			this.reviewTextSpellingErrorRatio = rs.getDouble("reviewTextSpellingErrorRatio");
			this.summarySpellingErrorRatio = rs.getDouble("summarySpellingErrorRatio");
			this.spellingErrRatio = rs.getDouble("spellingErrRatio");
			this.reviewTextFOG = rs.getDouble("reviewTextFOG");
			this.summaryFOG = rs.getDouble("summaryFOG");
			this.reviewTextFK = rs.getDouble("reviewTextFK");
			this.summaryFK = rs.getDouble("summaryFK");
			this.reviewTextARI = rs.getDouble("reviewTextARI");
			this.summaryARI = rs.getDouble("summaryARI");
			this.reviewTextCLI = rs.getDouble("reviewTextCLI");
			this.summaryCLI = rs.getDouble("summaryCLI");
			this.polarityReviewText = rs.getDouble("polarityReviewText");
			this.polaritySummary = rs.getDouble("polaritySummary");
			this.polarity = rs.getDouble("polarity");
			this.deviation = rs.getDouble("deviation");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error when parsing from SQL");
		}
	}
	
	/**
	 * print information to CSV format.
	 * @return
	 */
	public String getCSV(){
		return 	//"\"" + asin + "\"" + ";" +
				//"\"" + reviewerID + "\"" + ";" +
				//reviewDate.toString() + ";" + 
				rating + "," + 
//				nbHelpful + "," +
//				nbVotes + ","+
				helpfulness + "," + 
				reviewTextLength + "," + 
				summaryLength + "," +
				reviewTextSpellingErrorRatio + "," + 
				summarySpellingErrorRatio + "," + 
//				spellingErrRatio + "," + 
				reviewTextFOG + "," + 
				summaryFOG + "," + 
				reviewTextFK + "," + 
				summaryFK + "," +
				reviewTextARI + "," +
				summaryARI + "," +
				reviewTextCLI + "," +
				summaryCLI + "," +
				polarityReviewText + "," +
				polaritySummary + "," +
//				polarity + "," +
				deviation + "\n";
	}
	
	/**
	 * Print information to CSV format, include the asin, reviewerID and reviewDate.
	 * @return
	 */
	public String getCSVFull(){
		return 	"\"" + asin + "\"" + "," +
				"\"" + reviewerID + "\"" + "," +
				"\"" + reviewDate.toString() + "\"" + "," + 
				rating + "," + 
				nbHelpful + "," +
				nbVotes + ","+
				helpfulness + "," + 
				reviewTextLength + "," + 
				summaryLength + "," +
				reviewTextSpellingErrorRatio + "," + 
				summarySpellingErrorRatio + "," + 
				spellingErrRatio + "," + 
				reviewTextFOG + "," + 
				summaryFOG + "," + 
				reviewTextFK + "," + 
				summaryFK + "," +
				reviewTextARI + "," +
				summaryARI + "," +
				reviewTextCLI + "," +
				summaryCLI + "," + 
				polarityReviewText + "," +
				polaritySummary + "," +
				polarity + "," +
				deviation + "\n";
	}
}
