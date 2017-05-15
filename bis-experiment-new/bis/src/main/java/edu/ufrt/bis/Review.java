package edu.ufrt.bis;

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
	
//	String[] reviewTextSentences;
//	String[] summarySentences;
	
//	String[] reviewTextWords;
//	String[] summaryWords;
	
//	int[] reviewTextWordsNbSyllables;
//	int reviewTextWordsNbSyllablesLen;
//	int[] summaryWordsNbSyllables;
//	int summaryWordsNbSyllablesLen;
	
//	int reviewTextNbComplexWords;
//	int summaryNbComplexWords;
	
	int reviewTextSpellingError = 0;
	int summarySpellingError = 0;
	
	double reviewTextSpellingErrorRatio = 0.0;
	double summarySpellingErrorRatio = 0.0;
	
	double spellingErrorRatio = 0.0;
	
	Readability readabilityReviewText;
	Readability readabilitySummary;
	
	double reviewTextFOG = 0.0;
	double summaryFOG = 0.0;

	double reviewTextFK = 0.0;
	double summaryFK = 0.0;
	
	double reviewTextARI = 0.0;
	double summaryARI = 0.0;
	
	double reviewTextCLI = 0.0;
	double summaryCLI = 0.0;
	
	double polarity = 0.0;
	double deviation = 0.0;
	
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
				"    polarity: " + polarity + "\n" + 
				"    deviation: " + deviation + "\n"
				;
	}
	
	public void preprocessed(){
		this.reviewerName = reviewerName.replace("'", "''");
		this.reviewText = reviewText.replace("'", "''");
		this.summary = summary.replace("'", "''");
		this.reviewTime = reviewTime.replace("'", "''");
	}
	
	public String getInsertIntoReviewDataTest(){
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
	
	public String getInsertIntoReviewDataFiltered3050MetricsScore(){
		String insertIntoText = "INSERT INTO [AmazonReviewData].[dbo].[ReviewDataFiltered3050MetricsScore] ([asin],[reviewerID],[reviewDate],[rating],[nbHelpful],[nbVotes],[helpfulness],[reviewTextLength],[summaryLength],[spellingErrRatio],[reviewTextFOG],[summaryFOG],[reviewTextFK],[summaryFK],[reviewTextARI],[summaryARI],[reviewTextCLI],[summaryCLI],[polarity],[deviation])";
		
		double helpfulness = 0.0;
		if(helpful[1] != 0 ){ 
			helpfulness = (double) helpful[0] / (double) helpful[1];
		} else {
			helpfulness = 0.0;
		}
		
		String valueText = "VALUES ('" + asin + "','" + reviewerID + "','" +
		reviewDate + "'," + overall + "," + helpful[0] + "," + helpful[1] + "," +  
		helpfulness + "," + reviewTextLength + "," + summaryLength + "," +
		spellingErrorRatio + "," + 
		reviewTextFOG + "," + summaryFOG + "," + reviewTextFK + "," + 
		summaryFK + "," + reviewTextARI + "," + summaryARI + "," + 
		reviewTextCLI + "," + summaryCLI + "," + polarity + "," + deviation + ");";
		
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
			
			//reset derived variable
			this.reviewTextLength = 0;
			this.summaryLength = 0;
			
//			this.reviewTextSentences = null;
//			this.summarySentences = null;
//			
//			this.reviewTextWords = null;
//			this.summaryWords = null;
			
//			this.reviewTextWordsNbSyllables = null;
//			this.reviewTextWordsNbSyllablesLen = 0;
//			this.summaryWordsNbSyllables = null;
//			this.summaryWordsNbSyllablesLen = 0;
//			
//			this.reviewTextNbComplexWords = 0;
//			this.summaryNbComplexWords = 0;
			
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
			
			this.polarity = 0.0;
			this.deviation = 0.0;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error when parsing from SQL");
		}
	}
	
	
	public void calculateMetrics(){
		calculateLength();
//		calculateARI();
		calculateSpellingError();
//		calculateSyllable();
//		calculateComplexWords();
//		calculateFOG();
		calculateAllReadability();
		calculatePolarityAndDeviation();
	}
	
	private void calculateLength(){
		this.reviewTextLength = reviewText.length();
		this.summaryLength = summary.length();
	}
	
	private void calculateSpellingError(){
		
//		this.reviewTextSentences = reviewText.split("[!?\\.]+");
//		this.summarySentences = summary.split("[!?\\.]+");
//		
//		this.reviewTextWords = reviewText.split("[&\"?!;,\\s\\.]+");
//		this.summaryWords = summary.split("[&\"?!;,\\s\\.]+");
		
		try {
			JLanguageTool langTool;
			langTool = new JLanguageTool(new BritishEnglish());
			
			List<RuleMatch> matches;
			
			matches = langTool.check(reviewText);
			
			for (RuleMatch match : matches) {
//			  System.out.println("Potential typo at characters " +
//			      match.getFromPos() + "-" + match.getToPos() + ": " +
//			      match.getMessage());
//			  System.out.println("Suggested correction(s): " +
//			      match.getSuggestedReplacements());
			  this.reviewTextSpellingError++;
			}
			
			if(reviewTextLength != 0 ){
				this.reviewTextSpellingErrorRatio = (double)reviewTextSpellingError / (double) reviewTextLength;
			} else {
				this.reviewTextSpellingErrorRatio = 0;
			}
			
//			System.out.println("reviewTextSpellingError: " + reviewTextSpellingError);
//			System.out.println("reviewTextLength: " + reviewTextLength);
//			System.out.println("reviewTextSpellingErrorRatio: " + reviewTextSpellingErrorRatio);
			
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
			
//			System.out.println("summarySpellingError: " + summarySpellingError);
//			System.out.println("summaryLength: " + summaryLength);
//			System.out.println("summarySpellingErrorRatio: " + summarySpellingErrorRatio);
			
			if((reviewTextLength + summaryLength) != 0){
				this.spellingErrorRatio = ((double)reviewTextSpellingError + (double)summarySpellingError) / 
										((double)reviewTextLength + (double)summaryLength);
			} else{
				this.spellingErrorRatio = 0.0;
			}
			
//			System.out.println("spellingErrorRatio: " + spellingErrorRatio);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void calculateAllReadability(){
		
		this.readabilityReviewText = new Readability(reviewText);
		this.readabilitySummary = new Readability(summary);
		
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
	
	private void calculatePolarityAndDeviation(){
		String text = this.reviewText + ". " + summary;
		this.polarity = Main.polarityCalculator.getParagraphScore(text);
		
		double temp = overall - new Float(Main.product.asinAvgScore.get(asin));
		
		this.deviation = Math.abs(temp);
	}
	
	// Unused
//	private void calculateARI(){
//		
//		this.reviewTextSentences = reviewText.split("[!?\\.]+");
//		this.summarySentences = summary.split("[!?\\.]+");
//		
////		System.out.println("reviewTextSentence:");
////		for(String s : reviewTextSentences)
////			System.out.println(s);
////		
////		System.out.println("summarySentence:");
////		for(String s : summarySentences)
////			System.out.println(s);
////		
//		this.reviewTextWords = reviewText.split("[&\"?!;,\\s\\.]+");
//		this.summaryWords = summary.split("[&\"?!;,\\s\\.]+");
//		
////		System.out.println("reviewTextWord:");
////		for(String s : reviewTextWords)
////			System.out.println(s);
////		
////		System.out.println("summaryWord:");
////		for(String s : summaryWords)
////			System.out.println(s);
//		
//		// automated readability index formula
//		// 4.71 (characters / words) + 0.5 (words / sentences) - 21.43
//		// where:
//		// characters is the number of letters and numbers
//		// words is the number of spaces
//		// sentences is the number of sentences
//		
//		int reviewTextCharactersScore = 0;
//		for(String s : reviewTextWords){
//			reviewTextCharactersScore += s.length();
//		}
//		int reviewTextWordsScore = reviewTextWords.length;
//		int reviewTextSentencesScore = reviewTextSentences.length;
//		
//		int summaryCharactersScore = 0;
//		for(String s : summaryWords){
//			summaryCharactersScore += s.length();
//		}
//		int summaryWordsScore = summaryWords.length;
//		int summarySentencesScore = summarySentences.length;
//		
////		System.out.println("summaryCharactersScore: " + summaryCharactersScore);
////		System.out.println("summaryWordsScore: " + summaryWordsScore);
////		System.out.println("summarySentencesScore: " + summarySentencesScore);
//		
//		this.reviewTextARI = (4.71 * ((double)reviewTextCharactersScore / (double)reviewTextWordsScore)) 
//							 + (0.5 * ((double)reviewTextWordsScore / (double)reviewTextSentencesScore)) 
//							 - 21.43;
//		
//		this.summaryARI = (4.71 * ((double)summaryCharactersScore / (double)summaryWordsScore)) 
//				 + (0.5 * ((double)summaryWordsScore / (double)summarySentencesScore)) 
//				 - 21.43;
//		
//		System.out.println("reviewTextARI: " + reviewTextARI);
//		System.out.println("summaryARI: " + summaryARI);
//	}
	
//	private void calculateSyllable(){
//		this.reviewTextWordsNbSyllables = new int[reviewTextWords.length];
//		reviewTextWordsNbSyllablesLen = reviewTextWords.length;
//		
//		this.summaryWordsNbSyllables = new int[summaryWords.length];
//		summaryWordsNbSyllablesLen = summaryWords.length;
//		
//		EnglishSyllableCounter esc = new EnglishSyllableCounter();
//		
//		for(int ii = 0; ii < reviewTextWordsNbSyllablesLen; ii++){
//			reviewTextWordsNbSyllables[ii] = esc.countSyllables(reviewTextWords[ii]);
//		}
//		
//		for(int ii = 0; ii < summaryWordsNbSyllablesLen; ii++){
//			summaryWordsNbSyllables[ii] = esc.countSyllables(summaryWords[ii]);
//		}
//		
//		// checking
////		System.out.println("Check Syllables");
////		System.out.println("reviewText");
////		for(int ii = 0; ii < reviewTextWordsNbSyllablesLen; ii++){
////			System.out.println(reviewTextWords[ii] + 
////					" = " + reviewTextWordsNbSyllables[ii]);
////		}
////		
////		System.out.println("summary");
////		for(int ii = 0; ii < summaryWordsNbSyllablesLen; ii++){
////			System.out.println(summaryWords[ii] + " = " +
////					summaryWordsNbSyllables[ii]);
////		}
//	}
//	
//	private void calculateComplexWords(){
//		// reviewText
//		for(int ii = 0; ii < reviewTextWordsNbSyllablesLen; ii++){
//			if(reviewTextWordsNbSyllables[ii] > 2){
//				reviewTextNbComplexWords++;
//			}
//		}
//		
//		// summary
//		for(int ii = 0; ii < summaryWordsNbSyllablesLen; ii++){
//			if(summaryWordsNbSyllables[ii] > 2) {
//				summaryNbComplexWords++;
//			}
//		}
//		
//		// checking
////		System.out.println("reviewTextNbComplexWords: " + reviewTextNbComplexWords);
////		System.out.println("summaryNbComplexWords: " + summaryNbComplexWords);
//	}
//	
//	private void calculateFOG(){
//		// FOG Formula
//		// FOG = 0.4 * [(words / sentences) + 100 * (complex words / words)]
//		
//		reviewTextFOG = 0.4 * ((reviewTextWords.length / reviewTextSentences.length) 
//					+ (100 * (reviewTextNbComplexWords / reviewTextNbComplexWords)));
//		summaryFOG = 0.4 * ((summaryWords.length / summarySentences.length) 
//				+ (100 * (summaryNbComplexWords / summaryNbComplexWords)));
//		
//		System.out.println("reviewTextFOG: " + reviewTextFOG);
//		System.out.println("summaryFOG: " + summaryFOG);
//		
//	}
}
