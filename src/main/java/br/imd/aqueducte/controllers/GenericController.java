package br.imd.aqueducte.controllers;

import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

public class GenericController {

    @ModelAttribute("user-id")
    public String getIdUser(HttpServletRequest request) {
        return (String) request.getAttribute("user-id");
    }

    public boolean checkUserIdIsEmpty(String userId) {
        return userId == null || userId.trim().equals("");
    }
}
