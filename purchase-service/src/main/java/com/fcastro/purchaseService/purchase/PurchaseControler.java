package com.fcastro.purchaseService.purchase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseControler {

    private final PurchaseService service;

    public PurchaseControler(PurchaseService service) {
        this.service = service;
    }

    @PostMapping("/purchase")
    public ResponseEntity<PurchaseDto> createPurchase() {
        return ResponseEntity.ok(service.createPurchaseOrder());
    }
}
