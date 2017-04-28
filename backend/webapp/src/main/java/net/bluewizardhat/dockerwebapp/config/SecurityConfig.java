package net.bluewizardhat.dockerwebapp.config;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;

import com.allanditzel.springframework.security.web.csrf.CsrfTokenResponseHeaderBindingFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.domain.logic.security.UserDetailsAdapter;
import net.bluewizardhat.dockerwebapp.domain.logic.security.UserRoles;
import net.bluewizardhat.dockerwebapp.rest.data.UserDetails;
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

	@Autowired
	private ObjectMapper objectMapper;

	private class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

		@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
			if (authentication instanceof UsernamePasswordAuthenticationToken) {
				UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
				if (token.getPrincipal() instanceof UserDetailsAdapter) {
					UserDetailsAdapter adapter = (UserDetailsAdapter) token.getPrincipal();
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					response.setStatus(HttpStatus.OK.value());
					response.getOutputStream();
					objectMapper.writeValue(response.getOutputStream(),
							new UserDetails(adapter.getUser(),
									adapter.getAuthorities().stream()
										.map(a -> a.getAuthority())
										.collect(Collectors.toList())));
					return;
				}
			}
			log.warn("Unknown authentication type; '{}'", authentication);
			response.setStatus(HttpStatus.NO_CONTENT.value());
		}
	}

	private class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
		@Override
		public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
			log.debug("Authentication Failure: {} '{}'", exception.getClass().getSimpleName(), exception.getMessage());
			response.setStatus(HttpStatus.FORBIDDEN.value());
		}
	}

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
					.successHandler(new CustomAuthenticationSuccessHandler())
					.failureHandler(new CustomAuthenticationFailureHandler())
			.and()
				.logout()
					.logoutUrl("/logout")
					.deleteCookies("JSESSIONID")
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
