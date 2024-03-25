package com.fcastro.pantryservice.pantry;

import com.fcastro.app.model.ProductDto;
import com.fcastro.pantryservice.JsonUtil;
import com.fcastro.pantryservice.pantryitem.PantryItemDto;
import com.fcastro.security.accesscontrol.AccessControlService;
import com.fcastro.security.authorization.AuthorizationHandler;
import com.fcastro.security.authorization.CustomAuthorizationManager;
import com.fcastro.security.authorization.CustomMethodSecurityExpressionHandler;
import com.fcastro.security.authorization.CustomMethodSecurityExpressionRoot;
import com.fcastro.security.config.SecurityConfig;
import com.fcastro.security.core.config.SecurityPropertiesConfig;
import com.fcastro.security.core.handler.CustomAccessDeniedHandler;
import com.fcastro.security.core.handler.CustomAuthenticationEntryPointHandler;
import com.fcastro.security.core.jwt.JWTHandler;
import com.fcastro.security.core.model.AccessControlDto;
import com.fcastro.security.core.model.AccountGroupMemberDto;
import com.fcastro.security.core.model.PermissionDto;
import com.fcastro.security.core.model.RoleDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
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
        includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                SecurityConfig.class, JWTHandler.class,
                AuthorizationManagerBeforeMethodInterceptor.class,
                CustomAccessDeniedHandler.class,
                CustomAuthenticationEntryPointHandler.class,
                CustomAuthorizationManager.class,
                CustomMethodSecurityExpressionHandler.class,
                CustomMethodSecurityExpressionRoot.class
        })})
@ContextConfiguration(initializers = {ConfigDataApplicationContextInitializer.class})
@EnableConfigurationProperties(value = SecurityPropertiesConfig.class)
@SecurityTestExecutionListeners
//@Import({com.fcastro.security.core.jwt.JWTHandler.class})
//         com.fcastro.security.config.SecurityConfig.class,
//        org.springframework.aop.Pointcut.class,
//        org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor.class,
//        com.fcastro.security.core.handler.CustomAccessDeniedHandler.class,
//        com.fcastro.security.core.handler.CustomAuthenticationEntryPointHandler.class,
//        com.fcastro.security.authorization.CustomAuthorizationManager.class,
//        com.fcastro.security.authorization.CustomMethodSecurityExpressionHandler.class,
//        com.fcastro.security.authorization.CustomMethodSecurityExpressionRoot.class
//})
@AutoConfigureDataJpa
public class PantryControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PantryService pantryService;

    @MockBean
    private AuthorizationHandler authorizationService;

    @MockBean
    private AccessControlService accessControlService;

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

        given(pantryService.get(anyLong())).willReturn(Optional.of(pantry));

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

        given(pantryService.getAll()).willReturn(list);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/pantries"))
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
    @WithMockUser
    public void givenNewPantry_whenCreate_shouldReturnCreated() throws Exception {
        //given
        var pantry = PantryDto.builder().id(10L).name("New Base Inventory").isActive(true).type("R").build();

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
    @WithMockUser
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
    @WithMockUser
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
    @WithMockUser(roles = {}, authorities = {})
    public void givenValidPantryId_whenDelete_shouldReturnNoContent() throws Exception {
        //given
        var access = AccessControlDto.builder()
                .clazz("pantry")
                .clazzId(1L)
                .accountGroupId(10L)
                .build();
        var permissions = List.of(PermissionDto.builder().name("list_pantry").build());
        var role = RoleDto.builder().permissions(permissions).build();
        var member = AccountGroupMemberDto.builder().accountGroupId(10L).accountId(1L).role(role).build();

        given(accessControlService.get(anyString(), anyLong())).willReturn(access);
        given(authorizationService.getGroupMember(anyLong(), anyString())).willReturn(member);
        doNothing().when(pantryService).delete(anyLong());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/pantries/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {""})
    void getWhenNoUserAuthorityThenForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/pantries"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getWhenNoUserAuthenticationThenUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/pantries"))
                .andExpect(status().isUnauthorized());
    }

}
