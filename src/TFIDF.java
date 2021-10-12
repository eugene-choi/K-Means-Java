import java.util.*;

import org.apache.commons.math3.util.DoubleArray;

import java.nio.file.*;
import static java.util.stream.Collectors.*;

// This class takes care of calculating term-frequency and
// inverse term frequency.
public class TFIDF {
	
	// Data fields for storing pre-processed terms.
	static Set<String> uniqueFilter = new HashSet<String>();
	static List<String> uniqueTerms = new ArrayList<String>();
	static List<Map<String, Double>> vectors = new ArrayList<Map<String, Double>>();

	// Methods for adding terms in the given document.
	public void addTerms(String[][] documents) {
		for(String[] rows : documents) for(String document : rows) for(String term : document.split(" ")) uniqueFilter.add(term);
	}
	public void addTerms(Object[] documents) {
		for(Object document : documents) for(String term : document.toString().split(" ")) uniqueFilter.add(term);		
	}
	// Method for generating (only) the unique terms as a list.
	public void getUniqueTerms() {
		uniqueTerms.addAll(uniqueFilter);
	}
	// A helper method for setting the document term matrix with the given size as input.
	public List<Map<String, Integer>> setTermMatrix(int n) { 
		List<Map<String, Integer>> termMatrix = new ArrayList<Map<String, Integer>>();
		while(n-- > 0) termMatrix.add(initTermMapInt());
		return termMatrix;
	}
	// A helper method for initializing mapping entries of every term with a numeric value for computing TF-IDF.
	// (Integer version)
	public static Map<String, Integer> initTermMapInt() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(String uniqueTerm : uniqueTerms) map.put(uniqueTerm, 0);
		return map;
	}
	// (Double version)
	public static Map<String, Double> initTermMapDouble() {
		Map<String, Double> map = new HashMap<String, Double>();
		for(String uniqueTerm : uniqueTerms) map.put(uniqueTerm, 0.0);
		return map;
	}
	// Method for counting the occurrences of each term in a matrix. 
	public List<Map<String, Integer>> getTermMatrix(List<Map<String,Integer>> termMatrix, String[] docs) {
		String[] doc, cur;
		int cnt, subCnt;
		for(int i = 0; i < docs.length; i++) {
			doc = docs[i].split(" ");
			for(String uniqueTerm : uniqueTerms) {
				cnt = 0;
				// This if block takes care of occurrences n-grams. 
				if(uniqueTerm.contains("_") ) {
					cur = uniqueTerm.split("_");
					for(int j = 0; j <= doc.length - cur.length; j++) {
						subCnt = 0;
						for(String word : cur) {
							if(word.equals(doc[j + subCnt])) subCnt++;
							else break;
						}
						if(cur.length == subCnt) cnt++;
					}
					termMatrix.get(i).replace(uniqueTerm, termMatrix.get(i).get(uniqueTerm) + cnt);
				}
				// Otherwise, treat it as a regular 1-gram word.
				else {
					for(String word : doc) if(word.equals(uniqueTerm)) cnt++;
					termMatrix.get(i).replace(uniqueTerm, termMatrix.get(i).get(uniqueTerm) + cnt);
				}
			}
		}	
		return termMatrix;
	}
	// A method for calculating term frequency matrix.
	public List<Map<String, Double>> getTFMatrix (List<Map<String, Integer>> termMatrix) { 
		List<Map<String, Double>> TFMatrix = new ArrayList<Map<String, Double>>();
		double cnt = 0.0;
		for(int i = 0; i < termMatrix.size(); i++) TFMatrix.add(initTermMapDouble());
		for(int i = 0; i < termMatrix.size(); i++, cnt = 0.0) {
			for(String term : uniqueTerms) cnt += termMatrix.get(i).get(term);
			for(String term : uniqueTerms) TFMatrix.get(i).replace(term, termMatrix.get(i).get(term) / cnt);
		}
		return TFMatrix;
	}
	// A method for calculating inverse term frequency matrix.
	public Map<String, Double> getIDFMatrix (List<Map<String, Integer>> termMatrix) { 
		Map<String, Double> IDFMatrix = new HashMap<String, Double>();
		int numerator = termMatrix.size();
		int[] cnt = new int[uniqueTerms.size()];
		for(int i = 0; i < uniqueTerms.size(); i++) {
			for(int j = 0; j < termMatrix.size(); j++) if(termMatrix.get(j).get(uniqueTerms.get(i)) > 0) cnt[i]++;
		}
		for(String uniqueTerm : uniqueTerms) IDFMatrix.put(uniqueTerm, Math.log10(numerator / cnt[uniqueTerms.indexOf(uniqueTerm)]));
		return IDFMatrix;
	}
	// A method for calculating TF-IDF matrix.
	public List<Map<String, Double>> getTFIDFMatrix (List<Map<String, Double>> TF, Map<String, Double> IDF) {
		List<Map<String, Double>> TFIDF = new ArrayList<Map<String, Double>>();
		for(int i = 0; i < TF.size(); i++) TFIDF.add(initTermMapDouble());
		for(int i = 0; i < TF.size(); i++) {
			for(String uniqueTerm : uniqueTerms) TFIDF.get(i).replace(uniqueTerm, TF.get(i).get(uniqueTerm) * IDF.get(uniqueTerm));
		}
		return TFIDF;
		
	}
	// Method for generating vectors from TF-IDF matrix.
	public List<Map<String, Double>> sumVectors(List<Map<String, Double>> TFIDF) {
		for(int i = 0; i < 3; i++) vectors.add(initTermMapDouble());
		double val;
		for(String uniqueTerm : uniqueTerms) {
			val = 0.0;
			for(int i = 0, j = 0; i < 24; i++) {
				val += TFIDF.get(i).get(uniqueTerm);
				if(i == 7 || i == 15 || i == 23) {
					vectors.get(j++).put(uniqueTerm, val);
					val = 0.0;
				}
			}
		}
		return vectors;
	}
	// Method for sorting the components of the vector by their values.
	public Map<String, Double> sortByValue(Map<String, Double> component) { 
		return component.entrySet().stream()
		        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
		        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    } 
}