package com.fcastro.kafka.event;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCompleteEvent implements Serializable {

    private List<PurchaseEventDto> items;
}
