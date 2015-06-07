package com.expanset.jackson;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

/**
 * ISO8601 date format for JSON of Jackson library.
 */
public class ISO8601DateFormat extends com.fasterxml.jackson.databind.util.ISO8601DateFormat {

    private final static String PATTERN = "yyyy-MM-dd";
    
    private final static TimeZone GMT = TimeZone.getTimeZone("GMT");
    
	private final static long serialVersionUID = 1L;
	
	@Override
    public Date parse(String source, ParsePosition pos) {
        try {
            return parseImpl(source, pos);
        }
        catch (ParseException e) {
            return null;
        }
    }

    @Override
    public Date parse(String source) 
    		throws ParseException {
    	return parseImpl(source, new ParsePosition(0));
    }
    
    /**
     * Attempt to recognize a date and time or date only.
     * @param source The text for recognition.
     * @param pos Current position.
     * @return The date recognized from a string.
     * @throws ParseException Parse error.
     */
    protected Date parseImpl(String source, ParsePosition pos)
    		throws ParseException {
    	if(StringUtils.isEmpty(source)) {
    		return null;
    	}
    	
    	if(source.length() - pos.getIndex() == PATTERN.length() ||
    			source.length() - pos.getIndex() + 1 == PATTERN.length()) {
    		// Date only.
    		return parseDateImpl(source, pos);
    	}
    	
        try {
        	return ISO8601Utils.parse(source, pos);
        }
        catch (ParseException e) {
    		// Date only.
        	return parseDateImpl(source, pos);
        }
    }
    
    protected Date parseDateImpl(String source, ParsePosition pos) {
    	final SimpleDateFormat df = new SimpleDateFormat(PATTERN, Locale.ROOT);
    	df.setTimeZone(GMT);
    	return df.parse(source, pos);
    }
}
