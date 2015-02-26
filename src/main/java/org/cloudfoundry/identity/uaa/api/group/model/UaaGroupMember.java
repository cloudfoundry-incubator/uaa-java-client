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

import org.cloudfoundry.identity.uaa.api.common.model.ValueObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * A member of a group. Should only be instantiated by JSON parsing engines
 * 
 * @author Josh Ghiloni
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = Inclusion.NON_NULL)
public class UaaGroupMember extends ValueObject {
	public UaaGroupMember() {
		super();
	}
	
	public UaaGroupMember(String value) {
		super(value);
	}

	private String type;

	private Collection<String> authorities;

	/**
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return
	 */
	public Collection<String> getAuthorities() {
		return authorities;
	}

	/**
	 * 
	 * @param authorities
	 */
	public void setAuthorities(Collection<String> authorities) {
		this.authorities = authorities;
	}
}
