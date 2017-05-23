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
	
	private static void checkAttributes(Instances instances){
		System.out.println("Check attributes:");
		for(int ii = 0; ii < instances.numAttributes(); ii++)
			System.out.println(ii + " : " + instances.attribute(ii).name());
	}
	
	
	
    public static void main( String[] args ) throws Exception
    {
    	evaluationData = new String[52][7];
    	evaluationData[0][0] = "dataset name";
    	evaluationData[0][1] = "Total number of instances";
    	evaluationData[0][2] = "Mean absolute error";
    	evaluationData[0][3] = "Root mean squared error";
    	evaluationData[0][4] = "Relative absolute error";
    	evaluationData[0][5] = "Root relative squared error";
    	evaluationData[0][6] = "Correlation coefficient";
    	
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
    
    private static void experiment(String trainFile, int fileNumber) throws Exception{
    	// initialize train data
        DataSource sourceTrain = new DataSource(trainFile);
        Instances train;
        train = sourceTrain.getDataSet();
        
        // remove unused indices
        int classIndex = 0;
        int[] removedIndices = new int [10];
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
        
//        System.out.println("Building Model Result:");
        PrintWriter printWriter = new PrintWriter(trainFile.split("\\.")[0] + " - model result.txt" );
        printWriter.write("helpfulness = \n");
        for(int ii = 0; ii < lmCoeffs.length; ii++){
        	if(ii != lmCoeffs.length - 1)
        		printWriter.write(lmCoeffs[ii] + " * " + trainReduced.attribute(ii).name() + " + \n");
        	else
        		printWriter.write("" + lmCoeffs[ii]);
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
   
    	
    }
    
}
