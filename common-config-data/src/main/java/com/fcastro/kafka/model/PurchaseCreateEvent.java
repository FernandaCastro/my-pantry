package com.fcastro.kafka.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCreateEvent implements Serializable {

    private PurchaseEventItemDto item;
}
