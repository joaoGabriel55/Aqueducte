package br.imd.smartsysnc.processors;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.imd.smartsysnc.processors.sigeduc.EscolaEntityProcessor;
import br.imd.smartsysnc.utils.RequestsUtils;

public class Processor {

	public static final int IDS_FOR_RELATIONSHIP = 1;
	public static final int IMPORTATION_TO_SGEOL = 2;

	private LinkedHashMap<Object, Object> ldObj;
	private String entity;
	private int operation;

	public Processor(LinkedHashMap<Object, Object> ldObj, String entity, int operation) {
		this.ldObj = ldObj;
		this.entity = entity;
		this.operation = operation;
	}

	public LinkedHashMap<Object, Object> execute() {
		LinkedHashMap<Object, Object> response = new LinkedHashMap<>();
		if (operation == 1) {
			response = selectEspecificEntityIdsForRelationship();
		} else if (operation == 2) {
			response = null;
		}

		return response;
	}

	private LinkedHashMap<Object, Object> selectEspecificEntityIdsForRelationship() {

		if (entity.equals("escolas_seec"))
			return getIdsForRelationship(new EscolaEntityProcessor(ldObj, entity, operation));
		else
			return new LinkedHashMap<Object, Object>();

	}

	public LinkedHashMap<Object, Object> getIdsForRelationship(Processor processor) {
		return processor.getIdsForRelationship(ldObj);
	}

	public LinkedHashMap<Object, Object> getIdsForRelationship(LinkedHashMap<Object, Object> ldObj) {
		return getIdsForRelationship(ldObj);
	}

	public void importToSGEOL(Map<Object, Object> entidadeToImport, String entity,
			List<Map<String, Object>> listIDsLinked, List<Map<String, Object>> listIdsRelationships) {

		String url = RequestsUtils.URL_SGEOL + entity;

		RequestsUtils.postMethodRestTemplate(url, entidadeToImport);

		String idForSGEOL = entidadeToImport.get("id").toString();
		Map<String, Object> mapIDs = new HashMap<>();
		mapIDs.put("idSGEOL", idForSGEOL);
		mapIDs.put("idsRelationships", listIdsRelationships);

		listIDsLinked.add(mapIDs);
	}
}
