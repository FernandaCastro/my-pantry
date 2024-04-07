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

@Component
public class AuthorizationHandler {

    Logger log = LoggerFactory.getLogger(AuthorizationHandler.class);

    private final RestClient authzServer;

    public AuthorizationHandler(RestClient authzServer) {
        this.authzServer = authzServer;
    }

    public List<AccountGroupMemberDto> hasPermissionInAnyGroup(String email, String permission) {

        StringBuilder uri = new StringBuilder("/authorization/permission-in-any-group?")
                .append("email=").append(email)
                .append("&permission=").append(permission);

        return authzServer.get()
                .uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() >= 400, (request, response) -> {
                    throw new AccessDeniedException("Request to AuthorizationServer(permission-in-any-group) failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(new ParameterizedTypeReference<List<AccountGroupMemberDto>>() {
                });
    }

    public List<AccountGroupMemberDto> hasPermissionInAGroup(String email, String permission, Long accountGroupId) {

        StringBuilder uri = new StringBuilder("/authorization/permission-in-group?")
                .append("email=").append(email)
                .append("&permission=").append(permission)
                .append("&accountGroupId=").append(accountGroupId);

        return authzServer.get()
                .uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() >= 400, (request, response) -> {
                    throw new AccessDeniedException("Request to AuthorizationServer(permission-in-group) failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(new ParameterizedTypeReference<List<AccountGroupMemberDto>>() {
                });
    }

    public List<AccountGroupMemberDto> hasPermissionInObject(String email, String permission, String clazz, Long clazzId) {

        StringBuilder uri = new StringBuilder("authorization/permission-in-object?")
                .append("email=").append(email)
                .append("&permission=").append(permission)
                .append("&clazz=").append(clazz)
                .append("&clazzId=").append(clazzId);

        return authzServer.get()
                .uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() >= 400, (request, response) -> {
                    throw new AccessDeniedException("Request to AuthorizationServer(permission-in-object) failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(new ParameterizedTypeReference<List<AccountGroupMemberDto>>() {
                });
    }


    public AccessControlDto getAccessControl(String clazz, Long clazzId) {
        return authzServer.get()
                .uri("/accessControl?clazz={clazz}&clazzId={clazzId}", clazz, clazzId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() >= 400, (request, response) -> {
                    throw new AccessDeniedException("Request to AuthorizationServer(accessControl) failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(AccessControlDto.class);
    }

    public List<AccessControlDto> listAccessControl(String email, String clazz, Long clazzId, Long accountGroupId) {
        StringBuilder uri = new StringBuilder("/authorization/access-control?")
                .append("email=").append(email)
                .append("&clazz=").append(clazz);

        if (clazzId != null) uri.append("&clazzId=").append(clazzId);
        if (accountGroupId != null) uri.append("&accountGroupId=").append(accountGroupId);

        return authzServer.get()
                .uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() >= 400, (request, response) -> {
                    throw new AccessDeniedException("Request AuthorizationServer(access-control) failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(new ParameterizedTypeReference<List<AccessControlDto>>() {
                });
    }

    public void saveAccessControl(String clazz, Long clazzId, Long accountGroupId) {

        var body = AccessControlDto.builder()
                .clazz(clazz)
                .clazzId(clazzId)
                .accountGroup(AccountGroupDto.builder().id(accountGroupId).build())
                .build();

        authzServer.post()
                .uri("/authorization/access-control")
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

        StringBuilder uri = new StringBuilder("authorization/access-control?")
                .append("clazz=").append(clazz)
                .append("&clazzId={clazzId}");

        authzServer.delete()
                .uri(uri.toString(), clazz, clazzId)
                .retrieve()
                .onStatus(status ->
                        status.value() >= 400, (request, response) -> {
                    throw new AccessDeniedException("Request to delete AccessControl from AuthorizationServer failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .toBodilessEntity();
    }
}
