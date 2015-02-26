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
package org.cloudfoundry.identity.uaa.api.user.impl;

import static org.cloudfoundry.identity.uaa.api.common.model.ScimMetaObject.SCHEMAS;

import java.util.Collections;

import org.cloudfoundry.identity.uaa.api.common.impl.UaaConnectionHelper;
import org.cloudfoundry.identity.uaa.api.common.model.PagedResult;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequest;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
import org.cloudfoundry.identity.uaa.api.user.UaaUserOperations;
import org.cloudfoundry.identity.uaa.api.user.model.UaaUser;
import org.cloudfoundry.identity.uaa.api.user.model.UaaUsersResults;
import org.springframework.util.Assert;

/**
 * @see UaaUserOperations
 * 
 * @author Josh Ghiloni
 *
 */
public class UaaUserOperationsImpl implements UaaUserOperations {
	private UaaConnectionHelper helper;

	public UaaUserOperationsImpl(UaaConnectionHelper helper) {
		this.helper = helper;
	}

	public UaaUser createUser(UaaUser user) {
		Assert.notNull(user);
		Assert.hasText(user.getUserName());

		user.setSchemas(SCHEMAS);

		return helper.post("/Users", user, UaaUser.class);
	}

	public UaaUser updateUser(UaaUser user) {
		Assert.notNull(user);
		Assert.hasText(user.getId());

		// don't try to update the stuff we can't update here
		user.setGroups(null);
		user.setPassword(null);

		return helper.putScimObject("/Users/{id}", user, UaaUser.class, user.getId());
	}

	public void deleteUser(String userId) {
		Assert.hasText(userId);
		helper.delete("/Users/{id}", String.class, userId);
	}

	public void changeUserPassword(String userId, String newPassword) {
		Assert.hasText(userId);
		Assert.hasText(newPassword);

		helper.put("/Users/{id}/password", Collections.singletonMap("password", newPassword), String.class, userId);
	}

	public PagedResult<UaaUser> getUsers(FilterRequest request) {
		Assert.notNull(request);

		return helper.get(helper.buildScimFilterUrl("/Users", request), UaaUsersResults.class);
	}

	public UaaUser getUserByName(String userName) {
		FilterRequest request = new FilterRequestBuilder().equals("username", userName).build();
		PagedResult<UaaUser> result = getUsers(request);

		if (result != null && result.getResources() != null && result.getResources().size() == 1) {
			return result.getResources().iterator().next();
		}

		return null;
	}
}
