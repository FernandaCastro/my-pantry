package com.fcastro.events;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCompleteEvent implements Serializable {

    private List<ItemDto> items;
}
