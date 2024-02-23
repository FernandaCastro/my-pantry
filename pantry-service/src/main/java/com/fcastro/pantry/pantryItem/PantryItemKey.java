package com.fcastro.pantry.pantryItem;

import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PantryItemKey implements Serializable {

    @Column(name = "pantry_id")
    private long pantryId;

    @Column(name = "product_id")
    private long productId;

}
