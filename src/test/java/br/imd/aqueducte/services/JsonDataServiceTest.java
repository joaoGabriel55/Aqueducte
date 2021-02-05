package br.imd.aqueducte.services;

import br.imd.aqueducte.AqueducteApplicationTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class JsonDataServiceTest extends AqueducteApplicationTests {

    @Autowired
    private JsonDataService jsonDataService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void getFlatJSONTest() throws Exception {
        var objectList = new ArrayList<>();
        var object = new LinkedHashMap<>();

        object.put("a", "s");
        var b = new LinkedHashMap<>();
        var c = new LinkedHashMap<>();
        c.put("d", "z");
        b.put("c", c);
        b.put("c2", Arrays.asList(1, 2, 3));
        object.put("b", b);

        var resultObj = jsonDataService.getFlatJSON(object).toString();
        Assert.assertEquals("{a=s, b_c_d=z, b_c2=[1, 2, 3]}", resultObj);

        var geometry = new LinkedHashMap<>();
        geometry.put("type", "Point");
        geometry.put("coordinates", Arrays.asList(125.6, 10.1));
        object.put("geoJson", geometry);

        objectList.add(object);

        var result = jsonDataService.getFlatJSON(objectList).toString();
        Assert.assertEquals("[{a=s, b_c_d=z, b_c2=[1, 2, 3], geoJson={type=Point, coordinates=[125.6, 10.1]}}]", result);
    }
}
