package com.overpass.common;

import java.util.Collection;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.overpass.model.User;

import lombok.Setter;

@Setter
public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = -4057948677347528209L;
	private Collection<? extends GrantedAuthority> authorities;
	private String username;
	private String password;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean enabled;
	private boolean credentialsNonExpired;
	private User user;
	
	public CustomUserDetails() {}
	public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, User user) {
		super();
		this.username = username;
		this.password = password;
		this.enabled = true;
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
		/*this.accountNonExpired = accountNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.enabled = enabled;
		this.credentialsNonExpired = credentialsNonExpired;*/
		this.user = user;
		this.authorities = authorities;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return this.credentialsNonExpired;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonLocked;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	public User getUser() {
		return user;
	}


	
}
