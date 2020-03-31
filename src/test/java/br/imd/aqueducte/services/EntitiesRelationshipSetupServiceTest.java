package br.imd.aqueducte.services;

import br.imd.aqueducte.AqueducteApplicationTests;
import br.imd.aqueducte.entitiesrelationship.services.EntitiesRelationshipSetupService;
import br.imd.aqueducte.models.entitiesrelationship.enums.EntitiesRelationshipSetupStatus;
import br.imd.aqueducte.models.entitiesrelationship.enums.RelationshipType;
import br.imd.aqueducte.models.entitiesrelationship.mongodocuments.EntitiesRelationshipSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntitiesRelationshipSetupServiceTest extends AqueducteApplicationTests {

    @Autowired
    private EntitiesRelationshipSetupService service;

    private List<String> setupsToRemove;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        setupsToRemove = new ArrayList<>();
    }

    @Test
    public void findAllTest() {
        EntitiesRelationshipSetup setup1 = new EntitiesRelationshipSetup();
        setup1.setIdUser("123456");
        setup1.setRelationshipType(RelationshipType.ONE_TO_ONE);
        setup1.setStatus(EntitiesRelationshipSetupStatus.DONE);
        service.createOrUpdate(setup1);

        EntitiesRelationshipSetup setup2 = new EntitiesRelationshipSetup();
        setup2.setIdUser("123456");
        setup2.setRelationshipType(RelationshipType.ONE_TO_ONE);
        setup2.setStatus(EntitiesRelationshipSetupStatus.DONE);
        service.createOrUpdate(setup2);

        EntitiesRelationshipSetup setup3 = new EntitiesRelationshipSetup();
        setup3.setIdUser("123456");
        setup3.setRelationshipType(RelationshipType.ONE_TO_ONE);
        setup3.setStatus(EntitiesRelationshipSetupStatus.DONE);
        service.createOrUpdate(setup3);

        setupsToRemove.add(setup1.getId());
        setupsToRemove.add(setup2.getId());
        setupsToRemove.add(setup3.getId());

        List<EntitiesRelationshipSetup> setupsFound = service.findAll();
        assertEquals(3, setupsFound.size());

        service.delete(setup1.getId());
        service.delete(setup2.getId());
        service.delete(setup3.getId());
    }

    @Test
    public void findByIdTest() {
        EntitiesRelationshipSetup setup = new EntitiesRelationshipSetup();
        setup.setIdUser("123456");
        setup.setRelationshipType(RelationshipType.ONE_TO_ONE);
        setup.setStatus(EntitiesRelationshipSetupStatus.DONE);
        EntitiesRelationshipSetup setupCreated = service.createOrUpdate(setup);
        Optional<EntitiesRelationshipSetup> setupFound = service.findById(setupCreated.getId());
        assertTrue(setupFound.isPresent());
        service.delete(setupCreated.getId());
    }

    @Test
    public void findByStatusTest() {
        EntitiesRelationshipSetup setup1 = new EntitiesRelationshipSetup();
        setup1.setIdUser("123456");
        setup1.setRelationshipType(RelationshipType.ONE_TO_ONE);
        setup1.setStatus(EntitiesRelationshipSetupStatus.PENDING);
        service.createOrUpdate(setup1);

        EntitiesRelationshipSetup setup2 = new EntitiesRelationshipSetup();
        setup2.setIdUser("123456");
        setup2.setRelationshipType(RelationshipType.ONE_TO_ONE);
        setup2.setStatus(EntitiesRelationshipSetupStatus.DONE);
        service.createOrUpdate(setup2);

        EntitiesRelationshipSetup setup3 = new EntitiesRelationshipSetup();
        setup3.setIdUser("123456");
        setup3.setRelationshipType(RelationshipType.ONE_TO_ONE);
        setup3.setStatus(EntitiesRelationshipSetupStatus.PENDING);
        service.createOrUpdate(setup3);

        setupsToRemove.add(setup1.getId());
        setupsToRemove.add(setup2.getId());
        setupsToRemove.add(setup3.getId());

        List<EntitiesRelationshipSetup> setupsFound = service.findByStatus(
                EntitiesRelationshipSetupStatus.PENDING.name()
        );
        assertEquals(2, setupsFound.size());

        service.delete(setup1.getId());
        service.delete(setup2.getId());
        service.delete(setup3.getId());
    }

    @Test
    public void saveTest() {
        EntitiesRelationshipSetup setup = new EntitiesRelationshipSetup();
        setup.setIdUser("123456");
        setup.setRelationshipType(RelationshipType.ONE_TO_ONE);
        setup.setStatus(EntitiesRelationshipSetupStatus.DONE);
        EntitiesRelationshipSetup setupCreated = service.createOrUpdate(setup);
        assertEquals(setup, setupCreated);
        service.delete(setupCreated.getId());
    }

    @Test
    public void updateTest() {
        EntitiesRelationshipSetup setup = new EntitiesRelationshipSetup();
        setup.setIdUser("123456");
        setup.setRelationshipType(RelationshipType.ONE_TO_ONE);
        setup.setStatus(EntitiesRelationshipSetupStatus.DONE);
        EntitiesRelationshipSetup setupCreated = service.createOrUpdate(setup);
        setup.setRelationshipType(RelationshipType.ONE_TO_MANY);
        EntitiesRelationshipSetup setupUpdated = service.createOrUpdate(setupCreated);
        assertEquals(setupCreated.getRelationshipType().name(), setupUpdated.getRelationshipType().name());
        service.delete(setupCreated.getId());
    }

    @Test
    public void deleteTest() {
        EntitiesRelationshipSetup setup = new EntitiesRelationshipSetup();
        setup.setIdUser("123456");
        setup.setRelationshipType(RelationshipType.ONE_TO_ONE);
        setup.setStatus(EntitiesRelationshipSetupStatus.DONE);
        EntitiesRelationshipSetup setupCreated = service.createOrUpdate(setup);
        String setupIdDeleted = service.delete(setupCreated.getId());
        assertEquals(setupCreated.getId(), setupIdDeleted);
    }

    @After
    @Override
    public void close() {
        super.close();
        for (String id : setupsToRemove) {
            service.delete(id);
        }
        setupsToRemove.clear();
    }
}
