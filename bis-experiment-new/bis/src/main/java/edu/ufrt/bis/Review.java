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
	
	String[] reviewTextSentences;
	String[] summarySentences;
	
	String[] reviewTextWords;
	String[] summaryWords;
	
	double reviewTextARI = 0.0;
	double summaryARI = 0.0;
	
	int reviewTextSpellingError = 0;
	int summarySpellingError = 0;
	
	public Review(){
		
	}
	
	public String toString() {
		return  "Database Variables" + "\n" +
//				"    reviewerID: " + reviewerID + "\n" +
//				"    asin: " + asin + "\n" +
//				"    reviewerName: " + reviewerName + "\n" +
//				"    helpful: " + "[" + helpful[0] + "," + helpful[1] + "]" + "\n" +
				"    reviewText: " + reviewText + "\n" +
//				"    overall: " + overall + "\n" +
				"    summary: " + summary + "\n" +
//				"    unixReviewTime: " + unixReviewTime + "\n" +
//				"    reviewTime: " + reviewTime + "\n" +
//				"    reviewDate: " + reviewDate + "\n" +
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
			
			//reset derived variable
			this.reviewTextLength = 0;
			this.summaryLength = 0;
			
			this.reviewTextSentences = null;
			this.summarySentences = null;
			
			this.reviewTextWords = null;
			this.summaryWords = null;
			
			this.reviewTextARI = 0.0;
			this.summaryARI = 0.0;
			
			this.reviewTextSpellingError = 0;
			this.summarySpellingError = 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error when parsing from SQL");
		}
	}
	
	
	public void calculateMetrics(){
		calculateLength();
		calculateReadability();
		calculateSpellingError();
	}
	
	private void calculateLength(){
		this.reviewTextLength = reviewText.length();
		this.summaryLength = summary.length();
	}
	
	private void calculateReadability(){
		
		this.reviewTextSentences = reviewText.split("[!?\\.]+");
		this.summarySentences = summary.split("[!?\\.]+");
		
//		System.out.println("reviewTextSentence:");
//		for(String s : reviewTextSentences)
//			System.out.println(s);
//		
//		System.out.println("summarySentence:");
//		for(String s : summarySentences)
//			System.out.println(s);
//		
		this.reviewTextWords = reviewText.split("[&\"?!;,\\s\\.]+");
		this.summaryWords = summary.split("[&\"?!;,\\s\\.]+");
		
//		System.out.println("reviewTextWord:");
//		for(String s : reviewTextWords)
//			System.out.println(s);
//		
//		System.out.println("summaryWord:");
//		for(String s : summaryWords)
//			System.out.println(s);
		
		// automated readability index formula
		// 4.71 (characters / words) + 0.5 (words / sentences) - 21.43
		// where:
		// characters is the number of letters and numbers
		// words is the number of spaces
		// sentences is the number of sentences
		
		int reviewTextCharactersScore = 0;
		for(String s : reviewTextWords){
			reviewTextCharactersScore += s.length();
		}
		int reviewTextWordsScore = reviewTextWords.length;
		int reviewTextSentencesScore = reviewTextSentences.length;
		
		int summaryCharactersScore = 0;
		for(String s : summaryWords){
			summaryCharactersScore += s.length();
		}
		int summaryWordsScore = summaryWords.length;
		int summarySentencesScore = summarySentences.length;
		
//		System.out.println("summaryCharactersScore: " + summaryCharactersScore);
//		System.out.println("summaryWordsScore: " + summaryWordsScore);
//		System.out.println("summarySentencesScore: " + summarySentencesScore);
		
		this.reviewTextARI = (4.71 * ((double)reviewTextCharactersScore / (double)reviewTextWordsScore)) 
							 + (0.5 * ((double)reviewTextWordsScore / (double)reviewTextSentencesScore)) 
							 - 21.43;
		
		this.summaryARI = (4.71 * ((double)summaryCharactersScore / (double)summaryWordsScore)) 
				 + (0.5 * ((double)summaryWordsScore / (double)summarySentencesScore)) 
				 - 21.43;
		
//		System.out.println("reviewTextARI: " + reviewTextARI);
//		System.out.println("summaryARI: " + summaryARI);
	}
	
	private void calculateSpellingError(){
		try {
			JLanguageTool langTool;
			langTool = new JLanguageTool(new BritishEnglish());
//			for (Rule rule : langTool.getAllRules()) {
//			  if (!rule.isDictionaryBasedSpellingRule()) {
//			    langTool.disableRule(rule.getId());
//			  }
//			}
			
			List<RuleMatch> matches;
			
			matches = langTool.check(reviewText);
			
			for (RuleMatch match : matches) {
			  System.out.println("Potential typo at characters " +
			      match.getFromPos() + "-" + match.getToPos() + ": " +
			      match.getMessage());
			  System.out.println("Suggested correction(s): " +
			      match.getSuggestedReplacements());
			  this.reviewTextSpellingError++;
			}
			
			System.out.println("reviewTextSpellingError: " + reviewTextSpellingError);
			
			matches = langTool.check(summary);
			
			for (RuleMatch match : matches) {
			  System.out.println("Potential typo at characters " +
			      match.getFromPos() + "-" + match.getToPos() + ": " +
			      match.getMessage());
			  System.out.println("Suggested correction(s): " +
			      match.getSuggestedReplacements());
			  this.summarySpellingError++;
			}
			
			System.out.println("summarySpellingError: " + summarySpellingError);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
