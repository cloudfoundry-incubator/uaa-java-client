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
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.cloudfoundry.identity.uaa.api.common.model.PagedResult;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
import org.cloudfoundry.identity.uaa.api.group.UaaGroupOperations;
import org.cloudfoundry.identity.uaa.api.group.model.UaaGroup;
import org.cloudfoundry.identity.uaa.api.group.model.UaaGroupMember;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Josh Ghiloni
 *
 */
public class UaaGroupOperationTest extends AbstractOperationTest {
	private static UaaGroupOperations operations;

	@BeforeClass
	public static void setUp() throws Exception {
		init();

		operations = getConnection().groupOperations();
	}

	@Test
	public void testGroupRetrieval() {
		ignoreIfUaaNotRunning();
		PagedResult<UaaGroup> groups = operations.getGroups(FilterRequestBuilder.showAll());

		assertNotNull(groups);

		assertEquals(28, groups.getTotalResults());
		assertEquals(28, groups.getResources().size());
		assertEquals(1, groups.getStartIndex());
		assertEquals(100, groups.getItemsPerPage());
	}

	@Test
	public void testGroupCreateUpdateDelete() {
		ignoreIfUaaNotRunning();
		
		String id = "marissa";

		UaaGroup newGroup = new UaaGroup();
		newGroup.setDisplayName("test.group");

		UaaGroup createdGroup = operations.createGroup(newGroup);

		assertNotNull(createdGroup.getId());

		UaaGroup newNameGroup = operations.updateGroupName(createdGroup.getId(), "test.group.renamed");

		assertEquals(createdGroup.getId(), newNameGroup.getId());

		UaaGroup updatedGroup = operations.addMember(newNameGroup.getId(), id);

		Collection<UaaGroupMember> oldMembers = newNameGroup.getMembers();
		Collection<UaaGroupMember> newMembers = updatedGroup.getMembers();

		assertTrue((oldMembers == null && newMembers.size() == 1) || (oldMembers.size() == newMembers.size() - 1));
		assertEquals(newNameGroup.getId(), updatedGroup.getId());

		UaaGroup shrunkGroup = operations.deleteMember(updatedGroup.getId(), id);

		oldMembers = newNameGroup.getMembers();
		newMembers = shrunkGroup.getMembers();

		assertTrue((oldMembers == null && newMembers == null) || (oldMembers.size() == newMembers.size()));
		assertEquals(updatedGroup.getId(), shrunkGroup.getId());

		operations.deleteGroup(createdGroup.getId());
	}
}
