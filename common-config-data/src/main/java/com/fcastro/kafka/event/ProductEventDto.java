package com.fcastro.kafka.event;

import com.fcastro.model.Action;
import com.fcastro.model.ProductDto;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductEventDto implements Serializable {
    Action action;
    ProductDto product;
}
