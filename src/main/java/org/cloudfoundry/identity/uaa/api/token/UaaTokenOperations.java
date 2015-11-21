package org.cloudfoundry.identity.uaa.api.token;

import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public interface UaaTokenOperations {

    OAuth2AccessToken get(OAuth2ProtectedResourceDetails resourceDetails);

}
