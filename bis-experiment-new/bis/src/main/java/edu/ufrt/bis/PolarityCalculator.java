package edu.ufrt.bis;

import java.io.BufferedReader;
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

//	    	System.out.println(i + " : " + taggedWords[i] + " : " + words[i]);
	        String tail = taggedWords[i].substring(words[i].length() + 1);
	        Double score = null;
	        if(tail!=null){
	            score = extract(words[i], tail);
	            if (score != null) countSentiment++;
//	            System.out.println(taggedWords[i] + "\t" + words[i] + "\t" + tail + "\t" + score);
	        }
	        if (score == null)
	            continue;
	        totalScore += score;
	    }

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
	
	    String sample = " It works much better with this great example";
	    
	    PolarityCalculator test = new PolarityCalculator();
	    System.out.println(test.getSentenceScore(sample));
	    System.out.println(test.getSentenceScore(sample + " excellent!"));
	    
//	    String paragraph = "It works much better with this great example! It works much better with this great example excellent!";
//	    String paragraph = "I usually don''t like getting books that I did not ask for. When someone buys a book for me, there is a certain obligation to actually read it, but doing so takes away time from the books I really want to read. ";
//	    String paragraph = "There are so many books on my reading list and so little time to read them all that getting an unasked-for book feels like someone actually stealing from me: stealing valuable reading time.In the case of *The Prophet,* however, I did not resent the choice of my benefactor, even though I had not asked for it.First of all, it was a very quick read, consisting of twenty-nine poetic speeches by the fictional \"prophet.\" He delivers them as his last word on various topics, since he is about to head back home after having lived in a foreign city for twelve years, and the people ask him to speak on all the important issues that touch on human life: family, food, work, emotions, economics, social problems, art, morality, spirituality, death, etc.On most of these issues, the prophet takes what might be called an Eastern stance. He stresses the importance of \"letting go\" rather than \"taking charge,\" whether it''s in relationship to your children, conflict situations, or the end of your life.I did not find it necessary, however, to fully agree with this Eastern outlook to appreciate the book, both for its poetic beauty and for inspiring thoughts. For whatever your own worldview, a degree of \"letting go\" is an art we can all learn.- Jacob Schriftman, Author ofJob''s Wager: An Alternative to Pascal''s Wager and the Atheist''s Wager (With Color Illustrations)";
//	    String paragraph = "I usually don''t like getting books that I did not ask for. When someone buys a book for me, there is a certain obligation to actually read it, but doing so takes away time from the books I really want to read. There are so many books on my reading list and so little time to read them all that getting an unasked-for book feels like someone actually stealing from me: stealing valuable reading time.In the case of *The Prophet,* however, I did not resent the choice of my benefactor, even though I had not asked for it.First of all, it was a very quick read, consisting of twenty-nine poetic speeches by the fictional \"prophet.\" He delivers them as his last word on various topics, since he is about to head back home after having lived in a foreign city for twelve years, and the people ask him to speak on all the important issues that touch on human life: family, food, work, emotions, economics, social problems, art, morality, spirituality, death, etc.On most of these issues, the prophet takes what might be called an Eastern stance. He stresses the importance of \"letting go\" rather than \"taking charge,\" whether it''s in relationship to your children, conflict situations, or the end of your life.I did not find it necessary, however, to fully agree with this Eastern outlook to appreciate the book, both for its poetic beauty and for inspiring thoughts. For whatever your own worldview, a degree of \"letting go\" is an art we can all learn.- Jacob Schriftman, Author ofJob''s Wager: An Alternative to Pascal''s Wager and the Atheist''s Wager (With Color Illustrations)";
	    String paragraph = "I usually don''t like getting books that I did not ask for. When someone buys a book for me, there is a certain obligation to actually read it, but doing so takes away time from the books I really want to read. There are so many books on my reading list and so little time to read them all that getting an unasked-for book feels like someone actually stealing from me: stealing valuable reading time.In the case of *The Prophet,* however, I did not resent the choice of my benefactor, even though I had not asked for it.First of all, it was a very quick read, consisting of twenty-nine poetic speeches by the fictional \"prophet.\" He delivers them as his last word on various topics, since he is about to head back home after having lived in a foreign city for twelve years, and the people ask him to speak on all the important issues that touch on human life: family, food, work, emotions, economics, social problems, art, morality, spirituality, death, etc.On most of these issues, the prophet takes what might be called an Eastern stance. He stresses the importance of \"letting go\" rather than \"taking charge,\" whether it''s in relationship to your children, conflict situations, or the end of your life.I did not find it necessary, however, to fully agree with this Eastern outlook to appreciate the book, both for its poetic beauty and for inspiring thoughts. For whatever your own worldview, a degree of \"letting go\" is an art we can all learn.- Jacob Schriftman, Author ofJob''s Wager: An Alternative to Pascal''s Wager and the Atheist''s Wager (With Color Illustrations). Great Gift Book";
	    System.out.println(test.getParagraphScore(paragraph));
	    
	    
	}
}
