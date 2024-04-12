package com.fcastro.purchaseservice.purchase;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("purchases")
public class PurchaseController {

    private final PurchaseService service;

    public PurchaseController(PurchaseService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasPermissionInObjectList('Pantry', #pantryIds, 'purchase_pantry')")
    public ResponseEntity<List<PurchaseDto>> listPurchase(@P("pantryIds") @RequestParam Set<Long> pantryIds) {
        return ResponseEntity.ok(service.listPurchaseOrder(SecurityContextHolder.getContext().getAuthentication().getName(), pantryIds));
    }

    @Deprecated
    @GetMapping("/open")
    @PreAuthorize("hasPermissionInObjectList('Pantry', #pantryIds, 'purchase_pantry')")
    public ResponseEntity<PurchaseDto> getOpenPurchaseOrder(@P("pantryIds") @RequestParam Set<Long> pantryIds) {
        return ResponseEntity.ok(service.getOpenPurchaseOrder(SecurityContextHolder.getContext().getAuthentication().getName(), pantryIds));
    }

    @PostMapping("/new")
    @PreAuthorize("hasPermissionInObjectList('Pantry', #pantryIds, 'purchase_pantry')")
    public ResponseEntity<PurchaseDto> createPurchaseOrder(@P("pantryIds") @RequestBody Set<Long> pantryIds) {
        return ResponseEntity.ok(service.createPurchaseOrder(SecurityContextHolder.getContext().getAuthentication().getName(), pantryIds));
    }

    @PostMapping("/close")
    @PreAuthorize("hasPermissionInAnyGroup('purchase_pantry')")
    public ResponseEntity<PurchaseDto> closePurchaseOrder(@RequestBody PurchaseDto dto) {
        return ResponseEntity.ok(service.closePurchaseOrder(SecurityContextHolder.getContext().getAuthentication().getName(), dto));
    }
}
