package com.fcastro.pantryservice.pantry;

import com.fcastro.accountservice.accesscontrol.AccessControlService;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.authorization.AuthorizationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PantryServiceUnitTest {

    @InjectMocks
    PantryService service;

    @Mock
    PantryRepository repository;

    @Spy
    ModelMapper modelMapper;

    @Mock
    private AccessControlService accessControlService;

    @Mock
    private AuthorizationHandler authorizationHandler;

    private static final String MOCK_USER = "MOCK_USER";

    @Test
    public void givenValidPantryId_whenGet_ShouldReturnPantryDto() {
        //given
        var entity = Pantry.builder().id(1L).name("Base Inventory").isActive(true).type("R").build();
        given(repository.findById(anyLong())).willReturn(Optional.of(entity));

        //when
        var dto = service.get(1);

        //then
        org.assertj.core.api.Assertions.assertThat(dto).isNotNull();
        assertThat(dto.isPresent()).isEqualTo(true);
        assertThat(dto.get().getId()).isEqualTo(1L);
        assertThat(dto.get().getName()).isEqualTo("Base Inventory");
        assertThat(dto.get().getIsActive()).isEqualTo(true);
        assertThat(dto.get().getType()).isEqualTo("R");
    }

    @Test
    public void givenValidDto_whenSave_ShouldReturnNewDto() {
        //given
        var entity = Pantry.builder().id(1L).name("Base Inventory").isActive(true).type("R").build();
        given(repository.save(any(Pantry.class))).willReturn(entity);
        doNothing().when(accessControlService).save(anyString(), anyLong(), anyLong());

        //when
        var dto = service.save(PantryDto.builder().id(1L).name("Base Inventory").isActive(true).type("R").accountGroupId(10L).build());

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Base Inventory");
        assertThat(dto.getIsActive()).isEqualTo(true);
        assertThat(dto.getType()).isEqualTo("R");
    }

    @Test
    public void givenValidPantryId_whenDelete_ShouldReturnNull() {
        //given
        var dto = Pantry.builder().id(1L).name("Base Inventory").isActive(true).type("R").build();
        given(repository.findById(anyLong())).willReturn(Optional.of(dto));
        doNothing().when(repository).deleteById(anyLong());
        doNothing().when(accessControlService).delete(anyString(), anyLong());

        //when //then
        service.delete(1);

        //then
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void givenInvalidPantryId_whenDelete_ShouldThrowException() {
        //given
        given(repository.findById(anyLong())).willReturn(Optional.empty());

        //when //then
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1));

        //then
        verify(repository, times(0)).deleteById(1L);
    }

}
