package br.imd.smartsysnc.processors.education.sigeduc.treats;

import br.imd.smartsysnc.processors.impl.NGSILDTreatImpl;
import br.imd.smartsysnc.processors.Processor;
import br.imd.smartsysnc.processors.education.sigeduc.EscolaEntityProcessor;
import br.imd.smartsysnc.utils.NGSILDUtils;

import java.util.*;
import java.util.Map.Entry;

public class EscolaNGSILDTreat extends NGSILDTreatImpl {

    /**
     * @param data         - data provided by QuarkSmart.
     * @param ownLayerName - {@link Optional} - Define a specific name for layer.
     * @param entity       - {@link Optional} - Define the wished entity.
     * @param contextLink  - Context for entity
     */
    @SuppressWarnings("unchecked")
    public List<LinkedHashMap<Object, Object>> convertToEntityNGSILD(
            LinkedHashMap<Object, Object> data,
            String ownLayerName,
            String entity,
            Map<Object, Object> contextLink) {

        LinkedHashMap<Object, Object> linkedHashMapFinal = new LinkedHashMap<>();

        LinkedHashMap<Object, Object> linkedHashMapNGSILD = new LinkedHashMap<>();

        HashMap<Object, Object> value = new HashMap<>();

        String layerType = ownLayerName.length() != 0 ? ownLayerName : (String) data.get("name");

        // Property data provided of SIGEduc API
        ArrayList<Object> listObjSigeduc = (ArrayList<Object>) data.get("rows");

        List<LinkedHashMap<Object, Object>> listObjForSgeol = new ArrayList<>();

        // Attribute of entity obtained.
        Entry<Object, Object> propertiesContent = null;

        List<String> contextList = Arrays.asList(
                "https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld",
                contextLink.get("contextLink").toString());

        for (int i = 0; i < listObjSigeduc.size(); i++) {
            LinkedHashMap<Object, Object> ldObj;
            ldObj = (LinkedHashMap<Object, Object>) listObjSigeduc.get(i);
            UUID uuid = UUID.randomUUID();
            NGSILDUtils.initDefaultProperties(linkedHashMapNGSILD, contextList, layerType, uuid.toString());

            HashMap<Object, HashMap<Object, HashMap<Object, Object>>> properties = new HashMap<>();
            HashMap<Object, HashMap<Object, Object>> typeAndValueMap = new HashMap<>();

            HashMap<Object, Object> linkedHashIdForRelationship = new LinkedHashMap<>();
            if (ldObj != null) {
                EscolaEntityProcessor entityProcessor = new EscolaEntityProcessor();
                entityProcessor.getIdsForRelationship(ldObj);

                for (Iterator<Entry<Object, Object>> iterator = ldObj.entrySet().iterator(); iterator.hasNext(); ) {
                    propertiesContent = iterator.next();

                    if (propertiesContent.getKey() == "localizacao" && propertiesContent.getValue() != null) {
                        convertToGeoJson(value, propertiesContent);
                        typeAndValueMap.put("location", value);
                    } else {
                        value.put("type", "Property");
                        value.put("value", propertiesContent.getValue());
                        typeAndValueMap.put(propertiesContent.getKey(), value);
                    }
                    properties.put("properties", typeAndValueMap);
                    value = new HashMap<>();
                }
            }

            linkedHashMapNGSILD.putAll(properties);
            linkedHashMapFinal.put("idForRelationship", linkedHashIdForRelationship);
            linkedHashMapFinal.put("objNGSILD", linkedHashMapNGSILD);

            listObjForSgeol.add(linkedHashMapFinal);
            linkedHashMapNGSILD = new LinkedHashMap<>();
            linkedHashMapFinal = new LinkedHashMap<>();
        }

        return listObjForSgeol;
    }

    public void convertToGeoJson(HashMap<Object, Object> value, Entry<Object, Object> propertiesContent) {
        value.put("type", "GeoProperty");
        HashMap<Object, Object> valueGeometry = new HashMap<>();
        String[] coordenates = propertiesContent.getValue().toString().split(",");
        valueGeometry.put("coordinates", coordenates);
        valueGeometry.put("type", "MultiPoint");
        value.put("value", valueGeometry);
    }

    @Override
    public List<LinkedHashMap<Object, Object>> convertToEntityNGSILD(List<Object> data, String layerPath, Map<Object, Object> contextLink) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public void matchingWithContext(
            List<Object> listMatches,
            List<LinkedHashMap<Object, Object>> listNGSILD,
            HashMap<Object, HashMap<Object, Object>> propertiesBasedOnContext) {
        for (Object element : listNGSILD) {

            HashMap<Object, Object> objNGSILD = (HashMap<Object, Object>) ((HashMap<Object, Object>) element).get("objNGSILD");
            HashMap<Object, Object> properties = (HashMap<Object, Object>) objNGSILD.get("properties");

            for (Entry<Object, Object> property : properties.entrySet()) {
                String key = property.getKey().toString();
                for (Object matches : listMatches) {
                    LinkedHashMap<Object, Object> keyMatch = (LinkedHashMap<Object, Object>) matches;
                    String eqPropertyKey = keyMatch.get("eqProperty").toString();
                    String keyContext = keyMatch.get("name").toString();
                    if (key.equals(eqPropertyKey) || key.equals(keyContext)) {
                        if (((HashMap<Object, Object>) property.getValue()).get("value") != null
                                && ((HashMap<Object, Object>) property.getValue()).get("value") != "") {
                            propertiesBasedOnContext.put(keyContext, (HashMap<Object, Object>) property.getValue());
                            break;
                        }
                    }
                }
            }
            ((HashMap<Object, Object>) element).remove("properties");
            objNGSILD.put("properties", propertiesBasedOnContext);
            propertiesBasedOnContext = new HashMap<>();
        }
    }

    public Map<Object, Object> getLinkedIdListForImportDataToSGEOL(Map<Object, Object> entidadeToImport) {
        Processor processor = new EscolaEntityProcessor();
        return processor.getLinkedIdListForImportDataToSGEOL(entidadeToImport);
    }
}
