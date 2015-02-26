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

import org.cloudfoundry.identity.uaa.api.common.model.ScimMetaObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Representation of a UAA group to LDAP group
 * 
 * @author Josh Ghiloni
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = Inclusion.NON_NULL)
public class UaaGroupMapping extends ScimMetaObject {

	private String groupId;

	private String displayName;

	private String externalGroup;

	/**
	 * @return the UAA group ID
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * 
	 * @param groupId the UAA group ID
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * 
	 * @return the UAA group display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * 
	 * @param displayName the UAA group display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * 
	 * @return the LDAP group's DN
	 */
	public String getExternalGroup() {
		return externalGroup;
	}

	/**
	 * 
	 * @param externalGroup the LDAP group's DN
	 */
	public void setExternalGroup(String externalGroup) {
		this.externalGroup = externalGroup;
	}
}
