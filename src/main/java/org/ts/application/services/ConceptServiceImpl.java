package org.ts.application.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.data.entities.Concept;
import org.ts.data.repositories.ConceptRepository;
import org.ts.utils.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConceptServiceImpl implements ConceptService {


	@Autowired
	private ConceptRepository conceptRepository;

	@Override
	public List<Concept> getConcepts() {

		List<Concept> concepts = conceptRepository.findAll().stream()
		        .filter(p -> p.getVoided() != Lookup.VOIDED && p.getRetired() != Lookup.RETIRED)
		        .collect(Collectors.toList());
		concepts.sort(Comparator.comparing(Concept::getId));
		return concepts;
	}

	@Override
	public Concept getActiveConcept(Long conceptId) throws EntryNotActiveException, EntryNotFoundException {
		Concept concept = conceptRepository.findById(conceptId).orElse(null);
		if (concept != null && concept.getVoided() != Lookup.VOIDED && concept.getRetired() != Lookup.RETIRED) {
			return concept;
		} else {
			if (concept == null || concept.getVoided() == Lookup.VOIDED) {
				throw new EntryNotFoundException("Invalid operation for [CONCEPT]." + conceptId);
			} else {
				throw new EntryNotActiveException("Invalid operation for [CONCEPT]." + conceptId);
			}
		}
	}

	@Override
	public Concept getActiveConcept(String name) throws EntryNotActiveException, EntryNotFoundException {
		Concept concept = conceptRepository.findAllByName(name).stream().filter(p -> p.getVoided() != Lookup.VOIDED && p.getRetired() != Lookup.RETIRED && p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		if (concept != null) {
			return concept;
		} else {
			throw new EntryNotFoundException("Invalid operation for [CONCEPT]." + name);
		}
	}
}
