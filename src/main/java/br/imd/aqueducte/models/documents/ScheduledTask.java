package br.imd.aqueducte.models.documents;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import br.imd.aqueducte.enums.ScheduledTaskStatus;

/**
 * This Class represent scheduling Data Importation's Task
 */
@Document
public class ScheduledTask {

	@Id
	private String id;

	/** Name given from user for better identification */
	private String alias;

	private boolean isAutomaticSync;

	private Date dateForSync;

	private Date dateCreated;

	private Date dataModified;

	private ScheduledTaskStatus scheduledTaskStatus;

	/** Store the ID of matchingConfig Document to make the importation */
	private List<String> listMatchingConfig;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Date getDateForSync() {
		return dateForSync;
	}

	public void setDateForSync(Date dateForSync) {
		this.dateForSync = dateForSync;
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

	public ScheduledTaskStatus getScheduledTaskStatus() {
		return scheduledTaskStatus;
	}

	public void setScheduledTaskStatus(ScheduledTaskStatus scheduledTaskStatus) {
		this.scheduledTaskStatus = scheduledTaskStatus;
	}

	public List<String> getListMatchingConfig() {
		return listMatchingConfig;
	}

	public void setListMatchingConfig(List<String> listMatchingConfig) {
		this.listMatchingConfig = listMatchingConfig;
	}

	public boolean isAutomaticSync() {
		return isAutomaticSync;
	}

	public void setAutomaticSync(boolean isAutomaticSync) {
		this.isAutomaticSync = isAutomaticSync;
	}

}
