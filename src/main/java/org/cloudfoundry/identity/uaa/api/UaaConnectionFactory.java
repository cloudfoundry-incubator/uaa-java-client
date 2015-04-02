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
package org.cloudfoundry.identity.uaa.api;

import java.net.URL;

import org.cloudfoundry.identity.uaa.api.common.UaaConnection;
import org.cloudfoundry.identity.uaa.api.common.impl.UaaConnectionHelper;
import org.cloudfoundry.identity.uaa.api.common.impl.UaaConnectionImpl;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

/**
 * The initial entry point for the API classes
 * 
 * @author Josh Ghiloni
 *
 */
public final class UaaConnectionFactory {
	private UaaConnectionFactory() {

	}

	/**
	 * Get a connection object for the given UAA server, from which you can get access to different API operations.
	 * 
	 * @param uaaUrl the base {@link URL} of the UAA server. May have a path prefix (for example,
	 * <code>http://localhost:8080/uaa</code>)
	 * @param credentials the {@link UaaCredentials} representing the current user. May be client-only
	 * @return the connection entry point
	 */
	public static UaaConnection getConnection(URL uaaUrl, OAuth2ProtectedResourceDetails credentials) {
		UaaConnectionHelper helper = new UaaConnectionHelper(uaaUrl, credentials);
		return new UaaConnectionImpl(helper);
	}
}
