package com.fcastro.purchase.purchaseItem;

import com.fcastro.kafka.event.PurchaseEventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
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
        var dto = PurchaseEventDto.builder()
                .pantryId(1L)
                .pantryName("Product1")
                .productId(1L)
                .qtyProvisioned(1)
                .build();

        when(repository.save(captor.capture())).thenReturn(PurchaseItem.builder().build());

        //when
        service.processPurchaseEvent(dto);

        //then
        var item = captor.getValue();
        assertThat(item.getPantryId()).isEqualTo(dto.getPantryId());
        assertThat(item.getPantryName()).isEqualTo(dto.getPantryName());
        assertThat(item.getProduct().getId()).isEqualTo(dto.getProductId());
        assertThat(item.getQtyProvisioned()).isEqualTo(dto.getQtyProvisioned());
        assertThat(item.getQtyPurchased()).isEqualTo(0);
        assertThat(item.getPurchase()).isNull();

    }

//    @Test
//    public void whenListPendingPurchaseByCategory_thenCategorizeOk(){
//        var beverageCategory = "beverage";
//        var cleaningCategory = "cleaning";
//        var groceryCategory = "grocery";
//        var listPendingItems = List.of(
//                PurchaseItem.builder().build()
//        )
//        when(repository.listPendingPurchase()).thenReturn(listPendingItems);
//    }


}
