package com.fcastro.purchase.purchase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PurchaseControler {

    private final PurchaseService service;

    public PurchaseControler(PurchaseService service) {
        this.service = service;
    }

    @GetMapping("/purchases")
    public ResponseEntity<List<PurchaseDto>> listPurchase() {
        return ResponseEntity.ok(service.listPurchaseOrder());
    }

    @PostMapping("/purchase-create")
    public ResponseEntity<PurchaseDto> createPurchaseOrder() {
        return ResponseEntity.ok(service.getOrCreatePurchaseOrder());
    }

    @PostMapping("/purchase-close")
    public ResponseEntity<PurchaseDto> closePurchaseOrder(@RequestBody PurchaseDto dto) {
        return ResponseEntity.ok(service.closePurchaseOrder(dto));
    }
}
