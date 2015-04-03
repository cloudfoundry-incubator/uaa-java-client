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
package org.cloudfoundry.identity.uaa.api.group.impl;

import static org.cloudfoundry.identity.uaa.scim.ScimCore.SCHEMAS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.identity.uaa.api.common.impl.UaaConnectionHelper;
import org.cloudfoundry.identity.uaa.api.common.model.WrappedSearchResults;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequest;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
import org.cloudfoundry.identity.uaa.api.group.UaaGroupOperations;
import org.cloudfoundry.identity.uaa.rest.SearchResults;
import org.cloudfoundry.identity.uaa.scim.ScimGroup;
import org.cloudfoundry.identity.uaa.scim.ScimGroupExternalMember;
import org.cloudfoundry.identity.uaa.scim.ScimGroupMember;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @see ScimGroupOperations
 * 
 * @author Josh Ghiloni
 */
public class UaaGroupOperationsImpl implements UaaGroupOperations {

	private static final ParameterizedTypeReference<Object> OBJ_REF = new ParameterizedTypeReference<Object>() {
	};

	private static final ParameterizedTypeReference<ScimGroup> GROUP_REF = new ParameterizedTypeReference<ScimGroup>() {
	};

	private static final ParameterizedTypeReference<ScimGroupExternalMember> EXT_GROUP_REF = new ParameterizedTypeReference<ScimGroupExternalMember>() {
	};

	private static final ParameterizedTypeReference<WrappedSearchResults<ScimGroup>> GROUPS_REF = new ParameterizedTypeReference<WrappedSearchResults<ScimGroup>>() {
	};

	private static final ParameterizedTypeReference<WrappedSearchResults<ScimGroupExternalMember>> EXT_GROUPS_REF = new ParameterizedTypeReference<WrappedSearchResults<ScimGroupExternalMember>>() {
	};

	private UaaConnectionHelper helper;

	public UaaGroupOperationsImpl(UaaConnectionHelper helper) {
		this.helper = helper;
	}

	public ScimGroup createGroup(ScimGroup group) {
		Assert.notNull(group);
		Assert.hasText(group.getDisplayName());

		group.setSchemas(SCHEMAS);

		return helper.post("/Groups", group, GROUP_REF);
	}

	public void deleteGroup(String groupId) {
		Assert.hasText(groupId);

		helper.delete("/Groups/{id}", OBJ_REF, groupId);
	}

	public SearchResults<ScimGroup> getGroups(FilterRequest request) {
		Assert.notNull(request);

		return helper.get(helper.buildScimFilterUrl("/Groups", request), GROUPS_REF);
	}

	public ScimGroupExternalMember createGroupMapping(ScimGroupExternalMemberType type, String identifier,
			String externalGroupDn) {
		Assert.notNull(type);
		Assert.hasText(identifier);
		Assert.hasText(externalGroupDn);

		Map<String, Object> request = new LinkedHashMap<String, Object>(3);

		request.put("schemas", SCHEMAS);
		request.put(type.toString(), identifier);
		request.put("externalGroup", externalGroupDn);

		return helper.post("/Groups/External", request, EXT_GROUP_REF);
	}

	public void deleteGroupMapping(ScimGroupExternalMember mapping) {
		Assert.notNull(mapping);

		String id = null;
		String type = null;
		String external = mapping.getExternalGroup();

		if (StringUtils.hasText(mapping.getGroupId())) {
			id = mapping.getGroupId();
			type = "groupId";
		}
		else {
			id = mapping.getDisplayName();
			type = "displayName";
		}

		helper.delete("/Groups/External/{type}/{id}/externalGroup/{externalGroup}", OBJ_REF, type, id, external);
	}

	public SearchResults<ScimGroupExternalMember> getGroupMappings(FilterRequest request) {
		Assert.notNull(request);
		return helper.get(helper.buildScimFilterUrl("/Groups/External", request), EXT_GROUPS_REF);
	}

	public ScimGroup updateGroupName(String groupId, String newName) {
		ScimGroup group = getGroupById(groupId);
		group.setDisplayName(newName);

		return updateGroup(group);
	}

	public ScimGroup addMember(String groupId, String memberUserName) {
		Assert.hasText(memberUserName);

		ScimGroup group = getGroupById(groupId);

		String memberId = helper.getUserIdByName(memberUserName);

		List<ScimGroupMember> members = group.getMembers();
		if (members == null) {
			members = new ArrayList<ScimGroupMember>(1);
		}

		ScimGroupMember member = new ScimGroupMember(memberId);
		members.add(member);
		group.setMembers(members);

		return updateGroup(group);
	}

	public ScimGroup deleteMember(String groupId, String memberUserName) {
		Assert.hasText(memberUserName);

		ScimGroup group = getGroupById(groupId);

		String memberId = helper.getUserIdByName(memberUserName);

		List<ScimGroupMember> members = group.getMembers();
		if (members != null && !members.isEmpty()) {
			for (Iterator<ScimGroupMember> iter = members.iterator(); iter.hasNext();) {
				ScimGroupMember member = iter.next();

				if (memberId.equals(member.getMemberId())) {
					iter.remove();
					break;
				}
			}
		}

		return updateGroup(group);
	}

	private ScimGroup getGroupById(String groupId) {
		FilterRequest request = new FilterRequestBuilder().equals("id", groupId).build();
		SearchResults<ScimGroup> results = getGroups(request);

		if (results.getTotalResults() > 0) {
			return results.getResources().iterator().next();
		}

		return null;
	}

	private ScimGroup updateGroup(ScimGroup group) {
		Assert.notNull(group);

		HttpHeaders headers = new HttpHeaders();
		headers.set("if-match", String.valueOf(group.getMeta().getVersion()));

		return helper.putScimObject("/Groups/{id}", group, GROUP_REF, group.getId());
	}
}
