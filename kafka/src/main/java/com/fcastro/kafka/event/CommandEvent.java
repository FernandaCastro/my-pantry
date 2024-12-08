package com.fcastro.kafka.event;

import com.fcastro.kafka.model.CommandEventDto;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommandEvent implements Serializable {

    private String key; //commandName
    private CommandEventDto data;
    private ZonedDateTime createdAt;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CommandEvent{");
        sb.append(" key=").append(key);
        sb.append(" data=").append(data);
        sb.append(" createdAt=").append(createdAt.toString());
        sb.append(" }");
        return sb.toString();
    }
}
