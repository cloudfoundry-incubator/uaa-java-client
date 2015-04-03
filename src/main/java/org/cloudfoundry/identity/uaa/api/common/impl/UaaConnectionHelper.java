/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cloudfoundry.identity.uaa.api.common.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.identity.uaa.api.common.model.WrappedSearchResults;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequest;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
import org.cloudfoundry.identity.uaa.rest.SearchResults;
import org.cloudfoundry.identity.uaa.scim.ScimCore;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * A helper clas used by the various <code>*Operations</code> implementations to handle JSON HTTP communications with
 * the UAA server
 * 
 * @author Josh Ghiloni
 *
 */
public class UaaConnectionHelper {
	private static final AccessTokenProviderChain CHAIN = new AccessTokenProviderChain(
			Arrays.<AccessTokenProvider> asList(new ClientCredentialsAccessTokenProvider(),
					new ImplicitAccessTokenProvider(), new ResourceOwnerPasswordAccessTokenProvider()));

	private OAuth2AccessToken token;

	private URL url;

	private OAuth2ProtectedResourceDetails creds;

	private static final Log log = LogFactory.getLog(UaaConnectionHelper.class);

	/**
	 * Establish connectivity information for this session.
	 * 
	 * @param url
	 * @param creds
	 * @see org.cloudfoundry.identity.uaa.api.UaaConnectionFactory#getConnection(URL, OAuth2ProtectedResourceDetails)
	 */
	public UaaConnectionHelper(URL url, OAuth2ProtectedResourceDetails creds) {
		this.url = url;
		this.creds = creds;
	}

	/**
	 * Do an HTTP GET
	 * 
	 * @param uri the URI of the endpoint (relative to the base URL set in the constructor)
	 * @param responseType the object type to be returned
	 * @param uriVariables any uri variables
	 * @return the response body
	 * @see #exchange(HttpMethod, Object, String, Class, Object...)
	 */
	public <ResponseType> ResponseType get(String uri, ParameterizedTypeReference<ResponseType> responseType,
			Object... uriVariables) {
		return exchange(HttpMethod.GET, null, uri, responseType, uriVariables);
	}

	/**
	 * Do an HTTP DELETE
	 * 
	 * @param uri the URI of the endpoint (relative to the base URL set in the constructor)
	 * @param responseType the object type to be returned
	 * @param uriVariables any uri variables
	 * @return the response body
	 * @see #exchange(HttpMethod, Object, String, Class, Object...)
	 */
	public <ResponseType> ResponseType delete(String uri, ParameterizedTypeReference<ResponseType> responseType,
			Object... uriVariables) {
		return exchange(HttpMethod.DELETE, null, uri, responseType, uriVariables);
	}

	/**
	 * Do an HTTP POST
	 *
	 * @param uri the URI of the endpoint (relative to the base URL set in the constructor)
	 * @param body the request body
	 * @param responseType the object type to be returned
	 * @param uriVariables any uri variables
	 * @return the response body
	 * @see #exchange(HttpMethod, Object, String, Class, Object...)
	 */
	public <RequestType, ResponseType> ResponseType post(String uri, RequestType body,
			ParameterizedTypeReference<ResponseType> responseType, Object... uriVariables) {
		return exchange(HttpMethod.POST, body, uri, responseType, uriVariables);
	}

	/**
	 * Do an HTTP PUT
	 *
	 * @param uri the URI of the endpoint (relative to the base URL set in the constructor)
	 * @param body the request body
	 * @param responseType the object type to be returned
	 * @param uriVariables any uri variables
	 * @return the response body
	 * @see #exchange(HttpMethod, Object, String, Class, Object...)
	 */
	public <RequestType, ResponseType> ResponseType put(String uri, RequestType body,
			ParameterizedTypeReference<ResponseType> responseType, Object... uriVariables) {
		return exchange(HttpMethod.PUT, body, uri, responseType, uriVariables);
	}

