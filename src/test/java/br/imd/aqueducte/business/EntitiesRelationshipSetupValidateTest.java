package br.imd.aqueducte.business;

import br.imd.aqueducte.AqueducteApplicationTests;
import br.imd.aqueducte.entitiesrelationship.business.EntitiesRelationshipSetupValidate;
import br.imd.aqueducte.models.entitiesrelationship.dtos.LayerSetup;
import br.imd.aqueducte.models.entitiesrelationship.dtos.PropertyNGSILD;
import br.imd.aqueducte.models.entitiesrelationship.enums.EntitiesRelationshipSetupStatus;
import br.imd.aqueducte.models.entitiesrelationship.enums.RelationshipType;
import br.imd.aqueducte.models.entitiesrelationship.mongodocuments.EntitiesRelationshipSetup;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EntitiesRelationshipSetupValidateTest extends AqueducteApplicationTests {

    @Autowired
    private EntitiesRelationshipSetupValidate validator;

    @Test
    public void validateEntitiesRelationshipSetupBadPathTest() {
        EntitiesRelationshipSetup setup = new EntitiesRelationshipSetup();

        List<LayerSetup> layerSetups = new ArrayList<>();
        LayerSetup layerSetup1 = new LayerSetup();
        LayerSetup layerSetup2 = new LayerSetup();
        layerSetup1.setName("");
        layerSetup1.setPath("LayerA");
        layerSetup2.setName("LayerB");
        layerSetup2.setPath("");

        List<Map<String, String>> contextSources = new ArrayList<>();
        Map<String, String> contextSource = new LinkedHashMap<>();
        contextSource.put("", "");
        contextSources.add(contextSource);
        layerSetup1.setContextSources(contextSources);
        layerSetup2.setContextSources(contextSources);

        layerSetups.add(layerSetup1);
        layerSetups.add(layerSetup2);

        List<PropertyNGSILD> propertiesLinked = new ArrayList<>();
        PropertyNGSILD propertyNGSILD1 = new PropertyNGSILD();
        PropertyNGSILD propertyNGSILD2 = new PropertyNGSILD();
        propertyNGSILD1.setName("");
        propertyNGSILD1.setType(PropertyNGSILD.GEOPROPERTY);
        propertyNGSILD2.setName("");
        propertyNGSILD2.setType(PropertyNGSILD.PROPERTY);
        propertiesLinked.add(propertyNGSILD1);
        propertiesLinked.add(propertyNGSILD2);

        Map<String, String> relationships = new LinkedHashMap<>();
        relationships.put("", "");
        relationships.put("LayerC", "hasSomething");

        setup.setLayerSetup(layerSetups);
        setup.setPropertiesLinked(propertiesLinked);
        setup.setRelationships(relationships);
        setup.setRelationshipType(null);
        setup.setStatus(null);

        List<String> errors = validator.validateEntitiesRelationshipSetup(setup);

        assertEquals(7, errors.size());
    }

    @Test
    public void validateEntitiesRelationshipSetupHappyPathTest() {
        EntitiesRelationshipSetup setup = new EntitiesRelationshipSetup();

        List<LayerSetup> layerSetups = new ArrayList<>();
        LayerSetup layerSetup1 = new LayerSetup();
        LayerSetup layerSetup2 = new LayerSetup();
        layerSetup1.setName("LayerA");
        layerSetup1.setPath("layer_a");
        layerSetup2.setName("LayerB");
        layerSetup2.setPath("layer_b");

        List<Map<String, String>> contextSources = new ArrayList<>();
        Map<String, String> contextSource = new LinkedHashMap<>();
        contextSource.put("contextA", "contextA.jsonld");
        contextSources.add(contextSource);
        contextSources.add(contextSource);
        layerSetup1.setContextSources(contextSources);
        layerSetup2.setContextSources(contextSources);

        layerSetups.add(layerSetup1);
        layerSetups.add(layerSetup2);

        List<PropertyNGSILD> propertiesLinked = new ArrayList<>();
        PropertyNGSILD propertyNGSILD1 = new PropertyNGSILD();
        PropertyNGSILD propertyNGSILD2 = new PropertyNGSILD();
        propertyNGSILD1.setName("propA");
        propertyNGSILD1.setType(PropertyNGSILD.PROPERTY);
        propertyNGSILD2.setName("propB");
        propertyNGSILD2.setType(PropertyNGSILD.PROPERTY);
        propertiesLinked.add(propertyNGSILD1);
        propertiesLinked.add(propertyNGSILD2);

        Map<String, String> relationships = new LinkedHashMap<>();
        relationships.put("layer_a", "hasSomething");
        relationships.put("layer_b", "hasSomething");

        setup.setLayerSetup(layerSetups);
        setup.setPropertiesLinked(propertiesLinked);
        setup.setRelationships(relationships);
        setup.setRelationshipType(RelationshipType.ONE_TO_ONE);
        setup.setStatus(EntitiesRelationshipSetupStatus.DONE);

        List<String> errors = validator.validateEntitiesRelationshipSetup(setup);

        assertEquals(0, errors.size());
    }

}
