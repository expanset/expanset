package com.expanset.jndi;

import static org.junit.Assert.*;

import java.util.Hashtable;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.expanset.jndi.InMemoryContextFactory;

import static org.mockito.Mockito.*;

public class InMemoryContextTest {

	@Test
	public void addToEnvironment() 
			throws NamingException {
		Hashtable<String, Object> environment = new Hashtable<>();
		environment.put("test", "test");
		final Context context = new InMemoryContextFactory().getInitialContext(environment);
		
		assertEquals("test", context.getEnvironment().get("test"));
	}
	
	@Test
	public void createSubSubcontext() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		
		assertNotNull(subContext);
		assertEquals("test", subContext.getNameInNamespace());

		final Context subSubContext = subContext.createSubcontext("test1");
		
		assertNotNull(subSubContext);
		assertEquals("test/test1", subSubContext.getNameInNamespace());	
	}

	@Test(expected=NameAlreadyBoundException.class)
	public void createSubcontextAlreadyBound() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		context.createSubcontext("test");
		context.createSubcontext("test");
	}
	
	@Test
	public void lookupSubcontext() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		
		assertNotNull(subContext);
		assertEquals("test", subContext.getNameInNamespace());

		final Context subSubContext = subContext.createSubcontext("test1");
		
		assertNotNull(subSubContext);
		assertEquals("test/test1", subSubContext.getNameInNamespace());
		
		final Object obj1 = context.lookup("test");

		assertNotNull(obj1);
		assertTrue(obj1 instanceof Context);
		assertEquals("test", ((Context)obj1).getNameInNamespace());		

		final Object obj2 = context.lookup("test/test1");

		assertNotNull(obj2);
		assertTrue(obj2 instanceof Context);
		assertEquals("test/test1", ((Context)obj2).getNameInNamespace());		
	}
	
	@Test(expected=InvalidNameException.class)
	public void lookupNull1() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		context.lookup((String)null);
	}

	@Test(expected=InvalidNameException.class)
	public void lookupNull2() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		context.lookup((Name)null);
	}

	@Test
	public void lookupEmpty() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);		
		final Context context1 = (Context) context.lookup(StringUtils.EMPTY);
		
		assertTrue(context == context1);
	}	
	
	@Test
	public void bind() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		subContext.createSubcontext("test1");
		
		final Object obj = new Object();
		context.bind("test/test1/test2", obj);
		
		final Object obj1 = context.lookup("test/test1/test2");
		
		assertNotNull(obj1);
	}

	@Test(expected=InvalidNameException.class)
	public void bindNullName1() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		final Object obj = new Object();
		context.bind((String)null, obj);
	}

	@Test(expected=InvalidNameException.class)
	public void bindNullName2() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		final Object obj = new Object();
		context.bind((Name)null, obj);
	}
	
	@Test(expected=NameNotFoundException.class)
	public void nameNotFound() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		subContext.createSubcontext("test1");
		
		context.lookup("test/test1/test2");
	}
	
	@Test(expected=NameAlreadyBoundException.class)
	public void bindAlreadyBound() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		final Object obj = new Object();
		context.bind("test", obj);
		context.bind("test", obj);
	}

	@Test
	public void rebind() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		subContext.createSubcontext("test1");
		
		final Object obj = new Object();
		context.bind("test/test1/test2", obj);
		
		final Object obj2 = new Object();
		context.rebind("test/test1/test2", obj2);
		
		final Object obj3 = context.lookup("test/test1/test2");
		
		assertNotNull(obj3);
		assertTrue(obj2 == obj3);
	}

	@Test(expected=NameNotFoundException.class)
	public void rebindContextNotFound() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		subContext.createSubcontext("test1");
		
		final Object obj = new Object();
		context.bind("test/test1/test2", obj);
		
		final Object obj2 = new Object();
		context.rebind("test/test21/test2", obj2);
	}

	@Test(expected=NameNotFoundException.class)
	public void rebindObjectNotFound() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		subContext.createSubcontext("test1");
		
		final Object obj = new Object();
		context.bind("test/test1/test2", obj);
		
		final Object obj2 = new Object();
		context.rebind("test/test1/test3", obj2);
	}
	
	@Test(expected=InvalidNameException.class)
	public void rebindNullName1() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		final Object obj = new Object();
		context.rebind((String)null, obj);
	}

	@Test(expected=InvalidNameException.class)
	public void rebindNullName2() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		final Object obj = new Object();
		context.rebind((Name)null, obj);
	}

	@Test(expected=InvalidNameException.class)
	public void rebindNullName3() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		final Object obj = new Object();
		context.rebind(StringUtils.EMPTY, obj);
	}

	@Test(expected=InvalidNameException.class)
	public void rebindNullName4() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		final Object obj = new Object();
		context.rebind(new CompositeName(StringUtils.EMPTY), obj);
	}

	@Test
	public void unbind() 
			throws Exception {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		subContext.createSubcontext("test1");

		final AutoCloseable obj = mock(AutoCloseable.class);
		context.bind("test/test1/test2", obj);
		
		context.unbind("test/test1/test2");
		
		boolean found = false;
		try {
			context.lookup("test/test1/test2");
			found = true;
		} catch (NameNotFoundException e) {
		}		
		
		assertFalse(found);
		
		verify(obj, times(1)).close();
	}
	
	@Test(expected=NameNotFoundException.class)
	public void unbindContextNotFound() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		subContext.createSubcontext("test1");
		
		final Object obj = new Object();
		context.bind("test/test1/test2", obj);
		
		context.unbind("test/test21/test2");
	}

	@Test(expected=NameNotFoundException.class)
	public void unbindObjectNotFound() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		subContext.createSubcontext("test1");
		
		final Object obj = new Object();
		context.bind("test/test1/test2", obj);
		
		context.unbind("test/test1/test3");
	}

	@Test(expected=InvalidNameException.class)
	public void unbindNullName1() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		context.unbind((String)null);
	}

	@Test(expected=InvalidNameException.class)
	public void unbindNullName2() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		context.unbind((Name)null);
	}
	
	@Test(expected=InvalidNameException.class)
	public void unbindNullName3() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		context.unbind(StringUtils.EMPTY);
	}

	@Test(expected=InvalidNameException.class)
	public void unbinddNullName4() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		context.unbind(new CompositeName(StringUtils.EMPTY));
	}
	
	@Test
	public void rename() 
			throws Exception {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		subContext.createSubcontext("test1");
		
		final AutoCloseable obj = mock(AutoCloseable.class);
		context.bind("test/test1/test2", obj);
		
		context.rename("test/test1/test2", "test/test1/test3");
		
		final Object obj3 = context.lookup("test/test1/test3");
		
		assertNotNull(obj3);
		assertTrue(obj == obj3);
		
		boolean found = false;
		try {
			context.lookup("test/test1/test2");
			found = true;
		} catch (NameNotFoundException e) {
		}		
		
		assertFalse(found);
		
		verify(obj, times(0)).close();		
	}	

	@Test(expected=NameNotFoundException.class)
	public void renameContextNotFound1() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		subContext.createSubcontext("test1");
		
		final Object obj = new Object();
		context.bind("test/test1/test2", obj);
		
		context.rename("test/test21/test2", "test/test1/test2");
	}

	@Test(expected=NameNotFoundException.class)
	public void renameContextNotFound2() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		subContext.createSubcontext("test1");
		
		final Object obj = new Object();
		context.bind("test/test1/test2", obj);
		
		context.rename("test/test1/test2", "test/test21/test2");
	}
	
	@Test(expected=NameNotFoundException.class)
	public void renameObjectNotFound() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		final Context subContext = context.createSubcontext("test");
		subContext.createSubcontext("test1");
		
		final Object obj = new Object();
		context.bind("test/test1/test2", obj);
		
		context.rename("test/test1/test3", "test/test1/test1");
	}

	@Test(expected=InvalidNameException.class)
	public void renameNullName1() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		context.rename((String)null, "test");
	}

	@Test(expected=InvalidNameException.class)
	public void renameNullName2() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		context.rename((Name)null, new CompositeName("test"));
	}
	
	@Test(expected=InvalidNameException.class)
	public void renameNullName3() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		context.rename(StringUtils.EMPTY, "test");
	}

	@Test(expected=InvalidNameException.class)
	public void renameNullName4() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		
		context.rename(new CompositeName(StringUtils.EMPTY), new CompositeName("test"));
	}

	@Test(expected=InvalidNameException.class)
	public void renameNullName5() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		context.bind("test", new Object());
		
		context.rename("test", (String)null);
	}

	@Test(expected=InvalidNameException.class)
	public void renameNullName6() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		context.bind("test", new Object());
		
		context.rename(new CompositeName("test"), (Name)null);
	}
	
	@Test(expected=InvalidNameException.class)
	public void renameNullName7() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		context.bind("test", new Object());
		
		context.rename("test", StringUtils.EMPTY);
	}

	@Test(expected=InvalidNameException.class)
	public void renameNullName8() 
			throws NamingException {
		final Context context = new InMemoryContextFactory().getInitialContext(null);
		context.bind("test", new Object());
		
		context.rename(new CompositeName("test"), new CompositeName(StringUtils.EMPTY));
	}
	
	
}
