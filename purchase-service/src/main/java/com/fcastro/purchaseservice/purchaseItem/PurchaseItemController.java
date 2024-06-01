package com.fcastro.purchaseservice.purchaseItem;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("purchases")
public class PurchaseItemController {

    private final PurchaseItemService service;

    public PurchaseItemController(PurchaseItemService service) {
        this.service = service;
    }

    @GetMapping("/items")
    @PreAuthorize("hasPermissionInObjectList('Pantry', #pantryIds, 'purchase_pantry')")
    public ResponseEntity<List<PurchaseItemDto>> listPendingPurchase(@P("pantryIds") @RequestParam Set<Long> pantryIds, @RequestParam(required = false) Long supermarketId) {
        return ResponseEntity.ok(service.listPendingPurchaseByCategory(SecurityContextHolder.getContext().getAuthentication().getName(), pantryIds, supermarketId));
    }

    @GetMapping("/{id}/items")
    @PreAuthorize("hasPermissionInObjectList('Pantry', #pantryIds, 'purchase_pantry')")
    public ResponseEntity<List<PurchaseItemDto>> listPurchase(@PathVariable Long id, @P("pantryIds") @RequestParam Set<Long> pantryIds, @RequestParam(required = false) Long supermarketId) {
        return ResponseEntity.ok(service.listPurchaseByCategory(id, supermarketId));
    }

}
