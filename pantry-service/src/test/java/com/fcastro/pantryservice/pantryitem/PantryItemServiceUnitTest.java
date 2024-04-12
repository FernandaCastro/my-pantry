package com.fcastro.pantryservice.pantryitem;

import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.kafka.event.PurchaseEventDto;
import com.fcastro.kafka.exception.EventProcessingException;
import com.fcastro.pantryservice.event.PurchaseCreateEventProducer;
import com.fcastro.pantryservice.exception.PantryNotActiveException;
import com.fcastro.pantryservice.exception.QuantityNotAvailableException;
import com.fcastro.pantryservice.pantry.Pantry;
import com.fcastro.pantryservice.product.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PantryItemServiceUnitTest {

    @InjectMocks
    PantryItemService service;

    @Mock
    PantryItemRepository repository;

    @Mock
    PurchaseCreateEventProducer eventProducer;

    @Spy
    ModelMapper modelMapper;

    @Captor
    ArgumentCaptor<PantryItem> captor;

    @Test
    public void givenValidIds_whenGet_ShouldReturnPantryProductDto() {
        //given
        var entity = buildPantryItem(1L, 1L, 5, 10);
        given(repository.findById(any(PantryItemKey.class))).willReturn(Optional.of(entity));

        //when
        var dto = service.get(1L, 1L);

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.isPresent()).isEqualTo(true);
        assertThat(dto.get().getPantry().getId()).isEqualTo(1);
        assertThat(dto.get().getProduct().getId()).isEqualTo(1);
        assertThat(dto.get().getCurrentQty()).isEqualTo(5);
        assertThat(dto.get().getIdealQty()).isEqualTo(10);
    }

    @Test
    public void givenValidDto_whenSave_ShouldReturnNewDto() {
        //given
        var entity = buildPantryItem(1L, 1L, 5, 10);
        given(repository.save(any(PantryItem.class))).willReturn(entity);

        //when
        var dto = service.update(PantryItemDto.builder().build());

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.getPantry().getId()).isEqualTo(1);
        assertThat(dto.getProduct().getId()).isEqualTo(1);
        assertThat(dto.getCurrentQty()).isEqualTo(5);
        assertThat(dto.getIdealQty()).isEqualTo(10);
    }

    @Test
    public void givenValidIds_whenDelete_ShouldReturnNull() {
        //given
        var entity = buildPantryItem(1L, 1L, 5, 10);
        given(repository.findById(any(PantryItemKey.class))).willReturn(Optional.of(entity));
        doNothing().when(repository).deleteById(any(PantryItemKey.class));

        //when //then
        service.delete(1L, 1L);

        //then
        verify(repository, times(1)).deleteById(any(PantryItemKey.class));
    }

    @Test
    public void givenInvalidPantryId_whenDelete_ShouldThrowException() {
        //given
        given(repository.findById(any(PantryItemKey.class))).willReturn(Optional.empty());

        //when //then
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1, 1));

        //then
        verify(repository, times(0)).deleteById(
                PantryItemKey.builder()
                        .pantryId(1L)
                        .productId(1L).build());
    }

    @Test
    public void givenValidIds_whenConsumeProduct_shouldCalculateQtyAndNoPurchase() {
        //given
        var entity = PantryItem.builder()
                .currentQty(10)
                .idealQty(10)
                .pantry(Pantry.builder().id(1L).name("PANTRY1").isActive(true).build())
                .product(Product.builder().id(1L).description("MILK").size("1L").build())
                .build();

        given(repository.findEagerByPantryIdAndProductId(anyLong(), anyLong())).willReturn(Optional.of(entity));
        given(repository.save(captor.capture())).willReturn(entity);

        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(1).build();
        service.consumePantryItem(consumedDto);

        //then
        var item = captor.getValue();
        assertThat(item.getCurrentQty()).isEqualTo(9);
        verify(eventProducer, times(0)).send(any(PurchaseEventDto.class));
    }

    @Test
    public void givenValidIds_whenConsumeProduct_shouldCalculateQtyAndPurchase() {
        //given
        var entity = PantryItem.builder()
                .currentQty(6)
                .idealQty(10)
                .pantry(Pantry.builder().id(1L).name("PANTRY1").isActive(true).build())
                .product(Product.builder().id(1L).description("MILK").size("1L").build())
                .build();

        given(repository.findEagerByPantryIdAndProductId(anyLong(), anyLong())).willReturn(Optional.of(entity));
        given(repository.save(captor.capture())).willReturn(entity);
        doNothing().when(eventProducer).send(any(PurchaseEventDto.class));

        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(1).build();
        service.consumePantryItem(consumedDto);

        //then
        var item = captor.getValue();
        assertThat(item.getCurrentQty()).isEqualTo(5);
        assertThat(item.getProvisionedQty()).isEqualTo(5);


        var purchaseEventDto = PurchaseEventDto.builder()
                .qtyProvisioned(5)
                .pantryId(entity.getPantry().getId())
                .pantryName(entity.getPantry().getName())
                .productId(entity.getProduct().getId())
                .build();
        verify(eventProducer, times(0)).send(purchaseEventDto);
    }

    @Test
    public void givenInvalidIds_whenConsumeProduct_shouldReturnQuantityNotAvailable() {
        //given
        given(repository.findEagerByPantryIdAndProductId(anyLong(), anyLong())).willReturn(Optional.empty());

        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(1).build();
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.consumePantryItem(consumedDto));

        //then
        verify(repository, times(0)).save(any(PantryItem.class));
        verify(eventProducer, times(0)).send(any(PurchaseEventDto.class));
    }

    @Test
    public void givenInvalidQuantity_whenConsumeProduct_shouldRaiseQuantityNotAvailableException() {
        //given
        var entity = PantryItem.builder()
                .currentQty(1)
                .idealQty(10)
                .pantry(Pantry.builder().id(1L).name("PANTRY1").isActive(true).build())
                .product(Product.builder().id(1L).description("MILK").size("1L").build())
                .build();

        //1 item available but the request asks for 2 items
        given(repository.findEagerByPantryIdAndProductId(anyLong(), anyLong())).willReturn(Optional.of(entity));

        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(2).build();
        Assertions.assertThrows(QuantityNotAvailableException.class, () -> service.consumePantryItem(consumedDto));

        //then
        verify(repository, times(0)).save(any(PantryItem.class));
        verify(eventProducer, times(0)).send(any(PurchaseEventDto.class));
    }

    @Test
    public void givenPantryNotActive_whenConsumeProduct_shouldRaisePantryNotActiveException() {
        //given
        var pantry = Pantry.builder().id(1L).isActive(false).build();
        var entity = PantryItem.builder()
                .pantry(pantry)
                .currentQty(1)
                .idealQty(10)
                .pantry(Pantry.builder().id(1L).name("PANTRY1").isActive(false).build())
                .product(Product.builder().id(1L).description("MILK").size("1L").build())
                .build();

        given(repository.findEagerByPantryIdAndProductId(anyLong(), anyLong())).willReturn(Optional.of(entity));

        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(1).build();
        Assertions.assertThrows(PantryNotActiveException.class, () -> service.consumePantryItem(consumedDto));

        //then
        verify(repository, times(0)).save(any(PantryItem.class));
        verify(eventProducer, times(0)).send(any(PurchaseEventDto.class));
    }


    @Test
    public void givenListOfValidIds_whenConsumeProduct_shouldCalculateQtyAndPurchase() {
        //given
        var entity = PantryItem.builder()
                .currentQty(10)
                .idealQty(10)
                .pantry(Pantry.builder().id(1L).name("PANTRY1").isActive(true).build())
                .product(Product.builder().id(1L).description("MILK").size("1L").build())
                .build();

        given(repository.findEagerByPantryIdAndProductId(anyLong(), anyLong())).willReturn(Optional.of(entity));
        given(repository.save(captor.capture())).willReturn(entity);

        //when
        var list = new ArrayList<PantryItemConsumedDto>();
        list.add(PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(2).build());
        list.add(PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(2).build());
        service.consumePantryItem(1L, list);

        //then
        var item = captor.getValue();
        assertThat(item.getCurrentQty()).isEqualTo(6);
        assertThat(item.getProvisionedQty()).isEqualTo(0);
    }

    @Test
    public void givenInvalidPurchaseEventDtoList_whenProcessPurchaseCompleteEvent_shouldEventProcessingException() {
        //given
        var eventDtoList = new ArrayList<PurchaseEventDto>();
        eventDtoList.add(PurchaseEventDto.builder().pantryId(999).productId(999).qtyPurchased(2).build());
        eventDtoList.add(PurchaseEventDto.builder().pantryId(999).productId(999).qtyPurchased(2).build());
        eventDtoList.add(PurchaseEventDto.builder().pantryId(999).productId(999).qtyPurchased(2).build());
        given(repository.findById(any(PantryItemKey.class))).willReturn(Optional.empty());

        //when then
        var ex = Assertions.assertThrows(EventProcessingException.class, () -> service.processPurchaseCompleteEvent(eventDtoList));

        assertThat(ex.getThrowableMap()).isNotNull();
        assertThat(ex.getThrowableMap().size()).isEqualTo(3);
    }

    private PantryItem buildPantryItem(long pantryId, long productId, int currentQty, int idealQty) {
        return PantryItem.builder()
                .id(PantryItemKey.builder().pantryId(pantryId).productId(productId).build())
                .pantry(Pantry.builder().id(pantryId).build())
                .product(Product.builder().id(productId).build())
                .currentQty(currentQty)
                .idealQty(idealQty).build();
    }

}
