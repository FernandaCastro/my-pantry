package com.fcastro.pantryservice.product;

import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.pantryservice.event.ProductEventProducer;
import com.fcastro.pantryservice.exception.DatabaseConstraintException;
import com.fcastro.pantryservice.pantryitem.PantryItemRepository;
import com.fcastro.security.authorization.AuthorizationHandler;
import com.fcastro.security.core.model.AccountGroupDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {

    @InjectMocks
    ProductService service;

    @Mock
    ProductRepository repository;

    @Mock
    PantryItemRepository pantryItemRepository;

    @Mock
    ProductEventProducer productEventProducer;

    @Spy
    ModelMapper modelMapper;

    @Mock
    private AuthorizationHandler authorizationHandler;

    @Mock
    LocaleContextHolder localeContextHolder;

    @Test
    public void givenValidPantryId_whenGet_ShouldReturnPantryDto() {
        //given
        var entity = Product.builder().id(1L).code("MILK").description("Integral").size("1L").build();
        given(repository.findById(anyLong())).willReturn(Optional.of(entity));

        //when
        var dto = service.get(1L);

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.isPresent()).isEqualTo(true);
        assertThat(dto.get().getId()).isEqualTo(1);
        assertThat(dto.get().getCode()).isEqualTo("MILK");
        assertThat(dto.get().getDescription()).isEqualTo("Integral");
        assertThat(dto.get().getSize()).isEqualTo("1L");
    }

    @Test
    public void givenValidDto_whenSave_ShouldReturnNewDto() {
        //given
        var entity = Product.builder().id(1L).code("MILK").description("Integral").size("1L").build();
        given(repository.save(any(Product.class))).willReturn(entity);
        doNothing().when(authorizationHandler).saveAccessControl(anyString(), anyLong(), anyLong());

        //when
        var dto = service.update(ProductDto.builder().id(1L).code("MILK").description("Integral").size("1L")
                .accountGroup(AccountGroupDto.builder().id(1L).build()).build());

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getCode()).isEqualTo("MILK");
        assertThat(dto.getDescription()).isEqualTo("Integral");
        assertThat(dto.getSize()).isEqualTo("1L");
    }

    @Test
    public void givenValidId_whenDelete_ShouldReturnNull() {
        //given
        var dto = Product.builder().id(1L).code("MILK").description("Integral").size("1L").build();
        given(repository.findById(anyLong())).willReturn(Optional.of(dto));
        given(pantryItemRepository.countPantryItem(anyLong())).willReturn(0);
        doNothing().when(repository).deleteById(anyLong());
        doNothing().when(authorizationHandler).deleteAccessControl(anyString(), anyLong());

        //when //then
        service.delete(1);

        //then
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void givenValidId_whenDelete_ShouldReturnDatabaseConstraintException() {
        //given
        var dto = Product.builder().id(1L).code("MILK").description("Integral").size("1L").build();
        given(repository.findById(anyLong())).willReturn(Optional.of(dto));
        given(pantryItemRepository.countPantryItem(anyLong())).willReturn(1);
        localeContextHolder.setLocale(Locale.UK);

        //when //then
        Assertions.assertThrows(DatabaseConstraintException.class,
                () -> service.delete(1));

        //then
        verify(repository, times(0)).deleteById(1L);
    }

    @Test
    public void givenInvalidId_whenDelete_ShouldThrowException() {
        //given
        given(repository.findById(anyLong())).willReturn(Optional.empty());
        localeContextHolder.setLocale(Locale.UK);

        //when //then
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1));

        //then
        verify(repository, times(0)).deleteById(1L);
    }

}
