package com.fcastro.purchaseService.purchase;

import com.fcastro.purchaseService.purchaseItem.PurchaseItemDto;
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

    List<PurchaseItemDto> items;
}
