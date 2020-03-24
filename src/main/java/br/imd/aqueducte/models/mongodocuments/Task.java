package br.imd.aqueducte.models.mongodocuments;

import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.enums.TaskType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;

import static br.imd.aqueducte.models.config.MongoDBCollectionsConfig.TASK;

@Document(value = TASK)
public class Task {
    @Id
    private String id;
    @NotBlank
    private String userId;
    @NotBlank
    private String title;
    @NotBlank
    private TaskType type;
    @NotBlank
    private TaskStatus status;
    @NotBlank
    private String description;

    @CreatedDate
    private Date dateCreated;

    @LastModifiedDate
    private Date dateModified;

    public Task() {
    }

    public Task(
            @NotBlank String userId,
            @NotBlank String title,
            @NotBlank TaskType type,
            @NotBlank TaskStatus status,
            @NotBlank String description,
            Date dateCreated,
            Date dateModified
    ) {
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.status = status;
        this.description = description;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }
}
