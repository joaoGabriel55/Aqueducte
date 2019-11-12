package br.imd.aqueducte.dto;

import br.imd.aqueducte.models.MatchingConfig;

import java.util.List;
import java.util.Map;

public class MatchingConfigDTO {
    private Map<String, List<MatchingConfig>> matchingConfigContent;
    private Map<String, List<Map<String, Object>>> dataContentForNGSILDConversion;

    public Map<String, List<MatchingConfig>> getMatchingConfigContent() {
        return matchingConfigContent;
    }

    public void setMatchingConfigContent(Map<String, List<MatchingConfig>> matchingConfigContent) {
        this.matchingConfigContent = matchingConfigContent;
    }

    public Map<String, List<Map<String, Object>>> getDataContentForNGSILDConversion() {
        return dataContentForNGSILDConversion;
    }

    public void setDataContentForNGSILDConversion(Map<String, List<Map<String, Object>>> dataContentForNGSILDConversion) {
        this.dataContentForNGSILDConversion = dataContentForNGSILDConversion;
    }
}
