package com.example;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableAutoConfiguration
@RestController
@EnableResourceServer
public class Oauth2ClientCredDemoApplication {

	
	public static String BODY_HTML_STRING = "Hello World";
	
	@RequestMapping("/")
	public String home() {
		return BODY_HTML_STRING;
	}
	
	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration
			extends AuthorizationServerConfigurerAdapter {

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.inMemory().withClient("my-client-with-secret1")
			.authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
			.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT").scopes("read", "write", "trust")
			.resourceIds("oauth2-resource").accessTokenValiditySeconds(600).and()
			.withClient("my-client-with-registered-redirect").authorizedGrantTypes("authorization_code")
			.authorities("ROLE_CLIENT").scopes("read", "trust").resourceIds("oauth2-resource")
			.redirectUris("http://anywhere?key=value").and().withClient("my-client-with-secret")
			.authorizedGrantTypes("client_credentials", "password").authorities("ROLE_CLIENT").scopes("read")
			.resourceIds("oauth2-resource").secret("secret");

		}
		
		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			security.addTokenEndpointAuthenticationFilter(new HardCodedAuthenticationFilter());
		}		

	}

}
