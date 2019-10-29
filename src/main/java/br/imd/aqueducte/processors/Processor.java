package br.imd.aqueducte.processors;

import java.util.Map;

public interface Processor {

	Map<Object, Object> getIdsForRelationship(Map<Object, Object> ldObj);

	Map<Object, Object> getLinkedIdListForImportDataToSGEOL(Map<Object, Object> ldObj);
}
