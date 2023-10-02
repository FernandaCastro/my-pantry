package com.fcastro.pantry.controller;

import com.fcastro.pantry.JsonUtil;
import com.fcastro.pantry.model.PantryDto;
import com.fcastro.pantry.model.PantryProductDto;
import com.fcastro.pantry.model.ProductDto;
import com.fcastro.pantry.service.PantryProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PantryProductController.class)
public class PantryProductControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PantryProductService service;

    @Test
    public void givenValidIds_whenGet_shouldReturnOk() throws Exception {
        //given
        var dto = PantryProductDto.builder()
                .pantry(PantryDto.builder().id(1).build())
                .product(ProductDto.builder().id(1).build())
                .idealQty(5)
                .currentQty(5).build();

        given(service.get(anyLong(), anyLong())).willReturn(Optional.of(dto));

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantry/1/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pantry.id", is(1)))
                .andExpect(jsonPath("$.product.id", is(1)))
                .andExpect(jsonPath("$.idealQty", is(5)))
                .andExpect(jsonPath("$.currentQty", is(5)));
    }

    @Test
    public void whenGetAll_shouldReturnOk() throws Exception {
        //given
        var list = new ArrayList<PantryProductDto>();
        list.add(PantryProductDto.builder()
                .pantry(PantryDto.builder().id(1).build())
                .product(ProductDto.builder().id(1).build())
                .idealQty(5)
                .currentQty(5).build());
        list.add(PantryProductDto.builder()
                .pantry(PantryDto.builder().id(1).build())
                .product(ProductDto.builder().id(2).build())
                .idealQty(5)
                .currentQty(5).build());

        given(service.getAll(anyLong())).willReturn(list);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantry/1/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void givenInvalidIds_whenGet_shouldReturnNotFound() throws Exception {
        //given
        given(service.get(anyLong(), anyLong())).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantry/1/product/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenNewIds_whenCreate_shouldReturnCreated() throws Exception {
        //given
        var dto = PantryProductDto.builder()
                .pantry(PantryDto.builder().id(1).build())
                .product(ProductDto.builder().id(1).build())
                .idealQty(5)
                .currentQty(5).build();

        given(service.save(any(PantryProductDto.class))).willReturn(dto);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/pantry/1/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pantry.id", is(1)))
                .andExpect(jsonPath("$.product.id", is(1)))
                .andExpect(jsonPath("$.idealQty", is(5)))
                .andExpect(jsonPath("$.currentQty", is(5)));
    }

    @Test
    public void givenValidIds_whenReplace_shouldReturnCreated() throws Exception {
        //given
        var dto = PantryProductDto.builder()
                .pantry(PantryDto.builder().id(1).build())
                .product(ProductDto.builder().id(1).build())
                .idealQty(5)
                .currentQty(5).build();

        given(service.get(1L, 1L)).willReturn(Optional.of(dto));
        given(service.save(any(PantryProductDto.class))).willReturn(dto);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/pantry/1/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pantry.id", is(1)))
                .andExpect(jsonPath("$.product.id", is(1)))
                .andExpect(jsonPath("$.idealQty", is(5)))
                .andExpect(jsonPath("$.currentQty", is(5)));
    }

    @Test
    public void givenInvalidPantryId_whenReplace_shouldReturnCreated() throws Exception {
        //given
        var dto = PantryProductDto.builder()
                .pantry(PantryDto.builder().id(1).build())
                .product(ProductDto.builder().id(1).build())
                .idealQty(5)
                .currentQty(5).build();
        given(service.get(1L, 1L)).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/pantry/1/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenValidPantryId_whenDelete_shouldReturnNoContent() throws Exception {
        //given
        doNothing().when(service).delete(1L, 1L);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/pantry/1/product/1"))
                .andExpect(status().isNoContent());
    }

}
