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

import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * All APIs that return multiple object do so in a paginated fashion, following the SCIM standards, allowing for
 * filtering, and returning partial information based on a list of requested attributes. The Spring
 * {@link org.springframework.web.client.RestTemplate} used under the covers does not handle generic classes very well,
 * so concrete instances of this class should restrict to a specific type. This is only instantiated by JSON parsing
 * engines.
 * 
 * @author Josh Ghiloni
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public abstract class PagedResult<T> {

	private Collection<T> resources;

	private int startIndex;

	private int itemsPerPage;

	private int totalResults;

	private List<String> schemas;

	/**
	 * @return The actual list of resources
	 */
	public Collection<T> getResources() {
		return resources;
	}

	/**
	 * @param resources The actual list of resources
	 */
	public void setResources(Collection<T> resources) {
		this.resources = resources;
	}

	/**
	 * @return The first index of the first item on the page (1-based)
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * @param startIndex The first index of the first item on the page (1-based)
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	/**
	 * @return The number of items on the page
	 */
	public int getItemsPerPage() {
		return itemsPerPage;
	}

	/**
	 * @param itemsPerPage The number of items on the page
	 */
	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	/**
	 * @return The total number of results available
	 */
	public int getTotalResults() {
		return totalResults;
	}

	/**
	 * @param totalResults The total number of results available
	 */
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	/**
	 * @return The list of schemas (currently always
	 * {@link org.cloudfoundry.identity.uaa.api.common.model.ScimMetaObject#SCHEMAS})
	 */
	public List<String> getSchemas() {
		return schemas;
	}

	/**
	 * @param schemas The list of schemas (currently always
	 * {@link org.cloudfoundry.identity.uaa.api.common.model.ScimMetaObject#SCHEMAS}). This is set by JSON engines and
	 * internal APIs and should not be manually set
	 */
	public void setSchemas(List<String> schemas) {
		this.schemas = schemas;
	}
}
