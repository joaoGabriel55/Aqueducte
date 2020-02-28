package br.imd.aqueducte.models.dtos;

import java.util.List;
import java.util.Map;

public class ImportNSILDDataWithContextConfig {

    // TODO: Think about how put more than one context link.
    private String contextLink;
    private List<MatchingConfig> matchingConfigContent;
    private List<Map<String, Object>> dataContentForNGSILDConversion;

    public String getContextLink() {
        return contextLink;
    }

    public void setContextLink(String contextLink) {
        this.contextLink = contextLink;
    }

    public List<MatchingConfig> getMatchingConfigContent() {
        return matchingConfigContent;
    }

    public void setMatchingConfigContent(List<MatchingConfig> matchingConfigContent) {
        this.matchingConfigContent = matchingConfigContent;
    }

    public List<Map<String, Object>> getDataContentForNGSILDConversion() {
        return dataContentForNGSILDConversion;
    }

    public void setDataContentForNGSILDConversion(List<Map<String, Object>> dataContentForNGSILDConversion) {
        this.dataContentForNGSILDConversion = dataContentForNGSILDConversion;
    }
}
