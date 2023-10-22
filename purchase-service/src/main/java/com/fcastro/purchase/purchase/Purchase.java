package com.fcastro.purchase.purchase;

import com.fcastro.purchase.purchaseItem.PurchaseItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PURCHASE")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    @OneToMany(mappedBy = "purchase")
    List<PurchaseItem> items;
}