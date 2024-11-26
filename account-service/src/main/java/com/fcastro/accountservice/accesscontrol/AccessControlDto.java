package com.fcastro.accountservice.accesscontrol;

import com.fcastro.accountservice.accountgroup.AccountGroupDto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessControlDto {

    @NotBlank(message = "Class is mandatory")
    private String clazz;

    @NotBlank(message = "Class Id is mandatory")
    private Long clazzId;

    @NotBlank(message = "Account Group is mandatory")
    private AccountGroupDto accountGroup;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessControlDto that)) return false;

        return getClazz().equals(that.getClazz()) && getClazzId().equals(that.getClazzId()) && getAccountGroup().equals(that.getAccountGroup());
    }

    @Override
    public int hashCode() {
        int result = getClazz().hashCode();
        result = 31 * result + getClazzId().hashCode();
        result = 31 * result + getAccountGroup().hashCode();
        return result;
    }
}
