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
import org.cloudfoundry.identity.uaa.api.client.model.UaaClient;
import org.cloudfoundry.identity.uaa.api.client.model.UaaClientsResults;
import org.cloudfoundry.identity.uaa.api.common.impl.UaaConnectionHelper;
import org.cloudfoundry.identity.uaa.api.common.model.expr.FilterRequest;
import org.springframework.util.Assert;

/**
 * @see UaaClientOperations
 * @author Josh Ghiloni
 *
 */
public class UaaClientOperationsImpl implements UaaClientOperations {

	private UaaConnectionHelper helper;

	public UaaClientOperationsImpl(UaaConnectionHelper helper) {
		this.helper = helper;
	}

	public UaaClient create(UaaClient client) {
		Assert.notNull(client);
		Assert.hasText(client.getClientId());

		return helper.post("/oauth/clients", client, UaaClient.class);
	}

	public UaaClient findById(String clientId) {
		Assert.hasText(clientId);
		return helper.get("/oauth/clients/{id}", UaaClient.class, clientId);
	}

	public UaaClient update(UaaClient client) {
		Assert.notNull(client);
		Assert.hasText(client.getClientId());

		return helper.put("/oauth/clients/{id}", client, UaaClient.class, client.getClientId());
	}

	public UaaClient delete(String clientId) {
		Assert.hasText(clientId);
		return helper.delete("/oauth/clients/{id}", UaaClient.class, clientId);
	}

	public UaaClientsResults getClients(FilterRequest request) {
		Assert.notNull(request);

		return helper.get(helper.buildScimFilterUrl("/oauth/clients", request), UaaClientsResults.class);
	}

	public boolean changeClientSecret(String clientId, String oldSecret, String newSecret) {
		Map<String, String> body = new HashMap<String, String>(2);
		body.put("oldSecret", oldSecret);
		body.put("secret", newSecret);

		String result = helper.put("/oauth/clients/{id}/secret", body, String.class, clientId);
		System.out.println(result);
		
		return (result != null);
	}
}
