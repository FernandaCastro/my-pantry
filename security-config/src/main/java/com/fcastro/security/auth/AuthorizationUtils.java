package com.fcastro.security.auth;

import com.fcastro.security.exception.TokenVerifierException;
import com.fcastro.security.model.AccountGroupMemberDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Deprecated
@Component
public class AuthorizationUtils {

    //private static final String REGEX_ROLE = "ROLE_(?<groupId>\\d*)_(?<role>[A-Z]*)";
    private static final String REGEX_ROLE = "ROLE_(?<groupId>\\d*)_(?<role>\\d*)";
    private static final Pattern PATTERN_ROLE = Pattern.compile(REGEX_ROLE);

    public String getUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Map<Long, String> getGroupRolesFromContext() {

        var groupRoles = new HashMap<Long, String>();

        SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .forEach(authority -> {
                    extractGroupIdAndRole(groupRoles, authority);
                });

        return groupRoles;
    }

    private void extractGroupIdAndRole(HashMap<Long, String> groupRoles, GrantedAuthority a) {
        var matcher = PATTERN_ROLE.matcher(a.getAuthority());
        if (matcher.matches()) {
            var groupId = matcher.group("groupId");
            var role = matcher.group("role");
            groupRoles.put(Long.valueOf(groupId), role);
        } else {
            new TokenVerifierException("Unable to read permissions");
        }
    }

    public String translateGroupRoles(List<AccountGroupMemberDto> groupMemberList) {
        var roles = new StringBuilder();
        groupMemberList.forEach(m -> {
            roles.append("ROLE_" + m.getAccountGroupId() + "_" + m.getRole().getId()).append(",");
        });

        return roles.toString();
    }

    public String getRole(GrantedAuthority authority) {
        var matcher = PATTERN_ROLE.matcher(authority.getAuthority());
        if (matcher.matches()) {
            return matcher.group("role");
        }
        return "";
    }
}
