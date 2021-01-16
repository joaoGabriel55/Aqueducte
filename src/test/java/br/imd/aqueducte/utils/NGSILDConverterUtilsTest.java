package br.imd.aqueducte.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NGSILDConverterUtilsTest {

    @Test
    public void getISODateTest() throws Exception {
        String isoDate = NGSILDConverterUtils.getISODate("11-12-2020", "dd-MM-yyyy");
        assertEquals("2020-12-11T00:00:00Z", isoDate);
    }
}
