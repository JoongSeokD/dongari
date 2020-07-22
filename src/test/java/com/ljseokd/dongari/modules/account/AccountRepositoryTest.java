package com.ljseokd.dongari.modules.account;

import com.ljseokd.dongari.infra.AbstractContainerBaseTest;
import com.ljseokd.dongari.infra.MockMvcTest;
import com.ljseokd.dongari.infra.WithAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@MockMvcTest
class AccountRepositoryTest extends AbstractContainerBaseTest {

    @Autowired AccountRepository accountRepository;

    @Test
    @WithAccount("ljseokd")
    void findByEmail(){
        Account ljseokd = accountRepository.findByEmail("ljseokd@gmail.com");
        assertNotNull(ljseokd);
    }

    @Test
    @WithAccount("ljseokd")
    void findByNickname(){
        Account ljseokd = accountRepository.findByNickname("ljseokd");
        assertNotNull(ljseokd);
    }

    @Test
    @WithAccount("ljseokd")
    void existsByEmail(){
        assertTrue(accountRepository.existsByEmail("ljseokd@gmail.com"));
    }

    @Test
    @WithAccount("ljseokd")
    void existsByNickname(){
        assertTrue(accountRepository.existsByNickname("ljseokd"));
    }

}