package com.fcastro.pantryservice.product;

import com.fcastro.pantryservice.JsonUtil;
import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.jwt.JWTRequestFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {JWTRequestFilter.class})})
@EnableConfigurationProperties(value = SecurityPropertiesConfig.class)
@AutoConfigureDataJpa
public class ProductControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Test
    @WithMockUser
    public void givenValidId_whenGet_shouldReturnOk() throws Exception {
        //given
        var dto = ProductDto.builder().id(1L).code("MILK").description("Integral").size("1L").build();

        given(service.getEmbeddingAccountGroup(anyString(), anyLong())).willReturn(Optional.of(dto));

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantryservice/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("MILK")))
                .andExpect(jsonPath("$.description", is("Integral")))
                .andExpect(jsonPath("$.size", is("1L")));
    }

    @Test
    @WithMockUser
    public void givenInvalidId_whenGet_shouldReturnNoContent() throws Exception {
        //given
        given(service.get(anyLong())).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantryservice/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void whenGetAll_shouldReturnOk() throws Exception {
        //given
        var list = new ArrayList<ProductDto>();
        list.add(ProductDto.builder().id(1L).code("MILK").description("Integral").size("1L").build());
        list.add(ProductDto.builder().id(2L).code("VOLLMILK").description("Integral").size("2L").build());

        given(service.getAllBySearchParam(anyString(), anyLong(), anyString())).willReturn(list);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantryservice/products")
                        .param("groupId", "1")
                        .param("searchParam", "MILK")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void givenNewDto_whenCreate_shouldReturnCreated() throws Exception {
        //given
        var dto = ProductDto.builder().id(1L).code("MILK").description("Integral").size("1L").build();

        given(service.create(any(ProductDto.class))).willReturn(dto);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/pantryservice/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("MILK")))
                .andExpect(jsonPath("$.description", is("Integral")))
                .andExpect(jsonPath("$.size", is("1L")));
    }

    @Test
    public void givenValidId_whenReplace_shouldReturnCreated() throws Exception {
        //given
        var dto = ProductDto.builder().id(1L).code("MILK").description("Integral").size("1L").build();

        given(service.get(anyLong())).willReturn(Optional.of(dto));
        given(service.update(any(ProductDto.class))).willReturn(dto);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/pantryservice/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("MILK")))
                .andExpect(jsonPath("$.description", is("Integral")))
                .andExpect(jsonPath("$.size", is("1L")));
    }

    @Test
    public void givenInvalidId_whenReplace_shouldReturnCreated() throws Exception {
        //given
        var dto = ProductDto.builder().id(10L).code("MILK").description("Integral").size("1L").build();
        given(service.get(anyLong())).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/pantryservice/products/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenValidPantryId_whenDelete_shouldReturnNoContent() throws Exception {
        //given
        doNothing().when(service).delete(anyLong());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/pantryservice/products/1"))
                .andExpect(status().isNoContent());
    }

}
