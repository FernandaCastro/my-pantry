package com.fcastro.security.authorization;

import com.fcastro.security.core.model.AccountGroupMemberDto;
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

    private final RestClient authzServer;

    public AuthorizationHandler(RestClient authzServer) {
        this.authzServer = authzServer;
    }

    public AccountGroupMemberDto getGroupMember(Long groupId, String email) {

        return authzServer.get()
                .uri("/accountGroupMembers/{groupId}/" + email, groupId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new AccessDeniedException("User " + email + " is not authorized in this group.");
                })
                .body(AccountGroupMemberDto.class);
    }

    public List<AccountGroupMemberDto> getGroupMember(String email) {

        return authzServer.get()
                .uri("/accountGroupMembers?email=" + email)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() == 401, (request, response) -> {
                    throw new AccessDeniedException("Request unauthorized to retrieve authorization data.");
                })
                .onStatus(status ->
                        status.value() == 403, (request, response) -> {
                    throw new AccessDeniedException("Request forbidden to retrieve authorization data.");
                })
                .onStatus(status ->
                        status.value() == 404, (request, response) -> {
                    throw new AccessDeniedException("User " + email + " is not authorized in this group.");
                })
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public Set<Long> getAccountGroupList(String email) {
        var groupMembers = getGroupMember(email);
        return groupMembers.stream().map(AccountGroupMemberDto::getAccountGroupId).collect(Collectors.toSet());
    }
}
