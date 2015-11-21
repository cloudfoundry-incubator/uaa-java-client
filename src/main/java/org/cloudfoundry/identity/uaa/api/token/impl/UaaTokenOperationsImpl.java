package org.cloudfoundry.identity.uaa.api.token.impl;

import org.cloudfoundry.identity.uaa.api.common.impl.UaaConnectionHelper;
import org.cloudfoundry.identity.uaa.api.token.UaaTokenOperations;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.net.URI;

public class UaaTokenOperationsImpl implements UaaTokenOperations {

    public UaaTokenOperationsImpl() {
    }

    @Override
    public OAuth2AccessToken get(OAuth2ProtectedResourceDetails resourceDetails) {

        OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext());
        return template.getAccessToken();
    }
}
