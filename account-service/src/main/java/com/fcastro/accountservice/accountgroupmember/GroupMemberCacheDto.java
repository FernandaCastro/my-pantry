package com.fcastro.accountservice.accountgroupmember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberCacheDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long accountGroupId;
    private Long parentAccountGroupId;
    private String roleId;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupMemberCacheDto that)) return false;

        return getAccountGroupId().equals(that.getAccountGroupId()) && getRoleId().equals(that.getRoleId());
    }

    @Override
    public int hashCode() {
        int result = getAccountGroupId().hashCode();
        result = 31 * result + getRoleId().hashCode();
        return result;
    }
}
