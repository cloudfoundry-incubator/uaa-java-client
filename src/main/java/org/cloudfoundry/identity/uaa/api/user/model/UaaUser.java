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
package org.cloudfoundry.identity.uaa.api.user.model;

import java.util.Collection;

import org.cloudfoundry.identity.uaa.api.common.model.ScimMetaObject;
import org.cloudfoundry.identity.uaa.api.common.model.ValueObject;
import org.cloudfoundry.identity.uaa.api.group.model.UaaGroupMember;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * A Java representation of the JSON returned by UAA User API calls
 * 
 * @author Josh Ghiloni
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = Inclusion.NON_NULL)
public class UaaUser extends ScimMetaObject {

	private String userName;

	private Name name;

	private String password;

	private Collection<ValueObject> emails;

	private Collection<ValueObject> phoneNumbers;

	private Collection<UaaGroupMember> groups;

	/**
	 * @return The user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName The user name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return The user's real name
	 */
	public Name getName() {
		return name;
	}

	/**
	 * @param name The user's real name
	 */
	public void setName(Name name) {
		this.name = name;
	}

	/**
	 * @return The user's password. Will not be returned by API calls
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password The user's password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return a list of {@link ValueObject} with the email addresses
	 */
	public Collection<ValueObject> getEmails() {
		return emails;
	}

	/**
	 * @param emails The user's email address(es)
	 */
	public void setEmails(Collection<ValueObject> emails) {
		this.emails = emails;
	}

	/**
	 * @return a list of {@link ValueObject} with the phone numbers
	 */
	public Collection<ValueObject> getPhoneNumbers() {
		return phoneNumbers;
	}

	/**
	 * @param phoneNumbers The user's phone number(s)
	 */
	public void setPhoneNumbers(Collection<ValueObject> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	/**
	 * @return The user's groups
	 */
	public Collection<UaaGroupMember> getGroups() {
		return groups;
	}

	/**
	 * This should only be called by a JSON serializer
	 * @param groups the user's groups
	 * @see org.cloudfoundry.identity.uaa.api.group.UaaGroupOperations#addMember(String, String)
	 */
	public void setGroups(Collection<UaaGroupMember> groups) {
		this.groups = groups;
	}

	/**
	 * Represents a real name
	 * 
	 * @author Josh Ghiloni
	 */
	public static class Name {
		private String formatted;

		private String familyName;

		private String givenName;

		public Name() {
		}

		/**
		 * The user's name
		 * 
		 * @param formatted The full name, formatted in whatever way you choose (ex. "John Smith")
		 * @param familyName The user's family name (ex. Smith)
		 * @param givenName The user's given name (ex. John)
		 */
		public Name(String formatted, String familyName, String givenName) {
			this.formatted = formatted;
			this.familyName = familyName;
			this.givenName = givenName;
		}

		public String getFormatted() {
			return formatted;
		}

		public void setFormatted(String formatted) {
			this.formatted = formatted;
		}

		public String getFamilyName() {
			return familyName;
		}

		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}

		public String getGivenName() {
			return givenName;
		}

		public void setGivenName(String givenName) {
			this.givenName = givenName;
		}
	}
}
