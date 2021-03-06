package edu.ufrt.wekaexperiment.wekaexperiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.Debug.Random;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Hello world!
 *
 */
public class App 
{
	
	static String[][] evaluationData;
	static String[][] coefficientData;
	static String[][] testData;
//	static String testFile = "";
	
	private static void checkAttributes(Instances instances){
		System.out.println("Check attributes:");
		for(int ii = 0; ii < instances.numAttributes(); ii++)
			System.out.println(ii + " : " + instances.attribute(ii).name());
	}
	
	
	
    public static void main( String[] args ) throws Exception
    {
    	// header
    	evaluationData = new String[51][7];
    	evaluationData[0][0] = "train set name";
    	evaluationData[0][1] = "Total number of instances";
    	evaluationData[0][2] = "Correlation coefficient";
    	evaluationData[0][3] = "Mean absolute error";
    	evaluationData[0][4] = "Root mean squared error";
    	evaluationData[0][5] = "Relative absolute error";
    	evaluationData[0][6] = "Root relative squared error";
    	
    	testData = new String[1000][8];
    	testData[0][0] = "train set name";
    	testData[0][1] = "test set name";
    	testData[0][2] = "Total number of instances";
    	testData[0][3] = "Correlation coefficient";
    	testData[0][4] = "Mean absolute error";
    	testData[0][5] = "Root mean squared error";
    	testData[0][6] = "Relative absolute error";
    	testData[0][7] = "Root relative squared error";
    	
//    	coefficientData = new String[51][11];
    	coefficientData = new String[51][12];
    	
        File folder = new File(".\\");
        File[] listOfFiles = folder.listFiles();
        
        LinkedList<String> fileNames = new LinkedList<String>();
        
        for(int ii = 0; ii < listOfFiles.length; ii++){
        	if(listOfFiles[ii].isFile()) {
        		String fileName = listOfFiles[ii].getName();
        		if(fileName.endsWith("arff"))
        			fileNames.add(fileName);
        	}
        }
        
        for(int ii = 0; ii < fileNames.size(); ii++){
//        	System.out.println(fileNames.get(ii));
          experiment(fileNames.get(ii),ii+1);
        }

        printEvaluation();
        printCoefficient();
        printTestData();
        
    }
    
    private static void printEvaluation() throws FileNotFoundException{
    	PrintWriter writer = new PrintWriter ("evaluation result.csv");
    	for(int ii = 0; ii < evaluationData.length; ii ++){
    		for(int jj = 0; jj < evaluationData[ii].length; jj++){
    			writer.write(evaluationData[ii][jj] + ",");
    		}
    		writer.write("\n");
    	}
    	writer.close();
    }
    
    private static void printTestData() throws FileNotFoundException{
    	PrintWriter writer = new PrintWriter ("test result Martina.csv");
    	for(int ii = 0; ii < testData.length; ii ++){
    		for(int jj = 0; jj < testData[ii].length; jj++){
    			writer.write(testData[ii][jj] + ",");
    		}
    		writer.write("\n");
    	}
    	writer.close();
    }
    
    private static void printCoefficient() throws FileNotFoundException{
    	PrintWriter writer = new PrintWriter ("coefficient result.csv");
    	for(int ii = 0; ii < coefficientData.length; ii ++){
    		for(int jj = 0; jj < coefficientData[ii].length; jj++){
    			writer.write(coefficientData[ii][jj] + ",");
    		}
    		writer.write("\n");
    	}
    	writer.close();
    }
    
