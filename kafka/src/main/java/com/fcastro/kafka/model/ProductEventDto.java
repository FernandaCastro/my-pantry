package com.fcastro.kafka.model;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductEventDto implements Serializable {
    Action action;
    private long id;
    private String code;
    private String description;
    private String size;
    private String category;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ProductEventDto{");
        sb.append("action=").append(action);
        sb.append(", id=").append(id);
        sb.append(", code='").append(code).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", size='").append(size).append('\'');
        sb.append(", category='").append(category).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
