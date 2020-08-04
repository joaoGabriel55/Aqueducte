package br.imd.aqueducte.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ImportNSILDContextDataConfig {
    private String primaryField;
    private List<String> contextLinks;
    private List<MatchingConfig> matchingConfigContent;
    private List<Map<String, Object>> dataContentForNGSILDConversion;

}
