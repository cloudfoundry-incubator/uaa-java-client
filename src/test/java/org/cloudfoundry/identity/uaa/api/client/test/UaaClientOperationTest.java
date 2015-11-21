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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.cloudfoundry.identity.uaa.api.client.UaaClientOperations;
import org.cloudfoundry.identity.uaa.api.common.model.UaaTokenGrantType;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
import org.cloudfoundry.identity.uaa.rest.SearchResults;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

/**
 * @author Josh Ghiloni
 */
public class UaaClientOperationTest extends AbstractOperationTest {

	@ClassRule public static UaaServerAvailable uaaServerAvailable = new UaaServerAvailable();

	private UaaClientOperations operations;
	private BaseClientDetails testClient;
	private BaseClientDetails testClientDetails;

	@Before
	public void setup() throws Exception {

		operations = getConnection().clientOperations();

		try {
			operations.delete("test");
		} catch (Exception ignore) {}

		testClientDetails = new BaseClientDetails();
		testClientDetails.setClientId("test");
		testClientDetails.setClientSecret("testsecret");
		testClientDetails.setAccessTokenValiditySeconds(3600);
		testClientDetails.setAuthorizedGrantTypes(Arrays.asList(UaaTokenGrantType.authorization_code.toString(),
				UaaTokenGrantType.client_credentials.toString()));
		testClientDetails.setRefreshTokenValiditySeconds(86400);
		testClientDetails.setAuthorities(AuthorityUtils.createAuthorityList("uaa.resource", "clients.secret"));

		testClient = operations.create(testClientDetails);
	}

	@Test
	public void testGetClients() throws Exception {

		SearchResults<BaseClientDetails> clients = operations.getClients(FilterRequestBuilder.showAll());

		assertEquals("Total Results wrong", 12, clients.getTotalResults()); // default 11 + test client 1 = 12 clients
		assertEquals("Items Per Page wrong", 12, clients.getItemsPerPage());
		assertEquals("Actual result count wrong", 12, clients.getResources().size());
	}

	@Test
	public void testGetClient() throws Exception {

		BaseClientDetails client = operations.findById("app");

		assertEquals("ID wrong", "app", client.getClientId());
		assertNull("Secret should not be returned", client.getClientSecret());
	}

	@Test
	public void testCreateDelete() throws Exception {

		BaseClientDetails checkClient = operations.findById(testClient.getClientId());
		assertEquals(testClient.getClientId(), checkClient.getClientId());

		operations.delete(checkClient.getClientId());
	}

	@Test
	public void testUpdate() throws Exception {

		BaseClientDetails client = operations.findById(testClient.getClientId());

		client.setScope(Arrays.asList("foo"));
		BaseClientDetails updated = operations.update(client);

		assertNotEquals(testClient.getScope(), updated.getScope());
		assertEquals(client.getScope().iterator().next(), updated.getScope().iterator().next());
	}

	@Test
	public void testChangeClientSecretForTestUser() throws Exception {

		ClientCredentialsResourceDetails testUserCredentials = getDefaultClientCredentials();
		testUserCredentials.setClientId(testClientDetails.getClientId());
		testUserCredentials.setClientSecret(testClientDetails.getClientSecret());
		testUserCredentials.setScope(new ArrayList<String>(testClientDetails.getScope()));

		UaaClientOperations clientOperations = getConnection().clientOperations();

		String clientSecret = testClientDetails.getClientSecret();

		assertTrue("Could not change password",
				clientOperations.changeClientSecret(testClientDetails.getClientId(), clientSecret, "newSecret"));

		try {
			clientOperations.changeClientSecret(testClientDetails.getClientId(), clientSecret, "shouldfail");
			fail("First password change failed");
		} catch (Exception expected) {
			expected.toString(); // to avoid empty catch-block.
		}
	}
}
