package com.fcastro.security.authorization;

import com.fcastro.security.core.model.AccountGroupMemberDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthorizationHandler {

    Logger log = LoggerFactory.getLogger(AuthorizationHandler.class);

    private final RestClient authzServer;

    public AuthorizationHandler(RestClient authzServer) {
        this.authzServer = authzServer;
    }

    public AccountGroupMemberDto getGroupMember(Long groupId, String email) {

        return authzServer.get()
                .uri("/accountGroupMembers/{groupId}/" + email, groupId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() >= 400, (request, response) -> {
                    throw new AccessDeniedException("Request to retrieve GroupMember from AuthorizationServer failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(AccountGroupMemberDto.class);
    }

    public List<AccountGroupMemberDto> getGroupMember(String email) {

        return authzServer.get()
                .uri("/accountGroupMembers?email=" + email)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() >= 400, (request, response) -> {
                    throw new AccessDeniedException("Request to retrieve GroupMember from AuthorizationServer failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(new ParameterizedTypeReference<List<AccountGroupMemberDto>>() {
                });
    }

    public Set<Long> getAccountGroupList(String email) {
        log.info("Account " + email + "requesting AccountGroups");
        if (email == null || email.length() == 0)
            throw new AccessDeniedException("Email is empty. Request unauthorized.");
        var groupMembers = getGroupMember(email);
        if (groupMembers == null) return null;
        return groupMembers.stream().map(AccountGroupMemberDto::getAccountGroupId).collect(Collectors.toSet());
    }
}
