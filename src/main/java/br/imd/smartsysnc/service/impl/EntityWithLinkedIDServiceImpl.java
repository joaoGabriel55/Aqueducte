package br.imd.smartsysnc.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.imd.smartsysnc.models.EntityWithLinkedID;
import br.imd.smartsysnc.models.ReferenceForRelationship;
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

	public EntityWithLinkedID findLinkedIdsWhichAlreadyExists(List<ReferenceForRelationship> listToAdd,
			EntityWithLinkedID entityWithLinkedID) {

		List<Map<String, Object>> listReferenceForRelationshipsNew = listToAdd.stream().map(elem -> {
			Map<String, Object> idsRelationship = (LinkedHashMap<String, Object>) elem.getIdsRelationship();
			return idsRelationship;
		}).collect(Collectors.toList());
		
		List<Map<String, Object>> listReferenceForRelationshipsPersisted = entityWithLinkedID.getRefList().stream().map(elem -> {
			Map<String, Object> idsRelationship =  elem.getIdsRelationship();
			return idsRelationship;
		}).collect(Collectors.toList());

		List<Map<String, Object>> listFilted = listReferenceForRelationshipsNew.stream().filter(elem -> 
			!listReferenceForRelationshipsPersisted.contains(elem)
		).collect(Collectors.toList());

		boolean isToAdd = false;

		return entityWithLinkedID;
	}

}
