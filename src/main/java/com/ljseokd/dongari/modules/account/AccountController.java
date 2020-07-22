package com.ljseokd.dongari.modules.account;

import com.ljseokd.dongari.modules.account.form.EmailForm;
import com.ljseokd.dongari.modules.account.form.SignUpForm;
import com.ljseokd.dongari.modules.account.security.CurrentAccount;
import com.ljseokd.dongari.modules.account.validator.EmailValidator;
import com.ljseokd.dongari.modules.account.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final SignUpFormValidator signUpFormValidator;
    private final EmailValidator emailValidator;

    @InitBinder("signUpForm")
    public void signUpInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }

    @InitBinder("emailForm")
    public void emailInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(emailValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm,
                               Errors errors, Model model){
        if (errors.hasErrors()){
            return "account/sign-up";
        }
        accountService.processNewAccount(signUpForm);

        return "redirect:/";
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentAccount Account account, Model model){
        model.addAttribute("email", account.getEmail());
        return "account/check-email";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(@Valid EmailForm emailForm,
            Errors errors, Model model){

        String view = "account/checked-email";

        if (errors.hasErrors()){
            return view;
        }

        Account account = accountRepository.findByEmail(emailForm.getEmail());
        accountService.completeSignUp(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());

        return view;
    }
}
