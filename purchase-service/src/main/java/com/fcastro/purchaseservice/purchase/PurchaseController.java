package com.fcastro.purchaseservice.purchase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("purchases")
public class PurchaseController {

    private final PurchaseService service;

    public PurchaseController(PurchaseService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<List<PurchaseDto>> listPurchase() {
        return ResponseEntity.ok(service.listPurchaseOrder());
    }

    @GetMapping("/open")
    public ResponseEntity<PurchaseDto> getOpenPurchaseOrder() {
        return ResponseEntity.ok(service.getOpenPurchaseOrder());
    }

    @PostMapping("/new")
    public ResponseEntity<PurchaseDto> createPurchaseOrder() {
        return ResponseEntity.ok(service.createPurchaseOrder());
    }

    @PostMapping("/close")
    public ResponseEntity<PurchaseDto> closePurchaseOrder(@RequestBody PurchaseDto dto) {
        return ResponseEntity.ok(service.closePurchaseOrder(dto));
    }
}
