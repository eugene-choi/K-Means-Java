import java.io.*;
import java.util.*;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class ConfusionMatrix {
	
	int[][] finalCluster;
	String[] clusterLabels;
	

	// A driver method for generating confusion matrix.
	public void getConfusionMatrix(int[][] myClusters, int[][] targetClusters, String[] labels) throws IOException {
		if(myClusters.length == targetClusters.length) {
			List<List<Integer>> myClusterList = new ArrayList<List<Integer>>();
			for(int i = 0; i < myClusters.length; i++) {
				myClusterList.add(new ArrayList<Integer>());
				for(int j = 0; j < myClusters[0].length; j++) if(targetClusters[i][j] == 1) myClusterList.get(i).add(j);
			}
			store(myClusters, myClusterList);
			storeLabels(labels);
			String temp = printClusterMatrix(labels);
			generateText("clusterMatrix.txt", temp);
			System.out.println(temp);
			generateText("confusionMatrix.txt", calculateConfusionMatrix(myClusters));
		}
	}
	// Method for generating confusion matrix to assess performance. 
	private String calculateConfusionMatrix(int[][] clusters) {
		int n = clusters[0].length;
		int allPairN = (n * (n - 1)) << 1;
		int cnt = 0;
		int TPFP = 0;
		for(int[] cluster : clusters) {
			cnt = 0;
			for(int element : cluster) if(element == 1) cnt++;
			TPFP += CombinatoricsUtils.binomialCoefficient(cnt, 2);
		}
		int TP = 0;
		for(int[] row : finalCluster) {
			for(int component : row) if(component >= 2) TP += CombinatoricsUtils.binomialCoefficient(component, 2);
		}		
		int FP = TPFP - TP;
		int TNFN = allPairN - TPFP;
		int FN = 0;
		for(int j = 0; j < finalCluster[0].length; j++) {
			for(int i = 0; i < finalCluster.length-1; i++, cnt = 0) {
				for(int k = i+1; k < finalCluster.length; k++) cnt += finalCluster[k][j];
				FN += finalCluster[i][j] * cnt;
			}
		}
		int TN = TNFN - FN;
		double precision = TP / (double) TPFP;
		double recall = TP / ((double) TP + FN);
		double fMeasure = (2 * precision * recall) / (precision + recall);
		StringBuilder output = new StringBuilder();
		output.append("\n>>>>>>>>>>> Confusion Matrix:\n" + String.format("%30s", ""));
		output.append(String.format("%30s", "Same Cluster"));
		output.append(String.format("%30s", "Different Clusters\n"));
		output.append(String.format("%30s", "Same Classes"));
		output.append(String.format("%30s", "TP = " + TP));
		output.append(String.format("%30s", "FN = " + FN ));
		output.append("\n");
		output.append(String.format("%30s", "Different Classes"));
		output.append(String.format("%30s", "FP = " + FP));
		output.append(String.format("%30s", "TN = " + TN));
		output.append("\n\n>>>>>>>>>>> Result Details:\n" + String.format("%30s", ""));
		output.append("\n" + (String.format("%10s", "") + "Precision = " + precision));
		output.append("\n" + (String.format("%10s", "") + "Recall = " + recall));
		output.append("\n" + (String.format("%10s", "") + "FMeasure = " + fMeasure));
		System.out.println(output.toString());
		return output.toString();
	}
	//
	private void store(int[][] myClusters, List<List<Integer>> myClusterList) {
		finalCluster = new int[myClusters.length][myClusterList.size()];
		int cnt = 0;
		for(int j = 0; j < myClusterList.size(); j++) {
			for(int i = 0; i < myClusters.length; i++, cnt = 0) {
				for(int k = 0; k < myClusterList.get(j).size(); k++) if(myClusters[i][myClusterList.get(j).get(k)] == 1) cnt++;
				finalCluster[i][j] = cnt;
			}
		}
	}
	// A getter method for cluster labels.
	public String[] getFinalClusterLabels() {
		return clusterLabels;
	}
	// A method for storing cluster labels.
	private void storeLabels(String[] clusterLabels) {
		int max = Integer.MIN_VALUE; 
		int max_i = -1;
		this.clusterLabels = new String[finalCluster.length];
		for(int i = 0; i < finalCluster.length; i++) {
			max = Integer.MIN_VALUE;
			max_i = -1;
			for(int j = 0; j < finalCluster[i].length; j++) {
				if(finalCluster[i][j] > max) {
					max = finalCluster[i][j];
					max_i = j;
				}
			}
			this.clusterLabels[i] = clusterLabels[max_i];
		}
	}
	// A helper method for printing cluster matrix.
	private String printClusterMatrix(String[] labels) {
		StringBuilder output = new StringBuilder();
		output.append("\n>>>>>>>>>>> Cluster Label Matrix:\n" + String.format("%50s", ""));
		for(int i = 0; i < finalCluster.length; i++) output.append(String.format("%15s", "Cluster " + (i + 1) + " "));
		output.append("\n");
		for(int i = 0; i < finalCluster[0].length; i++) {
			output.append(String.format("%50s", labels[i]));
			for(int j = 0; j < finalCluster.length; j++) {
				output.append(String.format("%15d", finalCluster[j][i]));
			}
			output.append("\n");
		}
		return output.toString();
	}
	// A helper method for creating a text document to store confusion matrix / .
	public static void generateText(String title, String content) throws IOException {
		try(FileWriter writer = new FileWriter(title);
			BufferedWriter bw = new BufferedWriter(writer)) {
			bw.write(content);
			bw.close();	
 		}
		catch (IOException e) { 
			System.err.format("IOException: %s%n", e);
		}	    
	}

	
}
