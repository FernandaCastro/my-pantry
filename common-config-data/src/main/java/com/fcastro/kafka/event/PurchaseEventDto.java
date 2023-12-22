package com.fcastro.kafka.event;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseEventDto implements Serializable {
    long pantryId;
    String pantryName;
    long productId;
    int qtyProvisioned;
    int qtyPurchased;

    @Override
    public String toString() {
        return "PurchaseEventDto{" +
                "pantryId=" + pantryId +
                ", pantryName='" + pantryName + '\'' +
                ", productId=" + productId +
                ", qtyProvisioned=" + qtyProvisioned +
                ", qtyPurchased=" + qtyPurchased +
                '}';
    }
}
