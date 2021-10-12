import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Paths;


// A driver class for executing the program.
public class Main {
	
	static String[][] docMatrix2D = new String[3][8];
	static String[] docMatrix1D = new String[24];
	static String document;
	static List<String> stopwords = new ArrayList<String>();
	
	static Map<String, Integer> ngrams = new HashMap<String, Integer>();
	static Iterator<Map.Entry<String, Integer>> iteratorE;
	static Map.Entry<String, Integer> entry;
	static String curKey;
	static int value;
	
	static List<Map<String, Integer>> termMatrix = new ArrayList<Map<String, Integer>>();
	static List<Map<String, Double>> TFMatrix = new ArrayList<Map<String, Double>>();
	static Map<String, Double> IDFMatrix = new HashMap<String, Double>();
	static List<Map<String, Double>> tfidfMatrix = new ArrayList<Map<String, Double>>();
	static List<Map<String,Double>> vectors = new ArrayList<Map<String, Double>>();
	
	static double[][] docVectors, centers;
	static int[][] target = new int[3][24];
	
	public static void main(String[] args) throws Exception {
		
		System.out.println(">>>>>>>>>>> Preprocessing started for 24 document...\n");
		// Loading document Files
		System.out.println("Loading document files...");
		loadDocuments(new File("./dataset_3/data/"));
		System.out.println("- SUCCESS: succefully loaded all 24 files!\n");
		System.out.println("Preparing stopwords...");
		loadStopwords(new File("./dataset_3/stopwords.txt"));
		System.out.println("- SUCCESS: succefully loaded stopwords for use!\n");
		//Pre-processing
		System.out.println("\nPreprocessing loaded document files...");
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 8; j++) {
				// Pre-process each document.
				document = PreProcessing.preprocess(docMatrix2D[i][j], stopwords);
				// Store the processed document.
				docMatrix2D[i][j] = document;
				// Generating n-grams of 2-words length and 3-words length.
				ngram(document, 2);
				ngram(document, 3);
			}
		}
		// Removes n-grams that occur less than the given thresholds. For the following instance,
		// it removes n-grams that occurs less than 3 times.
		ngramThreshold(3);
		System.out.println("- SUCCESS: preprocessed all 24 documents!\n");
		// Now we are ready to compute TF-IDF matrix.
		System.out.println("\n>>>>>>>>>>> Generating TF-IDF of the processed 24 documents...\n");
		tfidfHelper();
		System.out.println("- SUCCESS: completed making TF-IDF matrix and its corresponding vectors!\n");
		// Generates list of topics for each clusters in a text document.
		generateTopicDocument();
		// Get target labels for the clustering.
	    String[] targetLabels = new String[3];
	    StringBuilder label = new StringBuilder();
	    for(int i = 0, j = 0; i < targetLabels.length; i++, j = 0, label = new StringBuilder()) {
	    	for(String key : vectors.get(i).keySet()) {
				label.append(key);
				if(++j == 5) break;
				label.append(" / ");
			}
	    	targetLabels[i] = label.toString();
	    }
	    
	    // Clustering
	    docVectors = new double[tfidfMatrix.size()][tfidfMatrix.get(0).size()];
	    ArrayList<String> terms = new ArrayList<String>();
	    for(String key : tfidfMatrix.get(0).keySet()) terms.add(key);
	    
	    // Storing results of TF-IDF matrix into the vectorized document matrix.
	    for(int i = 0; i < 24; i++) {
	    	for(int j = 0; j < terms.size(); j++) docVectors[i][j] = tfidfMatrix.get(i).get(terms.get(j));
    	} 
	    
	    // Implementing K-Means Plus Plus to get centers.
        KMeansPlusPlus kMeansPP = new KMeansPlusPlus(3, docVectors);
        kMeansPP.doKMeansPlusPlus();
        kMeansPP.getPrecise();
        // Instantiate K-means class to perform clustering.
        KMeans kmeans = new KMeans(3, docVectors, kMeansPP.getCenters(), "cosine", 10);
        int[][] clusters = kmeans.perfromKMeans();

        // Now finished with clustering, we now evaluate our results.
        System.out.println("\n>>>>>>>>>>> Confusion Matrix:\n" + String.format("%50s", ""));
        ConfusionMatrix evaluation = new ConfusionMatrix();
        // Generating target(goal) matrix.
        makeTargetMatrix();
        
        // Now building confusion matrix....
        evaluation.getConfusionMatrix(clusters, target, targetLabels);
        
        // Applying PCA to the clustered document vectors. I chose to reduce the
        // data into 2-dimensions in order to get visualization in R2 space.
        double[][] pcaResult = DimensionalityReduction.PCA(docVectors, 2);      
        
        // Creating image files of the clusters generated.
        System.out.println("Creating visual images of the clusters created: ");
        new Visualisation("TargetClusters.png", "Target Clusters", targetLabels, target, pcaResult);
        new Visualisation("MyClusters.png", "My Clusters", evaluation.getFinalClusterLabels(), clusters, pcaResult);
	}
	
	// Method for loading all the text documents in a folder.
	public static void loadDocuments(File file) throws IOException {
		File[] paths = file.listFiles();
		int row = 0, col = 0;
		for(File path : paths) {
			if(path.isDirectory()) {
				File[] curPath = path.listFiles();
				for(File text : curPath) {
					if(text.getName().endsWith(".txt")) {
						document = new String(Files.readAllBytes(Paths.get(text.getPath())));
						document.replaceAll("\\s{2,}", " ");
						docMatrix2D[row][col] = document;
						docMatrix1D[(row << 3) + col++] = document;
					}
				}
				row++; col = 0;
			}
		}
	}
	// Method for loading stop words.
	public static void loadStopwords(File stopwordsFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(stopwordsFile));
        String currentStopword;
    	while((currentStopword = br.readLine()) != null) stopwords.add(currentStopword.trim());
	}
	// Method for loading n-grams.
	public static void ngram(String document, int n) {
		for(iteratorE = PreProcessing.ngram_creator(document, 2).entrySet().iterator(); iteratorE.hasNext();) {
			entry = iteratorE.next();
			curKey = entry.getKey();
			value = entry.getValue();
			if(ngrams.containsKey(curKey)) ngrams.replace(curKey, ngrams.get(curKey) + value);
			else ngrams.put(curKey, value);
        }
	}
	// Method for removing n-grams with occurences less than the given threshold.
	public static void ngramThreshold(int n) {
		for(iteratorE = ngrams.entrySet().iterator(); iteratorE.hasNext();) {
			if(iteratorE.next().getValue() < n) iteratorE.remove();
        }
	}
	// Method for loading n-grams.
	public static void generateTopicDocument() throws IOException {
		try(FileWriter writer = new FileWriter("topics.txt");
			BufferedWriter bw = new BufferedWriter(writer)) {
			int i = 1;
			for(Map<String, Double> folder : vectors) {
				bw.write("Terms/topics for the " + ((i/7 == 0) ? "first" : (i/7 == 1) ? "second" : "third") + " folder: \n");
				for(String term : folder.keySet()) {
					bw.write(((i % 7 == 0) ? 7 : (i % 7)) + ". " + term.replaceAll("_", " ") + "\n");
					if(i++ % 7 == 0) break;
				}
				bw.write("\n\n");
			}
			bw.close();	
 		}
		catch (IOException e) { 
			System.err.format("IOException: %s%n", e);
		}	    
	}
	// Method for generating target (clustering) matrix.
	public static void makeTargetMatrix() {
		for(int i = 0, j = 0; j < 24; j++) {
        	if(j == 8 || j == 16) i++;
        	target[i][j]++;
        }
	}
	
	public static void tfidfHelper() {
		TFIDF tfidf = new TFIDF();
		// Adding all the terms processed to calculate TF-IDF matrix.
		tfidf.addTerms(docMatrix2D);
		tfidf.addTerms(ngrams.keySet().toArray());
		tfidf.getUniqueTerms();
		termMatrix = tfidf.setTermMatrix(24);
		// Pre-processing all the documents stored inside 1D matrix of raw documents.
		for(int i = 0; i < 24; i++) docMatrix1D[i] = PreProcessing.preprocess(docMatrix1D[i], stopwords);
		termMatrix = tfidf.getTermMatrix(termMatrix, docMatrix1D);
		TFMatrix = tfidf.getTFMatrix(termMatrix);
		IDFMatrix = tfidf.getIDFMatrix(termMatrix);
		tfidfMatrix = tfidf.getTFIDFMatrix(TFMatrix, IDFMatrix);
		vectors = tfidf.sumVectors(tfidfMatrix);
		for(int i = 0; i < 3; i++) vectors.set(i, tfidf.sortByValue(vectors.get(i)));
	}
}