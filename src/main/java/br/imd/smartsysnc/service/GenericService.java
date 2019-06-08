package br.imd.smartsysnc.service;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public interface GenericService<T> {
	
	T createOrUpdate(T obj);

	List<T> findAll();
	
	List<T> findById();


}
