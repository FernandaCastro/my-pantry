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
}
