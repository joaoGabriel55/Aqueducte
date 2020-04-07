package br.imd.aqueducte.models.mongodocuments;

import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.enums.TaskType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;

import static br.imd.aqueducte.models.config.MongoDBCollectionsConfig.TASK;

@Document(value = TASK)
@Getter
@Setter
@NoArgsConstructor
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
}
