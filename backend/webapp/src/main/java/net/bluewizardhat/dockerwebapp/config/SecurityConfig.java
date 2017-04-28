package net.bluewizardhat.dockerwebapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;

import com.allanditzel.springframework.security.web.csrf.CsrfTokenResponseHeaderBindingFilter;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.domain.logic.security.UserRoles;
import net.bluewizardhat.dockerwebapp.util.security.CustomAuthenticationProvider;
import net.bluewizardhat.dockerwebapp.util.security.UserdetailsServiceImpl;

@Slf4j
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserdetailsServiceImpl userDetailsService;

	@Autowired
	@Qualifier("argon2PasswordEncoder")
	private PasswordEncoder passwordEncoder;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/api/public/**").permitAll()
				.antMatchers("/api/admin/**").hasRole(UserRoles.ADMIN.name())
				.antMatchers("/api/**").hasRole(UserRoles.USER.name())
				.antMatchers("/login/login").permitAll()
				.antMatchers("/login/**").hasRole(UserRoles.PRE_AUTH_USER.name())
				.antMatchers("/logout/**").permitAll()
			.and()
				.formLogin()
					.loginPage("/login.html")
					.loginProcessingUrl("/login/login")
					.failureUrl("/login.html?login-error")
					.defaultSuccessUrl("/login.html?pre_auth", true)
			.and()
				.logout()
					.logoutUrl("/logout")
		;

		http.csrf().disable();
		// Add CSRF token to response headers
		http.addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class);

		log.debug("Configured http security");
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, AuthenticationProvider authenticationProvider) throws Exception {
		auth
			.authenticationProvider(authenticationProvider)
			.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder)
			;
		log.debug("Configured authentication methods");
	}

}
