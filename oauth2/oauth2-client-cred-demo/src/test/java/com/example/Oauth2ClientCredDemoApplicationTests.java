package com.example;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@SpringApplicationConfiguration(classes = {Oauth2ClientCredDemoApplication.class})
public class Oauth2ClientCredDemoApplicationTests extends AbstractIntegrationTests{

	private static final String CLIENT_ID_CORRECT = "my-client-with-secret";
	private static final String CLIENT_ID_WORNG = "my-WRONG-client";
	protected URI tokenUri;

	@Before
	public void setUp() {
		tokenUri = URI.create(http.getUrl("/oauth/token"));
	}

    @Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testHardCodedAuthenticationAndPageAccess() {

		RestTemplate restTemplate = new RestTemplate();
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("grant_type", "client_credentials");
		params.add("client_id", CLIENT_ID_CORRECT);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		RequestEntity<MultiValueMap<String, String>> req = new RequestEntity<MultiValueMap<String, String>>(params,
				headers, HttpMethod.POST, tokenUri);

		ResponseEntity<Map> response = restTemplate.exchange(req, Map.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		Map<String, String> body = response.getBody();
		String accessToken = body.get("access_token");
		
		//access the other URL with the accessToken
		URI actualUri = URI.create(http.getUrl("/"));
		headers.set("Authorization", "Bearer " + accessToken);
		RequestEntity<MultiValueMap<String, String>> accessReq = new RequestEntity<MultiValueMap<String, String>>(params,
				headers, HttpMethod.GET, actualUri);
		
		ResponseEntity<String> actualResponse = restTemplate.exchange(accessReq, String.class);
		assertEquals(Oauth2ClientCredDemoApplication.BODY_HTML_STRING, actualResponse.getBody());
		
		System.out.println("Body " + actualResponse.getBody());
	}
    
    @Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testHardCodedAuthenticationAndPageAccessWrongClient() {

		RestTemplate restTemplate = new RestTemplate();
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("grant_type", "client_credentials");
		params.add("client_id", CLIENT_ID_WORNG);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		RequestEntity<MultiValueMap<String, String>> req = new RequestEntity<MultiValueMap<String, String>>(params,
				headers, HttpMethod.POST, tokenUri);

		try {
			ResponseEntity<Map> response = restTemplate.exchange(req, Map.class);
		}
		catch (HttpStatusCodeException e) {
			assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
		}

	}


}
