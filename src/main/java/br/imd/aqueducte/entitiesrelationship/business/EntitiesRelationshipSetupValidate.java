package br.imd.aqueducte.entitiesrelationship.business;

import br.imd.aqueducte.models.entitiesrelationship.dtos.LayerSetup;
import br.imd.aqueducte.models.entitiesrelationship.dtos.PropertyNGSILD;
import br.imd.aqueducte.models.entitiesrelationship.enums.EntitiesRelationshipSetupStatus;
import br.imd.aqueducte.models.entitiesrelationship.enums.RelationshipType;
import br.imd.aqueducte.models.entitiesrelationship.mongodocuments.EntitiesRelationshipSetup;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EntitiesRelationshipSetupValidate {

    public List<String> validateEntitiesRelationshipSetup(EntitiesRelationshipSetup setup) {
        List<String> errors = new ArrayList<>();
        if (!validatePropertyType(setup.getPropertiesLinked()))
            errors.add("Types of Properties not equals");
        if (!validateLayerSetup(setup.getLayerSetup()))
            errors.add("Layer path and name is required");
        if (!validateContextSources(setup.getLayerSetup()))
            errors.add("Context Source is required");
        if (!validatePropertiesLinked(setup.getPropertiesLinked()))
            errors.add("Property Linked must be contain name or entity id");
        if (!validateRelationshipType(setup.getRelationshipType()))
            errors.add("Relationship type is invalid. Must be: \"ONE_TO_ONE\", \"ONE_TO_MANY\" or \"MANY_TO_MANY\"");
        if (!validateRelationshipsProperties(setup))
            errors.add("Relationships are invalid");
        if (!validateStatus(setup.getStatus()))
            errors.add("Status is invalid");
        return errors;
    }

    public boolean validatePropertyType(List<PropertyNGSILD> propertiesLinked) {
        return propertiesLinked.get(0).getType().equals(propertiesLinked.get(1).getType());
    }

    private boolean validateLayerSetup(List<LayerSetup> layerSetup) {
        if (layerSetup.size() != 2)
            return false;
        return layerSetup.get(0).getPath() != null && !layerSetup.get(0).getPath().equals("") &&
                layerSetup.get(0).getName() != null && !layerSetup.get(0).getName().equals("") &&
                layerSetup.get(1).getPath() != null && !layerSetup.get(1).getPath().equals("") &&
                layerSetup.get(1).getName() != null && !layerSetup.get(1).getName().equals("");
    }

    private boolean validateContextSources(List<LayerSetup> layerSetup) {
        if (layerSetup.size() != 2)
            return false;
        for (LayerSetup setup : layerSetup) {
            if (setup.getContextSources().size() != 2) {
                return false;
            } else {
                for (Map<String, String> contextSource : setup.getContextSources()) {
                    for (Map.Entry<String, String> entry : contextSource.entrySet()) {
                        if (entry.getKey().equals("") || entry.getValue().equals("")) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean validatePropertiesLinked(List<PropertyNGSILD> propertyLinked) {
        if (propertyLinked.size() != 2)
            return false;

        return (propertyLinked.get(0).getName() != null && !propertyLinked.get(0).getName().equals("") &&
                propertyLinked.get(1).getName() != null && !propertyLinked.get(1).getName().equals("")) ||
                (propertyLinked.get(0).getEntityId() != null && !propertyLinked.get(0).getEntityId().equals("") &&
                        propertyLinked.get(1).getEntityId() != null && !propertyLinked.get(1).getEntityId().equals(""));
    }

    private boolean validateRelationshipType(RelationshipType relationshipType) {
        if (relationshipType == null)
            return false;
        return relationshipType.equals(RelationshipType.ONE_TO_ONE) ||
                relationshipType.equals(RelationshipType.ONE_TO_MANY) ||
                relationshipType.equals(RelationshipType.MANY_TO_MANY);
    }

    private boolean validateRelationshipsProperties(EntitiesRelationshipSetup setup) {
        if (setup.getRelationships().size() == 0 || setup.getRelationships().size() > 2)
            return false;

        if (!setup.getRelationships().containsKey(setup.getLayerSetup().get(0).getPath()) ||
                !setup.getRelationships().containsKey(setup.getLayerSetup().get(1).getPath()))
            return false;

        for (Map.Entry<String, String> relationship : setup.getRelationships().entrySet()) {
            if (relationship.getKey().equals("") ||
                    (relationship.getValue() == null || relationship.getValue().equals(""))
            ) {
                return false;
            }
        }

        return true;
    }

    private boolean validateStatus(EntitiesRelationshipSetupStatus status) {
        if (status == null)
            return false;
        return status.equals(EntitiesRelationshipSetupStatus.DONE) ||
                status.equals(EntitiesRelationshipSetupStatus.PENDING);
    }

    public boolean validateStatusParam(String status) {
        if (status == null || status.equals(""))
            return false;
        return status.equalsIgnoreCase(EntitiesRelationshipSetupStatus.DONE.name()) ||
                status.equalsIgnoreCase(EntitiesRelationshipSetupStatus.PENDING.name());
    }
}
