package com.fcastro.pantryservice.product;

import com.fcastro.app.model.ProductDto;
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
    public void givenValidId_whenGet_shouldReturnOk() throws Exception {
        //given
        var dto = ProductDto.builder().id(1).code("MILK").description("Integral").size("1L").build();

        given(service.get(anyLong())).willReturn(Optional.of(dto));

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("MILK")))
                .andExpect(jsonPath("$.description", is("Integral")))
                .andExpect(jsonPath("$.size", is("1L")));
    }

    @Test
    public void givenInvalidId_whenGet_shouldReturnNoContent() throws Exception {
        //given
        given(service.get(anyLong())).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetAll_shouldReturnOk() throws Exception {
        //given
        var list = new ArrayList<ProductDto>();
        list.add(ProductDto.builder().id(1).code("MILK").description("Integral").size("1L").build());
        list.add(ProductDto.builder().id(2).code("VOLLMILK").description("Integral").size("2L").build());

        given(service.getAll("MILK")).willReturn(list);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("searchParam", "MILK")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void givenNewDto_whenCreate_shouldReturnCreated() throws Exception {
        //given
        var dto = ProductDto.builder().id(1).code("MILK").description("Integral").size("1L").build();

        given(service.save(any(ProductDto.class))).willReturn(dto);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
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
        var dto = ProductDto.builder().id(1).code("MILK").description("Integral").size("1L").build();

        given(service.get(anyLong())).willReturn(Optional.of(dto));
        given(service.save(any(ProductDto.class))).willReturn(dto);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/products/1")
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
        var dto = ProductDto.builder().id(10).code("MILK").description("Integral").size("1L").build();
        given(service.get(anyLong())).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/products/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenValidPantryId_whenDelete_shouldReturnNoContent() throws Exception {
        //given
        doNothing().when(service).delete(anyLong());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/1"))
                .andExpect(status().isNoContent());
    }

}
