package br.imd.aqueducte.models.mongodocuments;

import br.imd.aqueducte.models.dtos.MatchingConfig;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Document
public class ImportationSetupWithContext extends ImportationSetup {

    public ImportationSetupWithContext() {
        super();
    }

    @NotBlank(message = "Context source(s) is required")
    private Map<String, String> contextSources;

    @NotBlank(message = "Matching config list required")
    private List<MatchingConfig> matchingConfigList;

    public Map<String, String> getContextSources() {
        return contextSources;
    }

    public void setContextSources(Map<String, String> contextSources) {
        this.contextSources = contextSources;
    }

    public List<MatchingConfig> getMatchingConfigList() {
        return matchingConfigList;
    }

    public void setMatchingConfigList(List<MatchingConfig> matchingConfigList) {
        this.matchingConfigList = matchingConfigList;
    }
}
