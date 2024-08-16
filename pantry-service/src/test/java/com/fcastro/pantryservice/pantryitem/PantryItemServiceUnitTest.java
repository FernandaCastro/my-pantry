package com.fcastro.pantryservice.pantryitem;

import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.app.model.Action;
import com.fcastro.kafka.exception.EventProcessingException;
import com.fcastro.kafka.model.PurchaseEventDto;
import com.fcastro.pantryservice.event.PurchaseEventProducer;
import com.fcastro.pantryservice.exception.PantryNotActiveException;
import com.fcastro.pantryservice.exception.QuantityNotAvailableException;
import com.fcastro.pantryservice.pantry.Pantry;
import com.fcastro.pantryservice.product.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.Locale;
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
    PurchaseEventProducer eventProducer;

    @Spy
    ModelMapper modelMapper;

    @Captor
    ArgumentCaptor<PantryItem> captorPantryItem;

    @Captor
    ArgumentCaptor<PurchaseEventDto> captorPurchaseEventDto;

    @Mock
    LocaleContextHolder localeContextHolder;

    @BeforeEach
    public void setupLocale() {
        localeContextHolder.setLocale(Locale.UK);
    }

    @Test
    public void givenValidIds_whenGet_ShouldReturnPantryProductDto() {
        //given
        var entity = buildPantryItem(1L, 1L, 5, 10);
        given(repository.findById(any(PantryItemKey.class))).willReturn(Optional.of(entity));

        //when
        var optionalDto = service.get(1L, 1L);

        //then
        assertThat(optionalDto).isNotNull();
        assertThat(optionalDto.isPresent()).isEqualTo(true);
        var dto = optionalDto.get();
        assertThat(dto.getPantry().getId()).isEqualTo(1);
        assertThat(dto.getProduct().getId()).isEqualTo(1);
        assertThat(dto.getCurrentQty()).isEqualTo(5);
        assertThat(dto.getIdealQty()).isEqualTo(10);
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
    public void givenInvalidPurchaseEventDtoList_whenProcessPurchaseCompleteEvent_shouldEventProcessingException() {
        //given
        var eventDtoList = new ArrayList<PurchaseEventDto>();
        eventDtoList.add(PurchaseEventDto.builder().pantryId(999).productId(999).qtyPurchased(2).build());
        eventDtoList.add(PurchaseEventDto.builder().pantryId(999).productId(999).qtyPurchased(2).build());
        eventDtoList.add(PurchaseEventDto.builder().pantryId(999).productId(999).qtyPurchased(2).build());
        given(repository.findById(any(PantryItemKey.class))).willReturn(Optional.empty());

        //when then
        Assertions.assertThrows(EventProcessingException.class, () -> service.processPurchaseCompleteEvent(eventDtoList));
    }

    @Test
    public void givenHighAvailabilityAndLowConsumption_whenConsumeProduct_shouldOnlyDecreaseCurrentQtyNoProvision() {
        //given
        var entity = PantryItem.builder()
                .currentQty(10)
                .idealQty(10)
                .provisionedQty(0)
                .pantry(Pantry.builder().id(1L).name("PANTRY1").isActive(true).build())
                .product(Product.builder().id(1L).description("MILK").size("1L").build())
                .build();

        given(repository.findEagerByPantryIdAndProductId(anyLong(), anyLong())).willReturn(Optional.of(entity));
        given(repository.save(captorPantryItem.capture())).willReturn(entity);

        //when
        var list = new ArrayList<PantryItemConsumedDto>();
        list.add(PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(2).build());
        list.add(PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(2).build());
        service.consumePantryItem(1L, list);

        //then
        var item = captorPantryItem.getValue();
        assertThat(item.getIdealQty()).isEqualTo(10);
        assertThat(item.getCurrentQty()).isEqualTo(6);
        assertThat(item.getProvisionedQty()).isEqualTo(0);
        verify(eventProducer, times(0)).send(any(PurchaseEventDto.class));

    }

    @Test
    public void givenHighAvailabilityAndHighConsumption_whenConsumeProduct_shouldCreateProvisioning() {
        //given
        var entity = PantryItem.builder()
                .currentQty(6)
                .idealQty(10)
                .provisionedQty(0)
                .pantry(Pantry.builder().id(1L).name("PANTRY1").isActive(true).type("R").build())
                .product(Product.builder().id(1L).description("MILK").size("1L").build())
                .build();

        given(repository.findEagerByPantryIdAndProductId(anyLong(), anyLong())).willReturn(Optional.of(entity));
        given(repository.save(captorPantryItem.capture())).willReturn(entity);
        doNothing().when(eventProducer).send(captorPurchaseEventDto.capture());

        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(1).build();
        service.consumePantryItem(consumedDto);

        //then
        var item = captorPantryItem.getValue();
        assertThat(item.getIdealQty()).isEqualTo(10);
        assertThat(item.getCurrentQty()).isEqualTo(5);
        assertThat(item.getProvisionedQty()).isEqualTo(5);

        verify(eventProducer, times(1)).send(any(PurchaseEventDto.class));
        var purchaseEventDto = captorPurchaseEventDto.getValue();
        assertThat(purchaseEventDto.getAction()).isEqualTo(Action.CREATE);
        assertThat(purchaseEventDto.getQtyProvisioned()).isEqualTo(5);
    }

    @Test
    public void givenLowAvailabilityAndLowProvisionedQty_whenConsumeProduct_shouldIncreaseProvision() {
        //given
        var entity = PantryItem.builder()
                .currentQty(5)
                .idealQty(10)
                .provisionedQty(5)
                .pantry(Pantry.builder().id(1L).name("PANTRY1").type("R").isActive(true).build())
                .product(Product.builder().id(1L).description("MILK").size("1L").build())
                .build();

        given(repository.findEagerByPantryIdAndProductId(anyLong(), anyLong())).willReturn(Optional.of(entity));
        given(repository.save(captorPantryItem.capture())).willReturn(entity);
        doNothing().when(eventProducer).send(captorPurchaseEventDto.capture());

        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(1).build();
        service.consumePantryItem(consumedDto);

        //then
        var item = captorPantryItem.getValue();
        assertThat(item.getIdealQty()).isEqualTo(10);
        assertThat(item.getCurrentQty()).isEqualTo(4);
        assertThat(item.getProvisionedQty()).isEqualTo(6);

        verify(eventProducer, times(1)).send(any(PurchaseEventDto.class));
        var purchaseEventDto = captorPurchaseEventDto.getValue();
        assertThat(purchaseEventDto.getAction()).isEqualTo(Action.CREATE);
        assertThat(purchaseEventDto.getQtyProvisioned()).isEqualTo(1);
    }

    @Test
    public void givenHighAvailabilityAndHasProvisionedQty_whenConsumeProduct_shouldZeroProvision() {
        //given
        var entity = PantryItem.builder()
                .currentQty(10)
                .idealQty(10)
                .provisionedQty(5)
                .pantry(Pantry.builder().id(1L).name("PANTRY1").isActive(true).type("R").build())
                .product(Product.builder().id(1L).description("MILK").size("1L").build())
                .build();

        given(repository.findEagerByPantryIdAndProductId(anyLong(), anyLong())).willReturn(Optional.of(entity));
        given(repository.save(captorPantryItem.capture())).willReturn(entity);
        doNothing().when(eventProducer).send(captorPurchaseEventDto.capture());

        //when
        var consumedDto = PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(1).build();
        service.consumePantryItem(consumedDto);

        //then
        var item = captorPantryItem.getValue();
        assertThat(item.getIdealQty()).isEqualTo(10);
        assertThat(item.getCurrentQty()).isEqualTo(9);
        assertThat(item.getProvisionedQty()).isEqualTo(0);

        verify(eventProducer, times(1)).send(any(PurchaseEventDto.class));
        var purchaseEventDto = captorPurchaseEventDto.getValue();
        assertThat(purchaseEventDto.getAction()).isEqualTo(Action.DELETE);
        assertThat(purchaseEventDto.getQtyProvisioned()).isEqualTo(5);

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
