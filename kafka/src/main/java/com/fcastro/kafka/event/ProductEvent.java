package com.fcastro.kafka.event;

import com.fcastro.kafka.model.ProductEventDto;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductEvent implements Serializable {

    private String key; //product:<product_id>
    private ProductEventDto data;
    private ZonedDateTime createdAt;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ProductEvent{");
        sb.append(" key=").append(key);
        sb.append(" data=").append(data);
        sb.append(" createdAt=").append(createdAt.toString());
        sb.append(" }");
        return sb.toString();
    }
}
