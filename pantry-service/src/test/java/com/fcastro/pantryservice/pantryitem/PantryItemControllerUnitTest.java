package com.fcastro.pantryservice.pantryitem;

import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.pantryservice.JsonUtil;
import com.fcastro.pantryservice.exception.PantryNotActiveException;
import com.fcastro.pantryservice.exception.QuantityNotAvailableException;
import com.fcastro.pantryservice.pantry.PantryDto;
import com.fcastro.pantryservice.product.ProductDto;
import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.jwt.JWTRequestFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PantryItemController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {JWTRequestFilter.class})})
@EnableConfigurationProperties(value = SecurityPropertiesConfig.class)
@AutoConfigureDataJpa
public class PantryItemControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PantryItemService service;

    @Test
    public void givenValidIds_whenGet_shouldReturnOk() throws Exception {
        //given
        var dto = PantryItemDto.builder()
                .pantry(PantryDto.builder().id(1L).build())
                .product(ProductDto.builder().id(1L).build())
                .idealQty(5)
                .currentQty(5).build();

        given(service.get(anyLong(), anyLong())).willReturn(Optional.of(dto));

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantries/1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pantry.id", is(1)))
                .andExpect(jsonPath("$.product.id", is(1)))
                .andExpect(jsonPath("$.idealQty", is(5)))
                .andExpect(jsonPath("$.currentQty", is(5)));
    }

    @Test
    public void whenGetAll_shouldReturnOk() throws Exception {
        //given
        var list = new ArrayList<PantryItemDto>();
        list.add(PantryItemDto.builder()
                .pantry(PantryDto.builder().id(1L).build())
                .product(ProductDto.builder().id(1L).build())
                .idealQty(5)
                .currentQty(5).build());
        list.add(PantryItemDto.builder()
                .pantry(PantryDto.builder().id(1L).build())
                .product(ProductDto.builder().id(2L).build())
                .idealQty(5)
                .currentQty(5).build());

        given(service.getAll(anyLong())).willReturn(list);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantries/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void givenInvalidIds_whenGet_shouldReturnNotFound() throws Exception {
        //given
        given(service.get(anyLong(), anyLong())).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantries/1/items/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void givenNewIds_whenCreate_shouldReturnCreated() throws Exception {
        //given
        var dto = PantryItemDto.builder()
                .pantry(PantryDto.builder().id(1L).build())
                .product(ProductDto.builder().id(1L).build())
                .idealQty(5)
                .currentQty(5).build();

        given(service.get(anyLong(), anyLong())).willReturn(Optional.empty());
        given(service.create(any(PantryItemDto.class))).willReturn(dto);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/pantries/1/items")
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
        var dto = PantryItemDto.builder()
                .pantry(PantryDto.builder().id(1L).build())
                .product(ProductDto.builder().id(1L).build())
                .idealQty(5)
                .currentQty(5).build();

        given(service.get(1L, 1L)).willReturn(Optional.of(dto));
        given(service.update(any(PantryItemDto.class))).willReturn(dto);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/pantries/1/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pantry.id", is(1)))
                .andExpect(jsonPath("$.product.id", is(1)))
                .andExpect(jsonPath("$.idealQty", is(5)))
                .andExpect(jsonPath("$.currentQty", is(5)));
    }

    @Test
    public void givenInvalidPantryId_whenReplace_shouldReturnNotFound() throws Exception {
        //given
        var dto = PantryItemDto.builder()
                .pantry(PantryDto.builder().id(1L).build())
                .product(ProductDto.builder().id(1L).build())
                .idealQty(5)
                .currentQty(5).build();
        given(service.get(1L, 1L)).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/pantries/1/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenValidPantryId_whenDelete_shouldReturnNoContent() throws Exception {
        //given
        doNothing().when(service).delete(1L, 1L);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/pantries/1/items/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenValidIds_whenConsumeProduct_shouldReturnOk() throws Exception {
        //given
        var list = new ArrayList<PantryItemConsumedDto>();
        list.add(PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(1).build());
        given(service.consumePantryItem(anyLong(), any(List.class))).willReturn(null);
        given(service.getAll(anyLong())).willReturn(List.of(PantryItemDto.builder().build()));

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/pantries/1/items/consume")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(list)))
                .andExpect(status().isOk());
    }

    @Test
    public void givenInvalidIds_whenConsumeProduct_shouldReturnNotFound() throws Exception {
        //given
        var list = new ArrayList<PantryItemConsumedDto>();
        list.add(PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(1).build());
        Mockito.doThrow(ResourceNotFoundException.class).when(service).consumePantryItem(anyLong(), any(List.class));

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/pantries/1/items/consume")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(list)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenInvalidQuantity_whenConsumeProduct_shouldReturnBadRequest() throws Exception {
        //given
        var dto = PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(10).build();
        Mockito.doThrow(QuantityNotAvailableException.class).when(service).consumePantryItem(any(PantryItemConsumedDto.class));

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/pantries/10/items/consume")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenPantryNotActive_whenConsumeProduct_shouldReturnBadRequest() throws Exception {
        //given
        var dto = PantryItemConsumedDto.builder().pantryId(1L).productId(1L).qty(10).build();
        Mockito.doThrow(PantryNotActiveException.class).when(service).consumePantryItem(any(PantryItemConsumedDto.class));

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/pantries/10/items/consume")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isBadRequest());
    }

}
