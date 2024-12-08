package com.fcastro.kafka.model;

import lombok.*;

import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandEventDto {

    private String command;
    private Map<String, Object> params;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CommandEventDto{");
        sb.append("command='").append(command).append('\'');
        sb.append(", params=").append(params);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandEventDto that)) return false;

        return getCommand().equals(that.getCommand()) && getParams().equals(that.getParams());
    }

    @Override
    public int hashCode() {
        int result = getCommand().hashCode();
        result = 31 * result + getParams().hashCode();
        return result;
    }
}
