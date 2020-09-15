package br.imd.aqueducte.controllers;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;

import static br.imd.aqueducte.config.PropertiesParams.USE_USER_ID;

public class GenericController {

    protected String idUser;

    public boolean checkUserIdIsEmpty(HttpServletRequest request) {

        if (USE_USER_ID)
            this.idUser = (String) request.getAttribute("user-id");
        else
            this.idUser = "none";

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