    private static void experiment(String trainFile, int fileNumber) throws Exception{
    	// initialize train data
        DataSource sourceTrain = new DataSource(trainFile);
        Instances train;
        train = sourceTrain.getDataSet();
        
        // remove unused indices
        int classIndex = 0;
//        int[] removedIndices = new int [10];
        int[] removedIndices = new int [11];
        int j = 0;
        

        for (int i = 0; i < train.numAttributes(); i++){
			String name = train.attribute(i).name();
//			System.out.println("Check attribute no " + i + " : " + name);
			if (name.startsWith("helpfulness")){
				classIndex = i;
//				System.out.println("Set class : " + name);
			} else if (name.startsWith("asin") || 
					  name.startsWith("reviewerID") || 
					  name.startsWith("reviewDate") || 
					  name.startsWith("nbHelpful") ||
					  name.startsWith("nbVotes") ||
//					  name.startsWith("spellingErrRatio") ||
					  name.startsWith("reviewTextFOG") ||
					  name.startsWith("summaryFOG") ||
					  name.startsWith("summaryFK") ||
					  name.startsWith("reviewTextARI") ||
					  name.startsWith("summaryARI")){
				removedIndices[j] = i;
				j++;
//				System.out.println("Remove attribute: " + name);
			} 
		}	
        
        train.setClassIndex(classIndex);
       
        Instances trainReduced = sourceTrain.getDataSet();
        trainReduced.setClassIndex(classIndex);
        
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(removedIndices);
        remove.setInputFormat(train);
        
        // create reduced instances
        trainReduced = Filter.useFilter(trainReduced, remove);
//        checkAttributes(trainReduced);
        
        // create linear regression
        LinearRegression linearRegression = new LinearRegression();
        linearRegression.setNumDecimalPlaces(8);
        linearRegression.buildClassifier(trainReduced);
        
        
        double[] lmCoeffs = linearRegression.coefficients();
        
        coefficientData[0][0] = "dataset name";
        
        // initialize the header        
        int jj = 1;
        for(jj = 1; jj < lmCoeffs.length - 1;jj++){
        	coefficientData[0][jj] = trainReduced.attribute(jj-1).name();
        }
        coefficientData[0][jj] = "deviation";
        coefficientData[0][jj+1] = "constant";
        coefficientData[fileNumber][0] = trainFile;
        
//      System.out.println("Building Model Result:");
        PrintWriter printWriter = new PrintWriter(trainFile.split("\\.")[0] + " - model result.txt" );
        printWriter.write("helpfulness = \n");
//        System.out.print("helpfulness = \n");
        for(int ii = 0; ii < lmCoeffs.length; ii++){
        	coefficientData[fileNumber][ii+1] = "" + lmCoeffs[ii];
        	if(ii != lmCoeffs.length - 1){
//        		System.out.print(lmCoeffs[ii] + " * " + trainReduced.attribute(ii).name() + " + \n");
        		printWriter.write(lmCoeffs[ii] + " * " + trainReduced.attribute(ii).name() + " + \n");
        	}
        	else{
//        		System.out.println("" + lmCoeffs[ii]);
        		printWriter.write("" + lmCoeffs[ii]);
        	}
        }
        
        printWriter.close();
        
        Classifier classifier = linearRegression;
        FilteredClassifier filteredClassifier = new FilteredClassifier();
        filteredClassifier.setFilter(remove);
        filteredClassifier.setClassifier(classifier);
        
        // 10-cross fold validation
        Evaluation evaluation = new Evaluation(train);
        int nbFolds = 10;
        Random random = new Random(1); // using seed = 1
        evaluation.crossValidateModel(filteredClassifier, train, nbFolds, random);
        
        evaluationData[fileNumber][0] = trainFile;
        evaluationData[fileNumber][1] = "" + (int)evaluation.numInstances();
        evaluationData[fileNumber][2] = "" + evaluation.correlationCoefficient();
        evaluationData[fileNumber][3] = "" + evaluation.meanAbsoluteError();
        evaluationData[fileNumber][4] = "" + evaluation.rootMeanSquaredError();
        evaluationData[fileNumber][5] = "" + evaluation.relativeAbsoluteError();
        evaluationData[fileNumber][6] = "" + evaluation.rootRelativeSquaredError();
        
//        System.out.println(evaluation.toSummaryString("\nCross Validation Result", true));
        
        // evaluate using test set
        // initialize test data
        
        System.out.println("Train set: " + trainFile);
        
        File folderTest = new File(".\\");
        File[] listOfFilesTest = folderTest.listFiles();
        
        LinkedList<String> fileNamesTest = new LinkedList<String>();
        
        for(int ii = 0; ii < listOfFilesTest.length; ii++){
        	if(listOfFilesTest[ii].isFile()) {
//        		System.out.println(listOfFilesTest[ii]);
        		String fileName = listOfFilesTest[ii].getName();
        		if(fileName.endsWith("arff"))
        			fileNamesTest.add(fileName);
        	}
        }
        
        int nbTestFile = fileNamesTest.size();
        
        for(int ii = 0; ii < nbTestFile; ii++){
        	System.out.println("Test Set: " + fileNamesTest.get(ii));
        	String testFile = fileNamesTest.get(ii);
        	DataSource sourceTest = new DataSource(testFile);
	        Instances test;
	        test = sourceTest.getDataSet();
	        test.setClassIndex(classIndex);
	        
	        evaluation = new Evaluation(test);
	        evaluation.evaluateModel(filteredClassifier, test);
	//	        System.out.println(evaluation.toSummaryString("\nTest Set Result", true));
	        int counter = (fileNumber-1)*nbTestFile+ii;
//	        System.out.println("fileNumber: " + fileNumber + " counter: " + counter);
	        testData[counter][0] = trainFile;
	        testData[counter][1] = testFile;
	        testData[counter][2] = "" + (int)evaluation.numInstances();
	        testData[counter][3] = "" + evaluation.correlationCoefficient();
	        testData[counter][4] = "" + evaluation.meanAbsoluteError();
	        testData[counter][5] = "" + evaluation.rootMeanSquaredError();
	        testData[counter][6] = "" + evaluation.relativeAbsoluteError();
	        testData[counter][7] = "" + evaluation.rootRelativeSquaredError();
        }   
    }
    
