package org.cloud4All;

public class MyFunctions {

	static public String cameliseString(String str) {
		StringBuffer result = new StringBuffer(str.length());
		String strl = str.toLowerCase();
		boolean bMustCapitalize = false;
		for (int i = 0; i < strl.length(); i++) {
			char c = strl.charAt(i);
			if (c >= 'a' && c <= 'z') {
				if (bMustCapitalize) {
					result.append(strl.substring(i, i + 1).toUpperCase());
					bMustCapitalize = false;
				} else {
					result.append(c);
				}

			} else {
				bMustCapitalize = true;
			}

		}
		return result.toString();
	}

}
