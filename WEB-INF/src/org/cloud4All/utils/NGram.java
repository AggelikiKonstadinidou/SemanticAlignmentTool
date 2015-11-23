/*
 * This class implements NGram algorithm.
 */

package org.cloud4All.utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * <p>
 * This class implements NGram algorithm. Given a string, it finds the NGrams of
 * that string with specified value of "n". This implementation stores nGrams in
 * a hash map for easy access and to avoid duplication.
 * </p>
 * 
 * @author nikolaou
 */
public class NGram {
	// Variables
	private int nGram;
	private Hashtable nGramList;
	private String nGramString;

	// Constructor
	/**
	 * Initializes a NGram object for the String <i>s</i>. This constructor sets
	 * the value of "n" to 2. This one calls the another constructor with n = 2.
	 * 
	 * @param s
	 *            String of which NGrams are to be calculated
	 */
	public NGram(String s) {
		this(s, 2);
	}

	/**
	 * Initializes a NGram object for the String <i>s</i>. This constructor sets
	 * the value of "n" to the second input parameter.
	 * 
	 * @param s
	 *            String of which NGrams are to be calculated
	 * @param n
	 *            Value of n used for calculating NGrams
	 */
	public NGram(String s, int n) {
		nGram = n;
		nGramList = new Hashtable();
		nGramString = s;
	}

	/**
	 * This method calculates the NGrams for a string and stores them in a
	 * Hashtable.
	 */
	public void calculateNGrams() {
		// Get the length of the String
		int len = nGramString.length();

		for (int i = 0; i <= (len - nGram); i++) {
			// Get the substring of the length equal to "n"
			String gr = nGramString.substring(i, i + nGram);

			// Check if that substring is already used for NGram finding
			if (!nGramList.containsKey(gr)) {
				// Creates a new Gram object for the substring
				Gram gram = new Gram(gr, nGramString);

				// Calculate the number of occurences of the
				// substring in the main string
				gram.calculateOccurences();

				// If it is greater than zero then add it to Hashtable
				if (gram.getOccurences() > 0) {
					nGramList.put(gram.getGramString(), gram);
				}
			}
		}
	}

	/**
	 * This method returns the value of "n" used for this nGram algorithm
	 * implementation.
	 * 
	 * @return the value of n for this nGram algorithm
	 */
	public int getNGram() {
		return nGram;
	}

	/**
	 * This method returns the nGrams for a string in format of a Hash-map.
	 * 
	 * @return the hash-map containing the NGrams for a string
	 */
	public Hashtable getNGramList() {
		calculateNGrams();
		return nGramList;
	}

	/**
	 * Get NGrams of a String in Vector format
	 * 
	 * @return Vector of NGram
	 */
	public Vector getNGramVector() {
		Vector gram_vect = new Vector();
		int len = nGramString.length();
		for (int i = 0; i <= (len - nGram); i++) {
			String gr = nGramString.substring(i, i + nGram);
			Gram gram = new Gram(gr, nGramString);

			if (gram.calculateOccurences() > 0) {
				gram_vect.addElement(gram);
			}
		}
		return gram_vect;
	}

	/**
	 * This method sets the value of "n" to be used for calculating nGrams.
	 * 
	 * @param num
	 *            value of "n" to be used
	 */
	public void setNGram(int num) {
		nGram = num;
	}

	/**
	 * This method provides a String representation of the NGram object for a
	 * String.
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();

		Enumeration keys = nGramList.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			Gram g = (Gram) nGramList.get(key);
			str.append("\"" + g.getGramString() + "\" : " + g.getOccurences()
					+ "\n");
		}

		return str.toString();
	}

	/**
	 * @return Returns the String of Which nGrama are Calculated
	 */
	public String getNGramString() {
		return nGramString;
	}

	/**
	 * @param gramString
	 *            The String of which nGrams are to be calculated
	 */
	public void setNGramString(String gramString) {
		nGramString = gramString;
	}
}