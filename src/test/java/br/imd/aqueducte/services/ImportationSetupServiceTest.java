package br.imd.aqueducte.services;

import br.imd.aqueducte.AqueducteApplicationTests;
import br.imd.aqueducte.models.mongodocuments.ImportationSetup;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithoutContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ImportationSetupServiceTest extends AqueducteApplicationTests {

    @Autowired
    private ImportationSetupWithoutContextService service;

    private List<String> importSetupIdsToRemove;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        importSetupIdsToRemove = new ArrayList<>();

        ImportationSetupWithoutContext importationSetup1 = new ImportationSetupWithoutContext();
        importationSetup1.setIdUser("user1");
        importationSetup1.setLabel("Test1");
        importationSetup1.setImportType(ImportationSetup.FILE);
        importationSetup1.setLayerSelected("TestName");
        importationSetup1.setLayerPathSelected("test");
        importationSetup1.setFilePath("//Test1.csv");

        ImportationSetupWithoutContext importationSetup2 = new ImportationSetupWithoutContext();
        importationSetup2.setIdUser("user2");
        importationSetup2.setLabel("Test2");
        importationSetup2.setImportType(ImportationSetup.FILE);
        importationSetup2.setLayerSelected("TestName");
        importationSetup2.setLayerPathSelected("test");
        importationSetup2.setFilePath("//Test1.csv");

        ImportationSetupWithoutContext importationSetup3 = new ImportationSetupWithoutContext();
        importationSetup3.setIdUser("user2");
        importationSetup3.setLabel("Test3");
        importationSetup3.setImportType(ImportationSetup.FILE);
        importationSetup3.setLayerSelected("TestName");
        importationSetup3.setLayerPathSelected("test");
        importationSetup3.setFilePath("//Test2.csv");

        ImportationSetupWithoutContext created1 = service.createOrUpdate(importationSetup1);
        ImportationSetupWithoutContext created2 = service.createOrUpdate(importationSetup2);
        ImportationSetupWithoutContext created3 = service.createOrUpdate(importationSetup3);
        importSetupIdsToRemove.add(created1.getId());
        importSetupIdsToRemove.add(created2.getId());
        importSetupIdsToRemove.add(created3.getId());
    }

    @Test
    public void findImportSetupByFilePath() throws Exception {
        List<ImportationSetupWithoutContext> listFiltered1 = service.findByUserIdAndFilePath("user1", "//Test1.csv");
        List<ImportationSetupWithoutContext> listFiltered2 = service.findByUserIdAndFilePath("user2", "//Test2.csv");
        assertEquals(1, listFiltered1.size());
        assertEquals(1, listFiltered2.size());
    }

    @After
    public void close() throws Exception {
        super.close();
        for (String id : importSetupIdsToRemove) {
            service.delete(id);
        }
        importSetupIdsToRemove.clear();
    }
}
