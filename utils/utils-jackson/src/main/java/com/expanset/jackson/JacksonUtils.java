package com.expanset.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Supplementary methods for JSON of Jackson library.
 */
public final class JacksonUtils {

	public static ObjectMapper createObjectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new ISO8601DateFormat());
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return objectMapper;
	}

	private JacksonUtils() {}
}
