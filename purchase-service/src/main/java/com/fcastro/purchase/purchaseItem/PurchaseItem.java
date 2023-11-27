package com.fcastro.purchase.purchaseItem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fcastro.purchase.purchase.Purchase;
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
    private Long id;

    private Long pantryId;
    private String pantryName;

    private Long productId;
    private String productCode;
    private String productDescription;
    private String productSize;

    private int qtyProvisioned;
    private int qtyPurchased;

    @JsonIgnoreProperties("items")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;
}
