package com.overpass;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.overpass.common.CustomAuthentication;
import com.overpass.common.CustomUserDetails;
import com.overpass.model.User;
import com.overpass.reposiroty.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.checkLogin(username);
        if(user == null) {
        	throw new UsernameNotFoundException("user not found");
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole()));
  
        return new CustomUserDetails(user.getUsername(), user.getPassword(), grantedAuthorities, user);
        //return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
         //   grantedAuthorities);
    }

}
