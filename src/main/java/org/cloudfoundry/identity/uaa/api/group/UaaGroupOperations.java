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
package org.cloudfoundry.identity.uaa.api.group;

import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequest;
import org.cloudfoundry.identity.uaa.rest.SearchResults;
import org.cloudfoundry.identity.uaa.scim.ScimGroup;
import org.cloudfoundry.identity.uaa.scim.ScimGroupExternalMember;

/**
 * Provides endpoints to the UAA group APIs specified <a
 * href="https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-APIs.rst#group-management-apis">here</a>
 * 
 * @author Josh Ghiloni
 *
 */
public interface UaaGroupOperations {
	public enum ScimGroupExternalMemberType {
		groupId, displayName
	}

	/**
	 * Create a group in the UAA database
	 * 
	 * @param group The partial group to be created. Members will not be created in this call
	 * @return The newly created group, with id and meta information
	 * @see #addMember(String, String)
	 */
	public ScimGroup createGroup(ScimGroup group);

	/**
	 * Update the display name in the UAA database
	 * 
	 * @param groupId the ID of the group
	 * @param newName the new display name
	 * @return the group with the specified group ID and the new Name
	 */
	public ScimGroup updateGroupName(String groupId, String newName);

	/**
	 * Add a member to the group
	 * 
	 * @param groupId the group id
	 * @param memberName the member's username (will be converted to ID)
	 * @return the group with the member in it
	 * @see org.cloudfoundry.identity.uaa.api.user.model.UaaUser#getUserName()
	 * @see org.cloudfoundry.identity.uaa.api.user.UaaUserOperations#getUserByName(String)
	 */
	public ScimGroup addMember(String groupId, String memberName);

	/**
	 * Remove a member from the group
	 * 
	 * @param groupId the group id
	 * @param memberName the member's username (will be converted to ID)
	 * @return the group without the member in it
	 * @see org.cloudfoundry.identity.uaa.api.user.model.UaaUser#getUserName()
	 * @see org.cloudfoundry.identity.uaa.api.user.UaaUserOperations#getUserByName(String)
	 */
	public ScimGroup deleteMember(String groupId, String memberName);

	/**
	 * Delete the group from the database. An exception will be thrown if the operation fails
	 * 
	 * @param groupId the group ID
	 */
	public void deleteGroup(String groupId);

	/**
	 * Get a page of groups based on the given {@link FilterRequest}
	 * 
	 * @param request the {@link FilterRequest}
	 * @return The page of groups.
	 * @see org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder
	 */
	public SearchResults<ScimGroup> getGroups(FilterRequest request);

	/**
	 * Create a mapping from an external LDAP group to an internal UAA group. Only effective when UAA is configured with
	 * ldap/ldap-groups-map-to-scopes.xml (see <a
	 * href="https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-LDAP.md#ldap-groups-to-scopes-configuration"
	 * >here</a>)
	 * 
	 * @param type mapping the local group by displayName or id
	 * @param identifier the identifier specified by <code>type</code>
	 * @param externalGroupDn the DN of the LDAP group
	 * @return the new mapping
	 */
	public ScimGroupExternalMember createGroupMapping(ScimGroupExternalMemberType type, String identifier,
			String externalGroupDn);

	/**
	 * Delete the group mapping. Only effective when UAA is configured with ldap/ldap-groups-map-to-scopes.xml (see <a
	 * href="https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-LDAP.md#ldap-groups-to-scopes-configuration"
	 * >here</a>). An exception is thrown if the operation fails.
	 * 
	 * @param mapping the mapping to delete
	 */
	public void deleteGroupMapping(ScimGroupExternalMember mapping);

	/**
	 * List the group mappings with an optional filter. Only effective when UAA is configured with
	 * ldap/ldap-groups-map-to-scopes.xml (see <a
	 * href="https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-LDAP.md#ldap-groups-to-scopes-configuration"
	 * >here</a>)
	 * 
	 * @param request the filter
	 * @return the list of group mappings
	 * @see org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder
	 */
	public SearchResults<ScimGroupExternalMember> getGroupMappings(FilterRequest request);
}