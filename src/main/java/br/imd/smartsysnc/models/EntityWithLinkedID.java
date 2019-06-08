package br.imd.smartsysnc.models;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This Class represent the Linked IDs of SGEOL Entities and PK id from WebService
 * */
@Document
public class EntityWithLinkedID {

	@Id
	private int id;

	private String entitySGEOL;

	private String entityWebService;

	private String webService;

	/**
	 * This attribute consists store in a List of Maps the Entity Id of SGEOL and PK
	 * Id of WebService
	 */
	private List<Map<String, Integer>> refList;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEntitySGEOL() {
		return entitySGEOL;
	}

	public void setEntitySGEOL(String entitySGEOL) {
		this.entitySGEOL = entitySGEOL;
	}

	public List<Map<String, Integer>> getRefList() {
		return refList;
	}

	public void setRefList(List<Map<String, Integer>> refList) {
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