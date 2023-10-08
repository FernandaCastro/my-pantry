package com.fcastro.events;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseEvent implements Serializable {

    private PurchaseEventDto item;
}
