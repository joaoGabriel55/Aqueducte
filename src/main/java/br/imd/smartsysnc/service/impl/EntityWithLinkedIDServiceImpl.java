package br.imd.smartsysnc.service.impl;

import java.util.ArrayList;
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
			EntityWithLinkedID entityWithLinkedID, String idIndentify) {

		List<Map<String, Object>> listReferenceForRelationshipsNew = listToAdd.stream().map(elem -> {
			Map<String, Object> idsRelationship = (LinkedHashMap<String, Object>) elem.getIdsRelationship();
			return idsRelationship;
		}).collect(Collectors.toList());

		List<Map<String, Object>> listReferenceForRelationshipsPersisted = entityWithLinkedID.getRefList().stream()
				.map(elem -> {
					Map<String, Object> idsRelationship = elem.getIdsRelationship();
					return idsRelationship;
				}).collect(Collectors.toList());

		if (!listReferenceForRelationshipsNew.equals(listReferenceForRelationshipsPersisted)) {

			entityWithLinkedID.setRefList(updateListForIdsRelationships(entityWithLinkedID, listToAdd, idIndentify));

			entityWithLinkedID.getRefList().addAll(getListOfNewReferenceForRelationships(
					listReferenceForRelationshipsNew, entityWithLinkedID, listToAdd));
		}

		return entityWithLinkedID;
	}

	private List<ReferenceForRelationship> updateListForIdsRelationships(EntityWithLinkedID entityWithLinkedID,
			List<ReferenceForRelationship> listToAdd, String idIndentify) {
		List<ReferenceForRelationship> listToUpdate = new ArrayList<>();

		for (ReferenceForRelationship objPersisted : entityWithLinkedID.getRefList()) {
			for (ReferenceForRelationship objNew : listToAdd) {
				if (objPersisted.getIdsRelationship().get(idIndentify)
						.equals(objNew.getIdsRelationship().get(idIndentify))) {
					objPersisted.setIdsRelationship(objNew.getIdsRelationship());
					listToUpdate.add(objPersisted);
				}
			}
		}
		return listToUpdate;
	}

	private List<ReferenceForRelationship> getListOfNewReferenceForRelationships(
			List<Map<String, Object>> listReferenceForRelationshipsNew, EntityWithLinkedID entityWithLinkedID,
			List<ReferenceForRelationship> listToAdd) {

		List<Map<String, Object>> listReferenceForRelationshipsPersisted = entityWithLinkedID.getRefList().stream()
				.map(elem -> {
					Map<String, Object> idsRelationship = elem.getIdsRelationship();
					return idsRelationship;
				}).collect(Collectors.toList());

		List<Map<String, Object>> listFilted = listReferenceForRelationshipsNew.stream()
				.filter(elem -> !listReferenceForRelationshipsPersisted.contains(elem)).collect(Collectors.toList());

		List<ReferenceForRelationship> listReferenceForRelationshipsNewFilted = listToAdd.stream()
				.filter(elem -> listFilted.contains(elem.getIdsRelationship())).collect(Collectors.toList());
		return listReferenceForRelationshipsNewFilted;
	}

}
