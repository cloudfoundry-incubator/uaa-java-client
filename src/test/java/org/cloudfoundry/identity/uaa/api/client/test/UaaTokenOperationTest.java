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

import org.cloudfoundry.identity.uaa.api.common.UaaConnection;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
import org.cloudfoundry.identity.uaa.api.token.UaaTokenOperations;
import org.cloudfoundry.identity.uaa.api.user.UaaUserOperations;
import org.cloudfoundry.identity.uaa.rest.SearchResults;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.cloudfoundry.identity.uaa.scim.ScimUser.Email;
import org.cloudfoundry.identity.uaa.scim.ScimUser.Name;
import org.cloudfoundry.identity.uaa.scim.ScimUser.PhoneNumber;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.net.URI;
import java.net.URL;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Josh Ghiloni
 */
public class UaaTokenOperationTest extends AbstractOperationTest {

	@ClassRule public static UaaServerAvailable uaaServerAvailable = new UaaServerAvailable();
	@Rule public ExpectedException expectedException = ExpectedException.none();

	private URL target;
	private UaaTokenOperations operations;
	private UaaConnection connection;

	@Before
	public void setUp() throws Exception {
		target = new URL("http://localhost:8080/uaa/");
		connection = this.getConnection(target);
	}

	@Test
	public void testUserRetrieval() throws Exception {
		operations = connection.tokenOperations();
		ClientCredentialsResourceDetails details = connection.newClientCredentialsResourceDetails();
		details.setClientId("admin");
		details.setClientSecret("adminsecret");

		OAuth2AccessToken token = operations.get(details);

		assertThat(token.isExpired(), is(false));
		assertThat(token.getTokenType(), is("bearer"));
	}

}
