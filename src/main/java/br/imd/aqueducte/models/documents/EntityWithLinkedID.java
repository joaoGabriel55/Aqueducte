package br.imd.aqueducte.models.documents;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This Class represent the Linked IDs of SGEOL Entities and PK id from
 * WebService
 */
@Document
public class EntityWithLinkedID {

	@Id
	private String id;

	private String entitySGEOL;

	private String entityWebService;

	private String webService;

	/**
	 * This attribute consists store in a List of Maps the Entity Id of SGEOL and PK
	 * Id of WebService
	 */
	private List<ReferenceForRelationship> refList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEntitySGEOL() {
		return entitySGEOL;
	}

	public void setEntitySGEOL(String entitySGEOL) {
		this.entitySGEOL = entitySGEOL;
	}

	public List<ReferenceForRelationship> getRefList() {
		return refList;
	}

	public void setRefList(List<ReferenceForRelationship> refList) {
		this.refList = refList;
	}

	public String getEntityWebService() {
		return entityWebService;
	}

	public void setEntityWebService(String entityWebService) {
		this.entityWebService = entityWebService;
	}

	public String getWebService() {
		return webService;
	}

	public void setWebService(String webService) {
		this.webService = webService;
	}

}
