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
package org.cloudfoundry.identity.uaa.api.client;

import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequest;
import org.cloudfoundry.identity.uaa.rest.SearchResults;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

/**
 * Provides endpoints to the UAA client APIs specified <a
 * href="https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-APIs.rst#client-registration-administration-apis"
 * >here</a>
 * 
 * @author Josh Ghiloni
 * 
 */
public interface UaaClientOperations {
	/**
	 * Create a new UAA Client
	 * 
	 * @param client the new client
	 * @return the newly created client
	 */
	public BaseClientDetails create(BaseClientDetails client);

	/**
	 * Find a given client by its ID
	 * @param clientId the client ID
	 * @return the client, or null if not found
	 */
	public BaseClientDetails findById(String clientId);

	/**
	 * Update the client. Secrets cannot be changed in this method.
	 * 
	 * @param updated the client with new data
	 * @return the client returned from the API
	 * @see #changeClientSecret(String, String, String)
	 */
	public BaseClientDetails update(BaseClientDetails updated);

	/**
	 * Delete the client with the given ID
	 * 
	 * @param clientId
	 * @return
	 */
	public BaseClientDetails delete(String clientId);

	/**
	 * Get clients based on the given SCIM filter.
	 * 
	 * @param request
	 * @return the clients
	 * @see org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequestBuilder
	 */
	public SearchResults<BaseClientDetails> getClients(FilterRequest request);

	/**
	 * Change a client's secret. Note that you MUST have the existing secret, as the APIs require it.
	 * 
	 * @param clientId The client ID whose secret should be changed
	 * @param oldSecret The existing secret
	 * @param newSecret The new secret
	 * @return true if the change was successful, false otherwise
	 */
	public boolean changeClientSecret(String clientId, String oldSecret, String newSecret);
}