public class SimilarityMeasure {
	
	// Since the assignment asked to implement 
	// - cosine similarity (true)
	// - Euclidean distance (false)
	// for similarity measures, I stored the chosen measure as boolean.
	boolean similarityMeasure;
	
	// Constructor for assigning the similarity measure.
	public SimilarityMeasure(boolean similarityMeasure) {
		this.similarityMeasure = similarityMeasure;
	}
	
	// Cosine similarity implementation.
	private double cosineSimilarity(double[] a, double[] b) {
		double curA, curB, denominator; 
		double dotProduct = 0.0;
		double aMag = 0.0;
		double bMag = 0.0;
		int dimensions = a.length;	//stores the dimensions of vectors given in the parameter.

		for(int i = 0; i < dimensions; i++) {
			curA = a[i]; curB = b[i];
			dotProduct += curA * curB;
			aMag += curA * curA;
			bMag += curB * curB;
		}
		denominator = Math.sqrt(aMag) * Math.sqrt(bMag);
		
		return (dotProduct / denominator);
	}
	
	// Euclidean distance implementation.
	private double euclideanDistance(double[] a, double[] b) {
		double distance = 0.0;
		int dimensions = a.length;
		for(int i = 0; i < dimensions; i++) distance += Math.pow(a[i] - b[i], 2);
		return Math.sqrt(distance);
	}
	
	// Getter method for calculating the desired similarity measure of choice.
	public double getSimilarity(double[] a, double[] b) {
		if(similarityMeasure) return cosineSimilarity(a, b);
		else return euclideanDistance(a, b);
	}
}
