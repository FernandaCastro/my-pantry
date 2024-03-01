package com.fcastro.account;

import com.fcastro.account.account.AccountController;
import com.fcastro.account.account.AccountService;
import com.fcastro.account.accountGroupMember.AccountGroupMemberController;
import com.fcastro.account.accountGroupMember.AccountGroupMemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = {AccountController.class, AccountGroupMemberController.class})
@AutoConfigureMockMvc
@ComponentScan(basePackages = {"com.fcastro.security"}) //Otherwise, it doesn't load SecurityConfig classes
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @MockBean
    AccountGroupMemberService accountGroupMemberService;

    @Test
    public void testUnauthenticatedAccessToProtectedEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user-info"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

//    @Test
//    @WithMockUser(roles = {"ADMIN"})
//    public void testAdminAccess() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/accountGroups/10/members"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }

//    @Test
//    @WithMockUser(roles = {"USER"})
//    public void testForbiddenAdminAccess() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/accountGroups/10/members"))
//                .andExpect(MockMvcResultMatchers.status().isForbidden());
//    }

//    @Test
//    @WithMockUser(roles = {"USER"})
//    public void testUserAccess() throws Exception {
//
//        given(accountService.getUser(anyString())).willReturn(Optional.of(AccountDto.builder().build()));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/auth/user-info"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
}
