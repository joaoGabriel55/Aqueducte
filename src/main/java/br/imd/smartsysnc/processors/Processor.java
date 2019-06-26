package br.imd.smartsysnc.processors;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import br.imd.smartsysnc.processors.sigeduc.EscolaEntityProcessor;
import br.imd.smartsysnc.utils.RequestsUtils;

public class Processor {

	public static final int IDS_FOR_RELATIONSHIP = 1;
	public static final int IMPORTATION_TO_SGEOL = 2;

	private Map<Object, Object> ldObj;
	private String entity;
	private int operation;
	private boolean importationToo;

	public Processor(Map<Object, Object> ldObj, String entity, int operation) {
		this.ldObj = ldObj;
		this.entity = entity;
		this.operation = operation;
	}
	
	public Processor(Map<Object, Object> ldObj, String entity, int operation, boolean importationToo) {
		this.ldObj = ldObj;
		this.entity = entity;
		this.operation = operation;
		this.importationToo = importationToo;
	}

	public Map<Object, Object> execute() {
		Map<Object, Object> response = new LinkedHashMap<>();
		if (operation == 1) {
			response = selectSpecificEntityIdsForRelationship();
		} else if (operation == 2) {
			response = selectEspectificEntityForGetLinkedIdListOrImportDataToSGEOL();
		}
		return response;
	}
	
	/**
	 * Method to select the specific entity for treat the Ids for relationship. 
	 * */
	private Map<Object, Object> selectSpecificEntityIdsForRelationship() {

		if (entity.equals("escolas_seec"))
			return getIdsForRelationship(new EscolaEntityProcessor(ldObj, entity, operation));
		else
			return new HashMap<>();

	}
	
	public Map<Object, Object> getIdsForRelationship(Processor processor) {
		return processor.getIdsForRelationship(ldObj);
	}
	
	public Map<Object, Object> getIdsForRelationship(Map<Object, Object> ldObj) {
		return getIdsForRelationship(ldObj);
	}
	
	/**
	 * Method to select the specific entity for return of LinkedId List for Document 
	 * (Persisted at DevBoard dataBase) or make the Importation of entities for SGEOL. 
	 * */
	private Map<Object, Object> selectEspectificEntityForGetLinkedIdListOrImportDataToSGEOL() {
		if (entity.equals("escolas_seec"))
			return getLinkedIdListOrImportDataToSGEOL(new EscolaEntityProcessor(ldObj, entity, operation));
		else
			return new HashMap<>();
	}	

	public Map<Object, Object> getLinkedIdListOrImportDataToSGEOL(Processor processor) {
		return processor.getLinkedIdListOrImportDataToSGEOL(ldObj, importationToo);
	}
	
	public Map<Object, Object> getLinkedIdListOrImportDataToSGEOL(Map<Object, Object> ldObj, boolean importationToo) {
		return getLinkedIdListOrImportDataToSGEOL(ldObj, importationToo);
	}

	/**
	 * Generic method, used for all specific processors to import data for SGEOL. 
	 * */
	public void importToSGEOL(Map<Object, Object> entidadeToImport, String entity) {
		String url = RequestsUtils.URL_SGEOL + entity;
		RequestsUtils.postMethodRestTemplate(url, entidadeToImport);
	}
}
