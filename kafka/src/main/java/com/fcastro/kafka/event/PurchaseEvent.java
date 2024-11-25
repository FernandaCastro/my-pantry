package com.fcastro.kafka.event;

import com.fcastro.kafka.model.PurchaseEventDto;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseEvent implements Serializable {

    private String key; //pantry:<pantry_id>-product:<product_id>
    private PurchaseEventDto data;
    private ZonedDateTime createdAt;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PurchaseEvent{");
        sb.append(" key=").append(key);
        sb.append(" data=").append(data);
        sb.append(" createdAt=").append(createdAt.toString());
        sb.append(" }");
        return sb.toString();
    }
}
