package br.imd.aqueducte.models.documents;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ReferenceForRelationship {

	private String idSGEOL;
	private Map<String, Object> idsRelationship;

	public ReferenceForRelationship(String idSGEOL, Map<String, Object> idsRelationship) {
		this.idSGEOL = idSGEOL;
		this.idsRelationship = (LinkedHashMap<String, Object>) idsRelationship;
	}

	public Map<String, Object> getIdsRelationship() {
		return idsRelationship;
	}

	public void setIdsRelationship(Map<String, Object> idsRelationship) {
		this.idsRelationship = idsRelationship;
	}

	public String getIdSGEOL() {
		return idSGEOL;
	}

	public void setIdSGEOL(String idSGEOL) {
		this.idSGEOL = idSGEOL;
	}

}
