package com.expanset.jndi;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.CompoundName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.Reference;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class InMemoryContext implements Context {

	protected final Context parent;
	
	protected final ConcurrentHashMap<String, Object> objects = 
			new ConcurrentHashMap<String, Object>();
	
	protected final ConcurrentHashMap<String, InMemoryContext> subContexts = 
			new ConcurrentHashMap<String, InMemoryContext>();

	protected final Hashtable<String, Object> environment = new Hashtable<>();

	protected final String contextName;

	private static final NameParser NAME_PARSER = new NameParserImpl();
	
	private static final String SEPARATOR = "/";
	
	protected InMemoryContext(Hashtable<?, ?> environment, Context parent, String name) {
		if(environment != null) {
			for(Entry<?, ?> entry : environment.entrySet()) {
				this.environment.put(entry.getKey().toString(), entry.getValue());
			}
		}
			
		this.parent = parent;
		this.contextName = name;
	}	
	
	@Override
	public Object lookup(Name name) 
			throws NamingException {
		if (name == null) {
			throw new InvalidNameException("Name cannot be null");
		}
		if (name.isEmpty()) {
			// NOTE Simply return current context.
			return this;
		}
		
		final Object result = lookupImpl(name);
		if(result == null) {
			throw new NameNotFoundException(name.toString());
		}
		if (result instanceof Reference) {
			throw new OperationNotSupportedException("References not supported");
		}
				
		return result;
	}

	@Override
	public Object lookup(String name) 
			throws NamingException {
		if (name == null) {
			throw new InvalidNameException("Name cannot be null");
		}		
		return lookup(new CompositeName(name));
	}

	@Override
	public void bind(Name name, Object obj) 
			throws NamingException {
		add(name, obj, false);
	}	

	@Override
	public void bind(String name, Object obj) 
			throws NamingException {
		if (name == null) {
			throw new InvalidNameException("Name cannot be null");
		}		
		bind(new CompositeName(name), obj);
	}

	@Override
	public void rebind(Name name, Object obj) 
			throws NamingException {
		if (name == null || name.isEmpty()) {
			throw new InvalidNameException("Name cannot be empty");
		}

		final String namePart = getNameFirstPart(name);
		if (name.size() == 1) {
			final Object prevObj = objects.replace(namePart, obj);
			if(prevObj == null) {
				throw new NameNotFoundException("Name not found: " + name);
			}
		} else {
			final Context context = subContexts.get(namePart); 
			if (context != null) {
				context.rebind(name.getSuffix(1), obj);
			} else {
				throw new NameNotFoundException("Name not found: " + name);
			}
		}
	}

	@Override
	public void rebind(String name, Object obj) 
			throws NamingException {
		if (name == null) {
			throw new InvalidNameException("Name cannot be null");
		}		
		rebind(new CompositeName(name), obj);
	}

	@Override
	public void unbind(Name name) 
			throws NamingException {
		final Object obj = remove(name);
		closeObject(obj);
	}

	@Override
	public void unbind(String name) 
			throws NamingException {
		if (name == null) {
			throw new InvalidNameException("Name cannot be null");
		}		
		unbind(new CompositeName(name));		
	}

	@Override
	public void rename(Name oldName, Name newName) 
			throws NamingException {
		if (oldName == null || oldName.isEmpty()) {
			throw new InvalidNameException("Old name cannot be empty");
		}
		if (newName == null || newName.isEmpty()) {
			throw new InvalidNameException("New name cannot be empty");
		}	
		
		final Object obj = lookup(oldName);
		if(obj instanceof Context) {
			throw new OperationNotSupportedException("Context renaming not supported");
		} else {
			remove(oldName);
			add(newName, obj, false);
		}
	}

	@Override
	public void rename(String oldName, String newName) 
			throws NamingException {
		if (StringUtils.isEmpty(oldName)) {
			throw new InvalidNameException("Old name cannot be empty");
		}
		if (StringUtils.isEmpty(newName)) {
			throw new InvalidNameException("New name cannot be empty");
		}		
		rename(new CompositeName(oldName), new CompositeName(newName));
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name) 
			throws NamingException {
		if (name == null || name.isEmpty()) {
			final List<NameClassPair> result = new LinkedList<NameClassPair>(); 
			subContexts.forEach((key, item) -> 
				result.add(new NameClassPair(item.contextName, Context.class.getName())));
			objects.forEach((key, item) ->
				result.add(new NameClassPair(key, item.getClass().getName())));
			return getNamingEnumerator(result);
		} else {
			final String namePart = getNameFirstPart(name);	
			final Context context = subContexts.get(namePart); 
			if (context != null) {
				return context.list(name.getSuffix(1));
			} else {
				throw new NameNotFoundException("Name not found: " + name);
			}
		}
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name) 
			throws NamingException {
		if(name == null) {
			name = StringUtils.EMPTY;
		}
		return list(new CompositeName(name));
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name) 
			throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name) 
			throws NamingException {
		if(name == null) {
			name = StringUtils.EMPTY;
		}		
		return listBindings(new CompositeName(name));
	}

	@Override
	public void destroySubcontext(Name name) 
			throws NamingException {
		if (name == null || name.isEmpty()) {
			throw new InvalidNameException("Name cannot be empty");
		}

		final String namePart = getNameFirstPart(name);
		if (name.size() == 1) {
			final Context subContext = subContexts.remove(namePart);
			if (subContext != null) {
				subContext.close();
			} else {
				throw new NameNotFoundException("Name not found: " + name);
			}
		} else {
			final Context context = subContexts.get(namePart); 
			if (context != null) {
				 context.destroySubcontext(name.getSuffix(1));
			} else {
				throw new NameNotFoundException("Name not found: " + name);
			}
		}
	}

	@Override
	public void destroySubcontext(String name) 
			throws NamingException {
		if (name == null) {
			throw new InvalidNameException("Name cannot be null");
		}		
		destroySubcontext(new CompositeName(name));
	}

	@Override
	public Context createSubcontext(Name name) 
			throws NamingException {
		if (name == null || name.isEmpty()) {
			throw new InvalidNameException("Name cannot be empty");
		}

		final String namePart = getNameFirstPart(name);
		if (name.size() == 1) {
			final MutableBoolean nameExist = new MutableBoolean(true);
			try {
				final InMemoryContext subContext = subContexts.computeIfAbsent(namePart, (key) -> { 
					nameExist.setValue(false);
					try {
						return createContext(this, key);
					} catch (Throwable e) {
						throw new RuntimeException(e);
					}
				});			
				if(nameExist.booleanValue()) {
					throw new NameAlreadyBoundException("Name exist: " + name);
				}			
				return subContext;
			} catch(RuntimeException e) {
				if(e.getCause() instanceof NamingException) {
					throw (NamingException)e.getCause();
				}
				throw e;
			}
		} else {
			final Context context = subContexts.get(namePart); 
			if (context != null) {
				 return context.createSubcontext(name.getSuffix(1));
			} else {
				throw new NameNotFoundException("Name not found: " + name);
			}
		}		
	}

	@Override
	public Context createSubcontext(String name) 
			throws NamingException {
		if (name == null) {
			throw new InvalidNameException("Name cannot be null");
		}		
		return createSubcontext(new CompositeName(name));
	}

	@Override
	public Object lookupLink(Name name) 
			throws NamingException {
		throw new OperationNotSupportedException();
	}

	@Override
	public Object lookupLink(String name) 
			throws NamingException {
		if(name == null) {
			name = StringUtils.EMPTY;
		}			
		return lookupLink(new CompositeName(name));
	}

	@Override
	public NameParser getNameParser(Name name) 
			throws NamingException {
		return NAME_PARSER;
	}

	@Override
	public NameParser getNameParser(String name) 
			throws NamingException {
		if(name == null) {
			name = StringUtils.EMPTY;
		}			
		return getNameParser(new CompositeName(name));
	}

	@Override
	public Name composeName(Name name, Name prefix) 
			throws NamingException {
		if (name == null) {
			throw new InvalidNameException("Name cannot be null");
		}
		if (prefix == null) {
			throw new InvalidNameException("Prefix cannot be null");
		}

		return new CompositeName().addAll(prefix).addAll(name);
	}

	@Override
	public String composeName(String name, String prefix) 
			throws NamingException {
		if (name == null) {
			throw new InvalidNameException("Name cannot be null");
		}
		if (prefix == null) {
			throw new InvalidNameException("Prefix cannot be null");
		}
		
		final StringBuilder result = new StringBuilder(prefix.length() + name.length() + 1);
		result.append(prefix);

		if(!StringUtils.isEmpty(name)) {
			if(prefix.length() > 0 && !prefix.endsWith(SEPARATOR)) {
				result.append(SEPARATOR);
			}
			result.append(name);
		}

		return result.toString();	
	}

	@Override
	public Object addToEnvironment(String propName, Object propVal) 
			throws NamingException {
		if (propName == null) {
			throw new InvalidNameException("PropName cannot be null");
		}		
		
		Object oldValue = environment.get(propName);
		environment.put(propName, propVal);
		return oldValue;
	}

	@Override
	public Object removeFromEnvironment(String propName) 
			throws NamingException {
		if (propName == null) {
			throw new InvalidNameException("PropName cannot be null");
		}		
		
		return environment.remove(propName);
	}

	@Override
	public Hashtable<?, ?> getEnvironment() 
			throws NamingException {
		return (Hashtable<?, ?>) environment.clone();
	}

	@Override
	public void close() 
			throws NamingException {
		for(Object obj : objects.values().toArray(new Object[0])) {
			closeObject(obj);
		}
		for(Context subContext : subContexts.values().toArray(new Context[0])) {
			subContext.close();
		}
	}

	@Override
	public String getNameInNamespace() 
			throws NamingException {
		if (parent == null) {
			return StringUtils.EMPTY;
		}

		return composeName(contextName, parent.getNameInNamespace());
	}
	
	protected InMemoryContext createContext(Context parent, String name) 
			throws NamingException {
		return  new InMemoryContext(environment, parent, name);
	}
	
	protected void closeObject(Object obj) {
		if(obj instanceof AutoCloseable) {
			try {
				((AutoCloseable) obj).close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	protected Object lookupImpl(Name name) 
			throws NamingException, NameNotFoundException {
		final String namePart = getNameFirstPart(name);
		final InMemoryContext context = subContexts.get(namePart);
		
		if (name.size() == 1) {
			final Object obj = objects.get(namePart);
			if(obj != null) {
				return obj;
			} else if(context != null) {
				return context;
			}
		} else if(context != null) {
			return context.lookupImpl(name.getSuffix(1));
		}
		
		return null;
	}	
	
	protected void add(Name name, Object obj, boolean createSubContexts) 
			throws NamingException {
		if (name == null || name.isEmpty()) {
			throw new InvalidNameException("Name cannot be empty");
		}
		
		final String namePart = getNameFirstPart(name);
		if (name.size() == 1) {
			final MutableBoolean nameExist = new MutableBoolean(true);
			objects.computeIfAbsent(namePart, (key) -> { 
				nameExist.setValue(false);
				return obj;
			});			
			if(nameExist.booleanValue()) {
				throw new NameAlreadyBoundException("Name exist: " + name);
			}			
		} else {
			InMemoryContext context = subContexts.get(namePart); 
			if (context == null) {
				if(createSubContexts) {
					try {
						context = subContexts.computeIfAbsent(namePart, (key) -> { 
							try {
								return createContext(this, key);
							} catch (Throwable e) {
								throw new RuntimeException(e);
							}
						});			
					} catch(RuntimeException e) {
						if(e.getCause() instanceof NamingException) {
							throw (NamingException)e.getCause();
						}
						throw e;
					}
				} else {
					throw new NameNotFoundException("Name not found: " + name);
				}
			}
			context.add(name.getSuffix(1), obj, createSubContexts);
		}		
	}	
	
	protected Object remove(Name name) 
			throws NamingException {
		if (name == null || name.isEmpty()) {
			throw new InvalidNameException("Name cannot be empty");
		}
		
		final String namePart = getNameFirstPart(name);
		if (name.size() == 1) {
			final Object obj = objects.remove(namePart);
			if (obj != null) {
				return obj;
			} else {
				throw new NameNotFoundException("Name not found: " + name);
			}
		} else {
			final InMemoryContext context = subContexts.get(namePart); 
			if (context != null) {
				 return context.remove(name.getSuffix(1));
			} else {
				throw new NameNotFoundException("Name not found: " + name);
			}
		}
	}

	private String getNameFirstPart(Name name) 
			throws NamingException {
		String namePart = name.get(0);
		if (namePart.startsWith("java:")) {
			namePart = namePart.substring(5);
			if (namePart.isEmpty()) {
				throw new InvalidNameException("Invalid name, see http://docs.oracle.com/javase/jndi/tutorial/beyond/misc/policy.html");
			}
		}
		return namePart;
	}
	
	private NamingEnumeration<NameClassPair> getNamingEnumerator(List<NameClassPair> list) {
		final Iterator<NameClassPair> i = list.iterator();
		return new NamingEnumeration<NameClassPair>() {

			@Override
			public boolean hasMoreElements() {
				return i.hasNext();
			}

			@Override
			public NameClassPair nextElement() {
				return i.next();
			}

			@Override
			public NameClassPair next() 
					throws NamingException {
				return i.next();
			}

			@Override
			public boolean hasMore() 
					throws NamingException {
				return i.hasNext();
			}

			@Override
			public void close() 
					throws NamingException {
			}			
		};
	}	
	
	private static class NameParserImpl implements NameParser {
		
		private static final Properties syntax = new Properties();	
		
		static {
			syntax.put("jndi.syntax.direction", "left_to_right");
			syntax.put("jndi.syntax.separator", "/");
			syntax.put("jndi.syntax.ignorecase", "false");
			syntax.put("jndi.syntax.escape", "\\");
			syntax.put("jndi.syntax.beginquote", "'");
			syntax.put("jndi.syntax.beginquote2", "\"");
		}
		
		public Name parse(String name) 
				throws NamingException {
			return new CompoundName(name, syntax);
		}
	}	
}
