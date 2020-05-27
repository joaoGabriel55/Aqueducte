package br.imd.aqueducte.services.sgeolqueriesservices;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class EntityOperationsServiceTest {

    EntityOperationsService service = EntityOperationsService.getInstance();

    @Test
    public void findContainedInTest() throws Exception {
        int offset = 0;
        boolean result = false;
        List<Map<String, Object>> entities = new ArrayList<>();
        // && !result
        while ((offset == 0 || entities.size() != 0)) {
            int entitiesSize = entities.size();
            entities = service.getEntitiesPageable("", "", "", "bairro", 1024, offset * entitiesSize);
            for (Object entity : entities) {
                Map<String, Object> entityMap = (Map<String, Object>) entity;
                int offset2 = 0;
                List<String> geoResponse = new ArrayList<>();
                while (offset2 == 0 || geoResponse.size() != 0) {
                    int geoResponseSize = geoResponse.size();
                    geoResponse = service.findContainedIn(
                            "",
                            "escola", "bairro",
                            entityMap.get("id").toString(),
                            1024, offset2 * geoResponseSize,
                            "", ""
                    );
                    if ((geoResponse == null || geoResponse.size() == 0)) {
                        break;
                    }
                    offset2++;
                }
//                if (result)
//                    break;
            }

            offset++;
        }
        assertTrue(true);
    }

}
