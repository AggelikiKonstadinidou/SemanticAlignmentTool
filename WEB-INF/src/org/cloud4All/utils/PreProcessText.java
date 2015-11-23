/*
 * This class implements a text pre-processor.
 */

package org.cloud4All.utils;

/**
 * 
 * @author nikolaou
 */
public class PreProcessText {

	/**
	 * This method converts the whole String to lower-case and removes
	 * punctuations from it.
	 * 
	 * @param desc
	 *            Text to be pre-processed
	 * @return Pre-processed String
	 */
	public static String preProcess(String desc) {
		// Trim and convert to lower case
		desc = desc.trim().toLowerCase();

		// Remove Punctuation
		desc = removePunctuation(desc);

		return desc.trim();
	}

	/**
	 * This method removes the Punctuation marks from the input String.
	 * 
	 * @param desc
	 *            The String from which punctuation is to be removed
	 * @return String with punctuation marks removed
	 */
	protected static String removePunctuation(String desc) {
		if (desc.length() > 0) {
			// Get each individual character of the String
			char[] c = desc.toCharArray();
			for (int i = 0; i < c.length; i++) {
				// Check if the character is a letter or digit
				if (!Character.isLetterOrDigit(c[i])) {
					c[i] = ' ';
				}
				if (c[i] == '_')
					c[i] = ' ';
			}

			return String.copyValueOf(c);
		} else {
			return "";
		}
	}
}