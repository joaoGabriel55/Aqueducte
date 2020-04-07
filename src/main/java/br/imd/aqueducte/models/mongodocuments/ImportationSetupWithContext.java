package br.imd.aqueducte.models.mongodocuments;

import br.imd.aqueducte.models.dtos.MatchingConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Document
@Getter
@Setter
@NoArgsConstructor
public class ImportationSetupWithContext extends ImportationSetup {

    @NotBlank(message = "Context source(s) is required")
    private Map<String, String> contextSources;

    @NotBlank(message = "Matching config list required")
    private List<MatchingConfig> matchingConfigList;

}

