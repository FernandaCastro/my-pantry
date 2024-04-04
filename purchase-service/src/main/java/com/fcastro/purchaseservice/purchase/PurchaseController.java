package com.fcastro.purchaseservice.purchase;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("purchases")
public class PurchaseController {

    private final PurchaseService service;

    public PurchaseController(PurchaseService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasPermissionInAnyGroup('purchase_pantry')")
    public ResponseEntity<List<PurchaseDto>> listPurchase() {
        return ResponseEntity.ok(service.listPurchaseOrder(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @GetMapping("/open")
    @PreAuthorize("hasPermissionInAnyGroup('purchase_pantry')")
    public ResponseEntity<PurchaseDto> getOpenPurchaseOrder() {
        return ResponseEntity.ok(service.getOpenPurchaseOrder(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @PostMapping("/new")
    @PreAuthorize("hasPermissionInAnyGroup('purchase_pantry')")
    public ResponseEntity<PurchaseDto> createPurchaseOrder() {
        return ResponseEntity.ok(service.createPurchaseOrder(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @PostMapping("/close")
    @PreAuthorize("hasPermissionInAnyGroup('purchase_pantry')")
    public ResponseEntity<PurchaseDto> closePurchaseOrder(@RequestBody PurchaseDto dto) {
        return ResponseEntity.ok(service.closePurchaseOrder(SecurityContextHolder.getContext().getAuthentication().getName(), dto));
    }
}
