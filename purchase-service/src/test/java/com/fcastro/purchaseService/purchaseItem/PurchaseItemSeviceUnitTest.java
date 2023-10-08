package com.fcastro.purchaseService.purchaseItem;

import com.fcastro.events.PurchaseEventDto;
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
    public void givenPurchaseEventDto_whenProcessPurchaseEvent_thenSuccess() {
        //given
        var dto = PurchaseEventDto.builder()
                .pantryId(1L)
                .productId(1L)
                .pantryName("Product1")
                .productSize("Size1")
                .quantity(1)
                .build();

        when(repository.save(captor.capture())).thenReturn(any(PurchaseItem.class));

        //when
        service.processPurchaseEvent(dto);

        //then
        var item = captor.getValue();
        assertThat(item.pantryId).isEqualTo(dto.getPantryId());
        assertThat(item.pantryName).isEqualTo(dto.getPantryName());
        assertThat(item.productId).isEqualTo(dto.getProductId());
        assertThat(item.productDescription).isEqualTo(dto.getProductDescription());
        assertThat(item.productSize).isEqualTo(dto.getProductSize());
        assertThat(item.quantity).isEqualTo(dto.getQuantity());
        assertThat(item.purchase).isNull();

    }


}
