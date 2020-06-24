package br.imd.aqueducte.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public interface GenericService<T> {

	T createOrUpdate(T obj) throws Exception;

	List<T> findAll() throws Exception;

	Optional<T> findById(String id) throws Exception;
	
	String delete(String id) throws Exception;

}
