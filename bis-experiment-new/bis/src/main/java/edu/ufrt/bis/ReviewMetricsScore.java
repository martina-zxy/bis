package edu.ufrt.bis;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewMetricsScore {
	
	// variables
	String asin;
	String reviewerID;
	Date reviewDate;
	double rating;
	int nbHelpful;
	int nbVotes;
	double helpfulness;
//	int reviewTextLength;
//	int summaryLength;
	double reviewTextLength;
	double summaryLength;
	double spellingErrRatio;
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
	
	public ReviewMetricsScore(){
		
	}
	
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
		this.reviewTextLength = Integer.parseInt(data[7]);
		this.summaryLength = Integer.parseInt(data[8]);
		this.spellingErrRatio = Double.parseDouble(data[9]);
		this.reviewTextFOG = Double.parseDouble(data[10]);
		this.summaryFOG = Double.parseDouble(data[11]);
		this.reviewTextFK = Double.parseDouble(data[12]);
		this.summaryFK = Double.parseDouble(data[13]);
		this.reviewTextARI = Double.parseDouble(data[14]);
		this.summaryARI = Double.parseDouble(data[15]);
		this.reviewTextCLI = Double.parseDouble(data[16]);
		this.summaryCLI = Double.parseDouble(data[17]);
		this.polarity = Double.parseDouble(data[18]);
		this.deviation = Double.parseDouble(data[19]);
	}
	
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
			this.spellingErrRatio = rs.getDouble("spellingErrRatio");
			this.reviewTextFOG = rs.getDouble("reviewTextFOG");
			this.summaryFOG = rs.getDouble("summaryFOG");
			this.reviewTextFK = rs.getDouble("reviewTextFK");
			this.summaryFK = rs.getDouble("summaryFK");
			this.reviewTextARI = rs.getDouble("reviewTextARI");
			this.summaryARI = rs.getDouble("summaryARI");
			this.reviewTextCLI = rs.getDouble("reviewTextCLI");
			this.summaryCLI = rs.getDouble("summaryCLI");
			this.polarity = rs.getDouble("polarity");
			this.deviation = rs.getDouble("deviation");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error when parsing from SQL");
		}
	}
	
	public String getCSV(){
		return 	//"\"" + asin + "\"" + ";" +
				//"\"" + reviewerID + "\"" + ";" +
				//reviewDate.toString() + ";" + 
				rating + "," + 
				nbHelpful + "," +
				nbVotes + ","+
				helpfulness + "," + 
				reviewTextLength + "," + 
				summaryLength + "," +
				spellingErrRatio + "," + 
				reviewTextFOG + "," + 
				summaryFOG + "," + 
				reviewTextFK + "," + 
				summaryFK + "," +
				reviewTextARI + "," +
				summaryARI + "," +
				reviewTextCLI + "," +
				summaryCLI + "," + 
				polarity + "," +
				deviation + "\n";
	}
	
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
				spellingErrRatio + "," + 
				reviewTextFOG + "," + 
				summaryFOG + "," + 
				reviewTextFK + "," + 
				summaryFK + "," +
				reviewTextARI + "," +
				summaryARI + "," +
				reviewTextCLI + "," +
				summaryCLI + "," + 
				polarity + "," +
				deviation + "\n";
	}
}
