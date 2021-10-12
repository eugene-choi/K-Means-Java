import java.util.*;

public class KMeansPlusPlus {
	
    double[][] vectors;
    double[][] centers;
    int vecCnt;
    int dimensions;
    int clusters;
    
    // Constructor which stores input data .
    public KMeansPlusPlus(int n, double[][] vectors) {
    	this.clusters = n;
        this.vectors = vectors;
        this.vecCnt = vectors.length;
        this.dimensions = vectors[0].length;
        this.centers = new double[clusters][dimensions];
    }
    
    // Invokes KMeans++ by randomly choosing a center from input vectors
    // and choosing remaining n - 1 centers that are furthest from the 
    // (in terms of Euclidean distance) the chosen center(s).
    public void doKMeansPlusPlus() {
    	double[] distances = new double[vecCnt];
        double distance = 0.0;
        double maxDist = Double.MIN_VALUE;
        int centerIndex = 0;
        int randomCenter = 0;
    	
        // Arbitrarily (randomly) chosen center.
        randomCenter = new Random().nextInt(vectors.length);
     
        // Store the vector components of the chosen center.
        for(int i = 0; i < dimensions; i++) centers[0][i] = vectors[randomCenter][i];
        
        // Choose vector(s) that are furthest away from the center(s)
        // chosen each iteration. (All the centers chosen up until the current
        // iteration are taken into account).
        for(int cluster = 1; cluster < clusters; cluster++, maxDist = Double.MIN_VALUE) {
            for(int i = 0; i < vecCnt; i++) {
            	distances[i] = 0.0;
                for(int j = 0; j < cluster; j++, distance = 0.0) {
                	for(int z = 0; z < dimensions; z++) distance += Math.pow(vectors[i][z] - centers[j][z], 2);
                    distances[i] += distance;
                }
                if(maxDist < distances[i]) {
                	maxDist = distances[i];
                	centerIndex = i;
                }
            }
            // Store the chosen center.
            for(int i = 0; i < dimensions; i++) centers[cluster][i] = vectors[centerIndex][i];
        }
    }
    
    // A hard-coded option for this project to get a more precise prediction by
    // taking the first document of each clusters as the three chosen centers.
    public void getPrecise() {
    	for(int i = 0, j = 0; i < clusters; i++, j += 8) {
    		for(int k = 0; k < dimensions; k++) centers[i][k] = vectors[j][k];
    	}
    }
    
    // Returns the centers chosen.
    public double[][] getCenters() {
        return this.centers;
    }
}
