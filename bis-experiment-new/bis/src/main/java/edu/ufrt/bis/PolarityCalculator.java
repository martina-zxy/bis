package edu.ufrt.bis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PolarityCalculator {
	private String pathToSWN = "SentiWordNet_3.0.0/SentiWordNet_3.0.0_20130122.txt";
    private HashMap<String, Double>_dict;
    private static MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
    
    public PolarityCalculator() {    	
    	_dict = new HashMap<String, Double>();
        HashMap<String, Vector<Double>> _temp = new HashMap<String, Vector<Double>>();
        try{
            BufferedReader csv =  new BufferedReader(new FileReader(pathToSWN));
            String line = "";           
            while((line = csv.readLine()) != null)
            {
            	
            	if (line.startsWith("#")) continue;
            	try {
	                String[] data = line.split("\t");
	                Double score = Double.parseDouble(data[2])-Double.parseDouble(data[3]);
	                String[] words = data[4].split(" ");
	                for(String w:words)
	                {
	                    String[] w_n = w.split("#");
	                    w_n[0] += "#"+data[0];
	                    int index = Integer.parseInt(w_n[1])-1;
	                    if(_temp.containsKey(w_n[0]))
	                    {
	                        Vector<Double> v = _temp.get(w_n[0]);
	                        if(index>v.size())
	                            for(int i = v.size();i<index; i++)
	                                v.add(0.0);
	                        v.add(index, score);
	                        _temp.put(w_n[0], v);
	                    }
	                    else
	                    {
	                        Vector<Double> v = new Vector<Double>();
	                        for(int i = 0;i<index; i++)
	                            v.add(0.0);
	                        v.add(index, score);
	                        _temp.put(w_n[0], v);
	                    }
	                }
            	}
            	catch (Exception e) {
            		System.out.println("Error : " + line);
            	}
            }
            Set<String> temp = _temp.keySet();
            for (Iterator<String> iterator = temp.iterator(); iterator.hasNext();) {
                String word = iterator.next();
                Vector<Double> v = _temp.get(word);
                double score = 0.0;
                double sum = 0.0;
                for(int i = 0; i < v.size(); i++)
                    score += ((double)1/(double)(i+1))*v.get(i);
                for(int i = 1; i<=v.size(); i++)
                    sum += (double)1/(double)i;
                score /= sum;
                
                _dict.put(word, score);
            }
        }
    	catch(Exception e){
    		e.printStackTrace();
    	} 
    }
    
    public Double getSentenceScore(String sentence) {
    	sentence = sentence.trim().replaceAll("([^a-zA-Z\\s])", "");

    	// Find the starting index of the sentence to read
        String patternStr = "[a-zA-Z0-9]";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(sentence);
        if(matcher.find()){
        	sentence = sentence.substring(matcher.start(),sentence.length());
        } else {
        	return 0.0;
        }
    	
    	int countSentiment = 0;
    	
	    String[] words = sentence.split("\\s+");
	    
	    String taggedSample = tagger.tagString(sentence);
	    String[] taggedWords = taggedSample.split("\\s+");
//	    System.out.println(tagger.tagString(sentence));
	    
	    double totalScore = 0;
	    
	    for (int i=0; i<taggedWords.length;i++) {

//	    	System.out.println(i + " : " + taggedWords[i]);
//	    	System.out.println(taggedWords[i] + " : " + words[i].length() + " : " + taggedWords[i].lastIndexOf('#') );
	    	int indexTail = taggedWords[i].lastIndexOf('_');
	    	
	    	if (indexTail < 0) continue;
	        String tail = taggedWords[i].substring(indexTail + 1);
	        String word = taggedWords[i].substring(0, indexTail );
	        
	        Double score = null;
	        if(tail!=null){
	            score = extract(word, tail);
	            if (score != null) countSentiment++;
//	            System.out.println(taggedWords[i] + "\t" + words[i] + "\t" + tail + "\t" + score);
	        }
	        if (score == null)
	            continue;
	        totalScore += score;
	    }
	    
	    if (countSentiment == 0)
	    	return 0.0;
	    
	    return totalScore/countSentiment;
    }
    
    public Double getParagraphScore(String paragraph) {
    	String[] sentences = paragraph.split("[!?\\.]+");
    	double totalScore = 0;
    	int countSentiment = 0;
    	
    	for (String s : sentences) {
    		double score = getSentenceScore(s);
    		
    		if (score > 0) {
    			totalScore += score;
    			countSentiment++;
    		}
		}
    	return totalScore/countSentiment;
    }

    public Double extract(String word, String tail) {
	    if (tail.contains("NN") || tail.contains("NNS")
	            || tail.contains("NNP")
	            || tail.contains("NNPS"))
	        return _dict.get(word + "#n");
	    else if (tail.contains("VB") || tail.contains("VBD")
	            || tail.contains("VBG") || tail.contains("VBN")
	            || tail.contains("VBP") || tail.contains("VBZ")) 
	        return _dict.get(word + "#v"); 
	    else if (tail.contains("JJ") || tail.contains("JJR")
	            || tail.contains("JJS"))
	        return _dict.get(word + "#a");
	    else if (tail.contains("RB") || tail.contains("RBR")
	            || tail.contains("RBS"))
	        return _dict.get(word + "#r");
	    else
	        return null;
	}

    public static void main(String[] args) throws IOException {
	
//	    String sample = " It works much better with this great example";
	    
	    PolarityCalculator test = new PolarityCalculator();
//	    System.out.println(test.getSentenceScore(sample));
//	    System.out.println(test.getSentenceScore(sample + " excellent!"));
	    
	    String paragraph = "I enjoyed this book for the most part, particularly the characters and the tales of circus life. I would have given it 5 stars but thought the ending was weak. Also, the jumping from nursing home to circus and to nursing home was distracting.. Good Book, Weak Ending";
//	    String paragraph = "Outstanding!";
	    System.out.println(test.getParagraphScore(paragraph));
	    
//	    checkData("review_text_summary.txt");
	}
    
    private static void checkData(String filename){
    	File file = new File(filename);
    	
    	PolarityCalculator test = new PolarityCalculator();
    	
    	try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			
			line = reader.readLine(); // skip first row
			
			int rows = 1;
			
			while((line = reader.readLine()) != null){
				String[] data = line.split("\t");
				String reviewText = data[0];
				String summary = data[1];
				
				String paragraph = reviewText + ". " + summary;
				
				System.out.println(rows++);
				System.out.println(paragraph);
				double score = test.getParagraphScore(paragraph);
				System.out.println(score);
			}
		} catch (Exception e) {
			System.out.println("Cannot find file");
		}
    }
}
