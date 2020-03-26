package br.imd.aqueducte.entitiesrelationship.business;

import br.imd.aqueducte.models.entitiesrelationship.dtos.PropertyNGSILD;
import br.imd.aqueducte.models.entitiesrelationship.enums.EntitiesRelationshipSetupStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntitiesRelationshipSetupValidate {

    public boolean validatePropertyType(List<PropertyNGSILD> propertiesLinked) {
        return propertiesLinked.get(0).getType().equals(propertiesLinked.get(1).getType());
    }

    public boolean validateStatusParam(String status) {
        return status.equalsIgnoreCase(EntitiesRelationshipSetupStatus.DONE.name()) ||
                status.equalsIgnoreCase(EntitiesRelationshipSetupStatus.PENDING.name());
    }
}
