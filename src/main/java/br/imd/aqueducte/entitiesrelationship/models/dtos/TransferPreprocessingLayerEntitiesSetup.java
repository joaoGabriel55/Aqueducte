package br.imd.aqueducte.entitiesrelationship.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransferPreprocessingLayerEntitiesSetup {

    private String preProcessingLayer1;
    private String tempPropertyLayer1;

    private String preProcessingLayer2;
    private String tempPropertyLayer2;

}
