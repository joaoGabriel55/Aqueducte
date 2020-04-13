package br.imd.aqueducte.repositories;

import br.imd.aqueducte.models.mongodocuments.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {
    Page<Task> findByType(String type, Pageable pages);

    Page<Task> findByUserId(String userId, Pageable pages);

    Page<Task> findByUserIdAndType(String userId, String type, Pageable pages);

    Page<Task> findByUserIdAndStatus(String userId, String status, Pageable pages);

    Page<Task> findByUserIdAndTypeAndStatus(String userId, String type, String status, Pageable pages);
}
