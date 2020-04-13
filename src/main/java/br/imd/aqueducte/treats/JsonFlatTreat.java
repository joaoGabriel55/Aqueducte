package br.imd.aqueducte.treats;

import java.util.List;
import java.util.Map;

public interface JsonFlatTreat {

    Object getFlatJSON(Object dataForConversion);

    List<String> getKeysCollectionFromJSON(Map<String, Object> dataForConversion);
}
