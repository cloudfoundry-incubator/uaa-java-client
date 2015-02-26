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
package org.cloudfoundry.identity.uaa.api.common.model;

/**
 * Certain UAA API calls return objects in the form {"value":"some value"}. This object represents that.
 * 
 * @author Josh Ghiloni
 *
 */
public class ValueObject {
	private String value;

	public ValueObject() {

	}

	/**
	 * @param value The value to set
	 */
	public ValueObject(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value The value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
