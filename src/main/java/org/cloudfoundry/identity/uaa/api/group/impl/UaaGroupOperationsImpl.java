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

import static org.cloudfoundry.identity.uaa.api.common.model.ScimMetaObject.SCHEMAS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.identity.uaa.api.common.impl.UaaConnectionHelper;
import org.cloudfoundry.identity.uaa.api.common.model.ScimMetaObject;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequest;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder;
import org.cloudfoundry.identity.uaa.api.group.UaaGroupOperations;
import org.cloudfoundry.identity.uaa.api.group.model.UaaGroup;
import org.cloudfoundry.identity.uaa.api.group.model.UaaGroupMapping;
import org.cloudfoundry.identity.uaa.api.group.model.UaaGroupMappingIdentifier;
import org.cloudfoundry.identity.uaa.api.group.model.UaaGroupMappingsResults;
import org.cloudfoundry.identity.uaa.api.group.model.UaaGroupMember;
import org.cloudfoundry.identity.uaa.api.group.model.UaaGroupsResults;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @see UaaGroupOperations
 * 
 * @author Josh Ghiloni
 */
public class UaaGroupOperationsImpl implements UaaGroupOperations {

	private UaaConnectionHelper helper;

	public UaaGroupOperationsImpl(UaaConnectionHelper helper) {
		this.helper = helper;
	}

	public UaaGroup createGroup(UaaGroup group) {
		Assert.notNull(group);
		Assert.hasText(group.getDisplayName());

		group.setSchemas(SCHEMAS);

		return helper.post("/Groups", group, UaaGroup.class);
	}

	public void deleteGroup(String groupId) {
		Assert.hasText(groupId);

		helper.delete("/Groups/{id}", Object.class, groupId);
	}

	public UaaGroupsResults getGroups(FilterRequest request) {
		Assert.notNull(request);

		return helper.get(helper.buildScimFilterUrl("/Groups", request), UaaGroupsResults.class);
	}

	public UaaGroupMapping createGroupMapping(UaaGroupMappingIdentifier type, String identifier, String externalGroupDn) {
		Assert.notNull(type);
		Assert.hasText(identifier);
		Assert.hasText(externalGroupDn);

		Map<String, Object> request = new LinkedHashMap<String, Object>(3);

		request.put("schemas", SCHEMAS);
		request.put(type.jsonKey(), identifier);
		request.put("externalGroup", externalGroupDn);

		return helper.post("/Groups/External", request, UaaGroupMapping.class);
	}

	public void deleteGroupMapping(UaaGroupMapping mapping) {
		Assert.notNull(mapping);

		String id = null;
		UaaGroupMappingIdentifier type = null;
		String external = mapping.getExternalGroup();

		if (StringUtils.hasText(mapping.getGroupId())) {
			id = mapping.getGroupId();
			type = UaaGroupMappingIdentifier.GROUP_ID;
		}
		else {
			id = mapping.getDisplayName();
			type = UaaGroupMappingIdentifier.DISPLAY_NAME;
		}

		helper.delete("/Groups/External/{type}/{id}/externalGroup/{externalGroup}", String.class, type, id, external);
	}

	public UaaGroupMappingsResults getGroupMappings(FilterRequest request) {
		Assert.notNull(request);
		return helper.get(helper.buildScimFilterUrl("/Groups/External", request), UaaGroupMappingsResults.class);
	}

	public UaaGroup updateGroupName(String groupId, String newName) {
		UaaGroup group = getGroupById(groupId);
		group.setDisplayName(newName);

		UaaModificationGroup modGroup = new UaaModificationGroup(group);

		return updateGroup(modGroup);
	}

	public UaaGroup addMember(String groupId, String memberUserName) {
		Assert.hasText(memberUserName);

		UaaGroup group = getGroupById(groupId);
		UaaModificationGroup modGroup = new UaaModificationGroup(group);

		String memberId = helper.getUserIdByName(memberUserName);

		Collection<String> members = modGroup.getMembers();
		if (members == null) {
			members = new ArrayList<String>(1);
		}

		members.add(memberId);
		modGroup.setMembers(members);

		return updateGroup(modGroup);
	}

	public UaaGroup deleteMember(String groupId, String memberUserName) {
		Assert.hasText(memberUserName);

		UaaGroup group = getGroupById(groupId);
		UaaModificationGroup modGroup = new UaaModificationGroup(group);

		String memberId = helper.getUserIdByName(memberUserName);

		Collection<String> members = modGroup.getMembers();
		if (members != null && !members.isEmpty()) {
			for (Iterator<String> iter = members.iterator(); iter.hasNext();) {
				String member = iter.next();

				if (memberId.equals(member)) {
					iter.remove();
					break;
				}
			}
		}

		return updateGroup(modGroup);
	}

	private UaaGroup getGroupById(String groupId) {
		FilterRequest request = new FilterRequestBuilder().equals("id", groupId).build();
		UaaGroupsResults results = getGroups(request);

		if (results.getTotalResults() > 0) {
			return results.getResources().iterator().next();
		}

		return null;
	}

	private UaaGroup updateGroup(UaaModificationGroup group) {
		Assert.notNull(group);

		HttpHeaders headers = new HttpHeaders();
		headers.set("if-match", group.getMeta().get("version"));

		return helper.putScimObject("/Groups/{id}", group, UaaGroup.class, group.getId());
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonSerialize(include = Inclusion.NON_NULL)
	public static class UaaModificationGroup extends ScimMetaObject {

		private String displayName;

		private String groupId;

		private Collection<String> members;

		UaaModificationGroup(UaaGroup clone) {
			setDisplayName(clone.getDisplayName());
			setGroupId(clone.getGroupId());
			setSchemas(clone.getSchemas());
			setId(clone.getId());
			setMeta(clone.getMeta());

			Collection<UaaGroupMember> members = clone.getMembers();
			if (members != null) {
				List<String> memberIds = new ArrayList<String>(members.size());
				for (UaaGroupMember member : members) {
					memberIds.add(member.getValue());
				}

				setMembers(memberIds);
			}
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		public Collection<String> getMembers() {
			return members;
		}

		public void setMembers(Collection<String> members) {
			this.members = members;
		}
	}
}
