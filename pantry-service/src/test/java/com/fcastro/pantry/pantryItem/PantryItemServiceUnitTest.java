package com.fcastro.pantry.pantryItem;

import com.fcastro.kafka.model.PurchaseEventItemDto;
import com.fcastro.pantry.config.EventProducer;
import com.fcastro.pantry.exception.PantryNotActiveException;
import com.fcastro.pantry.exception.QuantityNotAvailableException;
import com.fcastro.pantry.exception.ResourceNotFoundException;
import com.fcastro.pantry.pantry.Pantry;
import com.fcastro.pantry.product.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

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
    EventProducer eventProducer;

    @Spy
    ModelMapper modelMapper;

    @Captor
    ArgumentCaptor<PantryItem> captor;

    @Test
    public void givenValidIds_whenGet_ShouldReturnPantryProductDto() {
        //given
        //var pk = PantryItemKey.builder().pantryId(1L).productId(1L).build();
        var entity = PantryItem.builder()
                .pantryId(1L)
                .productId(1L)
                .currentQty(5)
                .idealQty(10).build();

        given(repository.findById(any(PantryItemKey.class))).willReturn(Optional.of(entity));

        //when
        var dto = service.get(1L, 1L);

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.isPresent()).isEqualTo(true);
        assertThat(dto.get().getPantryId()).isEqualTo(1);
        assertThat(dto.get().getProductId()).isEqualTo(1);
        assertThat(dto.get().getCurrentQty()).isEqualTo(5);
        assertThat(dto.get().getIdealQty()).isEqualTo(10);
    }

    @Test
    public void givenValidDto_whenSave_ShouldReturnNewDto() {
        //given
        var entity = PantryItem.builder()
                //.pantryItemKey(PantryItemKey.builder().pantryId(1L).productId(1L).build())
                .pantryId(1L)
                .productId(1L)
                .currentQty(5)
                .idealQty(10).build();

        given(repository.save(any(PantryItem.class))).willReturn(entity);

        //when
        var dto = service.save(PantryItemDto.builder().build());

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.getPantryId()).isEqualTo(1);
        assertThat(dto.getProductId()).isEqualTo(1);
        assertThat(dto.getCurrentQty()).isEqualTo(5);
        assertThat(dto.getIdealQty()).isEqualTo(10);
    }

    @Test
    public void givenValidIds_whenDelete_ShouldReturnNull() {
        //given
        var pk = PantryItemKey.builder().pantryId(1L).productId(1L).build();
        var entity = PantryItem.builder()
                //.pantryItemKey(pk)
                .pantryId(1L)
                .productId(1L)
                .currentQty(5)
                .idealQty(10).build();

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
        verify(repository, times(0)).deleteById(PantryItemKey.builder().pantryId(1L).productId(1L).build());
    }

    @Test
    public void givenValidIds_whenConsumeProduct_shouldCalculateQtyAndNoPurchase() {
        //given
        var pk = PantryItemKey.builder().pantryId(1L).productId(1L).build();
        var entity = PantryItem.builder()
                //.pantryItemKey(pk)
                .pantryId(1L)
                .productId(1L)
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
        verify(eventProducer, times(0)).send(any(PurchaseEventItemDto.class));
    }

    @Test
    public void givenValidIds_whenConsumeProduct_shouldCalculateQtyAndPurchase() {
        //given
        var pk = PantryItemKey.builder().pantryId(1L).productId(1L).build();
        var entity = PantryItem.builder()
                //.pantryItemKey(pk)
                .pantryId(1L)
                .productId(1L)
                .currentQty(6)
                .idealQty(10)
                .pantry(Pantry.builder().id(1L).name("PANTRY1").isActive(true).build())
                .product(Product.builder().id(1L).description("MILK").size("1L").build())
                .build();

        given(repository.findEagerByPantryIdAndProductId(anyLong(), anyLong())).willReturn(Optional.of(entity));
        given(repository.save(captor.capture())).willReturn(entity);
        doNothing().when(eventProducer).send(any(PurchaseEventItemDto.class));

        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(1).build();
        service.consumePantryItem(consumedDto);

        //then
        var item = captor.getValue();
        assertThat(item.getCurrentQty()).isEqualTo(5);
        assertThat(item.getProvisionedQty()).isEqualTo(5);


        var purchaseEventDto = PurchaseEventItemDto.builder()
                .qtyProvisioned(5)
                .pantryId(entity.getPantry().getId())
                .pantryName(entity.getPantry().getName())
                .productId(entity.getProduct().getId())
                .productDescription(entity.getProduct().getDescription())
                .productSize(entity.getProduct().getSize())
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
        verify(eventProducer, times(0)).send(any(PurchaseEventItemDto.class));
    }

    @Test
    public void givenInvalidQuantity_whenConsumeProduct_shouldRaiseQuantityNotAvailableException() {
        //given
        var pk = PantryItemKey.builder().pantryId(1L).productId(1L).build();
        var entity = PantryItem.builder()
                //.pantryItemKey(pk)
                .pantryId(1L)
                .productId(1L)
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
        verify(eventProducer, times(0)).send(any(PurchaseEventItemDto.class));
    }

    @Test
    public void givenPantryNotActive_whenConsumeProduct_shouldRaisePantryNotActiveException() {
        //given
        var pantry = Pantry.builder().id(1L).isActive(false).build();
        var entity = PantryItem.builder()
                .pantryId(1L)
                .productId(1L)
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
        verify(eventProducer, times(0)).send(any(PurchaseEventItemDto.class));
    }

}
