package com.fcastro.purchase.purchase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Entity(name = "purchase")
@Table(name = "PURCHASE")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    @JsonIgnoreProperties("purchase")
    @OneToMany(mappedBy = "purchase", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<PurchaseItem> items;
}
