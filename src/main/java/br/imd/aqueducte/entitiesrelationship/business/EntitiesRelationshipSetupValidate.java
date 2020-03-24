package br.imd.aqueducte.entitiesrelationship.business;

import br.imd.aqueducte.models.entitiesrelationship.dtos.PropertyNGSILD;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntitiesRelationshipSetupValidate {

    public boolean validatePropertyType(List<PropertyNGSILD> propertiesLinked) {
        return propertiesLinked.get(0).getType().equals(propertiesLinked.get(1).getType());
    }
}
