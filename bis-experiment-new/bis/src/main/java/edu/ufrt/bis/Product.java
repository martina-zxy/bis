package edu.ufrt.bis;

/**
 * This class is used to get the average customer review of each books.
 * The average customer review is calculated beforehand and is stored in a file.
 * This class open the file and put the average customer review utilizing HashMap class.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Product {
	// variables that stores the average customer review
	HashMap<String,Float> asinAvgScore;
	
	/**
	 * Constructor of this class. Receive the file name that stores the list of
	 * product (by it's asin) and it's average customer score.
	 * @param filename
	 */
	public Product(String filename){
		// variable initialization
		File file = new File(filename);
		asinAvgScore = new HashMap<String,Float>();
		
		try {
			// add the product information into the HashMap
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			
			while((line = reader.readLine()) != null){
				String[] data = line.split(",");
				asinAvgScore.put(data[0], new Float(data[1]));
			}
		} catch (Exception e) {
			System.out.println("Cannot find file");
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return the average customer review for the asin
	 */
	public String getData(String key){
		return key + " : " + asinAvgScore.get(key);
	}
	
	/**
	 * Main method to test this class.
	 * @param args
	 */
	public static void main(String args[]){
		Product books = new Product("asin_info.csv");
		
		System.out.println(books.getData("0001055178"));
	}
}
