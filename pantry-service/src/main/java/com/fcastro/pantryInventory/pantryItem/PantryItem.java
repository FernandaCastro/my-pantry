package com.fcastro.pantryInventory.pantryItem;

import com.fcastro.pantryInventory.pantry.Pantry;
import com.fcastro.pantryInventory.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "pantryItem")
@Table(name = "PANTRY_ITEM")
@IdClass(PantryItemKey.class)
public class PantryItem {

    @Id
    private Long pantryId;

    @Id
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pantryId")
    @JoinColumn(name = "pantry_id")
    private Pantry pantry;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    private int idealQty;
    private int currentQty;
    private int provisionedQty;
    private LocalDateTime lastProvisioning;
}

