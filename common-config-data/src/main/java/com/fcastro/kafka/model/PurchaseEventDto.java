package com.fcastro.kafka.model;

import com.fcastro.app.model.Action;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseEventDto implements Serializable {
    Action action;
    long pantryId;
    String pantryName;
    long productId;
    int qtyProvisioned;
    int qtyPurchased;

    @Override
    public String toString() {
        return "PurchaseEventDto{" +
                "action=" + action.toString() +
                ", pantryId=" + pantryId +
                ", pantryName='" + pantryName + '\'' +
                ", productId=" + productId +
                ", qtyProvisioned=" + qtyProvisioned +
                ", qtyPurchased=" + qtyPurchased +
                '}';
    }
}
