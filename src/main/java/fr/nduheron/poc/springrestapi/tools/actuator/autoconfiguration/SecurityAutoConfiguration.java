package fr.nduheron.poc.springrestapi.tools.actuator.autoconfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

/**
 * Auto-configuration de la sécurité pour actuator
 *
 */
@Configuration
@Order(1)
@Conditional(ActuatorCondition.class)
@ConditionalOnProperty(name = "management.endpoints.security.user")
public class SecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

	private static final String ACTUATOR_REALMNAME = "ACTUATOR";
	private static final String ROLE_ACTUATOR = "ACTUATOR";

	@Value("${management.endpoints.web.base-path:/actuator}/**")
	private String actuatorPathPattern;

	@Value("${management.endpoints.security.user}")
	private String user;

	@Value("${management.endpoints.security.password}")
	private String password;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		BasicAuthenticationEntryPoint authenticationEntryPoint = new BasicAuthenticationEntryPoint();
		authenticationEntryPoint.setRealmName(ACTUATOR_REALMNAME);
		http.antMatcher(actuatorPathPattern).httpBasic().authenticationEntryPoint(authenticationEntryPoint).and()
				.authorizeRequests().antMatchers(actuatorPathPattern).hasRole(ROLE_ACTUATOR);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser(user).password(passwordEncoder.encode(password)).roles(ROLE_ACTUATOR);
	}

}
