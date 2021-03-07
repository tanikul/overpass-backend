package com.overpass;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.overpass.reposiroty.UserRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
	@Autowired
	private UserRepository userRepository;
	
	//@Autowired
    //private CustomAuthenticationProvider authProvider;
	
	//@Autowired
    //private UserDetailsService userDetailsService;
	@Bean
	  public UserDetailsService userDetailsService() {
	    return new UserDetailsServiceImpl();
	  };
	  
	 //@Bean
	    CorsFilter corsFilter() {
	        CorsFilter filter = new CorsFilter();
	        return filter;
	    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
        
        		.antMatchers("/websocket/**").permitAll() 
        		.antMatchers(HttpMethod.POST, "/login").permitAll() 
                .antMatchers("/").permitAll() 
                .antMatchers("/api/**").authenticated()
                //.anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new JwtAuthenticationExceptionEntryPoint())

                .and()
                .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                
                ;
    	//http.authorizeRequests().anyRequest().permitAll();
    }
    
 
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }
 
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
 
       /* String password = "123";
 
        String encrytedPassword = this.passwordEncoder().encode(password);
        System.out.println("Encoded password of 123=" + encrytedPassword);
 
        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> //
        mngConfig = auth.inMemoryAuthentication();
 
        // Defines 2 users, stored in memory.
        // ** Spring BOOT >= 2.x (Spring Security 5.x)
        // Spring auto add ROLE_
        userRepository.getUserByUsername(auth)
        UserDetails u1 = User.withUsername("tom").password(encrytedPassword).roles("USER").build();
 
        mngConfig.withUser(u1);*/
    	//auth.authenticationProvider(authProvider);
    	auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }
    
    /*@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }*/
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
     
}