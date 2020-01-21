package br.imd.aqueducte;

import java.util.*;

public class TestJava {
    public static void main(String[] args) {
        Map<String, Object> object = new HashMap<>();
        Map<String, Object> props = new HashMap<>();
        props.put("type", "Primary");
        props.put("value", 1);
        object.put("id_pk", props);
        props = new HashMap<>();
        props.put("type", "Relationship");
        props.put("object", 12);
        object.put("hasSome", props);

        Map<String, Object> object2 = new HashMap<>();
        Map<String, Object> props2 = new HashMap<>();
        props2.put("type", "Primary");
        props2.put("value", 1);
        object2.put("id_pk", props2);
        props2 = new HashMap<>();
        props2.put("type", "Relationship");
        props2.put("object", 15);
        object2.put("hasSome", props2);

        Map<String, Object> object3 = new HashMap<>();
        Map<String, Object> props3 = new HashMap<>();
        props3.put("type", "Primary");
        props3.put("value", 2);
        object3.put("id_pk", props3);
        props3 = new HashMap<>();
        props3.put("type", "Relationship");
        props3.put("object", 15);
        object3.put("hasSome", props3);

        Map<String, Object> object4 = new HashMap<>();
        Map<String, Object> props4 = new HashMap<>();
        props4.put("type", "Primary");
        props4.put("value", 2);
        object4.put("id_pk", props4);
        props4 = new HashMap<>();
        props4.put("type", "Relationship");
        props4.put("object", 15);
        object4.put("hasSome", props4);


        List<Map<String, Object>> listOne = new ArrayList<>();
        listOne.add(object);
        listOne.add(object2);
        listOne.add(object3);

        boolean alreadyExistByPrimaryField = false;
        List<Object> listId = new ArrayList<>();
        Object primaryId = getValuePrimary(object4);
        for (Map<String, Object> obj : listOne) {
            if (primaryId != null) {
                Object primaryIdLocal = getValuePrimary(obj);
                for (Map.Entry<String, Object> entry : obj.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Map) {
                        Map<String, Object> objectMap = (Map<String, Object>) value;
                        if (Objects.equals(primaryIdLocal, primaryId) && objectMap.get("type").equals("Relationship")) {
                            Object o = ((Map<String, Object>) obj.get(key)).get("object");
                            if (!(o instanceof List)) {
                                listId.add(o);
                                listId.add(((Map<String, Object>) object2.get(key)).get("object"));
                                ((Map<String, Object>) obj.get(key)).put("object", listId);
                            } else {
                                ((List<Object>) ((Map<String, Object>) obj.get(key)).get("object"))
                                        .add(((Map<String, Object>) object2.get(key)).get("object"));
                            }

                            alreadyExistByPrimaryField = true;
                        }
                    }
                }
            }
        }
        if (!alreadyExistByPrimaryField)
            listOne.add(object2);
        System.out.println(listOne.toString());
    }

    private static Object getValuePrimary(Map<String, Object> objectMap) {
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) entry.getValue();
                if (map.containsKey("type") && map.get("type").equals("Primary"))
                    return map.get("value");
            }
        }
        return null;
    }
}
