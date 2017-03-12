package net.bluewizardhat.dockerwebapp.web;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/api/public/**").permitAll()
				.antMatchers("/api/login/**").permitAll()
				.antMatchers("/api/logout/**").permitAll()
			.and()
				.formLogin()
					.loginPage("/login")
					.failureUrl("/login-error")
		;
	}

}
