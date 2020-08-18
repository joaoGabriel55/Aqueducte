package br.imd.aqueducte.services;

import br.imd.aqueducte.AqueducteApplicationTests;
import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.MatchingConverterSetup;
import br.imd.aqueducte.models.mongodocuments.ImportNGSILDDataSetup;
import br.imd.aqueducte.models.mongodocuments.ImportationSetup;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class ImportNGSILDDataSetupServiceTest extends AqueducteApplicationTests {

    @Autowired
    private ImportNGSILDDataSetupService service;

    private ImportNGSILDDataSetup setup;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        setup = new ImportNGSILDDataSetup();
        setup.setIdUser("123aBc");
        setup.setLabel("Test");
        setup.setImportType(ImportationSetup.FILE);
        setup.setLayerSelected("TestName");
        setup.setLayerPathSelected("test");
        setup.setFilePath("//Test1.csv");
        setup.setDelimiterFileContent(",");

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
    }

    @Test
    public void saveTest() throws Exception {
        ImportNGSILDDataSetup setupSaved = service.createOrUpdate(setup.getIdUser(), setup);
        assertEquals(setupSaved.getLabel(), setup.getLabel());
        service.delete(setupSaved.getId());
    }

    @Test
    public void updateTest() throws Exception {
        ImportNGSILDDataSetup setupSaved = service.createOrUpdate(setup.getIdUser(), setup);
        assertEquals(setupSaved.getLabel(), setup.getLabel());
        setupSaved.setLabel("Updated");
        ImportNGSILDDataSetup setupUpdated = service.createOrUpdate(setupSaved);
        assertEquals(setupUpdated.getLabel(), setupSaved.getLabel());
        service.delete(setupSaved.getId());
    }


}
