package edu.ufrt.bis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Product {
	HashMap<String,Float> asinAvgScore;
	
	public Product(String filename){
		File file = new File(filename);
		asinAvgScore = new HashMap<String,Float>();
		
		try {
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
	
	public String getData(String key){
		return key + " : " + asinAvgScore.get(key);
	}
	
	public static void main(String args[]){
		Product books = new Product("asin_info.csv");
		
		System.out.println(books.getData("1476755590"));
		System.out.println(books.getData("966189914"));
		System.out.println(books.getData("B00F2WZSXU"));
		System.out.println(books.getData("1493788868"));
	}
}
