package com.fcastro.purchaseservice.purchaseItem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fcastro.purchaseservice.product.Product;
import com.fcastro.purchaseservice.purchase.Purchase;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int qtyProvisioned;
    private int qtyPurchased;

    @JsonIgnoreProperties("items")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = true)
    private Purchase purchase;
}
