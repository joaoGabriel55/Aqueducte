package br.imd.smartsysnc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.imd.smartsysnc.models.EntityWithLinkedID;
import br.imd.smartsysnc.repositories.EntityWithLinkedIDRepository;
import br.imd.smartsysnc.service.GenericService;

@Service
public class EntityWithLinkedIDServiceImpl implements GenericService<EntityWithLinkedID> {

	@Autowired
	private EntityWithLinkedIDRepository entityWithLinkedIDRepository;

	@Override
	public EntityWithLinkedID createOrUpdate(EntityWithLinkedID entityWithLinkedID) {
		return this.entityWithLinkedIDRepository.save(entityWithLinkedID);
	}

	@Override
	public List<EntityWithLinkedID> findAll() {
		return this.entityWithLinkedIDRepository.findAll();
	}

	@Override
	public List<EntityWithLinkedID> findById() {
		return null;
	}

	public EntityWithLinkedID findByEntitySGEOL(String entitySGEOL) {
		return this.entityWithLinkedIDRepository.findByEntitySGEOL(entitySGEOL);
	}

}
