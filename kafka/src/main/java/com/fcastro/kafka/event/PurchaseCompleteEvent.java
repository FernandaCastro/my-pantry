package com.fcastro.kafka.event;

import com.fcastro.kafka.model.PurchaseEventDto;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCompleteEvent implements Serializable {

    private String key;  //purchase:<purchase_id>
    private List<PurchaseEventDto> data;
    private ZonedDateTime createdAt;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PurchaseCompleteEvent{");
        sb.append(" key=").append(key);
        sb.append(" data=").append(data);
        sb.append(" createdAt=").append(createdAt.toString());
        sb.append(" }");
        return sb.toString();
    }
}
