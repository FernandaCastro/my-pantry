package com.fcastro.pantry.controller;

import com.fcastro.pantry.JsonUtil;
import com.fcastro.pantry.model.ProductDto;
import com.fcastro.pantry.service.ProductService;
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

@WebMvcTest(controllers = ProductController.class)
public class ProductControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Test
    public void givenValidId_whenGet_shouldReturnOk() throws Exception {
        //given
        var dto = ProductDto.builder().id(1).code("MILK").description("Integral").unit("CX").amount("1L").build();

        given(service.get(anyLong())).willReturn(Optional.of(dto));

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("MILK")))
                .andExpect(jsonPath("$.description", is("Integral")))
                .andExpect(jsonPath("$.unit", is("CX")))
                .andExpect(jsonPath("$.amount", is("1L")));
    }

    @Test
    public void givenInvalidId_whenGet_shouldReturnNoContent() throws Exception {
        //given
        given(service.get(anyLong())).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/product/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetAll_shouldReturnOk() throws Exception {
        //given
        var list = new ArrayList<ProductDto>();
        list.add(ProductDto.builder().id(1).code("MILK").description("Integral").unit("CX").amount("1L").build());
        list.add(ProductDto.builder().id(2).code("MILK").description("Integral").unit("CX").amount("2L").build());

        given(service.getAll()).willReturn(list);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void givenNewDto_whenCreate_shouldReturnCreated() throws Exception {
        //given
        var dto = ProductDto.builder().id(1).code("MILK").description("Integral").unit("CX").amount("1L").build();

        given(service.save(any(ProductDto.class))).willReturn(dto);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("MILK")))
                .andExpect(jsonPath("$.description", is("Integral")))
                .andExpect(jsonPath("$.unit", is("CX")))
                .andExpect(jsonPath("$.amount", is("1L")));
    }

    @Test
    public void givenValidId_whenReplace_shouldReturnCreated() throws Exception {
        //given
        var dto = ProductDto.builder().id(1).code("MILK").description("Integral").unit("CX").amount("1L").build();

        given(service.get(anyLong())).willReturn(Optional.of(dto));
        given(service.save(any(ProductDto.class))).willReturn(dto);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("MILK")))
                .andExpect(jsonPath("$.description", is("Integral")))
                .andExpect(jsonPath("$.unit", is("CX")))
                .andExpect(jsonPath("$.amount", is("1L")));
    }

    @Test
    public void givenInvalidId_whenReplace_shouldReturnCreated() throws Exception {
        //given
        var dto = ProductDto.builder().id(10).code("MILK").description("Integral").unit("CX").amount("1L").build();
        given(service.get(anyLong())).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/product/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenValidPantryId_whenDelete_shouldReturnNoContent() throws Exception {
        //given
        doNothing().when(service).delete(anyLong());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/product/1"))
                .andExpect(status().isNoContent());
    }

}
