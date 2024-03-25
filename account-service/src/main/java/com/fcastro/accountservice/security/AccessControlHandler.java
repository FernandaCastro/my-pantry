package com.fcastro.accountservice.security;

import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.model.AccessControlDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
public class AccessControlHandler {

    private final RestClient restClient;
    private final SecurityPropertiesConfig securityPropertiesConfig;

    public AccessControlHandler(RestClient restClient, SecurityPropertiesConfig securityPropertiesConfig) {
        this.restClient = restClient;
        this.securityPropertiesConfig = securityPropertiesConfig;
    }

    public boolean isInUse(long accountGroupId) {
        boolean isInUse = false;
        var domains = securityPropertiesConfig.getAccessControlDomains();

        return Arrays.stream(domains)
                .anyMatch((domain) -> isInUse(domain, accountGroupId));
    }

    private boolean isInUse(String domain, long accountGroupId) {
        var baseUri = domain + "/accessControl?accountGroupId=" + accountGroupId;
        var accessControlList = restClient.get()
                .uri(baseUri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() == 401, (request, response) -> {
                    throw new AccessDeniedException("Request unauthorized to retrieve access control data.");
                })
                .onStatus(status ->
                        status.value() == 403, (request, response) -> {
                    throw new AccessDeniedException("Request forbidden to retrieve access control data.");
                })
                .body(new ParameterizedTypeReference<List<AccessControlDto>>() {
                });
        return accessControlList != null && accessControlList.size() > 0;
    }

}
