package net.bluewizardhat.dockerwebapp.config;

import static net.bluewizardhat.dockerwebapp.domain.logic.security.UserRoles.ADMIN;
import static net.bluewizardhat.dockerwebapp.domain.logic.security.UserRoles.PRE_AUTH_USER;
import static net.bluewizardhat.dockerwebapp.domain.logic.security.UserRoles.USER;

import java.io.IOException;

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
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;

import com.allanditzel.springframework.security.web.csrf.CsrfTokenResponseHeaderBindingFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.bluewizardhat.dockerwebapp.domain.logic.security.UserDetailsAdapter;
import net.bluewizardhat.dockerwebapp.rest.data.LoginDetails;
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
							new LoginDetails(adapter.getUser(),
									!adapter.getAuthorities().contains(PRE_AUTH_USER.getAuthority())));
					return;
				}
			}
			log.warn("Unknown authentication type; '{}'", authentication);
			throw new ServletException("Unknown authentication type: " + authentication);
		}
	}

	private class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
		@Override
		public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
			log.debug("Authentication Failure: {} '{}'", exception.getClass().getSimpleName(), exception.getMessage());
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
		}
	}

	private class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
		@Override
		public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
			response.setStatus(HttpStatus.OK.value());
		}
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/api/public/login/login").permitAll()
				.antMatchers("/api/public/login/**").hasRole(PRE_AUTH_USER.name())
				.antMatchers("/api/public/**").permitAll()
				.antMatchers("/api/admin/**").hasRole(ADMIN.name())
				.antMatchers("/api/**").hasRole(USER.name())
			.and()
				.formLogin()
					.loginProcessingUrl("/api/public/login/login")
					.successHandler(new CustomAuthenticationSuccessHandler())
					.failureHandler(new CustomAuthenticationFailureHandler())
			.and()
				.logout()
					.logoutUrl("/api/public/logout")
					.logoutSuccessHandler(new CustomLogoutSuccessHandler())
					.deleteCookies("JSESSIONID", "XSRF-TOKEN")
			.and()
				.csrf()
					.csrfTokenRepository(new CookieCsrfTokenRepository())
		;

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
