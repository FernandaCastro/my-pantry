package com.fcastro.purchaseService.purchaseItem;

import com.fcastro.events.ItemDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PurchaseItemSeviceUnitTest {

    @InjectMocks
    PurchaseItemService service;

    @Mock
    PurchaseItemRepository repository;

    @Spy
    ModelMapper modelMapper;

    @Captor
    ArgumentCaptor<PurchaseItem> captor;

    @Test
    public void givenItemDto_whenProcessPurchaseCreateEvent_thenSuccess() {
        //given
        var dto = ItemDto.builder()
                .pantryId(1L)
                .productId(1L)
                .pantryName("Product1")
                .productSize("Size1")
                .qtyProvisioned(1)
                .build();

        when(repository.save(captor.capture())).thenReturn(any(PurchaseItem.class));

        //when
        service.processPurchaseEvent(dto);

        //then
        var item = captor.getValue();
        assertThat(item.getPantryId()).isEqualTo(dto.getPantryId());
        assertThat(item.getPantryName()).isEqualTo(dto.getPantryName());
        assertThat(item.getProductId()).isEqualTo(dto.getProductId());
        assertThat(item.getProductDescription()).isEqualTo(dto.getProductDescription());
        assertThat(item.getProductSize()).isEqualTo(dto.getProductSize());
        assertThat(item.getQtyProvisioned()).isEqualTo(dto.getQtyProvisioned());
        assertThat(item.getQtyPurchased()).isEqualTo(0);
        assertThat(item.getPurchase()).isNull();

    }


}
