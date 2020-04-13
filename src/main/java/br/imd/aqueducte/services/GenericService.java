package br.imd.aqueducte.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public interface GenericService<T> {

	T createOrUpdate(T obj);

	List<T> findAll();

	Optional<T> findById(String id);
	
	String delete(String id);

}
