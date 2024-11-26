package com.fcastro.kafka.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEventDto {

    Action action;
    private String email;
    private List<Long> pantryIds;
    private List<Long> productIds;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AccountEventDto{");
        sb.append("action=").append(action);
        sb.append(", email='").append(email).append('\'');
        sb.append(", pantryIds=").append(pantryIds);
        sb.append('}');
        return sb.toString();
    }
}
