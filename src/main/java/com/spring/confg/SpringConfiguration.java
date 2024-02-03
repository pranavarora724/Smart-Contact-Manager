package com.spring.confg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.spring.config.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SpringConfiguration 
{
	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public UserDetailsService getDetailsService()
	{
		return new UserDetailsServicesImpl();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
	
		
		daoAuthenticationProvider.setUserDetailsService(getDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return daoAuthenticationProvider;
		
	}
	
	@Bean
	 public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception
	{
		return http.csrf()
		.disable()
		.authorizeHttpRequests()
		.requestMatchers("/user/**").hasRole("USER")
		.requestMatchers("/home").permitAll()
		.requestMatchers("/about").permitAll()
		.requestMatchers("/signup").permitAll()
		.requestMatchers("/doregister").permitAll()
		.requestMatchers("/logout").permitAll()

		.requestMatchers("/css/**").permitAll()
		.requestMatchers("/js/**").permitAll()
		.requestMatchers("/img/**").permitAll()
	
		.and()
		.formLogin()
		.loginPage("/signin").permitAll()
		.loginProcessingUrl("/dologin").permitAll()
		.defaultSuccessUrl("/user/dashboard").permitAll()
//		.failureUrl("/login-fail")
		.and().build();
		}
}
