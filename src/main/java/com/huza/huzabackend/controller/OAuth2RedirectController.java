package com.huza.huzabackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class OAuth2RedirectController {

    @GetMapping("/login")
    public RedirectView loginWithGoogle() {
        return new RedirectView("/oauth2/authorization/google");
    }
}
