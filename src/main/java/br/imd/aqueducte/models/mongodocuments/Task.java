package br.imd.aqueducte.models.mongodocuments;

import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.enums.TaskType;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Document
public class Task {
    @NotBlank
    private Integer id;
    @NotBlank
    private String userId;
    @NotBlank
    private Integer index;
    @NotBlank
    private String title;
    @NotBlank
    private TaskType type;
    @NotBlank
    private TaskStatus status;
    @NotBlank
    private String description;

    public Task() {
    }

    public Task(@NotBlank Integer id,
                @NotBlank String userId,
                @NotBlank Integer index,
                @NotBlank String title,
                @NotBlank TaskType type,
                @NotBlank TaskStatus status,
                @NotBlank String description) {
        this.id = id;
        this.userId = userId;
        this.index = index;
        this.title = title;
        this.type = type;
        this.status = status;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
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
}
