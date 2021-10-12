import java.util.*;
import edu.stanford.nlp.simple.*;
import java.util.regex.Pattern;

// A class that contains all the methods required in the pre-processing stage.
public class PreProcessing {
	
	// A method for pre-processing the text document passed through the parameter.
	// This method takes care of leematizing the word as well as removing stop-words.
	public static String preprocess(String document, List<String> stopwords) {
		// StringBuilder variable for storing the finalized, preprocessed output.
		StringBuilder processed = new StringBuilder();
		Document doc = new Document(document);
		// Getting lemmas of the document first, then removing the ones that contain stop-words.
		for(Sentence sentences : doc.sentences()) {
			for(String word : sentences.lemmas()) {
				if(!Pattern.matches("[\\p{Punct}\\s\\d]+", word) && !stopwords.contains(word)) processed.append(word.trim() + " ");
			}	
		}
		processed.deleteCharAt(processed.length() - 1);
		return processed.toString();
	}
	
	// A method that implements sliding window technique to create 
	// n-grams. It takes the document to process and integer value n
	// that denotes the length of the window.
	public static Map<String,Integer> ngram_creator(String document, int n) {
        String[] grams = document.split(" ");
		Map<String,Integer> ngrams = new HashMap<String,Integer>();
		int range = grams.length - n + 1;
		StringBuilder ngram;
		String cur;
		for(int i = 0; i < range; i++) {
			ngram = new StringBuilder();
			for(int j = i; j < i + n; j++) {
				if(i != j) ngram.append("_");
				ngram.append(grams[j]);
			}
			cur = ngram.toString();
			if(ngrams.containsKey(cur)) ngrams.replace(cur, ngrams.get(cur) + 1);
			else ngrams.put(cur, 1);
		}
		return ngrams;
	}
	
	// A method that implements sliding window technique to create 
	// n-grams. It takes the document to process and integer value n
	// that denotes the length of the window.
	public static Map<String,Integer> wordCounter(String document) {
        String[] wordArray = document.split(" ");
		Map<String,Integer> map = new HashMap<String,Integer>();
		int range = wordArray.length;
		String cur;
		for(int i = 0; i < range; i++) {
			cur = wordArray[i];
			if(map.containsKey(cur)) map.replace(cur, map.get(cur) + 1);
			else map.put(cur, 1);
		}
		return map;
	}
}