package br.imd.aqueducte.models.documents;

import br.imd.aqueducte.models.ImportationSetup;
import br.imd.aqueducte.models.MatchingConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportationSetupWithContext extends ImportationSetup {

    public ImportationSetupWithContext() {
        super();
    }

    @NotBlank(message = "Context file name required")
    private String contextFileName;

    @NotBlank(message = "Matching config list required")
    private List<MatchingConfig> matchingConfigList;

    public String getContextFileName() {
        return contextFileName;
    }

    public void setContextFileName(String contextFileName) {
        this.contextFileName = contextFileName;
    }

    public List<MatchingConfig> getMatchingConfigList() {
        return matchingConfigList;
    }

    public void setMatchingConfigList(List<MatchingConfig> matchingConfigList) {
        this.matchingConfigList = matchingConfigList;
    }
}
