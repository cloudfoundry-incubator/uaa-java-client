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
import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import org.cloudfoundry.identity.uaa.api.common.model.PagedResult;
import org.cloudfoundry.identity.uaa.api.common.model.ValueObject;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
import org.cloudfoundry.identity.uaa.api.user.UaaUserOperations;
import org.cloudfoundry.identity.uaa.api.user.model.UaaUser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Josh Ghiloni
 *
 */
public class UaaUserOperationTest extends AbstractOperationTest {
	private static UaaUserOperations operations;

	@BeforeClass
	public static void setUp() throws Exception {
		init();

		operations = getConnection().userOperations();
	}

	@Test
	public void testUserRetrieval() {
		ignoreIfUaaNotRunning();

		PagedResult<UaaUser> users = operations.getUsers(FilterRequestBuilder.showAll());

		assertNotNull(users);

		assertEquals(1, users.getTotalResults());
		assertEquals(1, users.getResources().size());
		assertEquals(1, users.getStartIndex());
		assertEquals(100, users.getItemsPerPage());
	}

	@Test
	public void testUserCreateUpdateDelete() {
		ignoreIfUaaNotRunning();
		UaaUser newUser = new UaaUser();
		newUser.setUserName("testuser");
		newUser.setName(new UaaUser.Name("Test User", "User", "Test"));
		newUser.setEmails(Collections.singleton(new ValueObject("testuser@test.com")));
		newUser.setPhoneNumbers(Collections.singleton(new ValueObject("303-555-1212")));
		newUser.setPassword("p4ssw0rd");

		UaaUser createdUser = operations.createUser(newUser);
		assertNotNull(createdUser.getId());

		createdUser.setPhoneNumbers(Collections.singleton(new ValueObject("212-867-5309")));
		UaaUser updatedUser = operations.updateUser(createdUser);

		assertEquals(createdUser.getId(), updatedUser.getId());

		operations.deleteUser(updatedUser.getId());
	}

	@Test
	public void testUserPasswordChange() {
		ignoreIfUaaNotRunning();
		UaaUser user = operations.getUserByName("marissa");

		operations.changeUserPassword(user.getId(), "newk0ala");
	}
}
