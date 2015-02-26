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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Many of the UAA APIs follow <a href="http://www.simplecloud.info/">SCIM</a> standards. This object represents a base
 * SCIM object with common metadata
 * 
 * @author Josh Ghiloni
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = Inclusion.NON_NULL)
public abstract class ScimMetaObject {
	public static final List<String> SCHEMAS = Arrays.asList("urn:scim:schemas:core:1.0");

	protected String id;

	protected Collection<String> schemas;

	protected Map<String, String> meta;

	/**
	 * @return The object's ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id The object's ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return The schemas (currently always {@link #SCHEMAS})
	 */
	public Collection<String> getSchemas() {
		return schemas;
	}

	/**
	 * @param schemas The schemas. This is set by JSON engines and internal APIs and should not be manually set
	 */
	public void setSchemas(Collection<String> schemas) {
		this.schemas = schemas;
	}

	/**
	 * @return The metadata about the object (including version)
	 */
	public Map<String, String> getMeta() {
		return meta;
	}

	/**
	 * @param meta The metadata about the object. Set only by JSON engines
	 */
	public void setMeta(Map<String, String> meta) {
		this.meta = meta;
	}
}
