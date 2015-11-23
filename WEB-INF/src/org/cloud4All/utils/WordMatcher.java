/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloud4All.utils;

//import gr.iti.oasis.util.Util;
//
//import gr.iti.oasis.util.synonyms.WordNetSynonymSearch;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.StringTokenizer;
import java.util.*;

//import edu.smu.tspell.wordnet.*;

/**
 * 
 * @author nikolaou
 */
public class WordMatcher {

	// private NegativeDictionary nd;
	private PorterStemmer ps;
	private NGramMatcher nGram;
	private ArrayList table1;
	private ArrayList table2;

	public WordMatcher() {
		// nd = new NegativeDictionary();
		ps = new PorterStemmer();
		nGram = new NGramMatcher();
	}

	/**
	 * This method compares two Strings and returns their Similarity. It first
	 * converts the Strings to lower case, removes punctuations then finds
	 * Similarity of the resulting Strings using NGram. If this similarity
	 * <i>sim</i> is more than 0.9 then it returns 0.9 as Similarity value else
	 * it removes Stop Words (words that are not required like a, an, the etc.)
	 * and finds the Similarity <i>simPartial</i> of the resulting Strings. <br/>
	 * If this value is more than 0.9 it returns 0.9 as Similarity value else it
	 * Stems the Strings and again applies NGram to calculate Similarity
	 * <i>simStemmed</i>. <br/>
	 * If this value is greater than 0.9 then returns 0.9 as Similarity else it
	 * calculates Final Similarity as average of all the above three
	 * Similarities i.e. Simialrity = ( sim + simPartial + simStemmed ) / 3
	 * 
	 * @param str1
	 *            String to be matched
	 * @param str2
	 *            String to be Matched
	 * @return Match Score
	 */
	public double compare(String str1, String str2) {
		if (str1.equalsIgnoreCase(str2)) {
			return 1.0;
		}
		// Trim and convert to lower case and remove punctuations
		// double sim;
		// SimilarityAssessor sim1 = new SimilarityAssessor();
		// String metric = SimilarityAssessor.LIN;
		// double score = 0.0;
		// if ((!str1.equalsIgnoreCase("qqqqqqqqqqq"))
		// && (!str2.equalsIgnoreCase("qqqqqqqqqqq"))) {
		// try {
		// score = sim1.getSimilarity(str1, str2, metric);
		// // System.out.println("Semantic Similarity between "+str1+" and "+
		// // str2+" score "+score +" using "+metric+" metric");
		// if (score > 0.5) {
		// return score;
		// }
		// } catch (WordNotFoundException e) {
		// // TODO Auto-generated catch block
		// // sim=0.0;
		// // e.printStackTrace();
		// }
		// }
		// sim = nGram.getSimilarity(s1, s2) ;
		// if(sim >= 0.9) return sim ;
		// Remove Stop Words
		// s1 = nd.removeStopWords(s1) ;
		// s2 = nd.removeStopWords(s2) ;
		// Find the Similarity using NGram
		// double simPartial = nGram.getSimilarity(s1, s2) ;
		// if(simPartial >= 0.9) return simPartial ;
		// Stem
		// String s1 = ps.stemText(str1);
		// String s2= ps.stemText(str2);
		double simStemmed = 0.0;
		simStemmed = nGram.getSimilarity(str1, str2);
		// if (simStemmed >= 0.8) {
		return simStemmed;
		// }
		// Tokened Match
		// double simTokened = tokenedMatch(str1,str2) ;
		// if(simTokened >= 0.8) return simTokened ;
		// Average
		// sim = (sim + simPartial + simStemmed + simTokened) / 4 ;
		// return (score + simStemmed) / 2;
	}

	public double compare(String str1, String str2, SimilarityAssessor sim1,
			boolean useWordnet) {
		if (str1.equalsIgnoreCase(str2)) {
			return 1.0;
		}
		if (str1.equalsIgnoreCase("qqqqqqqqqqq")
				|| str2.equalsIgnoreCase("qqqqqqqqqqq")) {
			return 0.0;
		}
		// Trim and convert to lower case and remove punctuations
		// double sim;
		double score = 0.0;
		if (useWordnet) {
			String metric = SimilarityAssessor.LIN;

			if ((!str1.equalsIgnoreCase("qqqqqqqqqqq"))
					&& (!str2.equalsIgnoreCase("qqqqqqqqqqq"))) {
				try {
					score = sim1.getSimilarity(str1, str2, metric);
					// System.out.println("Semantic Similarity between "+str1+" and "+
					// str2+" score "+score +" using "+metric+" metric");
					if (score > 0.3) {
						return score;
					}
				} catch (WordNotFoundException e) {
					// TODO Auto-generated catch block
					// sim=0.0;
					// e.printStackTrace();
				}
			}
		}

		double simStemmed = 0.0;
		simStemmed = nGram.getSimilarity(str1, str2);
		if (simStemmed >= 0.8) {
			return simStemmed;
		}
		// Tokened Match
		// double simTokened = tokenedMatch(str1,str2) ;
		// if(simTokened >= 0.8) return simTokened ;
		// Average
		// sim = (sim + simPartial + simStemmed + simTokened) / 4 ;
		if (!useWordnet) {
			return simStemmed;
		} else {
			return (score + simStemmed) / 2;
		}
	}

