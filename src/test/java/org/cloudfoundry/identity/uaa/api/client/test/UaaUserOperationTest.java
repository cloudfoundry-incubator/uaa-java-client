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

import java.util.Collections;

import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
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

/**
 * @author Josh Ghiloni
 */
public class UaaUserOperationTest extends AbstractOperationTest {

	@ClassRule public static UaaServerAvailable uaaServerAvailable = new UaaServerAvailable();
	@Rule public ExpectedException expectedException = ExpectedException.none();

	private UaaUserOperations operations;

	@Before
	public void setUp() throws Exception {

		operations = getConnection().userOperations();

		ScimUser testUser = operations.getUserByName("testuser");
		if (testUser != null) {
			operations.deleteUser(testUser.getId());
		}
	}

	@Test
	public void testUserRetrieval() {

		SearchResults<ScimUser> users = operations.getUsers(FilterRequestBuilder.showAll());

		assertNotNull(users);

		assertEquals(1, users.getTotalResults());
		assertEquals(1, users.getResources().size());
		assertEquals(1, users.getStartIndex());
		assertEquals(100, users.getItemsPerPage());
	}

	@Test
	public void testUserCreateUpdateDelete() {

		ScimUser newUser = createNewTestUser();

		ScimUser createdUser = operations.createUser(newUser);
		assertNotNull(createdUser.getId());

		PhoneNumber updatedPhone = new PhoneNumber();
		updatedPhone.setValue("212-867-5309");
		createdUser.setPhoneNumbers(Collections.singletonList(updatedPhone));
		ScimUser updatedUser = operations.updateUser(createdUser);

		assertEquals(createdUser.getId(), updatedUser.getId());
	}

	@Test
	public void testUserPasswordChange() {

		ScimUser newUser = createNewTestUser();
		ScimUser createdUser = operations.createUser(newUser);

		operations.changeUserPassword(createdUser.getId(), newUser.getPassword(), "newk0ala");
	}

	private ScimUser createNewTestUser() {

		ScimUser newUser = new ScimUser();
		newUser.setUserName("testuser");
		newUser.setPassword("p4ssw0rd");

		newUser.setName(new Name("Test", "User"));

		Email email = new Email();
		email.setValue("testuser@test.com");

		newUser.setEmails(Collections.singletonList(email));

		PhoneNumber phone = new PhoneNumber();
		phone.setValue("303-555-1212");
		newUser.setPhoneNumbers(Collections.singletonList(phone));

		return newUser;
	}

}
