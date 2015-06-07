package com.expanset.common.errors;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Holder for multiple exceptions.
 */
public class MultiErrorException extends RuntimeException {

	protected final Object lock = new Object();

    protected final List<Throwable> throwables = new LinkedList<Throwable>();
    
	private final static long serialVersionUID = 1L;
	
    public MultiErrorException() {
        super();
    }

    /**
     * @param errors List of error for this container.
     */
    public MultiErrorException(List<Throwable> errors) {
        super(errors.get(0).getMessage(), errors.get(0));

        for (Throwable error : errors) {
            if (error instanceof MultiErrorException) {
            	final MultiErrorException me = (MultiErrorException) error;                
                throwables.addAll(me.throwables);
            }
            else {
                throwables.add(error);
            }
        }
    }

    /**
     * @param cause Previous error.
     */
    public MultiErrorException(Throwable cause) {
        super(cause.getMessage(), cause);

        if (cause instanceof MultiErrorException) {
        	final MultiErrorException me = (MultiErrorException) cause;
            throwables.addAll(me.throwables);
        }
        else {
            throwables.add(cause);
        }
    }

    /**
     * @param errors List of errors for this container.
     * @param cause Previous error.
     */
    public MultiErrorException(List<Throwable> errors, Throwable cause) {
        super(cause.getMessage(), cause);

        for (Throwable error : errors) {
            if (error instanceof MultiErrorException) {
            	final MultiErrorException me = (MultiErrorException) error;                
                throwables.addAll(me.throwables);
            }
            else {
                throwables.add(error);
            }
        }
    }
        
    /**
     * @return Error list in the container.
     */
    public List<Throwable> getErrors() {
        synchronized (lock) {
            return Collections.unmodifiableList(new ArrayList<>(throwables));
        }
    }

    /**
     * Adds an error to this container.
     * @param error Error to add to this container.
     */
    public void addError(Throwable error) {
    	assert error != null;
    	
        synchronized (lock) {
            throwables.add(error);
        }
    }

    @Override
    public String getMessage() {
        final List<Throwable> listCopy = getErrors();
        final StringBuffer sb = new StringBuffer("A MultiErrorException has ");
        sb.append(listCopy.size());
        sb.append(" exceptions.  They are:");
        sb.append(System.lineSeparator());
        
        int lcv = 1;
        for (Throwable error : listCopy) {
        	sb.append(lcv++);
        	sb.append(". ");
        	sb.append(error.getClass().getName());
        	sb.append(((error.getMessage() != null) ? ": " + error.getMessage() : "" ));
            sb.append(System.lineSeparator());
        }
        
        return sb.toString();
    }
    
    @Override
    public void printStackTrace(PrintStream s) {
        final List<Throwable> listCopy = getErrors();        
        if (listCopy.size() <= 0) {
            super.printStackTrace(s);            
            return;
        }
        
        int lcv = 1;
        for (Throwable error : listCopy) {
            s.println("MultiException stack " + lcv++ + " of " + listCopy.size());
            error.printStackTrace(s);
        }
    }
    
    @Override
    public void printStackTrace(PrintWriter s) {
        final List<Throwable> listCopy = getErrors();
        if (listCopy.size() <= 0) {
            super.printStackTrace(s);
            return;
        }
        
        int lcv = 1;
        for (Throwable error : listCopy) {
            s.println("MultiException stack " + lcv++ + " of " + listCopy.size());
            error.printStackTrace(s);
        }
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
