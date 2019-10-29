package br.imd.aqueducte.models;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Class for represent the Matching Configuration of user logged.
 * */
@Document
public class MatchingConfig {

	@Id
	private String id;

	private String userId;
	
	private Date dateCreated;
	
	private Date dataModified;

	private String entityNameService;

	private String entityNameServiceForSGEOL;

	private List<Map<String, String>> matchingsList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDataModified() {
		return dataModified;
	}

	public void setDataModified(Date dataModified) {
		this.dataModified = dataModified;
	}

	public String getEntityNameService() {
		return entityNameService;
	}

	public void setEntityNameService(String entityNameService) {
		this.entityNameService = entityNameService;
	}

	public String getEntityNameServiceForSGEOL() {
		return entityNameServiceForSGEOL;
	}

	public void setEntityNameServiceForSGEOL(String entityNameServiceForSGEOL) {
		this.entityNameServiceForSGEOL = entityNameServiceForSGEOL;
	}

	public List<Map<String, String>> getMatchingsList() {
		return matchingsList;
	}

	public void setMatchingsList(List<Map<String, String>> matchingsList) {
		this.matchingsList = matchingsList;
	}
}
