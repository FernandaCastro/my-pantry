package com.fcastro.purchase.purchaseItem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<PurchaseItemDto>> listPendingPurchase(@RequestParam(required = false) String supermarket) {
        return ResponseEntity.ok(service.listPendingPurchaseByCategory(supermarket));
    }

}
