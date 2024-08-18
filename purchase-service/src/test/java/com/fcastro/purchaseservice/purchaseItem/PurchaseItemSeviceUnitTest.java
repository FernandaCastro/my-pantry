package com.fcastro.purchaseservice.purchaseItem;

import com.fcastro.app.model.Action;
import com.fcastro.kafka.model.PurchaseEventDto;
import com.fcastro.purchaseservice.product.ProductDto;
import com.fcastro.purchaseservice.product.ProductService;
import com.fcastro.purchaseservice.purchase.Purchase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseItemSeviceUnitTest {

    @InjectMocks
    PurchaseItemService service;

    @Mock
    ProductService productService;

    @Mock
    PurchaseItemRepository repository;

    @Spy
    ModelMapper modelMapper;

    @Captor
    ArgumentCaptor<PurchaseItem> captorPurchaseItem;

    @Test
    public void givenProvisioningNotExistsAndCreateAction_whenProcessPurchaseCreateEvent_thenCreateNewProvisioning() {
        //given
        when(productService.get(anyLong())).thenReturn(Optional.ofNullable(ProductDto.builder().build()));
        when(repository.findByPantryIdAndProductIdAndOpenPurchaseOrder(anyLong(), anyLong())).thenReturn(null);
        when(repository.findByPantryIdAndProductIdAndNoPurchaseOrder(anyLong(), anyLong())).thenReturn(null);
        when(repository.save(captorPurchaseItem.capture())).thenReturn(null);

        //when
        var dto = PurchaseEventDto.builder()
                .action(Action.CREATE)
                .qtyProvisioned(1)
                .build();
        service.processPurchaseEvent(dto);

        //then
        var item = captorPurchaseItem.getValue();
        assertThat(item.getQtyProvisioned()).isEqualTo(1);
        assertThat(item.getPurchase()).isNull();
    }

    @Test
    public void givenProvisioningInNoOrderAndCreateAction_whenProcessPurchaseCreateEvent_thenIncreaseProvisioningQty() {
        //given
        var givenPurchaseItem = PurchaseItem.builder()
                .qtyProvisioned(2)
                .purchase(null)
                .build();


        when(productService.get(anyLong())).thenReturn(Optional.ofNullable(ProductDto.builder().build()));
        when(repository.findByPantryIdAndProductIdAndOpenPurchaseOrder(anyLong(), anyLong())).thenReturn(null);
        when(repository.findByPantryIdAndProductIdAndNoPurchaseOrder(anyLong(), anyLong())).thenReturn(givenPurchaseItem);
        when(repository.save(captorPurchaseItem.capture())).thenReturn(null);

        //when
        var dto = PurchaseEventDto.builder()
                .action(Action.CREATE)
                .qtyProvisioned(1)
                .build();
        service.processPurchaseEvent(dto);

        //then
        var item = captorPurchaseItem.getValue();
        assertThat(item.getQtyProvisioned()).isEqualTo(3);
        assertThat(item.getPurchase()).isNull();
    }

    @Test
    public void givenProvisioningInOpenOrderAndCreateAction_whenProcessPurchaseCreateEvent_thenIncreaseProvisioningQty() {
        //given
        var purchaseOrder = Purchase.builder().id(1L).build();
        var givenPurchaseItem = PurchaseItem.builder()
                .qtyProvisioned(2)
                .purchase(purchaseOrder)
                .build();

        when(productService.get(anyLong())).thenReturn(Optional.ofNullable(ProductDto.builder().build()));
        when(repository.findByPantryIdAndProductIdAndOpenPurchaseOrder(anyLong(), anyLong())).thenReturn(givenPurchaseItem);
        when(repository.save(captorPurchaseItem.capture())).thenReturn(null);

        //when
        var dto = PurchaseEventDto.builder()
                .action(Action.CREATE)
                .qtyProvisioned(1)
                .build();
        service.processPurchaseEvent(dto);

        //then
        var item = captorPurchaseItem.getValue();
        assertThat(item.getQtyProvisioned()).isEqualTo(3);
        assertThat(item.getPurchase().getId()).isEqualTo(1L);
    }

    @Test
    public void givenProvisioningQtyInNoOrderAndDeleteAction_whenProcessPurchaseCreateEvent_thenDecreaseProvisioningQty() {
        //given
        var givenPurchaseItem = PurchaseItem.builder()
                .qtyProvisioned(2)
                .purchase(null)
                .build();

        when(repository.findByPantryIdAndProductIdAndOpenPurchaseOrder(anyLong(), anyLong())).thenReturn(null);
        when(repository.findByPantryIdAndProductIdAndNoPurchaseOrder(anyLong(), anyLong())).thenReturn(givenPurchaseItem);
        when(repository.save(captorPurchaseItem.capture())).thenReturn(null);

        //when
        var dto = PurchaseEventDto.builder()
                .action(Action.DELETE)
                .qtyProvisioned(1)
                .build();
        service.processPurchaseEvent(dto);

        //then
        var item = captorPurchaseItem.getValue();
        assertThat(item.getQtyProvisioned()).isEqualTo(1);
        assertThat(item.getPurchase()).isNull();
    }

    @Test
    public void givenSameProvisioningQtyInNoOrderAndDeleteAction_whenProcessPurchaseCreateEvent_thenDeleteEntireProvisioning() {
        //given
        var givenPurchaseItem = PurchaseItem.builder()
                .id(1L)
                .qtyProvisioned(2)
                .purchase(null)
                .build();

        when(repository.findByPantryIdAndProductIdAndOpenPurchaseOrder(anyLong(), anyLong())).thenReturn(null);
        when(repository.findByPantryIdAndProductIdAndNoPurchaseOrder(anyLong(), anyLong())).thenReturn(givenPurchaseItem);
        doNothing().when(repository).delete(captorPurchaseItem.capture());

        //when
        var dto = PurchaseEventDto.builder()
                .action(Action.DELETE)
                .qtyProvisioned(2)
                .build();
        service.processPurchaseEvent(dto);

        //then
        var item = captorPurchaseItem.getValue();
        assertThat(item.getId()).isEqualTo(1L);
        verify(repository, times(0)).save(any(PurchaseItem.class));
        verify(repository, times(1)).delete(any(PurchaseItem.class));
    }

    @Test
    public void givenProvisioningInOpenOrderAndDeleteAction_whenProcessPurchaseCreateEvent_thenDecreaseProvisioningQty() {
        //given
        var purchaseOrder = Purchase.builder().id(1L).build();
        var givenPurchaseItem = PurchaseItem.builder()
                .qtyProvisioned(2)
                .purchase(purchaseOrder)
                .build();

        when(productService.get(anyLong())).thenReturn(Optional.ofNullable(ProductDto.builder().build()));
        when(repository.findByPantryIdAndProductIdAndOpenPurchaseOrder(anyLong(), anyLong())).thenReturn(givenPurchaseItem);
        when(repository.save(captorPurchaseItem.capture())).thenReturn(null);

        //when
        var dto = PurchaseEventDto.builder()
                .action(Action.CREATE)
                .qtyProvisioned(1)
                .build();
        service.processPurchaseEvent(dto);

        //then
        var item = captorPurchaseItem.getValue();
        assertThat(item.getQtyProvisioned()).isEqualTo(3);
        assertThat(item.getPurchase().getId()).isEqualTo(1L);
    }


}
