package edu.ufrt.bis;

/**
 * This class represents a review.
 */

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.languagetool.*;
import org.languagetool.language.BritishEnglish;
import org.languagetool.language.English;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;

import com.ipeirotis.readability.*;

public class Review {
	
	// database variables
	String reviewerID; // the ID of a reviewer
	String asin; // the asin of the product
	String reviewerName; // reviewer name
	int[] helpful = {0,0}; // 0 index = number of helpful votes; 1 index = number of votes
	String reviewText; // the text of the review
	double overall; // the rating of the review
	String summary; // the text of the summary
	int unixReviewTime; // the time of the review in Unix format
	String reviewTime; // the time of the review in (MM DD, YYYY) format
	Date reviewDate; // the time of the review in Date format
	
	// derived variables
	int reviewTextLength; // the length of the review text and summary text
	int summaryLength;
	
	// variables to calculate the spelling error metrics score
	// in the final version, only the combined review text and summary text is used (spellingErrorRatio)
	int reviewTextSpellingError = 0;
	int summarySpellingError = 0;
	
	double reviewTextSpellingErrorRatio = 0.0;
	double summarySpellingErrorRatio = 0.0;
	
	double spellingErrorRatio = 0.0;
	
	// the Readability variable used to calculate the readability index
	Readability readabilityReviewText;
	Readability readabilitySummary;
	
	// the readability index score
	double reviewTextFOG = 0.0;
	double summaryFOG = 0.0;

	double reviewTextFK = 0.0;
	double summaryFK = 0.0;
	
	double reviewTextARI = 0.0;
	double summaryARI = 0.0;
	
	double reviewTextCLI = 0.0;
	double summaryCLI = 0.0;
	
	// the polarity and deviation metrics score
	double polarityReviewText = 0.0;
	double polaritySummary = 0.0;
	double polarity = 0.0;
	double deviation = 0.0;
	
	public Review(){
		
	}
	
