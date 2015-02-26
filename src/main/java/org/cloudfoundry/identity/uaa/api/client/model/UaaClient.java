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
package org.cloudfoundry.identity.uaa.api.client.model;

import java.util.Collection;
import java.util.Collections;

import org.cloudfoundry.identity.uaa.api.common.model.UaaTokenGrantType;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * An OAuth2 client
 * 
 * @author Josh Ghiloni
 *
 */
/**
 * @author Josh Ghiloni
 *
 */
/**
 * @author Josh Ghiloni
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UaaClient {

	@JsonProperty("client_id")
	private String clientId;

	@JsonProperty("client_secret")
	private String clientSecret;

	@JsonProperty("access_token_validity")
	private int accessTokenValidity;

	@JsonProperty("refresh_token_validity")
	private int refreshTokenValidity;

	@JsonProperty("scope")
	private Collection<String> scope;

	@JsonProperty("resource_ids")
	private Collection<String> resourceIds;

	@JsonProperty("authorities")
	private Collection<String> authorities;

	@JsonProperty("authorized_grant_types")
	private Collection<UaaTokenGrantType> authorizedGrantTypes;

	/**
	 * 
	 * @return the client id
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * 
	 * @param clientId the client id
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * 
	 * @return the client secret
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * 
	 * @param clientSecret the client secret
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	/**
	 * 
	 * @return the length of time this access token is valid (in seconds)
	 */
	public int getAccessTokenValidity() {
		return accessTokenValidity;
	}

	/**
	 * @param accessTokenValidity the length of time this access token is valid (in seconds)
	 */
	public void setAccessTokenValidity(int accessTokenValidity) {
		this.accessTokenValidity = accessTokenValidity;
	}

	/**
	 * @return the length of time this refresh token is valid (in seconds)
	 */
	public int getRefreshTokenValidity() {
		return refreshTokenValidity;
	}

	/**
	 * @param refreshTokenValidity the length of time this refresh token is valid (in seconds)
	 */
	public void setRefreshTokenValidity(int refreshTokenValidity) {
		this.refreshTokenValidity = refreshTokenValidity;
	}

	/**
	 * @return The scopes. Never null
	 */
	public Collection<String> getScope() {
		if (scope != null) {
			return scope;
		}
		else {
			return Collections.emptyList();
		}
	}

	/**
	 * @param scope The scopes this client is authorized to use
	 */
	public void setScope(Collection<String> scope) {
		this.scope = scope;
	}

	/**
	 * @return The resource IDs for this client. Never null.
	 */
	public Collection<String> getResourceIds() {
		if (resourceIds != null) {
			return Collections.unmodifiableCollection(resourceIds);
		}
		else {
			return Collections.emptyList();
		}
	}

	/**
	 * @param resourceIds The resource IDs for this client.
	 */
	public void setResourceIds(Collection<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

	/**
	 * @return The list of authorities for this client. Never null.
	 */
	public Collection<String> getAuthorities() {
		if (authorities != null) {
			return Collections.unmodifiableCollection(authorities);
		}
		else {
			return Collections.emptyList();
		}
	}

	/**
	 * @param authorities The list of authorities for this client.
	 */
	public void setAuthorities(Collection<String> authorities) {
		this.authorities = authorities;
	}

	/**
	 * @return A collection of {@link org.cloudfoundry.identity.uaa.api.common.model.UaaTokenGrantType}
	 */
	public Collection<UaaTokenGrantType> getAuthorizedGrantTypes() {
		if (authorizedGrantTypes != null) {
			return Collections.unmodifiableCollection(this.authorizedGrantTypes);
		}
		else {
			return Collections.emptyList();
		}
	}

	/**
	 * @param authorizedGrantTypes A collection of {@link org.cloudfoundry.identity.uaa.api.common.model.UaaTokenGrantType}s
	 */
	public void setAuthorizedGrantTypes(Collection<UaaTokenGrantType> authorizedGrantTypes) {
		this.authorizedGrantTypes = authorizedGrantTypes;
	}
}