	/**
	 * This method adds a SPACE before every Upper-Case letter, which allows the
	 * String to be tokenizes based on Upper-case letter.
	 * 
	 * @param str
	 *            String to be Tokenized
	 * @return Cleaned up String
	 */
	protected String preTokenize(String str) {
		int j = 0;

		if (str.length() > 0) {
			// Convert the String to an array of Characters

			char[] c = str.toCharArray();

			// Create a new array of twice the length of the String

			char[] newC = new char[c.length * 2];

			for (int i = 0; i < c.length; i++) {
				// Check if Character is Upper-Case

				if (Character.isUpperCase(c[i])) {
					// if Character is Upper-Case then add a Blank Space before
					// it

					newC[j] = ' ';
					j = j + 1;
				}

				// Copy the Character to new Array

				newC[j] = c[i];
				j++;
			}

			// Get the String value of the Character Array

			String val = String.copyValueOf(newC);

			// Trim the resulting String before returning

			val = val.trim();
			return val;
		} else {
			return "";
		}
	}

	/**
	 * @param str1
	 *            String to be matched
	 * @param str2
	 *            Candidate String
	 * @return a value of 0.0 if no Match is found and 1.0 if str1 appears as
	 *         one of the token of str2
	 */
	protected double tokenedMatch(String str1, String str2) {
		StringTokenizer st = new StringTokenizer(str2);

		while (st.hasMoreTokens()) {
			String word = st.nextToken().trim();

			if (str1.indexOf(word) != -1) {
				return 1.0;
			}
		}

		st = new StringTokenizer(str1);

		while (st.hasMoreTokens()) {
			String word = st.nextToken().trim();

			if (str2.indexOf(word) != -1) {
				return 1.0;
			}
		}

		return 0.0;
	}

	public void formVectors(String str1, String str2) {

		String s1 = preTokenize(str1);
		String s2 = preTokenize(str2);
		s1 = PreProcessText.preProcess(s1);
		s2 = PreProcessText.preProcess(s2);
		/*
		 * if((nd.removeStopWords(s1)).length()!=0) s1 = nd.removeStopWords(s1);
		 * if((nd.removeStopWords(s2)).length()!=0) s2 = nd.removeStopWords(s2);
		 */
		s1 = ps.stemText(s1);
		s2 = ps.stemText(s2);
		StringTokenizer st1 = new StringTokenizer(s1);
		table1 = new ArrayList<String>();
		int i = 0;
		String String1 = new String();
		while (st1.hasMoreTokens()) {
			String1 = st1.nextToken();
			table1.add(String1);
			i++;
		}
		i = 0;
		st1 = new StringTokenizer(s2);
		table2 = new ArrayList<String>();
		/* table2=new String[st.countTokens()]; */
		while (st1.hasMoreTokens()) {
			table2.add(st1.nextToken());
			i++;
		}
		// System.out.println(table2);
		while (table1.size() != table2.size()) {
			if (table1.size() > table2.size()) {
				table2.add("Qqqqqqqqqqq");
			}
			if (table1.size() < table2.size()) {
				table1.add("Qqqqqqqqqqq");
			}
		}
	}

	public double[][] computeDistancesAmongTables() {

		int i;
		int j;
		double[][] weights;
		Object ss1[] = table1.toArray();
		Object ss2[] = table2.toArray();
		weights = new double[table1.size()][table2.size()];
		for (i = 0; i < table1.size(); i++) {
			for (j = 0; j < table2.size(); j++) {
				weights[i][j] = compare((String) ss1[i], (String) ss2[j]);
			}
		}
		return weights;

	}

	public double[][] computeDistancesAmongTables(SimilarityAssessor sim,
			boolean useWordnet) {

		int i;
		int j;
		double[][] weights;
		Object ss1[] = table1.toArray();
		Object ss2[] = table2.toArray();
		weights = new double[table1.size()][table2.size()];
		for (i = 0; i < table1.size(); i++) {
			for (j = 0; j < table2.size(); j++) {
				weights[i][j] = compare((String) ss1[i], (String) ss2[j], sim,
						useWordnet);
			}
		}
		return weights;

	}
}
