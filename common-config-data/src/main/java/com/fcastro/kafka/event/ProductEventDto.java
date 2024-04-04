package com.fcastro.kafka.event;

import com.fcastro.app.model.Action;
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
    private long id;
    private String code;
    private String description;
    private String size;
    private String category;
    private Long accountGroupId;
}
