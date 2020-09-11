package br.imd.aqueducte.models.mongodocuments.external_app_config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ServiceConfig {

    @NotBlank
    private String url;

    @NotBlank
    private String httpVerb;

    private Map<String, Object> headers;

    @NotBlank
    private Integer returnStatusCode;

}
