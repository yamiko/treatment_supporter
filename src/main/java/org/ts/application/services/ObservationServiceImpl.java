package org.ts.application.services;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.data.entities.Encounter;
import org.ts.data.entities.Observation;
import org.ts.data.repositories.ObservationRepository;
import org.ts.utils.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ObservationServiceImpl implements ObservationService {

	@Autowired
	private ObservationRepository observationRepository;

	@Autowired
	private EncounterService encounterService;

	@Autowired
	private Validator validator;

	@Override
	public Observation addObservation(Observation observation) throws EntryNotFoundException, EntryNotActiveException {

		Observation greenObservation = new Observation();

		// Extract all fields to safely add to DB
		greenObservation.setConcept(observation.getConcept());
		greenObservation.setConceptValue(observation.getConceptValue());
		greenObservation.setIntValue(observation.getIntValue());
		greenObservation.setDateTimeValue(observation.getDateTimeValue());
		greenObservation.setObservationDate(observation.getObservationDate());

		greenObservation.setVoided(Lookup.NOT_VOIDED);
		greenObservation.setRetired(Lookup.NOT_RETIRED);

		// Validate using Bean constraints
		Set<ConstraintViolation<Observation>> violations = validator.validate(greenObservation);
		if (!violations.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (ConstraintViolation<Observation> constraintViolation : violations) {
				sb.append(" -> " + constraintViolation.getMessage());
			}

			throw new ConstraintViolationException("Validation error: " + sb.toString(), violations);
		}

		// Only proceed to search for observation and encounter if we have references
		if (observation.getEncounter() == null || observation.getEncounter().getId() == null) {
			throw new EntryNotFoundException("Unable to find existing ENCOUNTER references");
		}

		Observation newObservation = new Observation();

		// Get reference entities
		Encounter existingEncounter = new Encounter();
		try {
			existingEncounter = encounterService.getActiveEncounter(observation.getEncounter().getId());
		} catch (EntryNotFoundException e) {
			throw new EntryNotFoundException("Unable to find existing ENCOUNTER reference");
		} catch (EntryNotActiveException e) {
			throw new EntryNotActiveException("Unable to find active ENCOUNTER reference");
		}

		// Only attempt to save the observation if we have an existing encounter
		newObservation = observationRepository.save(greenObservation);

		// Add references to existing Encounter instance
		newObservation.setEncounter(existingEncounter);
		newObservation = observationRepository.save(newObservation);

		return newObservation;
	}

	@Override
	public List<Observation> getObservations() {
		List<Observation> observations = observationRepository.findAll().stream()
		        .filter(p -> p.getVoided() != Lookup.VOIDED && p.getRetired() != Lookup.RETIRED)
		        .collect(Collectors.toList());
		observations.sort(Comparator.comparing(Observation::getId));
		return observations;
	}

	@Override
	public List<Observation> getObservations(Long encounterId) {
		List<Observation> observations = observationRepository.findAll().stream()
		        .filter(p -> p.getVoided() != Lookup.VOIDED && p.getRetired() != Lookup.RETIRED
		                && p.getEncounter().getId().longValue() == encounterId)
		        .collect(Collectors.toList());
		observations.sort(Comparator.comparing(Observation::getId));
		return observations;
	}

	@Override
	public Observation getActiveObservation(Long observationId) throws EntryNotActiveException, EntryNotFoundException {
		Observation observation = observationRepository.findById(observationId).orElse(null);
		if (observation != null && observation.getVoided() != Lookup.VOIDED
		        && observation.getRetired() != Lookup.RETIRED) {
			return observation;
		} else {
			if (observation == null || observation.getVoided() == Lookup.VOIDED) {
				throw new EntryNotFoundException("Invalid operation for [QUALIFICATION]." + observationId);
			} else {
				throw new EntryNotActiveException("Invalid operation for [QUALIFICATION]." + observationId);
			}
		}
	}

	@Override
	public void deleteObservation(Long observationId) throws EntryNotFoundException {
		Observation observation = observationRepository.findById(observationId).orElse(null);
		if (observation != null && observation.getVoided() != Lookup.VOIDED) {
			observation.setVoided(Lookup.VOIDED);
			observation.setVoidedReason("System operation - voided");
			observationRepository.save(observation);
			log.info("Deleted observation with ID: " + observationId);
		} else {
			throw new EntryNotFoundException("Invalid operation for [QUALIFICATION]." + observationId);
		}
	}

	@Override
	public void retireObservation(Long observationId) throws EntryNotFoundException {
		Observation observation = observationRepository.findById(observationId).orElse(null);
		if (observation != null && observation.getRetired() != Lookup.RETIRED) {
			observation.setRetired(Lookup.RETIRED);
			observation.setRetiredReason("System operation - retired");
			observationRepository.save(observation);
			log.info("Retired observation with ID: " + observationId);
		} else {
			throw new EntryNotFoundException("Invalid operation for [QUALIFICATION]." + observationId);
		}
	}
}
