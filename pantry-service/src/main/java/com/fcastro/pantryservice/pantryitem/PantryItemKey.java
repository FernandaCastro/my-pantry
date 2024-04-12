package com.fcastro.pantryservice.pantryitem;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PantryItemKey implements Serializable {

    @Column(name = "pantry_id")
    private Long pantryId;

    @Column(name = "product_id")
    private Long productId;

}
