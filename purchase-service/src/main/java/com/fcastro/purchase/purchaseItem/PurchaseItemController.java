package com.fcastro.purchase.purchaseItem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("purchases")
public class PurchaseItemController {

    private final PurchaseItemService service;

    public PurchaseItemController(PurchaseItemService service) {
        this.service = service;
    }

    @GetMapping("/items")
    public ResponseEntity<List<PurchaseItemDto>> listPendingPurchase(@RequestParam(required = false) String supermarket) {
        return ResponseEntity.ok(service.listPendingPurchaseByCategory(supermarket));
    }

}
