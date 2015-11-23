/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cloud4All.utils;

/**
 * 
 * @author kgiannou
 */

public class WordNotFoundException extends Exception {

	/**
	 * The constructor.
	 * 
	 * @param err
	 *            String The error message that is to be shown with the
	 *            exception.
	 */
	public WordNotFoundException(String err) {
		super(err);
	}

}
