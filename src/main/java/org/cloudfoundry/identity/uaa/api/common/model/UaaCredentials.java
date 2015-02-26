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
package org.cloudfoundry.identity.uaa.api.common.model;

import org.springframework.util.Assert;

/**
 * The OAuth2 client (and potentially user) credentials
 * 
 * @author Josh Ghiloni
 * 
 */
public class UaaCredentials {
	private String clientId;

	private String clientSecret;

	private String userId;

	private String password;

	/**
	 * This constructor will be used for generating an implicit token
	 * 
	 * @param clientId
	 */
	public UaaCredentials(String clientId) {
		this(clientId, null, null, null);
	}
	
	/**
	 * This constructor will be used for generating a client_credentials token
	 * 
	 * @param clientId
	 * @param clientSecret
	 */
	public UaaCredentials(String clientId, String clientSecret) {
		this(clientId, clientSecret, null, null);
	}

	/**
	 * This constructor will be used for generating a resource owner token
	 * 
	 * @param clientId
	 * @param clientSecret
	 * @param userId
	 * @param password
	 */
	public UaaCredentials(String clientId, String clientSecret, String userId, String password) {
		Assert.hasText(clientId);
		
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.userId = userId;
		this.password = password;
	}

	/**
	 * @return The client ID. Should never be null.
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @return The client secret. May be null.
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * @return The user ID. May be null.
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return The user password. May be null.
	 */
	public String getPassword() {
		return password;
	}
}
