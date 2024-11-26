package com.fcastro.kafka.event;

import com.fcastro.kafka.model.AccountEventDto;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountEvent implements Serializable {

    private String key; //account:<email>
    private AccountEventDto data;
    private ZonedDateTime createdAt;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AccountEvent{");
        sb.append(" key=").append(key);
        sb.append(" data=").append(data);
        sb.append(" createdAt=").append(createdAt.toString());
        sb.append(" }");
        return sb.toString();
    }
}
