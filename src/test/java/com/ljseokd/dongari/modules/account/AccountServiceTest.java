package com.ljseokd.dongari.modules.account;

import com.ljseokd.dongari.infra.AbstractContainerBaseTest;
import com.ljseokd.dongari.infra.MockMvcTest;
import com.ljseokd.dongari.infra.WithAccount;
import com.ljseokd.dongari.modules.account.form.SignUpForm;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@MockMvcTest
class AccountServiceTest extends AbstractContainerBaseTest {

    @Autowired AccountRepository accountRepository;
    @Autowired AccountService accountService;
    @Autowired
    EntityManager em;

    @DisplayName("회원 가입 성공")
    @Test
    void processNewAccount() throws Exception {
        SignUpForm ljseokd = SignUpForm.builder()
                .nickname("ljseokd")
                .email("ljseokd@gmail.com")
                .password("12345678")
                .build();

        accountService.processNewAccount(ljseokd);
        Account newAccount = accountRepository.findByNickname("ljseokd");
        assertNotNull(newAccount);
    }

    @DisplayName("회원 가입 실패")
    @Test
    void processNewAccount_fail() throws Exception {
        SignUpForm ljseokd = SignUpForm.builder()
                .nickname("ljseokd")
                .email("ljseokd@gmail.com")
                .password("12345678")
                .build();

        accountService.processNewAccount(ljseokd);
        accountService.processNewAccount(ljseokd);
        assertThrows(PersistenceException.class, ()-> em.flush());
    }

    @Test
    @WithAccount("ljseokd")
    void loadUserByUsername_success() throws Exception {
        UserDetails ljseokd = accountService.loadUserByUsername("ljseokd");
        assertNotNull(ljseokd);
    }
    @Test
    @WithAccount("ljseokd")
    void loadUserByUsername_fail() throws Exception {
        assertThrows(UsernameNotFoundException.class, ()-> accountService.loadUserByUsername("ljseokdasd"));
    }

}