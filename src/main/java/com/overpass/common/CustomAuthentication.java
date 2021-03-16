package com.overpass.common;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.overpass.model.User;

public class CustomAuthentication extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;
	private final Object principal;
	private Object credentials;
	
	public CustomAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, User user) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		super.setDetails(user);
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

}
