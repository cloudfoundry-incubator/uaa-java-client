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
package org.cloudfoundry.identity.uaa.api.client.test;

import java.net.URL;

import org.cloudfoundry.identity.uaa.api.UaaConnectionFactory;
import org.cloudfoundry.identity.uaa.api.common.UaaConnection;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

/**
 * @author Josh Ghiloni
 */
public abstract class AbstractOperationTest {

	private static final String UAA_BASE_URL = "http://localhost:8080/uaa";

	protected ClientCredentialsResourceDetails getDefaultClientCredentials() {

		ClientCredentialsResourceDetails credentials = new ClientCredentialsResourceDetails();
		credentials.setAccessTokenUri(UAA_BASE_URL + "/oauth/token");
		credentials.setClientAuthenticationScheme(AuthenticationScheme.header);
		credentials.setClientId("admin");
		credentials.setClientSecret("adminsecret");

		return credentials;
	}

	protected UaaConnection getConnection() throws Exception {
		return getConnection(getDefaultClientCredentials());
	}

	protected UaaConnection getConnection(ClientCredentialsResourceDetails clientCredentials) throws Exception {
		return UaaConnectionFactory.getConnection(new URL(UAA_BASE_URL), clientCredentials);
	}
}
