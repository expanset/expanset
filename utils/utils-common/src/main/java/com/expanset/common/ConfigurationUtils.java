package com.expanset.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.configuration.Configuration;

/**
 * Utilities for {@link org.apache.commons.configuration.Configuration}.
 */
public final class ConfigurationUtils {

	/**
	 * Convert configuration to {@link java.util.Map}.
	 * @param config {@link org.apache.commons.configuration.Configuration}.
	 * @return {@link java.util.Map}.
	 */
	public static Map<String, String> getMap(Configuration config) {
        final Map<String, String> props = new HashMap<>();
        for (Iterator<String> keys = config.getKeys(); keys.hasNext();) {
            String key = keys.next();
            String value = config.getString(key);

            props.put(key, value);
        }

        return props;		
	}
	
	private ConfigurationUtils() {}
}
