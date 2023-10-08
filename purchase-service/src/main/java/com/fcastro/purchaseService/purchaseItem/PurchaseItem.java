package com.fcastro.purchaseService.purchaseItem;

import com.fcastro.purchaseService.purchase.Purchase;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "purchaseItem")
@Table(name = "PURCHASE_ITEM")
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long pantryId;
    String pantryName;

    Long productId;
    String productDescription;
    String productSize;

    int quantity;
    boolean isPurchased;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    Purchase purchase;
}
