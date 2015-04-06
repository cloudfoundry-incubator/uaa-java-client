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
package org.cloudfoundry.identity.uaa.api.client.impl;

import java.util.HashMap;
import java.util.Map;

import org.cloudfoundry.identity.uaa.api.client.UaaClientOperations;
import org.cloudfoundry.identity.uaa.api.common.impl.UaaConnectionHelper;
import org.cloudfoundry.identity.uaa.api.common.model.WrappedSearchResults;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequest;
import org.cloudfoundry.identity.uaa.rest.SearchResults;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.Assert;

/**
 * @see UaaClientOperations
 * @author Josh Ghiloni
 *
 */
public class UaaClientOperationsImpl implements UaaClientOperations {

	private static final ParameterizedTypeReference<String> STRING_REF = new ParameterizedTypeReference<String>() {
	};

	private static final ParameterizedTypeReference<BaseClientDetails> CLIENT_REF = new ParameterizedTypeReference<BaseClientDetails>() {
	};

	private static final ParameterizedTypeReference<WrappedSearchResults<BaseClientDetails>> CLIENTS_REF = new ParameterizedTypeReference<WrappedSearchResults<BaseClientDetails>>() {
	};

	private UaaConnectionHelper helper;

	public UaaClientOperationsImpl(UaaConnectionHelper helper) {
		this.helper = helper;
	}

	public BaseClientDetails create(BaseClientDetails client) {
		Assert.notNull(client);
		Assert.hasText(client.getClientId());

		return helper.post("/oauth/clients", client, CLIENT_REF);
	}

	public BaseClientDetails findById(String clientId) {
		Assert.hasText(clientId);
		return helper.get("/oauth/clients/{id}", CLIENT_REF, clientId);
	}

	public BaseClientDetails update(BaseClientDetails client) {
		Assert.notNull(client);
		Assert.hasText(client.getClientId());

		return helper.put("/oauth/clients/{id}", client, CLIENT_REF, client.getClientId());
	}

	public BaseClientDetails delete(String clientId) {
		Assert.hasText(clientId);
		return helper.delete("/oauth/clients/{id}", CLIENT_REF, clientId);
	}

	public SearchResults<BaseClientDetails> getClients(FilterRequest request) {
		Assert.notNull(request);

		return helper.get(helper.buildScimFilterUrl("/oauth/clients", request), CLIENTS_REF);
	}

	public boolean changeClientSecret(String clientId, String oldSecret, String newSecret) {
		Map<String, String> body = new HashMap<String, String>(2);
		body.put("oldSecret", oldSecret);
		body.put("secret", newSecret);

		String result = helper.put("/oauth/clients/{id}/secret", body, STRING_REF, clientId);
		System.out.println(result);

		return (result != null);
	}
}
