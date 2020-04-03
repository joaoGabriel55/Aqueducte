package br.imd.aqueducte.controllers;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;

public class GenericController {

    protected String idUser;

    public boolean checkUserIdIsEmpty(HttpServletRequest request) {
        this.idUser = (String) request.getAttribute("user-id");
        return this.idUser == null || this.idUser.trim().equals("");
    }

    public List<LinkedHashMap<String, Object>> getSamples(List<LinkedHashMap<String, Object>> data) {
        try {
            return data.subList(0, 10);
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            return data;
        }
    }
}
