package com.fcastro.kafka.event;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCreateEvent implements Serializable {

    private PurchaseEventDto item;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PurchaseCreateEvent{");
        sb.append("item=").append(item);
        sb.append('}');
        return sb.toString();
    }
}
