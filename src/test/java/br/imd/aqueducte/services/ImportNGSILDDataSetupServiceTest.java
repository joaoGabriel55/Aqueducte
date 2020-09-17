package br.imd.aqueducte.services;

import br.imd.aqueducte.AqueducteApplicationTests;
import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.MatchingConverterSetup;
import br.imd.aqueducte.models.mongodocuments.ImportNGSILDDataSetup;
import br.imd.aqueducte.models.mongodocuments.ImportationSetup;
import lombok.extern.log4j.Log4j2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Log4j2
public class ImportNGSILDDataSetupServiceTest extends AqueducteApplicationTests {

    @Autowired
    private ImportNGSILDDataSetupService service;

    private static final String USER_ID = "123aBc";

    private ImportNGSILDDataSetup createMockSetup(String label, String importType, boolean useContext) {
        ImportNGSILDDataSetup setup = new ImportNGSILDDataSetup();
        setup.setIdUser(USER_ID);
        setup.setLabel(label);
        setup.setImportType(importType);
        setup.setUseContext(useContext);
        if (useContext) {
            Map<String, String> contextSources = new LinkedHashMap<>();
            contextSources.put("test", "test.jsonld");
            setup.setContextSources(contextSources);
        }
        setup.setLayerSelected("TestName");
        setup.setLayerPathSelected("test");

        if (importType.equals(ImportNGSILDDataSetup.FILE)) {
            setup.setFilePath("//Test1.csv");
            setup.setDelimiterFileContent(",");
        } else if (importType.equals(ImportNGSILDDataSetup.WEB_SERVICE)) {
            setup.setBaseUrl("http://test.com");
            setup.setPath("/test");
            setup.setHttpVerb("GET");
            setup.setDataSelected("data_test");
        }

        setup.setFieldsAvailable(Arrays.asList("a", "b", "c"));
        setup.setFieldsSelected(Arrays.asList("a", "b"));

        LinkedHashMap<String, MatchingConverterSetup> converterSetup = new LinkedHashMap<>();
        MatchingConverterSetup matchingConverterSetup = new MatchingConverterSetup();
        matchingConverterSetup.setFinalProperty("location");
        matchingConverterSetup.setForeignProperty("propF");
        matchingConverterSetup.setLocation(true);

        LinkedHashMap<String, GeoLocationConfig> geoLocationConfigSetup = new LinkedHashMap<>();
        GeoLocationConfig geoLocationConfig = new GeoLocationConfig();
        geoLocationConfig.setKey("location");
        geoLocationConfigSetup.put("location", geoLocationConfig);
        matchingConverterSetup.setGeoLocationConfig(geoLocationConfigSetup);

        converterSetup.put("location", matchingConverterSetup);
        setup.setMatchingConverterSetup(converterSetup);

        return setup;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void saveTest() throws Exception {
        ImportNGSILDDataSetup mockSetup = createMockSetup("Test", ImportationSetup.FILE, false);
        ImportNGSILDDataSetup setupSaved = service.createOrUpdate(mockSetup);
        assertEquals(setupSaved.getLabel(), mockSetup.getLabel());
    }

    @Test
    public void updateTest() throws Exception {
        ImportNGSILDDataSetup mockSetup = createMockSetup("Test", ImportationSetup.FILE, false);
        ImportNGSILDDataSetup setupSaved = service.createOrUpdate(mockSetup);
        assertEquals(setupSaved.getLabel(), mockSetup.getLabel());
        setupSaved.setLabel("Updated");
        ImportNGSILDDataSetup setupUpdated = service.createOrUpdate(setupSaved);
        assertEquals(setupUpdated.getLabel(), setupSaved.getLabel());
    }

    @Test
    public void findImportSetupWithFilters() throws Exception {
        ImportNGSILDDataSetup mockSetup1, mockSetup2;
        int size;

        mockSetup1 = createMockSetup("Test0", ImportationSetup.FILE, false);
        mockSetup2 = createMockSetup("Test1", ImportationSetup.FILE, false);
        service.createOrUpdate(mockSetup1);
        service.createOrUpdate(mockSetup2);

        size = service.findImportSetupWithFilters(
                USER_ID, ImportNGSILDDataSetup.FILE, false, 0, 15
        ).getContent().size();
        assertEquals(2, size);

        mockSetup1 = createMockSetup("Test2", ImportationSetup.FILE, true);
        mockSetup2 = createMockSetup("Test3", ImportationSetup.FILE, true);
        service.createOrUpdate(mockSetup1);
        service.createOrUpdate(mockSetup2);

        size = service.findImportSetupWithFilters(
                USER_ID, ImportNGSILDDataSetup.FILE, true, 0, 15
        ).getContent().size();
        assertEquals(2, size);

        mockSetup1 = createMockSetup("Test4", ImportationSetup.WEB_SERVICE, false);
        mockSetup2 = createMockSetup("Test5", ImportationSetup.WEB_SERVICE, false);
        service.createOrUpdate(mockSetup1);
        service.createOrUpdate(mockSetup2);

        size = service.findImportSetupWithFilters(
                USER_ID, ImportNGSILDDataSetup.WEB_SERVICE, false, 0, 15
        ).getContent().size();
        assertEquals(2, size);

        mockSetup1 = createMockSetup("Test6", ImportationSetup.WEB_SERVICE, true);
        mockSetup2 = createMockSetup("Test7", ImportationSetup.WEB_SERVICE, true);
        service.createOrUpdate(mockSetup1);
        service.createOrUpdate(mockSetup2);

        size = service.findImportSetupWithFilters(
                USER_ID, ImportNGSILDDataSetup.WEB_SERVICE, true, 0, 15
        ).getContent().size();
        assertEquals(2, size);

        // Find All
        size = service.findImportSetupWithFilters(
                USER_ID, null, null, 0, 15
        ).getContent().size();
        assertEquals(8, size);

    }

    @After
    public void tearDown() {
        final String collection = "importNGSILDDataSetup";
        log.info("Dropping collection: {} ", collection);
        mongoTemplate.dropCollection(collection);
    }
}
