package com.fcastro.accountservice.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCacheDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 2221528169840173134L;

    private Long accountGroupId;
    private String accountGroupName;
    private Long parentAccountGroupId;
    private String roleId;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberCacheDto that)) return false;

        return getAccountGroupId().equals(that.getAccountGroupId()) && getRoleId().equals(that.getRoleId());
    }

    @Override
    public int hashCode() {
        int result = getAccountGroupId().hashCode();
        result = 31 * result + getRoleId().hashCode();
        return result;
    }
}
