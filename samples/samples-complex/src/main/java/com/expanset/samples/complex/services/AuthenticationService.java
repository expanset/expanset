package com.expanset.samples.complex.services;

import java.util.Date;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.glassfish.hk2.api.PerLookup;
import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.annotations.Service;

import com.expanset.hk2.config.ConfiguredInteger;
import com.expanset.hk2.security.AbstractCredentials;
import com.expanset.hk2.security.AuthenicationResult;
import com.expanset.hk2.security.TokenCredentials;
import com.expanset.samples.complex.entities.User;
import com.expanset.samples.complex.entities.UserRepository;

@Service
@Contract
@PerLookup
public class AuthenticationService implements com.expanset.hk2.security.AuthenticationService {

	@Inject
	private UserRepository accountService;
	
	@ConfiguredInteger("authenticationMaxAge")
	private int authenticationMaxAge;
			
	public Optional<User> authenticate(String login, String password) {
		final Optional<User> user = accountService.findUserByLogin(login, password);
		return user;
	}

	@Override
	public Optional<AuthenicationResult> authenticate(AbstractCredentials credentials) {
		if(!(credentials instanceof TokenCredentials)) {
			return Optional.empty();
		} 
			
		final TokenCredentials tokenCredentials = (TokenCredentials)credentials;
		
		if(authenticationMaxAge != 0 && 
				DateUtils.addSeconds(tokenCredentials.getTokenCreationDate(), authenticationMaxAge).getTime() < new Date().getTime()) {
			return Optional.empty();
		}
		
		final String[] tokenParts = StringUtils.split(tokenCredentials.getToken(), ':');
		if(tokenParts.length != 2) {
			return Optional.empty();
		}
		
		final long userId = Long.parseLong(tokenParts[0]);
		final Optional<User> user = accountService.findUserById(userId);
		if(!user.isPresent()) {
			return Optional.empty();
		}

		final String passwordMD5 = tokenParts[1];
		if(!StringUtils.equals(DigestUtils.md2Hex(user.get().getPassword()), passwordMD5)) {
			return Optional.empty();
		}
		
		return Optional.of(new AuthenicationResult(new SitePrincipal(user.get())));
	}

	public TokenCredentials generateAuthenticationToken(User user) {
		final String token = Long.toString(user.getId()) + ":" + DigestUtils.md2Hex(user.getPassword());
		return new TokenCredentials(token);
	}
}
