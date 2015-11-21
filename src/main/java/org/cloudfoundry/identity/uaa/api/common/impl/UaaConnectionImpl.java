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
package org.cloudfoundry.identity.uaa.api.common.impl;

import org.cloudfoundry.identity.uaa.api.client.UaaClientOperations;
import org.cloudfoundry.identity.uaa.api.client.impl.UaaClientOperationsImpl;
import org.cloudfoundry.identity.uaa.api.common.UaaConnection;
import org.cloudfoundry.identity.uaa.api.group.UaaGroupOperations;
import org.cloudfoundry.identity.uaa.api.group.impl.UaaGroupOperationsImpl;
import org.cloudfoundry.identity.uaa.api.token.UaaTokenOperations;
import org.cloudfoundry.identity.uaa.api.token.impl.UaaTokenOperationsImpl;
import org.cloudfoundry.identity.uaa.api.user.UaaUserOperations;
import org.cloudfoundry.identity.uaa.api.user.impl.UaaUserOperationsImpl;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

import java.net.URI;
import java.net.URL;

/**
 * @see UaaConnection
 * @author Josh Ghiloni
 */
public class UaaConnectionImpl implements UaaConnection {

	private URL target;
	private UaaConnectionHelper helper;

	public UaaConnectionImpl(URL target, UaaConnectionHelper helper) {
		this.target = target;
		this.helper = helper;
	}

	public UaaClientOperations clientOperations() {
		return new UaaClientOperationsImpl(helper);
	}

	public UaaGroupOperations groupOperations() {
		return new UaaGroupOperationsImpl(helper);
	}

	public UaaUserOperations userOperations() {
		return new UaaUserOperationsImpl(helper);
	}

	@Override
	public UaaTokenOperations tokenOperations() {
		return new UaaTokenOperationsImpl();
	}

	@Override
	public ClientCredentialsResourceDetails newClientCredentialsResourceDetails() {
		ClientCredentialsResourceDetails credentials = new ClientCredentialsResourceDetails();
		credentials.setAccessTokenUri(target.toExternalForm() + "/oauth/token");
		credentials.setClientAuthenticationScheme(AuthenticationScheme.header);
		return credentials;
	}
}
