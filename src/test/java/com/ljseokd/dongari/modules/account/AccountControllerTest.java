package com.ljseokd.dongari.modules.account;

import com.ljseokd.dongari.infra.AbstractContainerBaseTest;
import com.ljseokd.dongari.infra.MockMvcTest;
import com.ljseokd.dongari.infra.mail.EmailMessage;
import com.ljseokd.dongari.infra.mail.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class AccountControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @MockBean
    EmailService emailService;

    @DisplayName("회원 가입 폼")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원 가입 성공")
    @Test
    void signUpSubmit_success() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "ljseokd")
                .param("email","ljseokd@gmail.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("signUpForm"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("ljseokd"));

        Account ljseokd = accountRepository.findByEmail("ljseokd@gmail.com");
        assertNotNull(ljseokd);
        assertNotEquals(ljseokd.getPassword(), "12345678");
        assertTrue(accountRepository.existsByEmail("ljseokd@gmail.com"));
        then(emailService).should().sendEmail(any (EmailMessage.class));
    }

    @DisplayName("회원 가입 실패")
    @Test
    void signUpSubmit_fail() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "ljseokd")
                .param("email","ljseokd,,")
                .param("password", "123456")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

}