package com.ljseokd.dongari.modules.account.validator;


import com.ljseokd.dongari.modules.account.Account;
import com.ljseokd.dongari.modules.account.AccountRepository;
import com.ljseokd.dongari.modules.account.form.EmailForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class EmailValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(EmailForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        EmailForm emailForm = (EmailForm) o;
        Account account = accountRepository.findByEmail(emailForm.getEmail());
        if (!isAccountNotNull(account)){
            errors.rejectValue("email", "wrong.email", "잘못된 정보입니다.");
        }
        if (isAccountNotNull(account) && !account.isValidToken(emailForm.getToken())){
            errors.rejectValue("token", "wrong.token", "잘못된 정보입니다.");
        }

    }

    private boolean isAccountNotNull(Account account) {
        return account != null;
    }
}
