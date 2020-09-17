package br.imd.aqueducte.controllers;

import java.util.LinkedHashMap;
import java.util.List;

public class GenericController {

    public List<LinkedHashMap<String, Object>> getSamples(List<LinkedHashMap<String, Object>> data) {
        try {
            return data.subList(0, 10);
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            return data;
        }
    }
}
