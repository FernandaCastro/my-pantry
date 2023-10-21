package com.fcastro.kafka.model;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemDto implements Serializable {
    long pantryId;
    String pantryName;

    long productId;
    String productDescription;
    String productSize;

    int qtyProvisioned;
    int qtyPurchased;

    @Override
    public String toString() {
        return "ItemDto{" +
                "pantryId=" + pantryId +
                ", pantryName='" + pantryName + '\'' +
                ", productId=" + productId +
                ", productDescription='" + productDescription + '\'' +
                ", productSize='" + productSize + '\'' +
                ", qtyProvisioned=" + qtyProvisioned +
                ", qtyPurchased=" + qtyPurchased +
                '}';
    }
}
