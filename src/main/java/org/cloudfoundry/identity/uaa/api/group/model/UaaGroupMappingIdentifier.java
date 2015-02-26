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

/**
 * How a UAA to LDAP group mapping is identified. This is specific to the UAA group, because the LDAP group is always
 * identified by DN.
 * 
 * @author Josh Ghiloni
 *
 */
public enum UaaGroupMappingIdentifier {
	GROUP_ID("groupId"), DISPLAY_NAME("displayName");

	private String jsonValue;

	UaaGroupMappingIdentifier(String jsonValue) {
		this.jsonValue = jsonValue;
	}

	public String toString() {
		return jsonValue;
	}

	public String jsonKey() {
		return jsonValue;
	}
}
