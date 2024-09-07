package com.fcastro.security.authorization;

import com.fcastro.security.core.model.AccessControlDto;
import com.fcastro.security.core.model.AccountGroupDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorizationClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationClient.class);

    private final String AUTH_URL = "/accountservice/authorization";
    private final String PERMISSION_IN_ANY_GROUP = "/permission-in-any-group?";
    private final String PERMISSION_IN_A_GROUP = "/permission-in-group?";
    private final String PERMISSION_IN_OBJECT = "/permission-in-object?";
    private final String PERMISSION_IN_OBJECT_LIST = "/permission-in-object-list?";
    private final String ACCESS_CONTROL_HIERARCHICAL = "/access-control/hierarchical?";
    private final String ACCESS_CONTROL_NON_HIERARCHICAL = "/access-control/non-hierarchical?";
    private final String ACCESS_CONTROL = "/access-control";

    private final RestClient authzServer;

    public AuthorizationClient(RestClient authzServer) {
        this.authzServer = authzServer;
    }

    /**
     * Send Request to Authorization Server: Does the user have the permission in at least one group?
     **/
    public boolean hasPermissionInAnyGroup(String email, String permission) {

        StringBuilder uri = new StringBuilder(AUTH_URL).append(PERMISSION_IN_ANY_GROUP)
                .append("email=").append(email)
                .append("&permission=").append(permission);

        return authzServer.get()
                .uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() != HttpStatus.OK.value(), (request, response) -> {
                    throw new AccessDeniedException("Request to AuthorizationServer(permission-in-any-group) failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(new ParameterizedTypeReference<Boolean>() {
                });
    }

    /**
     * Send Request to Authorization Server: Does the user have the permission in this group?
     **/
    public boolean hasPermissionInAGroup(String email, String permission, Long accountGroupId) {

        StringBuilder uri = new StringBuilder(AUTH_URL).append(PERMISSION_IN_A_GROUP)
                .append("email=").append(email)
                .append("&permission=").append(permission)
                .append("&accountGroupId=").append(accountGroupId);

        return authzServer.get()
                .uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() != HttpStatus.OK.value(), (request, response) -> {
                    throw new AccessDeniedException("Request to AuthorizationServer(permission-in-group) failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(new ParameterizedTypeReference<Boolean>() {
                });
    }

    public boolean hasPermissionInObject(String email, String permission, String clazz, Long clazzId) {

        StringBuilder uri = new StringBuilder(AUTH_URL).append(PERMISSION_IN_OBJECT)
                .append("email=").append(email)
                .append("&permission=").append(permission)
                .append("&clazz=").append(clazz)
                .append("&clazzId=").append(clazzId);

        return authzServer.get()
                .uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() != HttpStatus.OK.value(), (request, response) -> {
                    throw new AccessDeniedException("Request to AuthorizationServer(permission-in-object) failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(new ParameterizedTypeReference<Boolean>() {
                });
    }

    public boolean hasPermissionInObjectList(String email, String permission, String clazz, List<Long> clazzIds) {
        StringBuilder uri = new StringBuilder(AUTH_URL).append(PERMISSION_IN_OBJECT_LIST)
                .append("email=").append(email)
                .append("&permission=").append(permission)
                .append("&clazz=").append(clazz)
                .append("&clazzIds=").append(clazzIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

        return authzServer.get()
                .uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() != HttpStatus.OK.value(), (request, response) -> {
                    throw new AccessDeniedException("Request to AuthorizationServer(permission-in-object-list) failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(new ParameterizedTypeReference<Boolean>() {
                });
    }

    public List<AccessControlDto> listAccessControl(String email, String clazz, Long clazzId, Long accountGroupId, String permission) {
        StringBuilder uri = new StringBuilder(AUTH_URL).append(ACCESS_CONTROL_HIERARCHICAL)
                .append("email=").append(email)
                .append("&clazz=").append(clazz);

        if (clazzId != null) uri.append("&clazzId=").append(clazzId);
        if (accountGroupId != null) uri.append("&accountGroupId=").append(accountGroupId);
        if (permission != null && !permission.isEmpty()) uri.append("&permission=").append(permission);

        return authzServer.get()
                .uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() != HttpStatus.OK.value(), (request, response) -> {
                    throw new AccessDeniedException("Request to AuthorizationServer(/access-control/hierarchical) failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(new ParameterizedTypeReference<List<AccessControlDto>>() {
                });
    }

    public List<AccessControlDto> listAccessControlStrict(String email, String clazz, Long accountGroupId) {
        StringBuilder uri = new StringBuilder(AUTH_URL).append(ACCESS_CONTROL_NON_HIERARCHICAL)
                .append("email=").append(email)
                .append("&clazz=").append(clazz)
                .append("&accountGroupId=").append(accountGroupId);

        return authzServer.get()
                .uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status ->
                        status.value() != HttpStatus.OK.value(), (request, response) -> {
                    throw new AccessDeniedException("Request to AuthorizationServer(/access-control/non-hierarchical) failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .body(new ParameterizedTypeReference<List<AccessControlDto>>() {
                });
    }

    public void saveAccessControl(String clazz, Long clazzId, Long accountGroupId) {

        StringBuilder uri = new StringBuilder(AUTH_URL).append(ACCESS_CONTROL);
        var body = AccessControlDto.builder()
                .clazz(clazz)
                .clazzId(clazzId)
                .accountGroup(AccountGroupDto.builder().id(accountGroupId).build())
                .build();

        authzServer.post()
                .uri(uri.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .onStatus(status ->
                        status.value() != HttpStatus.NO_CONTENT.value(), (request, response) -> {
                    throw new AccessDeniedException("Request to save AccessControl from AuthorizationServer failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .toBodilessEntity();
    }

    public void deleteAccessControl(String clazz, Long clazzId) {
        StringBuilder uri = new StringBuilder(AUTH_URL).append(ACCESS_CONTROL)
                .append("?clazz=").append(clazz)
                .append("&clazzId=").append(clazzId);

        authzServer.delete()
                .uri(uri.toString())
                .retrieve()
                .onStatus(status ->
                        status.value() != HttpStatus.NO_CONTENT.value(), (request, response) -> {
                    throw new AccessDeniedException("Request to delete AccessControl from AuthorizationServer failed: [" + response.getStatusCode() + " : " + response.getStatusText() + "]");
                })
                .toBodilessEntity();
    }
}
