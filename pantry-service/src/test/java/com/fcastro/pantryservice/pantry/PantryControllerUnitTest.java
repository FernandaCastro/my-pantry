package com.fcastro.pantryservice.pantry;

import com.fcastro.pantryservice.JsonUtil;
import com.fcastro.pantryservice.pantryitem.PantryItemDto;
import com.fcastro.pantryservice.product.ProductDto;
import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.jwt.JWTRequestFilter;
import com.fcastro.security.core.model.*;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PantryController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {JWTRequestFilter.class})})
@EnableConfigurationProperties(value = SecurityPropertiesConfig.class)
@AutoConfigureDataJpa
//@AutoConfigureMockMvc(addFilters = false)
public class PantryControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PantryService pantryService;

    @Test
    @WithMockUser
    public void givenValidPantryId_whenGet_shouldReturnOk() throws Exception {
        //given
        var products = new ArrayList<PantryItemDto>();
        products.add(PantryItemDto.builder()
                .pantry(PantryDto.builder().id(1L).build())
                .product(ProductDto.builder().id(1L).build())
                .idealQty(5)
                .currentQty(5).build());

        var pantry = PantryDto.builder().id(1L).name("Base Inventory").isActive(true).type("R").items(products).build();

        given(pantryService.getEmbeddingAccountGroup(anyString(), anyLong())).willReturn(Optional.of(pantry));

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Base Inventory")))
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.type", is("R")))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    @WithMockUser
    public void whenGetAll_shouldReturnOk() throws Exception {
        //given
        var list = new ArrayList<PantryDto>();
        list.add(PantryDto.builder().id(1L).name("Base Inventory").isActive(true).type("R").build());
        list.add(PantryDto.builder().id(2L).name("Base Inventory").isActive(true).type("R").build());

        given(pantryService.getAll(anyString())).willReturn(list);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantries/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    public void givenInvalidPantryId_whenGet_shouldReturnNotFound() throws Exception {
        //given
        given(pantryService.get(anyLong())).willReturn(Optional.empty());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantries/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenNewPantry_whenCreate_shouldReturnCreated() throws Exception {
        //given
        var pantry = PantryDto.builder().id(10L).name("New Base Inventory").isActive(true).type("R")
                .accountGroup(AccountGroupDto.builder().id(1L).build()).build();

        given(pantryService.save(any(PantryDto.class))).willReturn(pantry);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/pantries")
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
        mockMvc.perform(MockMvcRequestBuilders.put("/pantries/10")
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
        mockMvc.perform(MockMvcRequestBuilders.put("/pantries/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(pantry)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenValidPantryId_whenDelete_shouldReturnNoContent() throws Exception {
        //given
        var access = AccessControlDto.builder()
                .clazz("pantry")
                .clazzId(1L)
                .accountGroup(AccountGroupDto.builder().id(10L).build())
                .build();
        var permissions = List.of(PermissionDto.builder().name("list_pantry").build());
        var role = RoleDto.builder().permissions(permissions).build();
        var member = AccountGroupMemberDto.builder().accountGroupId(10L).accountId(1L).role(role).build();

        doNothing().when(pantryService).delete(anyLong());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/pantries/1"))
                .andExpect(status().isNoContent());
    }
}
