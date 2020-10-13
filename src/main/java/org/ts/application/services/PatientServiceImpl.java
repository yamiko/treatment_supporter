package org.ts.application.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.ts.application.exceptions.EntryNotActiveException;
import org.ts.application.exceptions.EntryNotFoundException;
import org.ts.data.entities.ApplicationUser;
import org.ts.data.entities.Condition;
import org.ts.data.entities.Observation;
import org.ts.data.entities.Patient;
import org.ts.data.entities.RegimenCategory;
import org.ts.data.repositories.PatientRepository;
import org.ts.utils.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.temporal.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PatientServiceImpl implements PatientService {

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private ApplicationUserService userService;

	@Autowired
	private ObservationService observationService;

	@Autowired
	private RegimenCategoryService regimenCategoryService;

	@Autowired
	private Validator validator;

	@Override
	public Patient addPatient(Patient patient) {
		Patient greenPatient = new Patient();

		// Extract all fields to safely add to DB
		greenPatient.setAddressLine1(patient.getAddressLine1());
		greenPatient.setAddressLine2(patient.getAddressLine2());
		greenPatient.setAddressLine3(patient.getAddressLine3());
		greenPatient.setCountry(patient.getCountry());
		greenPatient.setPostcode(patient.getPostcode());

		greenPatient.setGender(patient.getGender());
		greenPatient.setDateOfBirth(patient.getDateOfBirth());
		greenPatient.setEmail(patient.getEmail());
		greenPatient.setPreferredContactNumber(patient.getPreferredContactNumber());
		greenPatient.setAlternativeContactNumber(patient.getAlternativeContactNumber());

		greenPatient.setFirstName(patient.getFirstName());
		greenPatient.setMiddleName(patient.getMiddleName());
		greenPatient.setLastName(patient.getLastName());
		greenPatient.setTitle(patient.getTitle());

		greenPatient.setVoided(Lookup.NOT_VOIDED);
		greenPatient.setRetired(Lookup.NOT_RETIRED);

		// Validate using Bean constraints
		Set<ConstraintViolation<Patient>> violations = validator.validate(greenPatient);
		if (!violations.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (ConstraintViolation<Patient> constraintViolation : violations) {
				sb.append(" -> " + constraintViolation.getMessage());
			}

			throw new ConstraintViolationException("Validation error: " + sb.toString(), violations);
		}

		// Only proceed to search for a user if we have a valid reference
		if (patient.getApplicationUser() == null || patient.getApplicationUser().getId() == null) {
			throw new EntryNotFoundException("Unable to find existing USER reference");
		}

		Patient newPatient = new Patient();

		// Get a user instance for the patient
		ApplicationUser existingUser = new ApplicationUser();
		try {
			existingUser = userService.getActiveUser(patient.getApplicationUser().getId());
		} catch (EntryNotFoundException e) {
			throw new EntryNotFoundException("Unable to find existing [USER] " + patient.getApplicationUser().getId());
		} catch (EntryNotActiveException e) {
			throw new EntryNotActiveException("Unable to find active [USER] " + patient.getApplicationUser().getId());
		}

		newPatient = patientRepository.save(greenPatient);

		// Add patient to an existing user instance
		newPatient.setApplicationUser(existingUser);
		newPatient = patientRepository.save(newPatient);

		return newPatient;
	}

	@Override
	public List<Patient> getPatients() {
		List<Patient> patients = patientRepository.findAll().stream()
		        .filter(p -> p.getVoided() != Lookup.VOIDED && p.getRetired() != Lookup.RETIRED)
		        .collect(Collectors.toList());
		patients.sort(Comparator.comparing(Patient::getId));
		return patients;
	}

	// Can only handle coded concept and age conditions
	@Override
	public List<RegimenCategory> getRecommendedRegimenCategories(Long patientId, LocalDateTime encounterDate) {
		Patient patient = getActivePatient(patientId);

		List<RegimenCategory> recommendations = new ArrayList<RegimenCategory>();
		for (RegimenCategory category : regimenCategoryService.getRegimenCategories()) {
			// Check if it is an applicable recommendation for this patient
			boolean addRecommendation = true;
			log.info("New recommendation");
			for (Condition condition : category.getCondition().stream().collect(Collectors.toList())) {
				log.info(
				        "Found condition of type " + condition.getConditionType() + " with ID -> " + condition.getId());
				if(condition.getConcept().getName().equalsIgnoreCase("temperature")) {
					log.info("Dealing with temperature");
				}
				if (addRecommendation) {
					if (condition.getConcept().getName().equalsIgnoreCase("Age")) { // Calculate and compare age
						log.info("Working with age");
						int age = getAge(patient);

						if (condition.getRelator() == Lookup.EQUALS) {
							if (!(age == condition.getIntValue())) {
								addRecommendation = false;
							}
						} else if (condition.getRelator() == Lookup.GREATER_OR_EQUAL) {
							if (!(age >= condition.getIntValue())) {
								addRecommendation = false;
							}
						} else if (condition.getRelator() == Lookup.LESS) {
							if (!(age < condition.getIntValue())) {
								addRecommendation = false;
							}
						} else {
							if (!(age == condition.getIntValue())) {
								addRecommendation = false;
							}
						}
					} else {
						if (condition.getConditionType() == Lookup.CONCEPT_CONDITION) { // Handle concept conditions
							log.info("Working with concept condition");
							List<Observation> observations = observationService.getObservations();
							observations = observations.stream()
							        .filter(p -> p.getEncounter().getEncounterDate().toLocalDate()
							                .isEqual(encounterDate.toLocalDate())
							                && p.getEncounter().getPatient().getId() == patient.getId()
							                && p.getConcept().getId() == condition.getConcept().getId())
							        .collect(Collectors.toList());

							observations.sort(Comparator.comparing(Observation::getObservationDate).reversed());

							Observation recentObservation = observations.stream().findFirst().orElse(new Observation());

							if (recentObservation.getId() > 0) {
								log.info("Recent obs -> " + recentObservation.getConceptValue().getId());
								log.info("Concept value -> " + condition.getConceptValue().getId());

								if (condition.getConceptValue().getId() != recentObservation.getConceptValue()
								        .getId()) {
									addRecommendation = false;
								}
							} else {
								addRecommendation = false;
							}

						} else if (condition.getConditionType() == Lookup.INTEGER_CONDITION) { // Handle integer
							log.info("Working with integer condition");
							// conditions
							List<Observation> observations = observationService.getObservations();
							observations = observations.stream()
							        .filter(p -> p.getEncounter().getEncounterDate().toLocalDate()
							                .isEqual(encounterDate.toLocalDate())
							                && p.getEncounter().getPatient().getId() == patient.getId()
							                && p.getConcept().getId() == condition.getConcept().getId())
							        .collect(Collectors.toList());

							observations.sort(Comparator.comparing(Observation::getObservationDate).reversed());
							for(Observation o: observations) {
								log.info("Found obs with ID -> " + o.getId());
							}

							Observation recentObservation = observations.stream().findFirst().orElse(new Observation());

							if(condition.getConcept().getName().equalsIgnoreCase("temperature")) {
								log.info("Current temperature -> " +recentObservation.getIntValue());
								for(Observation o: observations) {
									log.info("Found obs with ID -> " + o.getId());
									log.info("concept ID:: " + o.getConcept().getId());
									log.info("obsValue:: " + o.getIntValue());
								}

							}
							
							if (recentObservation.getId() > 0) {
								if (condition.getRelator() == Lookup.EQUALS) {
									if (!(recentObservation.getIntValue() == condition.getIntValue())) {
										addRecommendation = false;
									}
								} else if (condition.getRelator() == Lookup.GREATER_OR_EQUAL) {
									if (!(recentObservation.getIntValue() >= condition.getIntValue())) {
										addRecommendation = false;
									}
								} else if (condition.getRelator() == Lookup.LESS) {
									if (!(recentObservation.getIntValue() < condition.getIntValue())) {
										addRecommendation = false;
									}
								} else {
									if (!(recentObservation.getIntValue() == condition.getIntValue())) {
										addRecommendation = false;
									}
								}
							} else {
								addRecommendation = false;
							}

						}
					}
				} else {
					log.info("Skipping rest of recomendation");
				}
			}
			
			if (addRecommendation) {
				recommendations.add(category);
			}


		}

		return recommendations;
	}

	int getAge(Patient patient) {
		return Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
	}

	@Override
	public Patient getActivePatient(Long patientId) throws EntryNotActiveException, EntryNotFoundException {
		Patient patient = patientRepository.findById(patientId).orElse(null);
		if (patient != null && patient.getVoided() != Lookup.VOIDED && patient.getRetired() != Lookup.RETIRED) {
			return patient;
		} else {
			if (patient == null || patient.getVoided() == Lookup.VOIDED) {
				throw new EntryNotFoundException("Invalid operation for [CANDIDATE]." + patientId);
			} else {
				throw new EntryNotActiveException("Invalid operation for [CANDIDATE]." + patientId);
			}
		}
	}

	@Override
	public void deletePatient(Long patientId) throws EntryNotFoundException {
		Patient patient = patientRepository.findById(patientId).orElse(null);
		if (patient != null && patient.getVoided() != Lookup.VOIDED) {
			patient.setVoided(Lookup.VOIDED);
			patient.setVoidedReason("System operation - voided");
			patientRepository.save(patient);
			log.info("Deleted patient with ID: " + patientId);
		} else {
			throw new EntryNotFoundException("Invalid operation for [CANDIDATE]." + patientId);
		}
	}

	@Override
	public void retirePatient(Long patientId) throws EntryNotFoundException {
		Patient patient = patientRepository.findById(patientId).orElse(null);
		if (patient != null && patient.getRetired() != Lookup.RETIRED) {
			patient.setRetired(Lookup.RETIRED);
			patient.setRetiredReason("System operation - retired");
			patientRepository.save(patient);
			log.info("Retired patient with ID: " + patientId);
		} else {
			throw new EntryNotFoundException("Invalid operation for [CANDIDATE]." + patientId);
		}
	}
}
