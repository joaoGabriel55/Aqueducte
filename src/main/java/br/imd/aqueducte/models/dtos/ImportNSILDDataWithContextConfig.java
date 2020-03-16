package br.imd.aqueducte.models.dtos;

import java.util.List;
import java.util.Map;

public class ImportNSILDDataWithContextConfig {

    private List<String> contextLinks;
    private List<MatchingConfig> matchingConfigContent;
    private List<Map<String, Object>> dataContentForNGSILDConversion;

    public List<String> getContextLinks() {
        return contextLinks;
    }

    public void setContextLink(List<String> contextLinks) {
        this.contextLinks = contextLinks;
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
