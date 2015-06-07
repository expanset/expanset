package com.expanset.samples.complex.entities;

import java.util.Date;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.commons.lang.Validate;
import org.glassfish.hk2.api.PerLookup;
import org.jasypt.digest.StandardStringDigester;
import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.annotations.Service;

@Service
@Contract
@PerLookup
public class UserRepository {

	@PersistenceContext
	private EntityManager entityManager;
	
	private final StandardStringDigester passwordDigester = new StandardStringDigester();	
	
	public UserRepository() {
		passwordDigester.setAlgorithm("SHA-1");
	}	
	
	public boolean isUserExists(@Nullable String login) {
		final TypedQuery<Long> checkUserExists =
				entityManager.createNamedQuery("User.isExists", Long.class);
		checkUserExists.setParameter("login", login);
		final long counter = checkUserExists.getSingleResult();
	    if (counter > 0) {
	        return true;
	    }
	    return false;		
	}
	
	@Transactional
	public void createUser(@Nonnull User user, String password) 
			throws EntityExistsException {
		Validate.notNull(user, "user");
		
		if(isUserExists(user.getLogin())) {
			throw new EntityExistsException();
		}
		
		user.setCreated(new Date());
		user.setPassword(passwordDigester.digest(password));
		entityManager.persist(user);
		try {
			entityManager.flush();
		} catch (PersistenceException e) {
			EntityExistsException.throwIfExist(e);
			throw e;
		}
	}

	@Transactional
	public User updateUser(long userId, String password) 
			throws EntityNotFoundException {
		final User user = getUserById(userId);
		user.setPassword(passwordDigester.digest(password));
		
		entityManager.flush();
		
		return user;
	}
	
	@Transactional
	public void updateStockQuoteDate(long userId, Date stockQuoteDate) 
			throws EntityNotFoundException {
		final User user = getUserById(userId);
		user.setStockQuoteDate(stockQuoteDate);
		
		entityManager.flush();
	}
	
	public User getUserById(long id) 
			throws EntityNotFoundException {
		final User user = entityManager.find(User.class, id);
		if(user == null) {
			throw new EntityNotFoundException();
		}
		
		return user;
	}
	
	public Optional<User> findUserById(long id) {	
		final User user = entityManager.find(User.class, id);
		if(user != null) {
			return Optional.of(user);
		}
		
		return Optional.empty();
	}
	
	public Optional<User> findUserByLogin(@Nullable String login) {	
		final TypedQuery<User> findUserByLoginQuery = 
				entityManager.createNamedQuery("User.findByLogin", User.class);
		findUserByLoginQuery.setParameter("login", login);
		findUserByLoginQuery.setMaxResults(1);
		
		return findUserByLoginQuery.getResultList().stream().findFirst();
	}

	public Optional<User> findUserByLogin(@Nullable String login, @Nullable String password) {
		final Optional<User> user = findUserByLogin(login);
		if(!user.isPresent()) {
			return Optional.empty();
		}
		
		if(!passwordDigester.matches(password, user.get().getPassword())) {
			return Optional.empty();
		}

		return user;
	}
}
