package com.fcastro.pantry.service;

import com.fcastro.pantry.config.ResourceNotFoundException;
import com.fcastro.pantry.model.PantryDto;
import com.fcastro.pantry.model.PantryEntity;
import com.fcastro.pantry.repository.PantryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
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

    @Test
    public void givenValidPantryId_whenGet_ShouldReturnPantryDto() {
        //given
        var entity = PantryEntity.builder().id(1).name("Base Inventory").active(true).type("R").build();
        given(repository.findById(anyLong())).willReturn(Optional.of(entity));

        //when
        var dto = service.get(1);

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.isPresent()).isEqualTo(true);
        assertThat(dto.get().getId()).isEqualTo(1);
        assertThat(dto.get().getName()).isEqualTo("Base Inventory");
        assertThat(dto.get().isActive()).isEqualTo(true);
        assertThat(dto.get().getType()).isEqualTo("R");
    }

    @Test
    public void whenGetAll_ShouldReturnList() {
        //given
        var list = new ArrayList<PantryEntity>();
        list.add(PantryEntity.builder().id(1).name("Base Inventory").active(true).type("R").build());
        list.add(PantryEntity.builder().id(2).name("Base Inventory").active(true).type("R").build());

        given(repository.findAll()).willReturn(list);

        //when
        var result = service.getAll();

        //then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void givenValidDto_whenSave_ShouldReturnNewDto() {
        //given
        var entity = PantryEntity.builder().id(1).name("Base Inventory").active(true).type("R").build();
        given(repository.save(any(PantryEntity.class))).willReturn(entity);

        //when
        var dto = service.save(PantryDto.builder().build());

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Base Inventory");
        assertThat(dto.isActive()).isEqualTo(true);
        assertThat(dto.getType()).isEqualTo("R");
    }

    @Test
    public void givenValidPantryId_whenDelete_ShouldReturnNull() {
        //given
        var dto = PantryEntity.builder().id(1).name("Base Inventory").active(true).type("R").build();
        given(repository.findById(anyLong())).willReturn(Optional.of(dto));
        doNothing().when(repository).deleteById(anyLong());

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
