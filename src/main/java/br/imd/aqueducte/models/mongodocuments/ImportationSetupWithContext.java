package br.imd.aqueducte.models.mongodocuments;

import br.imd.aqueducte.models.pojos.MatchingConfig;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Document
public class ImportationSetupWithContext extends ImportationSetup {

    public ImportationSetupWithContext() {
        super();
    }

    @NotBlank(message = "Context file name required")
    private String contextFileName;

    @NotBlank(message = "Context file link required")
    private String contextFileLink;

    @NotBlank(message = "Matching config list required")
    private List<MatchingConfig> matchingConfigList;

    public String getContextFileName() {
        return contextFileName;
    }

    public void setContextFileName(String contextFileName) {
        this.contextFileName = contextFileName;
    }

    public String getContextFileLink() {
        return contextFileLink;
    }

    public void setContextFileLink(String contextFileLink) {
        this.contextFileLink = contextFileLink;
    }

    public List<MatchingConfig> getMatchingConfigList() {
        return matchingConfigList;
    }

    public void setMatchingConfigList(List<MatchingConfig> matchingConfigList) {
        this.matchingConfigList = matchingConfigList;
    }
}
