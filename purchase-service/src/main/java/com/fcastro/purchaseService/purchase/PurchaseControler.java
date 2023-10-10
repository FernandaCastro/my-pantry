package com.fcastro.purchaseService.purchase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseControler {

    private final PurchaseService service;

    public PurchaseControler(PurchaseService service) {
        this.service = service;
    }

    @PostMapping("/purchase-create")
    public ResponseEntity<PurchaseDto> createPurchase() {
        return ResponseEntity.ok(service.getOrCreatePurchaseOrder());
    }

    @PostMapping("/purchase-close")
    public ResponseEntity<PurchaseDto> closePurchase(@RequestBody PurchaseDto dto) {
        return ResponseEntity.ok(service.closePurchaseOrder(dto));
    }
}
