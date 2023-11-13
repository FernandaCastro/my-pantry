package com.fcastro.purchase.purchaseItem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("purchases")
public class PurchaseItemController {

    private final PurchaseItemService service;

    public PurchaseItemController(PurchaseItemService service) {
        this.service = service;
    }

    @GetMapping("/items")
    public ResponseEntity<List<PurchaseItemDto>> listPendingPurchase() {
        return ResponseEntity.ok(service.listPendingPurchase());
    }
}
