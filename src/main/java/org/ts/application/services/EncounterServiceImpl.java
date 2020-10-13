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
import org.ts.data.entities.Patient;
import org.ts.data.entities.Encounter;
import org.ts.data.repositories.EncounterRepository;
import org.ts.utils.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EncounterServiceImpl implements EncounterService {

	@Autowired
	private EncounterRepository encounterRepository;

	@Autowired
	private PatientService patientService;

	@Autowired
	private Validator validator;

	@Override
	public Encounter addEncounter(Encounter encounter) {

		Encounter greenEncounter = new Encounter();

		// Extract all fields to safely add to DB
		greenEncounter.setEncounterType(encounter.getEncounterType());
		greenEncounter.setEncounterDate(encounter.getEncounterDate());

		greenEncounter.setVoided(Lookup.NOT_VOIDED);
		greenEncounter.setRetired(Lookup.NOT_RETIRED);

		// Validate using Bean constraints
		Set<ConstraintViolation<Encounter>> violations = validator.validate(greenEncounter);
		if (!violations.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (ConstraintViolation<Encounter> constraintViolation : violations) {
				sb.append(" -> " + constraintViolation.getMessage());
			}

			throw new ConstraintViolationException("Validation error: " + sb.toString(), violations);
		}

		// Only proceed to search for a patient if we have a reference
		if (encounter.getPatient() == null || encounter.getPatient().getId() == null) {
			throw new EntryNotFoundException("Unable to find existing PATIENT reference");
		}

		Encounter newEncounter = new Encounter();

		// Get a patient instance for the new encounter
		Patient existingPatient = new Patient();
		try {
			existingPatient = patientService.getActivePatient(encounter.getPatient().getId());
		} catch (EntryNotFoundException e) {
			throw new EntryNotFoundException("Unable to find existing [PATIENT] " + encounter.getPatient().getId());
		} catch (EntryNotActiveException e) {
			throw new EntryNotActiveException("Unable to find active [PATIENT] " + encounter.getPatient().getId());
		}

		// Only attempt to save the qualification if we have an existing reference
		newEncounter = encounterRepository.save(greenEncounter);

		// Add encounters to an existing Patient instance
		newEncounter.setPatient(existingPatient);
		newEncounter = encounterRepository.save(newEncounter);

		return newEncounter;

	}

	@Override
	public List<Encounter> getEncounters() {

		List<Encounter> encounters = encounterRepository.findAll().stream()
		        .filter(p -> p.getVoided() != Lookup.VOIDED && p.getRetired() != Lookup.RETIRED)
		        .collect(Collectors.toList());
		
		encounters.sort(Comparator.comparing(Encounter::getId));
		return encounters;
	}

	@Override
	public List<Encounter> getEncounters(Long patientId) {
		List<Encounter> encounters = encounterRepository.findAll().stream().filter(p -> p.getVoided() != Lookup.VOIDED
		        && p.getRetired() != Lookup.RETIRED && p.getPatient().getId().longValue() == patientId)
		        .collect(Collectors.toList());
		encounters.sort(Comparator.comparing(Encounter::getId));
		return encounters;
	}

	@Override
	public Encounter getActiveEncounter(Long encounterId) throws EntryNotActiveException, EntryNotFoundException {
		Encounter encounter = encounterRepository.findById(encounterId).orElse(null);
		if (encounter != null && encounter.getVoided() != Lookup.VOIDED && encounter.getRetired() != Lookup.RETIRED) {
			return encounter;
		} else {
			if (encounter == null || encounter.getVoided() == Lookup.VOIDED) {
				throw new EntryNotFoundException("Invalid operation for [ENCOUNTER]." + encounterId);
			} else {
				throw new EntryNotActiveException("Invalid operation for [ENCOUNTER]." + encounterId);
			}
		}
	}

	@Override
	public void deleteEncounter(Long encounterId) throws EntryNotFoundException {
		Encounter encounter = encounterRepository.findById(encounterId).orElse(null);
		if (encounter != null && encounter.getVoided() != Lookup.VOIDED) {
			encounter.setVoided(Lookup.VOIDED);
			encounter.setVoidedReason("System operation - voided");
			encounterRepository.save(encounter);
			log.info("Deleted encounter with ID: " + encounterId);
		} else {
			throw new EntryNotFoundException("Invalid operation for [ENCOUNTER]." + encounterId);
		}
	}

	@Override
	public void retireEncounter(Long encounterId) throws EntryNotFoundException {
		Encounter encounter = encounterRepository.findById(encounterId).orElse(null);
		if (encounter != null && encounter.getRetired() != Lookup.RETIRED) {
			encounter.setRetired(Lookup.RETIRED);
			encounter.setRetiredReason("System operation - retired");
			encounterRepository.save(encounter);
			log.info("Retired encounter with ID: " + encounterId);
		} else {
			throw new EntryNotFoundException("Invalid operation for [ENCOUNTER]." + encounterId);
		}
	}
}
