package com.fcastro.kafka.event;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductEvent implements Serializable {
    private ProductEventDto data;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ProductEvent{");
        sb.append("data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