	/**
	 * toString method to gain information of the Review instances.
	 * Mainly used for debugging purpose.
	 */
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
				"    summaryLength: " + summaryLength + "\n" +
				"    reviewTextSpellingError: " + reviewTextSpellingError + "\n" +
				"    summarySpellingError: " + summarySpellingError + "\n" +
				"    spellingErrorRatio: " + spellingErrorRatio + "\n" + 
				"    reviewTextFOG: " + reviewTextFOG + "\n" + 
				"    summaryFOG: " + summaryFOG + "\n" + 
				"    reviewTextFK: " + reviewTextFK + "\n" +
				"    summaryFK: " + summaryFK + "\n" +
				"    reviewTextARI: " + reviewTextARI + "\n" +
				"    summaryARI: " + summaryARI + "\n" +
				"    reviewTextCLI: " + reviewTextCLI + "\n" + 
				"    summaryCLI: " + summaryCLI + "\n" +
				"    polarityReviewText: " + polarityReviewText + "\n" +
				"    polaritySummary: " + polaritySummary + "\n" +
				"    polarity: " + polarity + "\n" + 
				"    deviation: " + deviation + "\n"
				;
	}
	
	/**
	 * preprocess method to make the data applicable in Java.
	 * required because the raw data contains some dirtiness.
	 */
	public void preprocessed(){
		this.reviewerName = reviewerName.replace("'", "''");
		this.reviewText = reviewText.replace("'", "''");
		this.summary = summary.replace("'", "''");
		this.reviewTime = reviewTime.replace("'", "''");
	}
	
	/**
	 * method to parse the Review from the SQL.
	 * Used in conjunction with the SQL library.
	 * Mandatory due to the iteration of every element returned from the SQL query.
	 * This class is used in the main method in the Main class.
	 * @param rs
	 */
	public void parseFromSQL(ResultSet rs){
		try {
			// get the variables from the database
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
			
			//reset derived variable
			this.reviewTextLength = 0;
			this.summaryLength = 0;
			
			this.reviewTextARI = 0.0;
			this.summaryARI = 0.0;
			
			this.reviewTextSpellingError = 0;
			this.summarySpellingError = 0;
			
			this.reviewTextSpellingErrorRatio = 0.0;
			this.summarySpellingErrorRatio = 0.0;
			
			this.readabilityReviewText = null;
			this.readabilitySummary = null;
			
			this.reviewTextFOG = 0.0;
			this.summaryFOG = 0.0;
			
			this.reviewTextFK = 0.0;
			this.summaryFK = 0.0;
			
			this.reviewTextARI = 0.0;
			this.summaryARI = 0.0;
			
			this.reviewTextCLI = 0.0;
			this.summaryCLI = 0.0;
			
			this.polarityReviewText = 0.0;
			this.polaritySummary = 0.0;
			this.polarity = 0.0;
			this.deviation = 0.0;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error when parsing from SQL");
		}
	}
	
	/**
	 * Method to calculate the metrics score.
	 */
	public void calculateMetrics(){
		calculateLength();
		calculateSpellingError();
		calculateAllReadability();
		calculatePolarityAndDeviation();
	}
	
	/**
	 * Method to calculate the length.
	 */
	private void calculateLength(){
		this.reviewTextLength = reviewText.length();
		this.summaryLength = summary.length();
	}
	
	/**
	 * Method to calculate the spelling error.
	 * Use JLanguageTool to calculate the spelling error.
	 */
	private void calculateSpellingError(){
		try {
			JLanguageTool langTool;
			langTool = new JLanguageTool(new BritishEnglish());
			
			List<RuleMatch> matches;
			
			matches = langTool.check(reviewText);
			
			for (RuleMatch match : matches) {
				/**
				 * Everytime the JLanguagetTool find some potential spelling error, count.
				 * We can also print out the possible spelling error, however this is not needed.
				 */
//			  System.out.println("Potential typo at characters " +
//			      match.getFromPos() + "-" + match.getToPos() + ": " +
//			      match.getMessage());
//			  System.out.println("Suggested correction(s): " +
//			      match.getSuggestedReplacements());
			  this.reviewTextSpellingError++;
			}
			
			// calculate the review text spelling error ratio
			// in the final version, this variable is not used since the used one is the combination of
			// review text and summary text
			if(reviewTextLength != 0 ){
				this.reviewTextSpellingErrorRatio = (double)reviewTextSpellingError / (double) reviewTextLength;
			} else {
				this.reviewTextSpellingErrorRatio = 0;
			}
			
			// for debugging purpose
//			System.out.println("reviewTextSpellingError: " + reviewTextSpellingError);
//			System.out.println("reviewTextLength: " + reviewTextLength);
//			System.out.println("reviewTextSpellingErrorRatio: " + reviewTextSpellingErrorRatio);
			
			/**
			 * Applies the same method for the summary.
			 */
			matches = langTool.check(summary);
			
			for (RuleMatch match : matches) {
//			  System.out.println("Potential typo at characters " +
//			      match.getFromPos() + "-" + match.getToPos() + ": " +
//			      match.getMessage());
//			  System.out.println("Suggested correction(s): " +
//			      match.getSuggestedReplacements());
			  this.summarySpellingError++;
			}
			
			if(summaryLength != 0){
				this.summarySpellingErrorRatio = (double)summarySpellingError / (double) summaryLength;
			} else {
				this.summarySpellingErrorRatio = 0;
			}
			
			// for debugging purpose
//			System.out.println("summarySpellingError: " + summarySpellingError);
//			System.out.println("summaryLength: " + summaryLength);
//			System.out.println("summarySpellingErrorRatio: " + summarySpellingErrorRatio);
			
			/**
			 * Calculate the spelling error ratio.
			 * This is the final variable that were used.
			 */
			if((reviewTextLength + summaryLength) != 0){
				this.spellingErrorRatio = ((double)reviewTextSpellingError + (double)summarySpellingError) / 
										((double)reviewTextLength + (double)summaryLength);
			} else{
				this.spellingErrorRatio = 0.0;
			}
			
			// for debugging purpose
//			System.out.println("spellingErrorRatio: " + spellingErrorRatio);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to calculate all readability index.
	 */
	private void calculateAllReadability(){
		
		// initialize the readability variable
		this.readabilityReviewText = new Readability(reviewText);
		this.readabilitySummary = new Readability(summary);
		
		// calculate the readability index by utilizing available library.
		// mandatory to put try and catch exception.
		try{
			this.reviewTextFOG = readabilityReviewText.getGunningFog();
		} catch(Exception e){
			this.reviewTextFOG = 0.0;
		}
		
		try{
			this.summaryFOG = readabilitySummary.getGunningFog();
		} catch(Exception e){
			this.summaryFOG = 0.0;
		}
		
		try{
			this.reviewTextFK = readabilityReviewText.getFleschReadingEase();
		} catch (Exception e){
			this.reviewTextFK = 0.0;
		}
		
		try{
			this.summaryFK = readabilitySummary.getFleschReadingEase();
		} catch (Exception e){
			this.summaryFK = 0.0;
		}
		
		try{
			this.reviewTextARI = readabilityReviewText.getARI();
		} catch(Exception e){
			this.reviewTextARI = 0.0;
		}
		
		try{
			this.summaryARI = readabilitySummary.getARI();
		} catch (Exception e){
			this.summaryARI = 0.0;
		}
		
		try{
			this.reviewTextCLI = readabilityReviewText.getColemanLiau();
		} catch (Exception e){
			this.reviewTextCLI = 0.0;
		}
		
		try{
			this.summaryCLI = readabilitySummary.getColemanLiau();
		} catch (Exception e){
			this.summaryCLI = 0.0;
		}
		
		// for debugging purpose
//		System.out.println("FOG Index");
//		System.out.println("reviewTextFOG: " + reviewTextFOG);
//		System.out.println("summaryFOG: " + summaryFOG);
//		
//		System.out.println("FK Index");
//		System.out.println("reviewTextFK: " + reviewTextFK);
//		System.out.println("summaryFK: " + summaryFK);
//		
//		System.out.println("ARI Index");
//		System.out.println("reviewTextARI: " + reviewTextARI);
//		System.out.println("summaryARI: " + summaryARI);
//		
//		System.out.println("CLI Index");
//		System.out.println("reviewTextCLI: " + reviewTextCLI);
//		System.out.println("summaryCLI: " + summaryCLI);
	}
	
	/**
	 * Method to calculate the polarity and deviation metrics
	 */
	private void calculatePolarityAndDeviation(){
		// individual calculation
		this.polarityReviewText = Main.polarityCalculator.getParagraphScore(this.reviewText);
		this.polaritySummary = Main.polarityCalculator.getParagraphScore(this.summary);
		
		// combine the review text and summary text
		String text = this.reviewText + ". " + this.summary;
		// calculate the polarity
		this.polarity = Main.polarityCalculator.getParagraphScore(text);
		
		// calculate the deviation
		double temp = overall - new Float(Main.product.asinAvgScore.get(asin));
		this.deviation = Math.abs(temp);
	}
	
	/**
	 * Method to form the SQL statement to insert the metrics score into database.
	 * @return SQL statement
	 */
	public String getInsertIntoReviewDataFiltered3050MetricsScore(){
		String insertIntoText = "INSERT INTO [AmazonReviewData].[dbo].[ReviewDataFiltered3050MetricsScore] ([asin],[reviewerID],[reviewDate],[rating],[nbHelpful],[nbVotes],[helpfulness],[reviewTextLength],[summaryLength],[spellingErrRatio],[reviewTextFOG],[summaryFOG],[reviewTextFK],[summaryFK],[reviewTextARI],[summaryARI],[reviewTextCLI],[summaryCLI],[polarity],[deviation])";
		
		// calculate the helpfulness ratio beforehand
		double helpfulness = 0.0;
		if(helpful[1] != 0 ){ 
			helpfulness = (double) helpful[0] / (double) helpful[1];
		} else {
			helpfulness = 0.0;
		}
		
		// form the SQL query
		String valueText = "VALUES ('" + asin + "','" + reviewerID + "','" +
		reviewDate + "'," + overall + "," + helpful[0] + "," + helpful[1] + "," +  
		helpfulness + "," + reviewTextLength + "," + summaryLength + "," +
		spellingErrorRatio + "," + 
		reviewTextFOG + "," + summaryFOG + "," + reviewTextFK + "," + 
		summaryFK + "," + reviewTextARI + "," + summaryARI + "," + 
		reviewTextCLI + "," + summaryCLI + "," + polarity + "," + deviation + ");";
		
		return insertIntoText + valueText;	
	}
	
	/**
	 * Method to form the SQL statement to insert the metrics score into database.
	 * @return SQL statement
	 */
	public String getInsertIntoReviewData1110MetricsScore(){
		String insertIntoText = "INSERT INTO [AmazonReviewData].[dbo].[ReviewData1110MetricsScore] ([asin],[reviewerID],[reviewDate],[rating],[nbHelpful],[nbVotes],[helpfulness],[reviewTextLength],[summaryLength],[reviewTextSpellingErrorRatio],[summarySpellingErrorRatio],[spellingErrRatio],[reviewTextFOG],[summaryFOG],[reviewTextFK],[summaryFK],[reviewTextARI],[summaryARI],[reviewTextCLI],[summaryCLI],[polarityReviewText],[polaritySummary],[polarity],[deviation])";
		
		// calculate the helpfulness ratio beforehand
		double helpfulness = 0.0;
		if(helpful[1] != 0 ){ 
			helpfulness = (double) helpful[0] / (double) helpful[1];
		} else {
			helpfulness = 0.0;
		}
		
		// form the SQL query
		String valueText = "VALUES ('" + asin + "','" + reviewerID + "','" +
		reviewDate + "'," + overall + "," + helpful[0] + "," + helpful[1] + "," +  
		helpfulness + "," + reviewTextLength + "," + summaryLength + "," +
		reviewTextSpellingErrorRatio + "," + summarySpellingErrorRatio + "," + spellingErrorRatio + "," + 
		reviewTextFOG + "," + summaryFOG + "," + reviewTextFK + "," + 
		summaryFK + "," + reviewTextARI + "," + summaryARI + "," + 
		reviewTextCLI + "," + summaryCLI + "," + 
		polarityReviewText + "," + polaritySummary + "," + polarity + "," + 
		deviation + ");";
		
		return insertIntoText + valueText;	
	}
	
	/**
	 * Method to form the SQL statement to insert the metrics score into database.
	 * @return SQL statement
	 */
	public String getInsertIntoReviewData2410MetricsScore(){
		String insertIntoText = "INSERT INTO [AmazonReviewData].[dbo].[ReviewData2410MetricsScore] ([asin],[reviewerID],[reviewDate],[rating],[nbHelpful],[nbVotes],[helpfulness],[reviewTextLength],[summaryLength],[reviewTextSpellingErrorRatio],[summarySpellingErrorRatio],[spellingErrRatio],[reviewTextFOG],[summaryFOG],[reviewTextFK],[summaryFK],[reviewTextARI],[summaryARI],[reviewTextCLI],[summaryCLI],[polarityReviewText],[polaritySummary],[polarity],[deviation])";
		
		// calculate the helpfulness ratio beforehand
		double helpfulness = 0.0;
		if(helpful[1] != 0 ){ 
			helpfulness = (double) helpful[0] / (double) helpful[1];
		} else {
			helpfulness = 0.0;
		}
		
		// form the SQL query
		String valueText = "VALUES ('" + asin + "','" + reviewerID + "','" +
		reviewDate + "'," + overall + "," + helpful[0] + "," + helpful[1] + "," +  
		helpfulness + "," + reviewTextLength + "," + summaryLength + "," +
		reviewTextSpellingErrorRatio + "," + summarySpellingErrorRatio + "," + spellingErrorRatio + "," + 
		reviewTextFOG + "," + summaryFOG + "," + reviewTextFK + "," + 
		summaryFK + "," + reviewTextARI + "," + summaryARI + "," + 
		reviewTextCLI + "," + summaryCLI + "," + 
		polarityReviewText + "," + polaritySummary + "," + polarity + "," + 
		deviation + ");";
		
		return insertIntoText + valueText;	
	}
}
