import java.util.*;

public class KMeans {
	
	// Data fields for KMeans class.
	
	// Since the assignment asked to implement 
	// - cosine similarity (true)
	// - Euclidean distance (false)
	// for similarity measures, I stored the chosen measure as boolean.
	private int clusters;
	private double[][] vectors;
	private double[][] centers;
	private int iterations;
	private boolean similarityMeasure;
	private SimilarityMeasure measure;
	private int prevClusterSize, curClusterSize, centersRow, centersCol;
	private List<List<Integer>> prevClusters = new ArrayList<List<Integer>>();
	private List<List<Integer>> curCluster = new ArrayList<List<Integer>>();
	
	
	// Takes number of targeted clusters (clusters), vectors, chosen centers from running 
	// K-Means++, chosen similarity measure (cosine/Euclidean), and upper bound number of
	// iterations for K-means algorithm as parameters.
	public KMeans(int clusters, double[][] vectors, double[][] centers, String similarityMeasure, 
			int iterations) {
		this.clusters = clusters;
		this.vectors = vectors;
		this.centers = centers;
		this.similarityMeasure = similarityMeasure.equals("cosine") ? true : false;
		this.measure = new SimilarityMeasure(this.similarityMeasure);
		this.iterations = iterations;
		for(int i = 0; i < this.clusters; i++) {
			curCluster.add(new ArrayList<Integer>());
			prevClusters.add(new ArrayList<Integer>());
		}
	}
	
	// A driver method that performs K-means clustering once the data fields have been initilaized. 
	public int[][] perfromKMeans() {
		double distance, extreme;
		int clusterGroup = 0;
		int curIteration = 0;
		StringBuilder output = new StringBuilder();
		
		output.append("\n>>>>>>>>>>> K-Means clustering started for 24 articles...");
		long duration = System.nanoTime();
		for(curIteration = 0; curIteration < iterations; curIteration++) {
			output.append("\n\n****** Iteration " + (curIteration + 1) + ": ");
			for(int i = 0; i < clusters; i++) curCluster.get(i).clear();
			for(int doc = 0; doc < vectors.length; doc++) {
				extreme = (similarityMeasure) ? Double.MIN_VALUE : Double.MAX_VALUE;
				for(int center = 0; center < centers.length; center++) {
					distance = measure.getSimilarity(centers[center], vectors[doc]);
					if(similarityMeasure && distance > extreme) {
						// If we choose cosine similarity, the greater measure value we get,
						// more likely it is for the chosen vector to be in the cluster.
						extreme = distance;
						clusterGroup = center;
					}
					else if(!similarityMeasure && distance < extreme) {
						// If we choose euclidean distnace as similarity measure method,
						// similarity, the greater measure value we get,
						// more likely it is for the chosen vector to be in the current cluster.
						extreme = distance;
						clusterGroup = center;
					}
				}
				curCluster.get(clusterGroup).add(doc);
			}
			int j = 0;
			for(List<Integer> cluster : curCluster) {
				output.append("\n(*) Cluster " + (++j) + " : \n");
				for(int doc : cluster) output.append(String.format("Doc[%2d], ", doc));
				output.deleteCharAt(output.length() - 1);
				output.deleteCharAt(output.length() - 1);
			}
			if(stop()) break; // If there has not been any changes in cluster groups, end clustering recursion.
			updatePrevClusters();
			updateCurrentCenters();
		}
		output.append("\nClusters found in " + ++curIteration + " iteration cycles.\n");
		
		int[][] clusterResults = new int[curCluster.size()][vectors.length];
		
		for(int k = 0; k < curCluster.size(); k++) {
			for(int j = 0; j < vectors.length; j++) {
				if(curCluster.get(k).contains(j)) clusterResults[k][j] = 1;
			}
		}
		duration -= System.nanoTime();
		output.append(String.format("========= K-Means complete ========= Duration %d (secs)", (-duration/1000000000)));
		System.out.println(output.toString());
		return clusterResults;
	}
	
	// Updates the prevClusters array with new current clusters.
	private void updatePrevClusters() {
		curClusterSize = curCluster.size();
		for(int i = 0; i < curClusterSize; i++) {
			prevClusters.get(i).clear();
			for(int doc: curCluster.get(i)) prevClusters.get(i).add(doc);
		}
	}
	
	// Updates the current  array with new current clusters.
	private void updateCurrentCenters() {
		centersCol = centers[0].length;
		curClusterSize = curCluster.size();
		int subClusterSize;
		centers = new double[clusters][centersCol];
		for(int i = 0; i < clusters; i++) {
			for(int cluster : curCluster.get(i)) {
				for(int j = 0; j < vectors[0].length; j++) centers[i][j] += vectors[cluster][j];
			}
			subClusterSize = curCluster.get(i).size(); 
			for(int z = 0; z < vectors[0].length; z++) centers[i][z] /= subClusterSize;  
		}
	}
	
	// This method compares if there is any change in the current clusters from the previous clusters.
	private boolean stop() {
		curClusterSize = curCluster.size();
		prevClusterSize = prevClusters.size();
		if(curClusterSize == prevClusterSize) {
			for(int i = 0; i < curClusterSize; i++) {
				for(int docNum : curCluster.get(i)) if(!prevClusters.get(i).contains(docNum)) return false;
			}
			return true;
		}
		return false;
	}	
}
