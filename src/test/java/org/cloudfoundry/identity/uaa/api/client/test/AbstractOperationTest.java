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

import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;

import org.cloudfoundry.identity.uaa.api.UaaConnectionFactory;
import org.cloudfoundry.identity.uaa.api.common.UaaConnection;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

/**
 * @author Josh Ghiloni
 *
 */
public abstract class AbstractOperationTest {
	private static boolean uaaRunning;

	private static UaaConnection connection;

	protected static void init() throws Exception {
		try {
			Socket test = new Socket("localhost", 8080);
			uaaRunning = true;
			test.close();
		}
		catch (IOException e) {
			System.err.println("UAA is not running, skip these tests");
			uaaRunning = false;
			return;
		}
		finally {
			ClientCredentialsResourceDetails credentials = new ClientCredentialsResourceDetails();
			credentials.setAccessTokenUri("http://localhost:8080/uaa/oauth/token");
			credentials.setAuthenticationScheme(AuthenticationScheme.header);
			credentials.setClientAuthenticationScheme(AuthenticationScheme.header);
			credentials.setClientId("admin");
			credentials.setClientId("adminsecret");
			
			connection = UaaConnectionFactory.getConnection(new URL("http://localhost:8080/uaa"), credentials);
		}
	}

	protected static void ignoreIfUaaNotRunning() {
		assumeTrue(uaaRunning);
	}

	protected static UaaConnection getConnection() {
		return connection;
	}
}
