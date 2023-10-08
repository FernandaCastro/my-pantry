package com.fcastro.events;

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
    String productDescription;
    String productSize;

    int quantity;
    boolean isPurchased;

    @Override
    public String toString() {
        return "PurchaseEventDto{" +
                "pantryId=" + pantryId +
                ", pantryName='" + pantryName + '\'' +
                ", productId=" + productId +
                ", productDescription='" + productDescription + '\'' +
                ", productSize='" + productSize + '\'' +
                ", quantity=" + quantity +
                ", isPurchased=" + isPurchased +
                '}';
    }
}
