package com.fcastro.security.authorization;

import com.fcastro.security.core.model.AccessControlDto;
import com.fcastro.security.core.model.AccountGroupDto;
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

    public AccountGroupMemberDto getAccountGroupMemberList(Long groupId, String email) {

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

    public List<AccountGroupMemberDto> getAccountGroupMemberList(String email) {

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

    public Set<Long> getAccountGroupIdList(String email) {
        if (email == null || email.length() == 0)
            throw new AccessDeniedException("Email is empty. Request unauthorized.");
        var groupMembers = getAccountGroupMemberList(email);
        if (groupMembers == null) return null;
        return groupMembers.stream().map(AccountGroupMemberDto::getAccountGroupId).collect(Collectors.toSet());
    }

    public AccessControlDto getAccessControl(String clazz, Long clazzId) {
        return authzServer.get()
                .uri("/accessControl?clazz={clazz}&clazzId={clazzId}", clazz, clazzId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() >= 400, (request, response) -> {
                    throw new AccessDeniedException("Request to retrieve AccessControl from AuthorizationServer failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(AccessControlDto.class);
    }

    public void saveAccessControl(String clazz, Long clazzId, Long accountGroupId) {
        var body = AccessControlDto.builder()
                .clazz(clazz)
                .clazzId(clazzId)
                .accountGroup(AccountGroupDto.builder().id(accountGroupId).build())
                .build();

        authzServer.post()
                .uri("/accessControl")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .onStatus(status ->
                        status.value() >= 400, (request, response) -> {
                    throw new AccessDeniedException("Request to save AccessControl from AuthorizationServer failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .toBodilessEntity();
    }

    public void deleteAccessControl(String clazz, Long clazzId) {

        authzServer.delete()
                .uri("/accessControl?clazz={clazz}&clazzId={clazzId}\", clazz, clazzId")
                .retrieve()
                .onStatus(status ->
                        status.value() >= 400, (request, response) -> {
                    throw new AccessDeniedException("Request to delete AccessControl from AuthorizationServer failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .toBodilessEntity();
    }
}
