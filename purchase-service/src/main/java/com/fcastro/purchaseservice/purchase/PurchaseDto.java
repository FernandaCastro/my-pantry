package com.fcastro.purchaseservice.purchase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fcastro.purchaseservice.purchaseItem.PurchaseItemDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDto {

    Long id;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    @JsonIgnoreProperties("purchase")
    List<PurchaseItemDto> items;
}
