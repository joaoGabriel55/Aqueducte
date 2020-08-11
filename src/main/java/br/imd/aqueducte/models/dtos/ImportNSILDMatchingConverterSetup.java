package br.imd.aqueducte.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ImportNSILDMatchingConverterSetup {
    private String primaryField;
    private List<String> contextLinks;
    private LinkedHashMap<String, MatchingConverterSetup> matchingConverterSetup;
    private List<Map<String, Object>> dataCollection;
}
