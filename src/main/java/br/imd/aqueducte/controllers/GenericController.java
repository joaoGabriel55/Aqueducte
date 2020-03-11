package br.imd.aqueducte.controllers;

import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;

public class GenericController {

    @ModelAttribute("user-id")
    public String getIdUser(HttpServletRequest request) {
        return (String) request.getAttribute("user-id");
    }

    public boolean checkUserIdIsEmpty(String userId) {
        return userId == null || userId.trim().equals("");
    }

    public List<LinkedHashMap<String, Object>> getSamples(List<LinkedHashMap<String, Object>> data) {
        try {
            List<LinkedHashMap<String, Object>> samples = data.subList(0, 10);
            return samples;
        } catch (IndexOutOfBoundsException e) {
            return data;
        } catch (IllegalArgumentException e) {
            return data;
        }
    }
}
