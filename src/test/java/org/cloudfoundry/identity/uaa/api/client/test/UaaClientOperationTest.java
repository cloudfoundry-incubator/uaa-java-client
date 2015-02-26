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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;

import org.cloudfoundry.identity.uaa.api.client.UaaClientOperations;
import org.cloudfoundry.identity.uaa.api.client.model.UaaClient;
import org.cloudfoundry.identity.uaa.api.common.model.PagedResult;
import org.cloudfoundry.identity.uaa.api.common.model.UaaTokenGrantType;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Josh Ghiloni
 *
 */
public class UaaClientOperationTest extends AbstractOperationTest {

	private static UaaClientOperations operations;

	@BeforeClass
	public static void setUp() throws Exception {
		init();
		
		operations = getConnection().clientOperations();
	}

	@Test
	public void testGetClients() throws Exception {
		ignoreIfUaaNotRunning();
		
		PagedResult<UaaClient> clients = operations.getClients(FilterRequestBuilder.showAll());

		assertEquals("Total Results wrong", 11, clients.getTotalResults());
		assertEquals("Items Per Page wrong", 11, clients.getItemsPerPage());
		assertEquals("Actual result count wrong", 11, clients.getResources().size());
	}

	@Test
	public void testGetClient() throws Exception {
		ignoreIfUaaNotRunning();
		
		UaaClient client = operations.findById("app");

		assertEquals("ID wrong", "app", client.getClientId());
		assertNull("Secret should not be returned", client.getClientSecret());
	}

	@Test
	public void testCreateDelete() throws Exception {
		ignoreIfUaaNotRunning();
		
		UaaClient client = createClient();

		UaaClient checkClient = operations.findById(client.getClientId());
		assertEquals(client.getClientId(), checkClient.getClientId());

		operations.delete(client.getClientId());
	}

	@Test
	public void testUpdate() throws Exception {
		ignoreIfUaaNotRunning();
		
		UaaClient toUpdate = createClient();

		try {
			UaaClient client = operations.findById(toUpdate.getClientId());

			toUpdate.setScope(Arrays.asList("foo"));
			UaaClient updated = operations.update(toUpdate);
			assertNotEquals(client.getScope(), updated.getScope());
			assertEquals("foo", updated.getScope().iterator().next());
		}
		finally {
			operations.delete(toUpdate.getClientId());
		}
	}

	@Test
	public void testChangePassword() throws Exception {
		ignoreIfUaaNotRunning();
		
		UaaClient client = operations.findById("admin");

		operations.changeClientSecret(client.getClientId(), "adminsecret", "newSecret");

		try {
			operations.changeClientSecret(client.getClientId(), "adminsecret", "shouldfail");
			fail("First password change failed");
		}
		catch (Exception e) {
		}
		finally {
			operations.changeClientSecret(client.getClientId(), "newSecret", "adminsecret");
		}
	}

	private UaaClient createClient() {
		UaaClient client = new UaaClient();
		client.setClientId("test");
		client.setClientSecret("testsecret");
		client.setAccessTokenValidity(3600);
		client.setAuthorizedGrantTypes(Arrays.asList(UaaTokenGrantType.authorization_code,
				UaaTokenGrantType.client_credentials));
		client.setRefreshTokenValidity(86400);
		client.setAuthorities(Collections.singleton("uaa.resource"));

		return operations.create(client);
	}
}
