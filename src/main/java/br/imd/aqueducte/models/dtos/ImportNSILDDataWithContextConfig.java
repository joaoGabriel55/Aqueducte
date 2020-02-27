package br.imd.aqueducte.models.dtos;

import java.util.LinkedHashMap;
import java.util.List;

public class ImportNSILDDataWithContextConfig {

    // TODO: Think about how put more than one context link.
    private String contextLink;
    private List<MatchingConfig> matchingConfigContent;
    private List<LinkedHashMap<String, Object>> dataContentForNGSILDConversion;

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

    public List<LinkedHashMap<String, Object>> getDataContentForNGSILDConversion() {
        return dataContentForNGSILDConversion;
    }

    public void setDataContentForNGSILDConversion(List<LinkedHashMap<String, Object>> dataContentForNGSILDConversion) {
        this.dataContentForNGSILDConversion = dataContentForNGSILDConversion;
    }
}
