package com.expanset.common;

import org.apache.commons.lang3.StringUtils;

/**
 * URL helpers.
 */
public final class URLUtils {

	public static final char URL_SEPARATOR = '/';
	
	/**
	 * Combine URL parts.
	 * @param first First part.
	 * @param second Second part
	 * @return Concatenated URL parts.
	 */
	public static String combine(String first, String second) {
		final StringBuilder result = new StringBuilder();
		
		combine(result, first);
		combine(result, second);

		return result.toString();
	}

	/**
	 * Combine URL parts.
	 * @param first First part.
	 * @param second Second part
	 * @param third Third part
	 * @return Concatenated URL parts.
	 */
	public static String combine(String first, String second, String third) {
		final StringBuilder result = new StringBuilder();
		
		combine(result, first);
		combine(result, second);
		combine(result, third);

		return result.toString();
	}
	
	private static void combine(StringBuilder result, String part) {
		if(StringUtils.isEmpty(part)) {
			return;
		}
		
		if(result.length() == 0) {
			result.append(part);
		} else {
			if(result.charAt(result.length() - 1) == URL_SEPARATOR && part.charAt(0) == URL_SEPARATOR) {
				result.append(part, 1, part.length());
			} else if(result.charAt(result.length() - 1) == URL_SEPARATOR || part.charAt(0) == URL_SEPARATOR) {
				result.append(part);
			} else {
				result.append(URL_SEPARATOR);
				result.append(part);
			}
		}
	}	
	
	private URLUtils() {}
}
