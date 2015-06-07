package com.expanset.common;

import javax.annotation.Nonnull;

/**
 * Holds other objects.
 */
public interface Wrapper {

	/**
	 * Returns other object with the required type.
	 * @param iface Type of holding object.
	 * @param <T> Type of holding object.
	 * @return Other object with the required type.
	 * @throws Exception Unwrap error.
	 */
	default <T> T unwrap(@Nonnull Class<T> iface) 
			throws Exception {
		return null;
	}
	
	/**
	 * Check unwrap support of desired object.
	 * @param iface Type of holding object.
	 * @return true - object type is supported.
	 */
	default boolean isWrapperFor(@Nonnull Class<?> iface) {
		return false;
	}
}