    private static void generateKnowledgeFlowData(String filename) throws Exception{
    	// initialize train data
        DataSource sourceTrain = new DataSource(filename);
        Instances train;
        train = sourceTrain.getDataSet();
        
        // remove unused indices
        int classIndex = 0;
//        int[] removedIndices = new int [10];
        int[] removedIndices = new int [11];
        int j = 0;
        
        for (int i = 0; i < train.numAttributes(); i++){
			String name = train.attribute(i).name();
//			System.out.println("Check attribute no " + i + " : " + name);
			if (name.startsWith("helpfulness")){
				classIndex = i;
//				System.out.println("Set class : " + name);
			} else if (name.startsWith("asin") || 
					  name.startsWith("reviewerID") || 
					  name.startsWith("reviewDate") || 
					  name.startsWith("nbHelpful") ||
					  name.startsWith("nbVotes") ||
					  name.startsWith("spellingErrRatio") ||
					  name.startsWith("reviewTextFOG") ||
					  name.startsWith("summaryFOG") ||
					  name.startsWith("summaryFK") ||
					  name.startsWith("reviewTextARI") ||
					  name.startsWith("summaryARI")){
				removedIndices[j] = i;
				j++;
//				System.out.println("Remove attribute: " + name);
			} 
		}	
        
        train.setClassIndex(classIndex);
        
        Instances trainReduced = sourceTrain.getDataSet();
        trainReduced.setClassIndex(classIndex);
        
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(removedIndices);
        remove.setInputFormat(train);
        
        // create reduced instances
        trainReduced = Filter.useFilter(trainReduced, remove);
//        checkAttributes(trainReduced);
        
        // create linear regression
        LinearRegression linearRegression = new LinearRegression();
        linearRegression.setNumDecimalPlaces(8);
        linearRegression.buildClassifier(trainReduced);
        
        
    }
    
}
