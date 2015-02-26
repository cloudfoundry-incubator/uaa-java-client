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
package org.cloudfoundry.identity.uaa.api.group.model;

import java.util.Collection;

import org.cloudfoundry.identity.uaa.api.common.model.ScimMetaObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * A Java representation of the JSON returned by UAA Group API calls
 * 
 * @author Josh Ghiloni
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = Inclusion.NON_NULL)
public class UaaGroup extends ScimMetaObject {

	private String displayName;

	private String groupId;

	private Collection<UaaGroupMember> members;

	/**
	 * @return The group's display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * 
	 * @param displayName The group's display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return The group's members
	 */
	public Collection<UaaGroupMember> getMembers() {
		return members;
	}

	/**
	 * This should only be called by JSON serialization engines
	 * @param members the group members
	 * @see org.cloudfoundry.identity.uaa.api.group.UaaGroupOperations#addMember(String, String)
	 * @see org.cloudfoundry.identity.uaa.api.group.UaaGroupOperations#deleteMember(String, String)
	 */
	public void setMembers(Collection<UaaGroupMember> members) {
		this.members = members;
	}

	/**
	 * @return The group ID
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * 
	 * @param groupId The group ID
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UaaGroup)) {
			return false;
		}
		
		UaaGroup other = (UaaGroup)o;
		
		if (!safeEquals(this.id, other.id)) {
			return false;
		}
		
		if (!safeEquals(this.displayName, other.displayName)) {
			return false;
		}
		
		if (!safeEquals(this.groupId, other.groupId)) {
			return false;
		}
		
		if (!safeEquals(this.members, other.members)) {
			return false;
		}
		
		if (!safeEquals(this.meta, other.meta)) {
			return false;
		}
		
		if (!safeEquals(this.schemas, other.schemas)) {
			return false;
		}
		
		return true;
	}
	
	private <T extends Object> boolean safeEquals(T one, T two) {
		if (one == null && two != null) {
			return false;
		}
		
		if (one != null && two == null) {
			return false;
		}
		
		if (one == null && two == null) {
			return true;
		}
		
		return one.equals(two);
	}
}
