package com.fcastro.purchase.purchase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("purchases")
public class PurchaseControler {

    private final PurchaseService service;

    public PurchaseControler(PurchaseService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<List<PurchaseDto>> listPurchase() {
        return ResponseEntity.ok(service.listPurchaseOrder());
    }

    @PostMapping("/open")
    public ResponseEntity<PurchaseDto> createPurchaseOrder() {
        return ResponseEntity.ok(service.getOrCreatePurchaseOrder());
    }

    @PostMapping("/close")
    public ResponseEntity<PurchaseDto> closePurchaseOrder(@RequestBody PurchaseDto dto) {
        return ResponseEntity.ok(service.closePurchaseOrder(dto));
    }
}
