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
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequest;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
import org.cloudfoundry.identity.uaa.api.group.UaaGroupOperations;
import org.cloudfoundry.identity.uaa.api.group.model.ScimGroupExternalMembers;
import org.cloudfoundry.identity.uaa.api.group.model.ScimGroups;
import org.cloudfoundry.identity.uaa.scim.ScimGroup;
import org.cloudfoundry.identity.uaa.scim.ScimGroupExternalMember;
import org.cloudfoundry.identity.uaa.scim.ScimGroupMember;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @see ScimGroupOperations
 * 
 * @author Josh Ghiloni
 */
public class UaaGroupOperationsImpl implements UaaGroupOperations {

	private UaaConnectionHelper helper;

	public UaaGroupOperationsImpl(UaaConnectionHelper helper) {
		this.helper = helper;
	}

	public ScimGroup createGroup(ScimGroup group) {
		Assert.notNull(group);
		Assert.hasText(group.getDisplayName());

		group.setSchemas(SCHEMAS);

		return helper.post("/Groups", group, ScimGroup.class);
	}

	public void deleteGroup(String groupId) {
		Assert.hasText(groupId);

		helper.delete("/Groups/{id}", Object.class, groupId);
	}

	public ScimGroups getGroups(FilterRequest request) {
		Assert.notNull(request);

		return helper.get(helper.buildScimFilterUrl("/Groups", request), ScimGroups.class);
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

		return helper.post("/Groups/External", request, ScimGroupExternalMember.class);
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

		helper.delete("/Groups/External/{type}/{id}/externalGroup/{externalGroup}", String.class, type, id, external);
	}

	public ScimGroupExternalMembers getGroupMappings(FilterRequest request) {
		Assert.notNull(request);
		return helper.get(helper.buildScimFilterUrl("/Groups/External", request), ScimGroupExternalMembers.class);
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
		ScimGroups results = getGroups(request);

		if (results.getTotalResults() > 0) {
			return results.getResources().iterator().next();
		}

		return null;
	}

	private ScimGroup updateGroup(ScimGroup group) {
		Assert.notNull(group);

		HttpHeaders headers = new HttpHeaders();
		headers.set("if-match", String.valueOf(group.getMeta().getVersion()));

		return helper.putScimObject("/Groups/{id}", group, ScimGroup.class, group.getId());
	}

	/*
	 * @JsonIgnoreProperties(ignoreUnknown = true)
	 * 
	 * @JsonSerialize(include = Inclusion.NON_NULL) public static class UaaModificationGroup extends ScimMetaObject {
	 * 
	 * private String displayName;
	 * 
	 * private String groupId;
	 * 
	 * private Collection<String> members;
	 * 
	 * UaaModificationGroup(ScimGroup clone) { setDisplayName(clone.getDisplayName()); setGroupId(clone.getGroupId());
	 * setSchemas(clone.getSchemas()); setId(clone.getId()); setMeta(clone.getMeta());
	 * 
	 * Collection<ScimGroupMember> members = clone.getMembers(); if (members != null) { List<String> memberIds = new
	 * ArrayList<String>(members.size()); for (ScimGroupMember member : members) { memberIds.add(member.getValue()); }
	 * 
	 * setMembers(memberIds); } }
	 * 
	 * public String getDisplayName() { return displayName; }
	 * 
	 * public void setDisplayName(String displayName) { this.displayName = displayName; }
	 * 
	 * public String getGroupId() { return groupId; }
	 * 
	 * public void setGroupId(String groupId) { this.groupId = groupId; }
	 * 
	 * public Collection<String> getMembers() { return members; }
	 * 
	 * public void setMembers(Collection<String> members) { this.members = members; } }
	 */
}
