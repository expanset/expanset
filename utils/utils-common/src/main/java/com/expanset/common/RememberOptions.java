package com.expanset.common;

import javax.annotation.Nullable;

/**
 * Base class for options to remember something.
 */
public class RememberOptions {
	
	protected final Integer maxAge;

	public RememberOptions() {
		this.maxAge = null;
	}
	
	/**
	 * @param maxAge Seconds to remember something (null - current session).
	 */
	public RememberOptions(@Nullable Integer maxAge) {
		this.maxAge = maxAge;
	}

	/**
	 * @return Seconds to remember something (null - current session).
	 */
	public Integer getMaxAge() {
		return maxAge;
	}
}
