package com.fcastro.events;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCreateEvent implements Serializable {

    private ItemDto item;
}
