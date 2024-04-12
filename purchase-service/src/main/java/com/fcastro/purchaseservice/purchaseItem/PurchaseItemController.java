package com.fcastro.purchaseservice.purchaseItem;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<List<PurchaseItemDto>> listPendingPurchase(@RequestParam Set<Long> pantryIds, @RequestParam(required = false) String supermarket) {
        return ResponseEntity.ok(service.listPendingPurchaseByCategory(SecurityContextHolder.getContext().getAuthentication().getName(), pantryIds, supermarket));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<PurchaseItemDto>> listPurchase(@PathVariable Long id, @RequestParam(required = false) String supermarket) {
        return ResponseEntity.ok(service.listPurchaseByCategory(id, supermarket));
    }

}
