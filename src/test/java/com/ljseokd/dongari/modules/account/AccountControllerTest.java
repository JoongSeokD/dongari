package com.ljseokd.dongari.modules.account;

import com.ljseokd.dongari.infra.AbstractContainerBaseTest;
import com.ljseokd.dongari.infra.MockMvcTest;
import com.ljseokd.dongari.infra.WithAccount;
import com.ljseokd.dongari.infra.mail.EmailMessage;
import com.ljseokd.dongari.infra.mail.EmailService;
import com.ljseokd.dongari.modules.account.form.SignUpForm;
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

    @Autowired AccountService accountService;

    @MockBean
    EmailService emailService;

    @DisplayName("회원 가입 폼")
    @Test
    void sign_up_form() throws Exception {
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

    @DisplayName("이메일 인증 화면")
    @Test
    @WithAccount("ljseokd")
    void check_email() throws Exception {
        mockMvc.perform(get("/check-email"))
                .andExpect(model().attributeExists("email"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/check-email"));
    }

    @DisplayName("이메일 인증 성공")
    @Test
    void check_email_success() throws Exception {

        SignUpForm ljseokd = SignUpForm.builder()
                .nickname("ljseokd")
                .email("ljseokd@gmail.com")
                .password("12345678")
                .build();
        accountService.processNewAccount(ljseokd);
        Account account = accountRepository.findByEmail(ljseokd.getEmail());


        mockMvc.perform(get("/check-email-token")
                .param("email", account.getEmail())
                .param("token", account.getEmailCheckToken()))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("numberOfUser"))
            .andExpect(model().attributeExists("nickname"))
            .andExpect(authenticated().withUsername("ljseokd"));
    }

    @DisplayName("이메일 인증 실패")
    @Test
    void check_email_fail() throws Exception {

        mockMvc.perform(get("/check-email-token")
                .param("email", "email@gmail.com")
                .param("token", "1234"))
            .andExpect(status().isOk())
            .andExpect(model().hasErrors())
            .andExpect(unauthenticated());
    }

}