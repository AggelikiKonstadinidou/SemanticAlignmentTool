/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloud4All.utils;

/**
 * 
 * @author kgiannou
 */
public class StringComparison {

	public static double CompareStrings(String stringA, String stringB) {

		if (stringA.equalsIgnoreCase(stringB)) {
			return 1.0;
		} else {
			double score = 0.0;
			WordMatcher matcher = new WordMatcher();
			matcher.formVectors(stringA, stringB);
			double[][] table = matcher.computeDistancesAmongTables();
			double[][] origWeights = new double[table.length][table.length];
			for (int ii = 0; ii < table.length; ii++) {
				for (int jj = 0; jj < table.length; jj++) {
					origWeights[ii][jj] = table[ii][jj];
					table[ii][jj] = 1 - table[ii][jj];
				}
			}

			HungarianAlgorithm kk = new HungarianAlgorithm();
			int result[][] = null;
			if (table.length != 0) {
				result = kk.computeAssignments(table);
				double sum = 0;
				for (int ii = 0; ii < result.length; ii++) {
					sum = sum + origWeights[result[ii][0]][result[ii][1]];
				}
				score = sum / origWeights.length;
				return score;
			} else {
				return 0.0;
			}
		}
	}

	public static void main(String args[]) {
		System.out.println(CompareStrings("author", "writer"));
	}
}
