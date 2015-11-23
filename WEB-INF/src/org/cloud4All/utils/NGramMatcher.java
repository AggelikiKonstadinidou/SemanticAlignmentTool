/*
 * This class implements an NGram based Match algorithm.
 * NGram for the Strings to be matched are calculated and
 * Dice Coefficient is applied to find the Similarity.
 * see also http://www.catalysoft.com/articles/StrikeAMatch.html
 */

package org.cloud4All.utils;

import java.util.Vector;

/**
 * 
 * @author nikolaou
 */
public class NGramMatcher {
	int ngram;

	/**
	 * Initializes the NGram Matcher with value of "n" = 2
	 */
	public NGramMatcher() {
		ngram = 2;
	}

	/**
	 * This method takes as argument, two strings to be matched and returns
	 * their Match Score.
	 * <p/>
	 * It preprocesses the Strings, finds there NGrams and applies DICE
	 * COEFFICIENT to the NGram vectors to find the similarity.
	 * <p/>
	 * 
	 * @param str1
	 *            String to be matched
	 * @param str2
	 *            String to be matched
	 * @return Match Value
	 */
	public double compare(String str1, String str2) {
		// Clean strings
		// str1 = str1.toLowerCase() ;
		// str2 = str2.toLowerCase() ;

		PorterStemmer stemmer = new PorterStemmer();

		str1 = stemmer.stem(str1);
		str2 = stemmer.stem(str2);
		// StringBuffer computeStringDiff = new StringBuffer("") ;
		// StringBuffer computeStringSum = new StringBuffer("") ;

		// Find the NGram Vector for first String str1
		NGram nGram = new NGram(str1, ngram);

		Vector gram_vect1 = nGram.getNGramVector();

		// Find the NGram Vector for first String str2
		nGram.setNGramString(str2);
		Vector gram_vect2 = nGram.getNGramVector();

		// Dice Coeficient
		// Get the intersection set of NGram vecotrs
		Vector intersection = intersection(gram_vect1, gram_vect2);

		// Find the Similarity
		double distance = 0.0;
		if ((double) (gram_vect1.size() + gram_vect2.size()) != 0.0)
			distance = 2 * intersection.size()
					/ (double) (gram_vect1.size() + gram_vect2.size());

		return distance;
	}

	/**
	 * This method sets the value of "n" for calculating NGram Vector.
	 * 
	 * @param val
	 *            Value of N for NGram
	 */
	public void setNGramValue(int val) {
		ngram = val;
	}

	/**
	 * This method takes as argument, two NGram Vectors and calcualtes the
	 * intersection of the two NGram vectors.
	 * 
	 * @param a
	 *            NGram Vector
	 * @param b
	 *            NGram Vector
	 * @return Vector contaning intersection of the Vectors a and b
	 */
	private static Vector intersection(Vector a, Vector b) {
		Vector intersection = new Vector();

		for (int i = 0; i < a.size(); i++) {
			for (int j = 0; j < b.size(); j++) {
				Gram gram = (Gram) b.get(j);
				if (gram.getGramString().equals(
						((Gram) a.get(i)).getGramString())) {
					intersection.addElement(gram);
					break;
				}
			}
		}
		return intersection;
	}

	/**
	 * This method gets the similarity of two string for different "n" values
	 * and then gets its average to avoid false positive matches.
	 * 
	 * @param str1
	 *            The first string
	 * @param str2
	 *            The second string
	 * @return The similarity
	 */
	public double getSimilarity(String str1, String str2) {
		double similarity = 0.0;
		String str = " _";
		char blank = str.charAt(0);
		char underline = str.charAt(1);

		str1 = str1.replace(blank, underline);
		str2 = str2.replace(blank, underline);

		// PreProcessText preProcesser = new PreProcessText() ;
		str1 = PreProcessText.preProcess(str1);
		str2 = PreProcessText.preProcess(str2);
		setNGramValue(2);

		double sim2 = compare(str1, str2);

		setNGramValue(3);
		double sim3 = compare(str1, str2);

		setNGramValue(4);
		double sim4 = compare(str1, str2);

		similarity = (sim2 + sim3 + sim4) / 3;

		return similarity;
	}

}