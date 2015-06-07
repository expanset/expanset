package com.expanset.common.errors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

/**
 * Utilities to convert checked exceptions to unchecked.
 */
public final class ExceptionAdapter {

	@FunctionalInterface
	public interface Exceptionable {
		
		void run() throws Exception;
	}

	@FunctionalInterface
	public interface ExceptionableSupplier<T> {
		
		T get() throws Exception;
	}

	/**
	 * Adapts code with checked exception.
	 * @param exceptionable Code to run.
	 */
	public static void run(Exceptionable exceptionable) {
		try {
			exceptionable.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adapts code with checked exception, that returns value.
	 * @param exceptionable Code to run.
	 * @param <T> Type of returning value.
	 * @return Result of run code.
	 */
	public static <T> T get(ExceptionableSupplier<T> exceptionable) {
		try {
			return exceptionable.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Catches all errors when processing code for each element in the list and throw {@link MultiErrorException}.
	 * @param items List of processing items.
	 * @param operations List of operations for each item.
	 * @param <T> Type of returning value.
	 */
    @SafeVarargs
	public static <T> void run(Iterable<T> items, Consumer<T> ... operations) {
    	assert items != null;
    	
    	run(null, items, operations);
    }
	
    /**
	 * Catches all errors when processing code for each element in the list and throw {@link MultiErrorException}.
     * @param cause Initial error for {@link MultiErrorException}.
	 * @param items List of processing items.
	 * @param operations List of operations for each item.
	 * @param <T> Type of returning value.
     */
    @SafeVarargs
	public static <T> void run(Throwable cause, Iterable<T> items, Consumer<T> ... operations) {
    	assert items != null;
    	
    	List<Throwable> errors = null;
    	for(T item : items) {
			for(Consumer<T> operation : operations) {
	    		try {
	    			operation.accept(item);	
	    		} catch (Throwable e) {
	    			if(errors == null) {
	    				errors = new ArrayList<>(2);
	    			}
	    			errors.add(e);
	    		}
			}
    	}
    	if(errors != null) {
    		if(cause != null) {
    			throw new MultiErrorException(errors, cause);	
    		} else {
    			throw new MultiErrorException(errors);
    		}
    	} else if(cause != null) {
    		throw new MultiErrorException(cause);
    	}
    } 	

    /**
     * Closes object that implements {@link AutoCloseable}.
     * @param obj Object to close.
     */
	public static void close(Object obj) {
		try {
			if(obj instanceof AutoCloseable) {
				((AutoCloseable)obj).close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Closes object that implements {@link AutoCloseable}. If error occurs, it will be write to log.
	 * @param obj Object to close.
	 * @param log Access to log.
	 */
	public static void closeQuitely(Object obj, Logger log) {
		try {
			if(obj instanceof AutoCloseable) {
				((AutoCloseable)obj).close();
			}
		} catch (Exception e) {
			log.warn(MessageFormatter.format("Error when closing {}", obj).getMessage(), e);
		}		
	}
	
	private ExceptionAdapter() {};
}