	/**
	 * Do an HTTP PUT with SCIM features. SCIM requires PUT requests of a SCIM object have the version of the object set
	 * as the <code>If-Match</code> request header.
	 *
	 * @param uri the URI of the endpoint (relative to the base URL set in the constructor)
	 * @param body the request body
	 * @param responseType the object type to be returned
	 * @param uriVariables any uri variables
	 * @return the response body
	 * @see #exchange(HttpMethod, HttpHeaders, Object, String, Class, Object...)
	 */
	public <RequestType extends ScimCore, ResponseType> ResponseType putScimObject(String uri, RequestType body,
			ParameterizedTypeReference<ResponseType> responseType, Object... uriVariables) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("if-match", String.valueOf(body.getMeta().getVersion()));

		return exchange(HttpMethod.PUT, headers, body, uri, responseType, uriVariables);
	}

	/**
	 * Convenience method to get a user ID for a given username. Equivalent to calling
	 * 
	 * <pre>
	 * {@link org.cloudfoundry.identity.uaa.api.user.UaaUserOperations UaaUserOperations} operations = connection.userOperations();
	 * 
	 * {@link FilterRequestBuilder} builder = new FilterRequestBuilder();
	 * builder.equals("username", userName).attributes("id");
	 * 
	 * {@link org.cloudfoundry.identity.uaa.api.user.model.ScimUsers} users = operations.getUsers(builder.build());
	 * 
	 * return users.getResources().iterator().next().getId();
	 * </pre>
	 * 
	 * @param userName the userName
	 * @return the user ID
	 */
	public String getUserIdByName(String userName) {
		FilterRequestBuilder builder = new FilterRequestBuilder();
		builder.equals("username", userName).attributes("id");

		FilterRequest request = builder.build();

		String uri = buildScimFilterUrl("/Users", request);

		try {
			SearchResults<ScimUser> retval = exchange(HttpMethod.GET, null, uri,
					new ParameterizedTypeReference<WrappedSearchResults<ScimUser>>() {
					});

			Collection<ScimUser> resources = retval.getResources();

			if (CollectionUtils.isEmpty(resources)) {
				return null;
			}

			ScimUser user = resources.iterator().next();
			return user.getId();
		}
		catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	/**
	 * Make a REST call with default headers
	 * 
	 * @param method the Http Method (GET, POST, etc)
	 * @param uri the URI of the endpoint (relative to the base URL set in the constructor)
	 * @param body the request body
	 * @param responseType the object type to be returned
	 * @param uriVariables any uri variables
	 * @return the response body
	 * @see #exchange(HttpMethod, HttpHeaders, Object, String, Class, Object...)
	 */
	private <RequestType, ResponseType> ResponseType exchange(HttpMethod method, RequestType body, String uri,
			ParameterizedTypeReference<ResponseType> responseType, Object... uriVariables) {
		return exchange(method, new HttpHeaders(), body, uri, responseType, uriVariables);
	}

	/**
	 * Make a REST call with custom headers
	 * 
	 * @param method the Http Method (GET, POST, etc)
	 * @param uri the URI of the endpoint (relative to the base URL set in the constructor)
	 * @param body the request body
	 * @param responseType the object type to be returned
	 * @param uriVariables any uri variables
	 * @return the response body
	 * @see org.springframework.web.client.RestTemplate#exchange(String, HttpMethod, HttpEntity, Class, Object...)
	 */
	private <RequestType, ResponseType> ResponseType exchange(HttpMethod method, HttpHeaders headers, RequestType body,
			String uri, ParameterizedTypeReference<ResponseType> responseType, Object... uriVariables) {
		getHeaders(headers);

		RestTemplate template = new RestTemplate();
		template.setInterceptors(LoggerInterceptor.INTERCEPTOR);

		HttpEntity<RequestType> requestEntity = null;
		if (body == null) {
			requestEntity = new HttpEntity<RequestType>(headers);
		}
		else {
			requestEntity = new HttpEntity<RequestType>(body, headers);
		}

		// combine url into the varargs
		List<Object> varList = new ArrayList<Object>();
		varList.add(url);
		if (uriVariables != null && uriVariables.length > 0) {
			varList.addAll(Arrays.asList(uriVariables));
		}

		ResponseEntity<ResponseType> responseEntity = template.exchange("{base}" + uri, method, requestEntity,
				responseType, varList.toArray());

		if (HttpStatus.Series.SUCCESSFUL.equals(responseEntity.getStatusCode().series())) {
			return responseEntity.getBody();
		}
		else {
			return null;
		}
	}

	/**
	 * Because variable substitution used by {@link org.springframework.web.client.RestTemplate} escapes things in a way
	 * that makes SCIM filtering difficult, manually include the parameters in the uri
	 * 
	 * @param baseUrl the url relative to the base URL (i.e. /Users, /oauth/clients, etc)
	 * @param request the Filter Request to populate the URL
	 * @return the URL
	 */
	public String buildScimFilterUrl(String baseUrl, FilterRequest request) {
		StringBuilder uriBuilder = new StringBuilder(baseUrl);

		boolean hasParams = false;

		if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
			uriBuilder.append("?attributes=").append(
					StringUtils.collectionToCommaDelimitedString(request.getAttributes()));

			hasParams = true;
		}

		if (StringUtils.hasText(request.getFilter())) {
			if (hasParams) {
				uriBuilder.append("&");
			}
			else {
				uriBuilder.append("?");
			}

			uriBuilder.append("filter=").append(request.getFilter());
			hasParams = true;
		}

		if (request.getStart() > 0) {
			if (hasParams) {
				uriBuilder.append("&");
			}
			else {
				uriBuilder.append("?");
			}

			uriBuilder.append("startIndex=").append(request.getStart());
			hasParams = true;
		}

		if (request.getCount() > 0) {
			if (hasParams) {
				uriBuilder.append("&");
			}
			else {
				uriBuilder.append("?");
			}

			uriBuilder.append("count=").append(request.getCount());
			hasParams = true;
		}

		return uriBuilder.toString();
	}

	/**
	 * Add the Authorization, Content-Type, and Accept headers to the request
	 * 
	 * @param headers
	 */
	private void getHeaders(HttpHeaders headers) {
		OAuth2AccessToken token = getAccessToken();
		headers.add("Authorization", token.getTokenType() + " " + token.getValue());

		if (headers.getContentType() == null) {
			headers.setContentType(MediaType.APPLICATION_JSON);
		}

		if (headers.getAccept() == null || headers.getAccept().size() == 0) {
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		}
	}

	/**
	 * Get the OAuth access token (and refresh it if necessary)
	 * 
	 * @return
	 */
	private OAuth2AccessToken getAccessToken() {
		if (token == null) {
			token = CHAIN.obtainAccessToken(creds, new DefaultAccessTokenRequest());
		}
		else if (token.isExpired()) {
			refreshAccessToken();
		}

		return token;
	}

	/**
	 * refresh the access token
	 */
	private void refreshAccessToken() {
		Assert.notNull(token);

		token = CHAIN.refreshAccessToken(creds, token.getRefreshToken(), new DefaultAccessTokenRequest());
	}

	/**
	 * An interceptor used to log information about HTTP calls
	 * 
	 * @author Josh Ghiloni
	 *
	 */
	private static class LoggerInterceptor implements ClientHttpRequestInterceptor {
		public static final List<ClientHttpRequestInterceptor> INTERCEPTOR = Arrays
				.<ClientHttpRequestInterceptor> asList(new LoggerInterceptor());

		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			if (log.isDebugEnabled()) {
				log.debug(new String(body, "UTF-8"));
			}

			return execution.execute(request, body);
		}
	}
}
