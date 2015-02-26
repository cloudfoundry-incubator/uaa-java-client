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
package org.cloudfoundry.identity.uaa.api.common.model.expr;

import java.util.List;

/**
 * A class used to filter results from SCIM paged APIs. Must be constructed using {@link FilterRequestBuilder}
 * 
 * @author Josh Ghiloni
 *
 */
public class FilterRequest {
	private String filter;

	private List<String> attributes;

	private int start;

	private int count;

	public FilterRequest() {

	}

	FilterRequest(String filter, List<String> attributes, int start, int count) {
		this.filter = filter;
		this.attributes = attributes;
		this.start = start;
		this.count = count;
	}

	/**
	 * The SCIM filter string
	 * @return
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @return The 1-based starting index for the query. If &lt; 0, indicates the start of the list
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return The number of items to be returned by the query. If &lt; 1, no count will be passed to the API, and the
	 * default UAA behavior will take place.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return The attributes to be returned by the query. If the list is empty, all attributes are returned.
	 */
	public List<String> getAttributes() {
		return attributes;
	}

	static final FilterRequest SHOW_ALL = new FilterRequest(null, null, 0, 0);
}
