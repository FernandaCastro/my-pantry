package com.fcastro.pantryInventory.pantry;

import com.fcastro.pantryInventory.JsonUtil;
import com.fcastro.pantryInventory.pantryItem.PantryItemDto;
import com.fcastro.pantryInventory.product.ProductDto;
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

@WebMvcTest(controllers = PantryController.class)
public class PantryControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PantryService pantryService;

    @Test
    public void givenValidPantryId_whenGet_shouldReturnOk() throws Exception {
        //given
        var products = new ArrayList<PantryItemDto>();
        products.add(PantryItemDto.builder()
                .pantry(PantryDto.builder().id(1L).build())
                .product(ProductDto.builder().id(1L).build())
                .idealQty(5)
                .currentQty(5).build());

        var pantry = PantryDto.builder().id(1L).name("Base Inventory").isActive(true).type("R").products(products).build();

        given(pantryService.get(anyLong())).willReturn(Optional.of(pantry));

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantry/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Base Inventory")))
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.type", is("R")))
                .andExpect(jsonPath("$.products", hasSize(1)));
    }

    @Test
    public void whenGetAll_shouldReturnOk() throws Exception {
        //given
        var list = new ArrayList<PantryDto>();
        list.add(PantryDto.builder().id(1L).name("Base Inventory").isActive(true).type("R").build());
        list.add(PantryDto.builder().id(2L).name("Base Inventory").isActive(true).type("R").build());

        given(pantryService.getAll()).willReturn(list);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void givenInvalidPantryId_whenGet_shouldReturnNotFound() throws Exception {
        //given
        given(pantryService.get(anyLong())).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantry/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenNewPantry_whenCreate_shouldReturnCreated() throws Exception {
        //given
        var pantry = PantryDto.builder().id(10L).name("New Base Inventory").isActive(true).type("R").build();

        given(pantryService.save(any(PantryDto.class))).willReturn(pantry);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/pantry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(pantry)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("New Base Inventory")))
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.type", is("R")));
    }

    @Test
    public void givenValidPantryId_whenReplace_shouldReturnCreated() throws Exception {
        //given
        var pantry = PantryDto.builder().id(10L).name("Updated Base Inventory").isActive(true).type("R").build();

        given(pantryService.get(anyLong())).willReturn(Optional.of(pantry));
        given(pantryService.save(any(PantryDto.class))).willReturn(pantry);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/pantry/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(pantry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Updated Base Inventory")))
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.type", is("R")));
    }

    @Test
    public void givenInvalidPantryId_whenReplace_shouldReturnCreated() throws Exception {
        //given
        var pantry = PantryDto.builder().id(10L).name("Updated Base Inventory").isActive(true).type("R").build();
        given(pantryService.get(anyLong())).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/pantry/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(pantry)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenValidPantryId_whenDelete_shouldReturnNoContent() throws Exception {
        //given
        doNothing().when(pantryService).delete(anyLong());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/pantry/1"))
                .andExpect(status().isNoContent());
    }

}
